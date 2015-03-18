/*******************************************************************************
 * Copyright (c) 2014 Appiaries Corporation. All rights reserved.
 *******************************************************************************/
package com.appiaries.gallery.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import com.appiaries.gallery.common.Constants;

/**
 * @author nmcuong
 * 
 */
public class APIHelper {

	/**
	 * Get Access Token
	 * 
	 * @param ctx
	 *            Context
	 * @return accessToken String
	 */
	/*public static String getAccessToken(Context ctx) {
		SharedPreferences pref = ctx.getSharedPreferences(Constants.PREFS_NAME,
				0);
		String accessToken = pref.getString(Constants.ACCESS_TOKEN_KEY, "");

		return accessToken;
	}*/

	/**
	 * Save Access Token
	 * 
	 * @param ctx
	 *            Context
	 * @param accessToken
	 *            string
	 */
	/*public static void setAccessToken(Context ctx, String accessToken) {
		SharedPreferences pref = ctx.getSharedPreferences(Constants.PREFS_NAME,
				0);
		Editor editor = pref.edit();

		editor.putString(Constants.ACCESS_TOKEN_KEY, accessToken);
		editor.commit();
	}*/
	
	/**
	 * Build an Image File URL from a ObjectId, We can use this URL to display
	 * an image
	 * 
	 * @param objectId
	 * @return image file URL string
	 */
	public static String getImageFileUrlWithObjectId(String objectId, String imageCollectionId) {
		return String.format("%s/%s/%s/%s/%s/_bin",
				Constants.DATASTORE_FILE_URL_BASE, Constants.APIS_DATASTORE_ID,
				Constants.APIS_APP_ID, imageCollectionId, objectId);
	}

	
	/**
	 * Convert HTTPResponse Stream to String
	 * 
	 * @param inputStream
	 * @return string
	 * @throws IOException
	 */
	public static String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return result;
	}

	/**
	 * POST method to execute creating, updating, editing
	 * 
	 * @param url
	 * @param storeToken
	 * @param accessToken
	 * @param parameters
	 * @return
	 */
	public static String doPost(String url, String storeToken,
			String accessToken, HashMap<String, Object> parameters) {
		InputStream inputStream = null;
		String result = "";

		try {

			// create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			HttpPost httpPost = new HttpPost(url);

			// Set Request Header Token
			if (!storeToken.equals("")) {
				httpPost.setHeader("X-APPIARIES-TOKEN", storeToken);
			}

			if (!accessToken.equals("")) {
				httpPost.setHeader("Authorization",
						String.format("Bearer %s", accessToken));
			}

			if (parameters != null) {
				JSONObject holder = new JSONObject(parameters);

				StringEntity se = new StringEntity(holder.toString(),
						HTTP.UTF_8);
				se.setContentType("application/json");

				httpPost.setEntity(se);
			}

			// make POST request to the given URL
			HttpResponse httpResponse = httpclient.execute(httpPost);

			// receive response as inputStream
			if (httpResponse.getEntity() != null)
				inputStream = httpResponse.getEntity().getContent();

			// convert inputstream to string
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
			else
				result = "";

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return result;
	}

	/**
	 * Make a GET Request by URL and return an response content
	 * 
	 * @param url
	 * @return response content
	 */
	public static String doGet(String url, String storeToken, String accessToken) {

		InputStream inputStream = null;
		String result = "";
		try {

			// create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			HttpGet httpGet = new HttpGet(url);

			// Set Request Header Token
			if (!storeToken.equals("")) {
				httpGet.addHeader("X-APPIARIES-TOKEN", storeToken);
			}

			// Set Request Header Token
			if (!accessToken.equals("")) {
				httpGet.addHeader("Authorization",
						String.format("Bearer %s", accessToken));
			}

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(httpGet);

			// receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();

			// convert inputstream to string
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
			else
				result = "";

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return result;
	}
	
	public static void setStringToLocalStorage(Context ctx, String key, String value){
		SharedPreferences pref = ctx.getSharedPreferences(Constants.PREFS_NAME,
				0);
		Editor editor = pref.edit();

		editor.putString(key, value);
		editor.commit();
	}
	
	public static String getStringInLocalStorage(Context ctx, String key) {
		SharedPreferences pref = ctx.getSharedPreferences(Constants.PREFS_NAME,
				0);
		return pref.getString(key, "");
	}
	
}
