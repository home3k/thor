package com.haoyayi.thor.event;

import java.util.List;

import com.google.common.collect.Lists;

public class EventHolder {

	private static final ThreadLocal<List<AbstractModelEvent>> THREAD_EVENTS = new ThreadLocal<List<AbstractModelEvent>>() {
		@Override
		protected List<AbstractModelEvent> initialValue() {
			return Lists.newArrayList();
		}
	};

	public static List<AbstractModelEvent> getThreadEvents() {
		return THREAD_EVENTS.get();
	}
	
}
