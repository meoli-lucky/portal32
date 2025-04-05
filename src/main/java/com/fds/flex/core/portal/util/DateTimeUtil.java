package com.fds.flex.core.portal.util;

public class DateTimeUtil {
	public static long incrementSecond(int second) {
		long time = System.currentTimeMillis() + second * 1000;
		return time;
	}
}
