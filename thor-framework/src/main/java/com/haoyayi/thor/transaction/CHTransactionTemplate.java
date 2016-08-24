package com.haoyayi.thor.transaction;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.haoyayi.thor.changehistory.bo.ChangeLog;
import com.haoyayi.thor.changehistory.common.CHDataHolder;
import com.haoyayi.thor.changehistory.context.ChangehistoryContext;
import com.haoyayi.thor.changehistory.dao.ChangeLogDAO;
import com.haoyayi.thor.event.AbstractModelEvent;
import com.haoyayi.thor.event.EventHolder;
import com.haoyayi.thor.event.ModelAddEvent;
import com.haoyayi.thor.event.ModelDelEvent;
import com.haoyayi.thor.event.ModelSaveEvent;

@SuppressWarnings("serial")
public class CHTransactionTemplate extends TransactionTemplate implements ApplicationContextAware {
	
	@Autowired
	private ChangehistoryContext context;
	@Autowired
	private ChangeLogDAO changeLogDAO;
	
	private ApplicationContext applicationContext;
	
	@Override
	@SuppressWarnings("unchecked")
	public <T> T execute(TransactionCallback<T> action) throws TransactionException {
		ActionInvocationHandler<T> handler = new ActionInvocationHandler<T>(action);
		TransactionCallback<T> proxyAction = (TransactionCallback<T>) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] {TransactionCallback.class}, handler);
		T result = super.execute(proxyAction);
		TransactionStatus status = handler.getTransactionStatus();
		if (status != null && status.isCompleted() && status.isNewTransaction()) {
			publishAsyncEventAndClear();
			final List<ChangeLog> retryChangeLogs = new ArrayList<ChangeLog>();
			Long commitTime = System.currentTimeMillis();
			Map<ChangeLog, Long> changeLogs = CHDataHolder.getDataAddTime();
			CHDataHolder.clear();
			if (changeLogs != null && changeLogs.size() > 0) {
				for (Map.Entry<ChangeLog,Long> entry : changeLogs.entrySet()) {
					Long addTime = entry.getValue();
					if (commitTime - addTime > context.getRetryTime()) {
						retryChangeLogs.add(entry.getKey());
					}
				}
			}
			retryAddChangeLog(retryChangeLogs, 1);
		}
		return result;
	}
	
	@SuppressWarnings("rawtypes")
	private void publishAsyncEventAndClear() {
		for (AbstractModelEvent event : EventHolder.getThreadEvents()) {
			try {
				
				AbstractModelEvent asyncEvent = null;
				if (event instanceof ModelAddEvent) {
					asyncEvent = new ModelAddEvent(event.getSource());
				}
				if (event instanceof ModelSaveEvent) {
					asyncEvent = new ModelSaveEvent(event.getSource());
				}
				if (event instanceof ModelDelEvent) {
					asyncEvent = new ModelDelEvent(event.getSource());
				}
				PropertyUtils.copyProperties(asyncEvent, event);
				asyncEvent.setIsAsync(true);
				applicationContext.publishEvent(asyncEvent);
			} catch (Exception e) {
			}
		}
		EventHolder.getThreadEvents().clear();
	}
	
	private class ActionInvocationHandler<T> implements InvocationHandler {
		
		private TransactionStatus status;
		private TransactionCallback<T> action;
		
		private ActionInvocationHandler(TransactionCallback<T> action) {
			this.action = action;
		}
		
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if (method.getName().equals("doInTransaction")) {
				status = (TransactionStatus) args[0];
			}
			return method.invoke(action, args);
		}
		
		public TransactionStatus getTransactionStatus() {
			return this.status;
		}
	}
	
	private void retryAddChangeLog(final List<ChangeLog> retryChangeLogs, int retryCnt) {
		if (retryChangeLogs == null || retryChangeLogs.size() == 0) {
			return;
		}
		try {
			this.execute(new TransactionCallback<List<ChangeLog>>() {
				@Override
				public List<ChangeLog> doInTransaction(TransactionStatus status) {
					try {
						changeLogDAO.insertChangeLogs(retryChangeLogs);
						return retryChangeLogs;
					} catch (Exception e) {
						status.setRollbackOnly();
						throw new RuntimeException(e);
					}
				}
				
			});
		} catch (Exception e) {
			if (retryCnt == 3) {
				logger.error("retry to add changelogs error triple times with exception:", e);
				return;
			}
			retryAddChangeLog(retryChangeLogs, ++retryCnt);
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}
}
