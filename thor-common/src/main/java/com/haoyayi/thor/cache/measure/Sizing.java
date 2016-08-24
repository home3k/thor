/*
 * Copyright 2015 51haoyayi.com Inc Limited.
 * All rights reserved.
 */
package com.haoyayi.thor.cache.measure;

import java.util.Formatter;

/**
 * @author home3k (sunkai@51haoyayi.com)
 */
public class Sizing {

	public static int Gb(double giga) {
		return (int) giga * 1024 * 1024 * 1024;
	}

	public static int Mb(double mega) {
		return (int) mega * 1024 * 1024;
	}

	public static int Kb(double kilo) {
		return (int) kilo * 1024;
	}

	public static int unlimited() {
		return -1;
	}

	public static String inKb(long bytes) {
		return new Formatter().format("%(,.1fKb", (double) bytes / 1024)
				.toString();
	}

	public static String inMb(long bytes) {
		return new Formatter().format("%(,.1fMb", (double) bytes / 1024 / 1024)
				.toString();
	}

	public static String inGb(long bytes) {
		return new Formatter().format("%(,.1fKb",
				(double) bytes / 1024 / 1024 / 1024).toString();
	}

}
