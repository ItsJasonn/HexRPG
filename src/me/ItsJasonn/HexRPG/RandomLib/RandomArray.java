package me.ItsJasonn.HexRPG.RandomLib;

import java.util.List;

public class RandomArray {
	/**
	 * Converts list with any object to an array of strings
	 * @param list The list with objects to convert
	 * @return The array
	 */
	public static String[] objectToStringArray(List<Object> list) {
		String[] array = new String[list.size()];
		for(int i=0;i<list.size();i++) {
			array[i] = list.get(i).toString();
		}
		return array;
	}
	
	/**
	 * Converts list with any object to an array of integers
	 * @param list The list with objects to convert
	 * @return The array
	 */
	public static int[] objectToIntegerArray(List<Object> list) {
		int[] array = new int[list.size()];
		for(int i=0;i<list.size();i++) {
			try {
				array[i] = Integer.parseInt(list.get(i).toString());
			} catch(NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return array;
	}
	
	/**
	 * Converts list with any object to an array of booleans
	 * List can only contain 0's and 1's
	 * @param list The list with objects to convert
	 * @return The array
	 */
	public static boolean[] objectToBooleanArray(List<Object> list) {
		boolean[] array = new boolean[list.size()];
		for(int i=0;i<list.size();i++) {
			try {
				int binair = Integer.parseInt(list.get(i).toString());
				
				if(binair == 0) {
					array[i] = false;
				} else if(binair == 1) {
					array[i] = true;
				}
			} catch(NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return array;
	}
}
