package com.haoyayi.thor.context;

import org.apache.commons.lang3.StringUtils;

public class InterfaceContext {
	
	public static final String VERSION_1_0 = "1.0";
	
	// 针对兼容过去版本app关于医生好友关系中type字段前端不识别的问题
	public static final String VERSION_1_1 = "1.1"; 
	
	/**
	 * 针对病例圈草稿对旧版本app的兼容
	 */
	public static final String VERSION_1_2 = "1.2";
	
	/**
	 * 病例圈重大改版，八卦区，分答，优惠券等;
	 * 旧版本看不到八卦，分答主题讨论，没有优惠券相关的流水。
	 */
	public static final String VERSION_2_0 = "2.0";

	private static String version;

	public static String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		InterfaceContext.version = version;
	}
	
	public static boolean isOldVersion(String oldVersion) {
		return isOldVersion(oldVersion, InterfaceContext.version);
	}
	
	public static boolean isOldVersion(String oldVersion, String version) {
		if (StringUtils.isEmpty(oldVersion) || oldVersion.equals("-")) {
			return true;
		}
		return Double.parseDouble(oldVersion) < Double.parseDouble(version);
	}

}
