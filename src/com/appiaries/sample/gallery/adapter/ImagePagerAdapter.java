//
// Copyright (c) 2015 Appiaries Corporation. All rights reserved.
//
package com.appiaries.sample.gallery.adapter;

import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.appiaries.sample.gallery.R;
import com.appiaries.sample.gallery.common.ImageLoader;
import com.appiaries.sample.gallery.models.Illustration;

public class ImagePagerAdapter  extends PagerAdapter {
		
		private Context mContext;
		List<Illustration> mIllustrations;
		ImageLoader mImageLoader;
		
		public ImagePagerAdapter(Context context, List<Illustration> illustrations){
			this.mContext = context;
			this.mIllustrations = illustrations;
			this.mImageLoader = new ImageLoader(context);
		}

		@Override
		public int getCount() {
			return mIllustrations.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView imageView = new ImageView(mContext);
			int padding = mContext.getResources().getDimensionPixelSize(
					R.dimen.padding_small);
			imageView.setPadding(padding, padding, padding, padding);
			imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

            Illustration il = mIllustrations.get(position);
			mImageLoader.displayImage(il.getImageUrl(), imageView);
			container.addView(imageView, 0);
			
			return imageView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((ImageView) object);
		}
}
