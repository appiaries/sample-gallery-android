/*******************************************************************************
 * Copyright (c) 2014 Appiaries Corporation. All rights reserved.
 *******************************************************************************/
package com.appiaries.gallery.jsonmodels;

import java.io.Serializable;

import com.appiaries.gallery.common.APIHelper;
import com.appiaries.gallery.common.Constants;

public class Illustrations implements Serializable,  Comparable<Illustrations>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6555441627576638110L;

	public Illustrations(){}
	
	public Illustrations(String id, 
						String description,
						String imageURI, 
						Long cts, 
						Long uts,
						String cby, 
						String uby) {
		
		this.id = id;
		this.description = description;
		this.imageURI = imageURI;
		this.cts = cts;
		this.uts = uts;
		this.cby = cby;
		this.uby = uby;
	}

	/**
	 * identifier
	 */
	private String id;

	/**
	 * description
	 */
	private String description;

	/**
	 * list of image identifier
	 */
	private String imageURI;

	/**
	 * created date
	 */
	private long cts;

	private long uts;
	
	private String cby;
	
	private String uby;
	
	@Override
	public int compareTo(Illustrations info) {
		long compareUpdateTime = info.getUts(); 
		 
		//ascending order
		if (this.uts - compareUpdateTime < 0)
		{
			return 1;
		}
		else if (this.uts - compareUpdateTime > 0)
		{
			return -1;
		}
		
		return 0;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImageURI() {
		return imageURI;
	}

	public void setImageURI(String imageURI) {
		this.imageURI =  APIHelper.getImageFileUrlWithObjectId(imageURI, Constants.ILLUSTRATION_IMAGES_ID);
	}

	public long getCts() {
		return cts;
	}

	public void setCts(long cts) {
		this.cts = cts;
	}

	public long getUts() {
		return uts;
	}

	public void setUts(long uts) {
		this.uts = uts;
	}

	public String getCby() {
		return cby;
	}

	public void setCby(String cby) {
		this.cby = cby;
	}

	public String getUby() {
		return uby;
	}

	public void setUby(String uby) {
		this.uby = uby;
	}

}
