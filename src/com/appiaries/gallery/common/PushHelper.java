/*******************************************************************************
 * Copyright (c) 2014 Appiaries Corporation. All rights reserved.
 *******************************************************************************/
package com.appiaries.gallery.common;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

import com.appiaries.APISPush;

public class PushHelper {
	private final static PushHelper instance = new PushHelper();

	public static PushHelper getInstance() {
		return instance;
	}

	private static SharedPreferences mSharedPreferences;

	/**
	 * Send GCM registration ID to Appiaries server.
	 * 
	 * @param context
	 * @param regId
	 * @param mapAttr
	 * @param callback
	 */
	public void sendRegistrationId(Context ctx, String regId, Map<String, Object> attr) {

		if (attr == null) {
			attr = new HashMap<String, Object>();
		}

		try {
			// Send Registration ID to Appiaries Server
			APISPush.initialize(Constants.APIS_DATASTORE_ID,
					Constants.APIS_APP_ID, Constants.APIS_APP_TOKEN, ctx);
			APISPush.sendRegistrationID(regId, attr);

			if (regId != null) {
				saveRegistration(regId);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendUserPushRequest(String regId, String title, String message,
			Map<String, Object> userMessage) {

		// Send User Push Request URL
		String mRegistWebURL = String.format("%s/%s/%s/%s",
				Constants.USER_PUSH_URL, Constants.APIS_DATASTORE_ID,
				Constants.APIS_APP_ID, "?proc=create");

		HttpPost post = new HttpPost(mRegistWebURL);

		JsonMap data = new JsonMap();
		data.put("immediateSend", true);
		data.put("title", title);
		data.put("message", message);
		data.put("badgeIncrement", 0);
		data.put("sound", "");
		data.put("userMessage", userMessage);

		ArrayList<HashMap<String, Object>> sendConds = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> condition = new HashMap<String, Object>();
		condition.put("_deviceId", regId);

		sendConds.add(condition);

		data.put("sendCondition", sendConds);

		String json = "";

		InputStream inputStream = null;
		String result = "";

		try {
			// create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			post.setHeader("X-APPIARIES-TOKEN", Constants.APIS_APP_TOKEN);

			json = data.toJson();

			StringEntity se = new StringEntity(json, "UTF-8");
			se.setContentType("application/json");

			post.setEntity(se);

			// make POST request to the given URL
			HttpResponse httpResponse = httpclient.execute(post);

			// receive response as inputStream
			if (httpResponse.getEntity() != null)
				inputStream = httpResponse.getEntity().getContent();

			// convert inputstream to string
			if (inputStream != null)
				result = APIHelper.convertInputStreamToString(inputStream);
			else
				result = "";

			int status = httpResponse.getStatusLine().getStatusCode();

			if (status != 204) {
				Log.d("Send User Push Request", result);
			} else {
				Log.d("Send User Push Request", "Success!");
			}

		} catch (IOException e) {
			Log.d("Send User Push Request", e.getLocalizedMessage());
		}
	}

	protected void saveRegistration(String registrationID) throws IOException {
		Editor editor = mSharedPreferences.edit();
		editor.putString("registrationID", registrationID);
		editor.commit();
	}

	public static String getRegistrationID(Context ctx) {
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
		String gcmRegistID = mSharedPreferences.getString("registrationID",
				null);
		return gcmRegistID;
	}

	protected static void clearRegistration() {
		mSharedPreferences.edit().clear().commit();
	}

	// remove registrationID
	public void removeRegistrationID(Context ctx) throws IOException {
		String mRemoveWebURL = String.format("%s/%s/%s/%s",
				Constants.REGISTER_REGISTRATION_URL,
				Constants.APIS_DATASTORE_ID, Constants.APIS_APP_ID,
				"_target?proc=delete");

		HttpPost post = new HttpPost(mRemoveWebURL);

		JsonMap data = new JsonMap();
		data.put("regid", getRegistrationID(ctx));
		String json = data.toJson();

		post.setEntity(new StringEntity(json));

		InputStream inputStream = null;
		String result = "";

		try {
			// create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			post.setHeader("X-APPIARIES-TOKEN", Constants.APIS_APP_TOKEN);

			json = data.toJson();

			StringEntity se = new StringEntity(json, "UTF-8");
			se.setContentType("application/json");

			post.setEntity(se);

			// make POST request to the given URL
			HttpResponse httpResponse = httpclient.execute(post);

			// receive response as inputStream
			if (httpResponse.getEntity() != null)
				inputStream = httpResponse.getEntity().getContent();

			// convert inputstream to string
			if (inputStream != null)
				result = APIHelper.convertInputStreamToString(inputStream);
			else
				result = "";

			int status = httpResponse.getStatusLine().getStatusCode();

			// remove local after remove on server
			if (status == 204) {
				clearRegistration();
			}
			Log.d("Remove Registration ID status", "" + status + result);
		} catch (IOException e) {

		}
	}

	public static void saveLastSent(Context ctx) {
		mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
		String gcmRegistID = mSharedPreferences.getString("registrationID",
				null);

		Editor editor = mSharedPreferences.edit();
		editor.putString(gcmRegistID, new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()));
		editor.commit();

	}

}
