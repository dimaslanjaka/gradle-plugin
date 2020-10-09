package com.dimaslanjaka.gradle.Utils;

import java.util.Arrays;

public class Array {
	public static boolean stringContainsItemFromList(String inputStr, String[] items) {
		return Arrays.stream(items).anyMatch(inputStr::contains);
	}
}
