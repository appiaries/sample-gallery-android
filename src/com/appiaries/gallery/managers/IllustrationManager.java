/*******************************************************************************
 * Copyright (c) 2014 Appiaries Corporation. All rights reserved.
 *******************************************************************************/
package com.appiaries.gallery.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.appiaries.APISException;
import com.appiaries.APISJsonData;
import com.appiaries.APISQueryCondition;
import com.appiaries.APISResult;
import com.appiaries.gallery.common.Constants;
import com.appiaries.gallery.jsonmodels.Illustrations;
import com.appiaries.gallery.common.TextHelper;

public class IllustrationManager {

	private List<Illustrations> illustrationList;
	private final static IllustrationManager instance = new IllustrationManager();

	public static IllustrationManager getInstance() {
		return instance;
	}

	public List<Illustrations> getIllustrationObjectList() {
		return illustrationList;
	}
	
	public Illustrations getIllustrationObject(String objectId)
	{
		Illustrations result = null;
		
		for(Illustrations obj : illustrationList)
		{
			if (obj.getId().equals(objectId))
			{
				result = obj;
				break;
			}
		}
		
		return result;
	}

	public List<Illustrations> getIllustrationList(Context ctx)
			throws JSONException {

		// Make a request to get an JSON string
		APISQueryCondition appQueryCondition = new APISQueryCondition();
		APISResult responseResult;
		String jsonString = "";
		try {

			responseResult = APISJsonData.selectJsonData(
					Constants.COLLECTION_ILLUSTRATION, appQueryCondition);
			if (responseResult.getResponseData() != null) {
				jsonString = TextHelper.convertToJson(responseResult
						.getResponseData());
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		Log.d("JSON Response: ", jsonString);

		// Parse JsonString to JSONObject
		JSONObject jsonObj = new JSONObject(jsonString);

		// Get JSON Array by _objs key
		String objsString = jsonObj.getString("_objs");

		JSONArray jsonObjs = new JSONArray(objsString);

		List<Illustrations> informationList = new ArrayList<Illustrations>();

		for (int i = 0; i < jsonObjs.length(); i++) {

			JSONObject obj = jsonObjs.getJSONObject(i);

			// Build Information Object from an JSON Object
			informationList.add(new Illustrations(obj.getString("_id"), obj
					.getString("description"), obj.optString("image_id"), obj
					.getLong("_cts"), obj.getLong("_uts"), obj
					.getString("_cby"), obj.getString("_uby")));
		}

		// Ascending sort by Created time
		illustrationList = informationList;

		Collections.sort(illustrationList);
		Collections.sort(informationList);

		return informationList;
	}

	public APISResult deleteIllustration(String id) {
		try {
			return APISJsonData.deleteJsonData(
					Constants.COLLECTION_ILLUSTRATION, id);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public int registData(Map<String, Object> data) throws APISException {
		APISResult responseObject = null;

		HashMap<String, Object> registData = new HashMap<String, Object>();

		registData.put("description", data.get("description"));
		registData.put("image_id", data.get("image_id"));

		responseObject = APISJsonData.registJsonData(Constants.ILLUSTRATION_ID,
				null, registData);

		return responseObject.getResponseCode();
	}

	public int updateData(String objectId, Map<String, Object> data)
			throws APISException {

		HashMap<String, Object> registData = new HashMap<String, Object>();

		registData.put("description", data.get("description"));
		registData.put("image_id", data.get("image_id"));

		APISResult responseObject = APISJsonData.updateJsonData(
				Constants.ILLUSTRATION_ID, objectId, registData);

		return responseObject.getResponseCode();
	}
}
