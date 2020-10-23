package com.dimaslanjaka.versioning;

import junit.framework.TestCase;

public class AlphaNumeric {
}

class QuickTest extends TestCase {

	private final int reps = 1000000;

	public void testRegexp() {
		for (int i = 0; i < reps; i++)
			("ab4r3rgf" + i).matches("[a-zA-Z0-9]");
	}

	public void testIsAlphanumeric() {
		for (int i = 0; i < reps; i++)
			isAlphanumeric("ab4r3rgf" + i);
	}

	public void testIsAlphanumeric2() {
		for (int i = 0; i < reps; i++)
			isAlphanumeric2("ab4r3rgf" + i);
	}

	public boolean isAlphanumeric(String str) {
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (!Character.isLetterOrDigit(c))
				return false;
		}

		return true;
	}

	public boolean isAlphanumeric2(String str) {
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c < 0x30 || (c >= 0x3a && c <= 0x40) || (c > 0x5a && c <= 0x60) || c > 0x7a)
				return false;
		}
		return true;
	}

}
