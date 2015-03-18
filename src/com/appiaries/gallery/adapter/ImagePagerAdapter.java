/*******************************************************************************
 * Copyright (c) 2014 Appiaries Corporation. All rights reserved.
 *******************************************************************************/
package com.appiaries.gallery.adapter;

import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.appiaries.gallery.R;
import com.appiaries.gallery.common.APIHelper;
import com.appiaries.gallery.common.Constants;
import com.appiaries.gallery.common.ImageLoader;
import com.appiaries.gallery.jsonmodels.Illustrations;

public class ImagePagerAdapter  extends PagerAdapter{
		
		private Context context;
		List<Illustrations> imageList;
		ImageLoader imageLoader;
		
		public  ImagePagerAdapter(Context ctx, List<Illustrations> imageList){
			this.context = ctx;
			this.imageList = imageList;
			this.imageLoader = new ImageLoader(ctx);
		}

		@Override
		public int getCount() {
			return imageList.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == ((ImageView) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView imageView = new ImageView(context);
			int padding = context.getResources().getDimensionPixelSize(
					R.dimen.padding_small);
			imageView.setPadding(padding, padding, padding, padding);
			imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

			String imageURI = APIHelper.getImageFileUrlWithObjectId(imageList.get(position)
					.getImageURI(), Constants.ILLUSTRATION_IMAGES_ID); 
			
			imageLoader.DisplayImage(imageURI, imageView);
			((ViewPager) container).addView(imageView, 0);
			
			return imageView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((ImageView) object);
		}
}
