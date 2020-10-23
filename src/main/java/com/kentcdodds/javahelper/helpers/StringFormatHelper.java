package com.kentcdodds.javahelper.helpers;

/**
 * @author Kent
 */
public class StringFormatHelper {

	/**
	 * Exact code: String fixedNumber = cleanNumber(number).replace(".", ""); return
	 * StringHelper.insertIntoString(fixedNumber, char0, "", "", char3, "", "", char6);
	 *
	 * @param number
	 * @param char0
	 * @param char3
	 * @param char6
	 * @return
	 */
	public static String formatPhoneNumber(String number, String char0, String char3, String char6) {
		String fixedNumber = NumberHelper.cleanNumber(number).replace(".", "");
		return StringHelper.insertIntoString(fixedNumber, char0, "", "", char3, "", "", char6);
	}

	/**
	 * Returns: formatPhoneNumber(number, "(", ") ", "-") which formats a number to look like this: (555)
	 * 123-4567. Note: If you give too many numbers they'll just be added to the end of the number. Like this
	 * (555) 123-456789101112
	 *
	 * @param number
	 * @return formatted phone number
	 */
	public static String formatPhoneNumber(String number) {
		return formatPhoneNumber(number, "(", ") ", "-");
	}

	/**
	 * If the call was formatCreditCard("1234567812345678", "-") the result would be: 1234-5678-1234-5678. This
	 * will continue if you give too long or too short of a number. If you give 12345, the result will be:
	 * 1234-5. If you give 12345678123456789, it will return 1234-5678-1234-5678-9. NOTE: Also first calls
	 * NumberHelper.cleanNumber(creditCard).replace(".", "");
	 *
	 * @param creditCard the number string to format like a credit card number
	 * @param separator  the item to put between every 4 numbers
	 * @return formatted credit card
	 */
	public static String formatCreditCard(String creditCard, String separator) {
		String fixedCreditCard = NumberHelper.cleanNumber(creditCard).replace(".", "");
		char[] numbers = fixedCreditCard.toCharArray();
		//1234567812345678
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < numbers.length; i++) {
			if (i % 4 == 0 && i > 0) {
				sb.append(separator);
			}
			sb.append(numbers[i]);
		}
		return sb.toString();
	}

	/**
	 * This is posting something in a URL. It replaces anything in the string given with percentEncoded values
	 *
	 * @param originalString
	 * @return
	 */
	public static String percentEncodeString(String originalString) {
		//It is important that % is first, if you put it any later it will replace % with anything that's replace before.
		//Try it, you'll see what I mean.
		java.util.Map<Character, String> replaceMap = new java.util.TreeMap<Character, String>();
		//<editor-fold defaultstate="collapsed" desc="Set Map">
		replaceMap.put('%', "%25");
		replaceMap.put('!', "%21");
		replaceMap.put('*', "%2A");
		replaceMap.put('\'', "%27"); //Escape character. This value is: '
		replaceMap.put('(', "%28");
		replaceMap.put(')', "%29");
		replaceMap.put(';', "%3B");
		replaceMap.put(':', "%3A");
		replaceMap.put('@', "%40");
		replaceMap.put('&', "%26");
		replaceMap.put('=', "%3D");
		replaceMap.put('+', "%2B");
		replaceMap.put('$', "%24");
		replaceMap.put(',', "%2C");
		replaceMap.put('/', "%2F");
		replaceMap.put('?', "%3F");
		replaceMap.put('#', "%23");
		replaceMap.put('[', "%5B");
		replaceMap.put(']', "%5D");
		replaceMap.put(' ', "%20");
		//</editor-fold>
		char[] charArry = originalString.toCharArray();
		StringBuilder fixedString = new StringBuilder();
		for (int i = 0; i < charArry.length; i++) {
			Character c = charArry[i];
			String replacement = replaceMap.get(c);
			if (replacement != null) {
				fixedString.append(replacement);
			} else {
				fixedString.append(c);
			}
		}
		return fixedString.toString();
	}
}
