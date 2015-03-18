/*******************************************************************************
 * Copyright (c) 2014 Appiaries Corporation. All rights reserved.
 *******************************************************************************/
package com.appiaries.gallery.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.appiaries.gallery.R;
import com.appiaries.gallery.common.APIHelper;
import com.appiaries.gallery.common.Constants;
import com.appiaries.gallery.common.ImageLoader;
import com.appiaries.gallery.jsonmodels.Illustrations;

public class GridViewAdapter extends ArrayAdapter<Illustrations> {

	private Context context;
	private int layoutResourceId;
	private ImageLoader imageLoader;
	
	private ArrayList<Illustrations> data = new ArrayList<Illustrations>();

	public GridViewAdapter(Context context, int layoutResourceId,
			ArrayList<Illustrations> data) {
		super(context, layoutResourceId, data);
		
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
		this.imageLoader = new ImageLoader(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		ViewHolder holder = null;

		if (row == null) {
			LayoutInflater inflater =  (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(layoutResourceId, parent, false);
			holder = new ViewHolder();
			holder.image = (ImageView) row.findViewById(R.id.image);
			row.setTag(holder);
		} else {
			holder = (ViewHolder) row.getTag();
		}
		//code for real			
		Illustrations illustrationObj = data.get(position);
		if(illustrationObj != null ){
			String imageURI = APIHelper.getImageFileUrlWithObjectId(illustrationObj.getImageURI(), Constants.ILLUSTRATION_IMAGES_ID);
			this.imageLoader.DisplayImage(imageURI,holder.image);
		}
	
		
		return row;

	}

	static class ViewHolder {		
		ImageView image;
	}
}
