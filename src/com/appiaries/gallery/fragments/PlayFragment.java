/*******************************************************************************
 * Copyright (c) 2014 Appiaries Corporation. All rights reserved.
 *******************************************************************************/
package com.appiaries.gallery.fragments;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.appiaries.APISResult;
import com.appiaries.gallery.R;
import com.appiaries.gallery.jsonmodels.Illustrations;
import com.appiaries.gallery.managers.IllustrationImageManager;
import com.appiaries.gallery.managers.IllustrationManager;
import com.appiaries.gallery.activities.MainActivity;
import com.appiaries.gallery.adapter.ImagePagerAdapter;
import com.appiaries.gallery.common.APIHelper;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class PlayFragment extends Fragment {

	List<Illustrations> imageList;
	ProgressDialog progressBar;
	int playTime = 5000;
	int commentFlag = 1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_play, container,
				false);
		getActivity().setTitle("再生");

		if (APIHelper.getStringInLocalStorage(getActivity(), "interval") != null
				&& !APIHelper
						.getStringInLocalStorage(getActivity(), "interval")
						.equals("")) {
			
			String strTime = APIHelper.getStringInLocalStorage(
					getActivity(), "interval");
			if(strTime.equals("2秒"))
			{
				strTime = "2";
			}
			else if(strTime.equals("5秒")){
				strTime = "5";
			}
			else if(strTime.equals("10秒")){
				strTime = "10";
			}
			else{
				strTime = "5";
			}
			playTime = Integer.parseInt(strTime) * 1000;
		}

		if (APIHelper.getStringInLocalStorage(getActivity(), "comment") != null
				&& !APIHelper.getStringInLocalStorage(getActivity(), "comment")
						.equals("")) {
			commentFlag = Integer.parseInt(APIHelper.getStringInLocalStorage(
					getActivity(), "comment"));
		}

		// get image list from list screen
		imageList = IllustrationManager.getInstance()
				.getIllustrationObjectList();

		final ViewPager viewPager = (ViewPager) rootView
				.findViewById(R.id.view_pager);
		if (imageList.size() > 0) {
			ImagePagerAdapter adapter = new ImagePagerAdapter(getActivity(),
					imageList);

			viewPager.setAdapter(adapter);
		}

		final Button btnNext = (Button) rootView.findViewById(R.id.btnNext);
		final Button btnBack = (Button) rootView.findViewById(R.id.btnBack);

		btnNext.setVisibility(View.INVISIBLE);
		btnBack.setVisibility(View.INVISIBLE);

		final Button btnDelete = (Button) rootView.findViewById(R.id.btnDelete);
		final Button btnChange = (Button) rootView.findViewById(R.id.btnChange);
		final TextView tvComment = (TextView) rootView
				.findViewById(R.id.tvDescription);

		if (commentFlag == 1) {
			tvComment.setVisibility(View.VISIBLE);
		} else {
			tvComment.setVisibility(View.INVISIBLE);
		}

		btnNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int currentPosition = viewPager.getCurrentItem();
				if (currentPosition < imageList.size() - 1) {
					viewPager.setCurrentItem(currentPosition + 1, true);
				}

			}
		});

		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int currentPosition = viewPager.getCurrentItem();
				if (currentPosition > 0) {
					viewPager.setCurrentItem(currentPosition - 1, true);
				}
			}
		});

		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {

			}

			@Override
			public void onPageScrolled(int position, float arg1, int arg2) {
				tvComment.setText(imageList.get(position).getDescription());

				// do not show Arrow buttons if there is only one item
				if (imageList.size() <= 1) {
					btnNext.setVisibility(View.INVISIBLE);
					btnBack.setVisibility(View.INVISIBLE);
				} else if (position == imageList.size() - 1) // current position
																// is the last
																// item on the
																// image list
				{
					btnNext.setVisibility(View.INVISIBLE);
					btnBack.setVisibility(View.VISIBLE);
				} else if (position == 0) // current position is the first item
											// on the image list
				{
					btnNext.setVisibility(View.VISIBLE);
					btnBack.setVisibility(View.INVISIBLE);
				} else {
					btnNext.setVisibility(View.VISIBLE);
					btnBack.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

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
				Illustrations illustration = imageList.get(viewPager
						.getCurrentItem());

				AddFragment addfragment = new AddFragment();

				Bundle bundle = new Bundle();
				bundle.putString("illustrationId", illustration.getId());
				addfragment.setArguments(bundle);

				((MainActivity) getActivity()).addFragment(addfragment);
			}
		});

		final Handler handler = new Handler();
		final Runnable update = new Runnable() {
			int currentPage;

			public void run() {
				currentPage = viewPager.getCurrentItem();
				Log.d("currentPage ", currentPage + "");
				if (currentPage == imageList.size() - 1) {
					viewPager.setCurrentItem(0, true);
				} else {
					viewPager.setCurrentItem(viewPager.getCurrentItem() + 1,
							true);
				}
			}
		};

		Timer swipeTimer = new Timer();
		swipeTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				handler.post(update);
			}
		}, 3000, playTime);

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
						Log.d("Playfragment", "btn ok dialog click");
						new DeleteImageAsyncTask().execute(imagePosition);
					}
				});

		alertDialogBuilder.setPositiveButton("キャンセル",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						Log.d("Playfragment", "btn cancel dialog click");
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
				ActionBar actionBar = getActivity().getActionBar();
				actionBar.setSelectedNavigationItem(0);
			}
		}

	}
}
