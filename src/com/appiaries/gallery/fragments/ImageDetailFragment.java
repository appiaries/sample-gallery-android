/*******************************************************************************
 * Copyright (c) 2014 Appiaries Corporation. All rights reserved.
 *******************************************************************************/
package com.appiaries.gallery.fragments;

import java.util.ArrayList;
import java.util.List;
import com.appiaries.APISResult;
import com.appiaries.gallery.R;
import com.appiaries.gallery.jsonmodels.Illustrations;
import com.appiaries.gallery.managers.IllustrationImageManager;
import com.appiaries.gallery.managers.IllustrationManager;
import com.appiaries.gallery.activities.MainActivity;
import com.appiaries.gallery.adapter.ImagePagerAdapter;
import com.appiaries.gallery.common.APIHelper;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ImageDetailFragment extends Fragment {

	List<Illustrations> imageList;
	ProgressDialog progressBar;
	int playTime = 5000;
	int commentFlag = 1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_image_detail, container,
				false);
		getActivity().setTitle("一覧表示");	
			
		if(APIHelper.getStringInLocalStorage(getActivity(), "comment") != null && !APIHelper.getStringInLocalStorage(getActivity(), "comment").equals("")){
			commentFlag = Integer.parseInt(APIHelper.getStringInLocalStorage(getActivity(), "comment"));
		}

		Bundle bundle = getArguments();
		Illustrations illustration  = IllustrationManager.getInstance().getIllustrationObject(bundle.getString("illustrationId"));
		
		// build dataSource
		imageList = new ArrayList<Illustrations>();
		imageList.add(illustration);

		final ViewPager viewPager = (ViewPager) rootView
				.findViewById(R.id.view_pager);
		if(imageList.size() > 0){
			ImagePagerAdapter adapter = new ImagePagerAdapter(getActivity(), imageList);

			viewPager.setAdapter(adapter);
		}
		
		final Button btnDelete = (Button) rootView.findViewById(R.id.btnDelete);
		final Button btnChange = (Button) rootView.findViewById(R.id.btnChange);
		final TextView tvComment = (TextView) rootView
				.findViewById(R.id.tvDescription);
		
		if(commentFlag == 1){
			tvComment.setVisibility(View.VISIBLE);
			tvComment.setText(illustration.getDescription());			
		}else{
			tvComment.setVisibility(View.INVISIBLE);
		}
		
		btnDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// calling dialog
				dialog(getActivity(), viewPager.getCurrentItem());
			}
		});		

		btnChange.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// change current image
				Illustrations illustration = imageList.get(viewPager.getCurrentItem());												
								
				AddFragment addfragment = new AddFragment();
				
				Bundle bundle = new Bundle();			
				bundle.putString("illustrationId", illustration.getId());
				addfragment.setArguments(bundle);

				((MainActivity) getActivity()).addFragment(addfragment);
			}
		});		  

		return rootView;
	}

	@Override
	public void onStop() {
		super.onStop();

	}
	
	public void dialog(Context ctx, final int imagePosition) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
		alertDialogBuilder.setMessage("画像を削除します。よろしいですか？");

		
		alertDialogBuilder.setNegativeButton("OK",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {						
						new DeleteImageAsyncTask().execute(imagePosition);
					}
				});
		
		alertDialogBuilder.setPositiveButton("キャンセル",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {						
					}
				});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();

	}

	private class DeleteImageAsyncTask extends
			AsyncTask<Integer, Void, APISResult> {

		@Override
		protected APISResult doInBackground(Integer... params) {

			Illustrations illustration = imageList.get(params[0]);
			String id = illustration.getId();
			String imageId = illustration.getImageURI();

			try {
				// delete IllustrationImage object in IllustrationImage
				// collection
				APISResult responseObj = IllustrationImageManager.getInstance()
						.deleteIllustrationImage(imageId);
				if (responseObj != null && responseObj.getResponseCode() == 204) {
					return IllustrationManager.getInstance()
							.deleteIllustration(id);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressBar = new ProgressDialog(getActivity());
			progressBar.setMessage("Loading....");
			progressBar.setCancelable(false);
			progressBar.setCanceledOnTouchOutside(false);
			progressBar.show();
		}

		@Override
		protected void onPostExecute(APISResult result) {			
			progressBar.dismiss();
			super.onPostExecute(result);
			if (result != null && result.getResponseCode() == 204) {			
				// back to ListFragment
				((MainActivity)getActivity()).onBackPressed();
			}
		}

	}
}
