//
// Copyright (c) 2015 Appiaries Corporation. All rights reserved.
//
package com.appiaries.sample.gallery.activities;

import com.appiaries.baas.sdk.AB;
import com.appiaries.sample.gallery.R;
import com.appiaries.sample.gallery.models.Illustration;
import com.appiaries.sample.gallery.models.IllustrationImage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		
		// Activate Appiaries SDK
        AB.Config.setDatastoreID(getString(R.string.appiaries__datastore_id));
        AB.Config.setApplicationID(getString(R.string.appiaries__application_id));
        AB.Config.setApplicationToken(getString(R.string.appiaries__application_token));
        AB.activate(getApplicationContext());

		// Register subclasses
		AB.registerClass(Illustration.class);
		AB.registerClass(IllustrationImage.class);


		new Handler().postDelayed(new Runnable() {
			public void run() {
				// Session is still valid => Open Main Activity
				Intent intent = new Intent(SplashActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
			}
		}, 3000);
	}
}
