/*******************************************************************************
 * Copyright (c) 2014 Appiaries Corporation. All rights reserved.
 *******************************************************************************/
package com.appiaries.gallery.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class GCMBroadcastReceiver extends WakefulBroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent service = new Intent(context, GCMBroadcastReceiver.class);
		startWakefulService(context, service);
		
		Bundle bundle = intent.getExtras();
		Log.d("Received Message:", bundle.getString("title"));

		intent.putExtra("STARTACTIVITY", "com.appiaries.gallery.activities.MainActivity");

		/*// Use the pop-up dialog of default contained in the SDK
		AppiariesPushConfiguration conf = new AppiariesPushConfiguration();
		
		// Note: you must define two custom keys in Market Setup as following: "title", "message". 
		// The Appiaries built-in Pop-up Dialog will use "title" and "message" custom keys to set dialog displaying.

		// Initialization
		conf.initialize(Constants.APIS_DATASTORE_ID, Constants.APIS_APP_ID,
				Constants.APIS_APP_TOKEN);

		// Specifies the type of dialog
		conf.setDialogType(AppiariesPushConfiguration.DIALOG_SIMPLE);

		// Set message as opened
		conf.setOpenFlg(true);

		// Display
		PushHelper.getInstance()
				.getAppiariesPushInstance(context, intent, conf);*/
	}

}
