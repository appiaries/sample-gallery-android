/*******************************************************************************
 * Copyright (c) 2014 Appiaries Corporation. All rights reserved.
 *******************************************************************************/
package com.appiaries.gallery.managers;

import com.appiaries.APISFileData;
import com.appiaries.APISResult;
import com.appiaries.gallery.common.Constants;

public class IllustrationImageManager {
	
	private final static IllustrationImageManager instance = new IllustrationImageManager();

	public static IllustrationImageManager getInstance() {
		return instance;
	}
	
	public APISResult deleteIllustrationImage(String imageId){
		
		try {
			return APISFileData.deleteData(Constants.ILLUSTRATION_IMAGES_ID, imageId);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
