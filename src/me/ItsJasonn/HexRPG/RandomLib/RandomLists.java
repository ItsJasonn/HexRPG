package me.ItsJasonn.HexRPG.RandomLib;

import java.util.List;

import org.bukkit.ChatColor;

public class RandomLists {
	/**
	 * Gives the line in a list of a searched string
	 * @param list List to search in
	 * @param str String to look for in the list
	 * @return The line where the string got found in
	 */
	public static int getLineByString(List<String> list, String str) {
		for(int i=0;i<list.size();i++) {
			if(list.get(i).contains(str)) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Gives the line in a list of a searched string
	 * 'skips' is the amount the loop will skip the found string
	 * @param list List to search in
	 * @param str String to look for in the list
	 * @param ignoreColor Ignore color sensitive
	 * @param skips amount of skips
	 * @return The line where the string got found in
	 */
	public static int getLineByString(List<String> list, String str, boolean ignoreColor, int skips) {
		if(ignoreColor) {
			for(int i=0;i<list.size();i++) {
				list.set(i, ChatColor.stripColor(list.get(i)));
			}
		}
		
		for(int i=0;i<list.size();i++) {
			if(list.get(i).contains(str)) {
				if(skips == 0) {
					skips--;
					continue;
				}
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Gives the line in a list of a searched string
	 * @param list List to search in
	 * @param str String to look for in the list
	 * @param ignoreColor Ignore color sensitive
	 * @return The line where the string got found in
	 */
	public static int getLineByString(List<String> list, String str, boolean ignoreColor) {
		if(ignoreColor) {
			for(int i=0;i<list.size();i++) {
				list.set(i, ChatColor.stripColor(list.get(i)));
			}
		}
		
		for(int i=0;i<list.size();i++) {
			if(list.get(i).contains(str)) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Gives the line in a list of a searched string
	 * 'skips' is the amount the loop will skip the found string
	 * @param list List to search in
	 * @param str String to look for in the list
	 * @param skips amount of skips
	 * @return The line where the string got found in
	 */
	public static int getLineByString(List<String> list, String str, int skips) {
		for(int i=0;i<list.size();i++) {
			if(list.get(i).contains(str)) {
				if(skips == 0) {
					skips--;
					continue;
				}
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Searches for a specific piece of string in a list, and returns the whole string
	 * @param list List to search in
	 * @param str String to look for
	 * @return The complete found string in a list
	 */
	public static String getStringInList(List<String> list, String str) {
		String found = "";
		for(int i=0;i<list.size();i++) {
			if(list.get(i).contains(str)) {
				found = list.get(i);
			}
		}
		return found;
	}
	
	/**
	 * Searches for a key in a List<String>, then gets the value in an integer format of the key
	 * @param list List to search in
	 * @param key The key to look for
	 * @return The value in the same line as in the key
	 */
	public static int getValueByKey(List<String> list, String key) {
		int statLine = -1;
		for(int i=0;i<list.size();i++) {
			if(ChatColor.stripColor(list.get(i)).startsWith(key)) {
				statLine = i;
				break;
			}
		}
		if(statLine == -1) {
			return -1;
		}
		
		String value = "";
		String lineValue = ChatColor.stripColor(list.get(statLine));
		for (int i = 0; i < lineValue.length(); i++) {
			Character chars = Character.valueOf(lineValue.charAt(i));
			if (Character.isDigit(chars.charValue())) {
				value = value + chars;
			}
		}
		return Integer.parseInt(value);
	}
	
	/**
	 * Searches for a key in a List<String>, then gets the value in a string format of the key
	 * @param list List to search in
	 * @param key The key to look for
	 * @return The value in the same line as in the key
	 */
	public static String getValueByKey(List<String> list, String key, String splitCharacter) {
		int statLine = -1;
		for(int i=0;i<list.size();i++) {
			if(ChatColor.stripColor(list.get(i)).startsWith(key)) {
				statLine = i;
				break;
			}
		}
		if(statLine == -1) {
			return "";
		}
		
		String lineValue = ChatColor.stripColor(list.get(statLine));
		return lineValue.substring(lineValue.indexOf(splitCharacter) + splitCharacter.length());
	}
}