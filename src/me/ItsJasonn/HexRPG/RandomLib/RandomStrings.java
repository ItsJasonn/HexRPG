package me.ItsJasonn.HexRPG.RandomLib;

public class RandomStrings {
	/**
	 * Trims double (or more) whitespaces so only one space exists
	 * @param str String to trim
	 * @return String with only one whitespace each space
	 */
	public static String multipleTrim(String str) {
		for(int i=0;i<str.length();i++) {
			str = str.replace("  ", " ");
		}
		return str;
	}
	
	/**
	 * Convert a string into an integer where only the numbers are returned
	 * @param str String to convert
	 * @return int with all numbers from the string
	 */
	public static int getNumbersFromString(String str) {
		String tmp = "";
		for(int i=0;i<str.length();i++) {
			if(Character.isDigit(str.charAt(i))) {
				tmp += str.charAt(i);
			}
		}
		return Integer.parseInt(tmp);
	}
}