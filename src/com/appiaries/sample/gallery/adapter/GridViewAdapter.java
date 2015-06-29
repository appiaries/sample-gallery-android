//
// Copyright (c) 2015 Appiaries Corporation. All rights reserved.
//
package com.appiaries.sample.gallery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.appiaries.sample.gallery.R;
import com.appiaries.sample.gallery.common.ImageLoader;
import com.appiaries.sample.gallery.models.Illustration;

import java.util.ArrayList;
import java.util.List;

public class GridViewAdapter extends ArrayAdapter<Illustration> {

    static class ViewHolder {
        ImageView image;
    }

	private Context mContext;
    private int mLayoutResourceId;
    private ImageLoader mImageLoader;
    private List<Illustration> mIllustrations = new ArrayList<>();

	public GridViewAdapter(Context context, int resourceId, List<Illustration> illustrations) {
		super(context, resourceId, illustrations);
		this.mLayoutResourceId = resourceId;
		this.mContext = context;
		this.mIllustrations = illustrations;
		this.mImageLoader = new ImageLoader(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;

		ViewHolder viewHolder;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(mLayoutResourceId, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.image = (ImageView)row.findViewById(R.id.image);
			row.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)row.getTag();
		}

		//code for real
		Illustration il = mIllustrations.get(position);
		if (il != null) {
			this.mImageLoader.displayImage(il.getImageUrl(), viewHolder.image);
		}

		return row;
	}

}
