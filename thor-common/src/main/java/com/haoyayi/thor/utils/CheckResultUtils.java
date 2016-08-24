/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */

package com.haoyayi.thor.utils;

import com.haoyayi.thor.common.BizError;
import com.haoyayi.thor.common.CheckResult;

import java.util.*;


/**
 * @author home3k
 */
public class CheckResultUtils {

	public static<T> List<T> getOkData(List<CheckResult<T>> checkResultList){
		List<T> result = new ArrayList<T>();
		if(checkResultList==null||checkResultList.size()==0){
			return result;
		}
		for(CheckResult<T> checkResultItem:checkResultList){
			if(!checkResultItem.isErrorResult()){
				result.add(checkResultItem.getData());
			}
		}
		return result;
	}
	
	public static<T> List<BizError> getErrors(List<CheckResult<T>> checkResultList){
		List<BizError> result = new ArrayList<BizError>();
		if(checkResultList==null||checkResultList.size()==0){
			return result;
		}
		for(CheckResult<T> checkResultItem:checkResultList){
			if(checkResultItem.isErrorResult()){
				result.add(checkResultItem.getError());
			}
		}
		return result;
	}
	
	public static<T> List<CheckResult<T>> mergeCheckResult(List<CheckResult<T>> base,List<CheckResult<T>> newData){
		if(base==null||base.size()==0){
			return base;
		}
		if(newData==null||newData.size()==0){
			return base;
		}
		List<T> baseOk = getOkData(base);
		if(baseOk.size()<newData.size()){
			throw new IllegalStateException("newData size is greater than base size,can't merge");
		}
		Iterator<CheckResult<T>> newDataIter = newData.iterator();
		for(CheckResult<T> checkResult:base){
			if(checkResult.isErrorResult()){
				continue;
			}
			CheckResult<T> newDataResult = newDataIter.next();
			checkResult.setCheckResult(newDataResult);
		}
		return base;
	}
	
	/**
	 *
	 */
	public static<T> Map<Long,T> getOkData(Map<Long,CheckResult<T>> checkResultList){
		Map<Long,T> result = new HashMap<Long, T>();
		if(checkResultList==null||checkResultList.size()==0){
			return result;
		}
		for(Long key : checkResultList.keySet()){
			CheckResult<T> checkResultItem=checkResultList.get(key);
			if(!checkResultItem.isErrorResult()){
				result.put(key,checkResultItem.getData());
			}
		}
		return result;
	}
	
	/**
	 *
	 */
	public static<T> Map<Long,CheckResult<T>> mergeCheckResult(Map<Long,CheckResult<T>> base,Map<Long,CheckResult<T>> newData){
		if(base==null||base.size()==0){
			return base;
		}
		if(newData==null||newData.size()==0){
			return base;
		}
		for(Long key:base.keySet()){
			CheckResult<T> baseCheckResult=base.get(key);
			if(baseCheckResult.isErrorResult()){
				continue;
			}
			CheckResult<T> newCheckResult=newData.get(key);
			base.put(key, newCheckResult);
			
		}
		return base;
	}
	
	
}
