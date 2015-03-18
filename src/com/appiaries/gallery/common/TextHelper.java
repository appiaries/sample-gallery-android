/*******************************************************************************
 * Copyright (c) 2014 Appiaries Corporation. All rights reserved.
 *******************************************************************************/
package com.appiaries.gallery.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.google.gson.Gson;

public class TextHelper {
	public static Date toDate(String dateStr) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date date = null;
		try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {
		}

		return date;
	}

	/*
	 * @Description: Method to convert Map to JSON String
	 * 
	 * @param: map Map<String, String>
	 * 
	 * @return: json String
	 */
	public static String convertToJson(Map<?, ?> map) {
		Gson gson = new Gson();
		String json = gson.toJson(map);
		return json;
	}

	/*
	 * Check if current String is integer value
	 */
	@SuppressWarnings("unused")
	public static boolean isNumeric(String str) {
		try {
			Integer intVal = Integer.parseInt(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

}
