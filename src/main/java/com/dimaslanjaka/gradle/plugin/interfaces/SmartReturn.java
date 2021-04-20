package com.dimaslanjaka.gradle.plugin.interfaces;

public enum SmartReturn {
	IntegerType, DoubleType, ListType, ArrayType, StringType, StructType, StringListType;

	@SuppressWarnings({"unchecked", "unused"})
	public <T> T comeback(String value) {
		switch (this) {
			case IntegerType:
				return (T) Integer.valueOf(value);
			case DoubleType:
				return (T) Double.valueOf(value);
			default:
				return null;
		}
	}
}
