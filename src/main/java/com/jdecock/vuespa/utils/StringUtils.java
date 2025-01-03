package com.jdecock.vuespa.utils;

public abstract class StringUtils {
	public static boolean isEmpty(String str) {
		return str == null || str.trim().isEmpty();
	}

	public static boolean isSet(String str) {
		return !isEmpty(str);
	}
}
