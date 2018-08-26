package me.ItsJasonn.HexRPG.RandomLib;

public class RandomNumbers {
	/**
	 * Combines all found numbers in a string into one number
	 * @param str String to convert
	 * @return Combined numbers
	 */
	public static int getNumbersFromStringSequence(String str) {
		String num = "";
		for(int i=0;i<str.length();i++) {
			if(Character.isDigit(str.charAt(i))) {
				num += str.charAt(i);
			}
		}
		return Integer.parseInt(num);
	}
	
	public static boolean isInt(String str) {
		try {
			Integer.parseInt(str);
		} catch(NumberFormatException e) {
			return false;
		}
		return true;
	}
}