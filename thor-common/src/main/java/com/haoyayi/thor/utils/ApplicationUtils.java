package com.haoyayi.thor.utils;

import org.springframework.context.ApplicationContext;

public class ApplicationUtils {

	private static ApplicationContext applicationContext;
	
	public static void setApplicationContext(ApplicationContext applicationContext) {
		ApplicationUtils.applicationContext = applicationContext;
	}
	
	public static ApplicationContext getApplicationContext() {
		return ApplicationUtils.applicationContext;
	}
}
