/*******************************************************************************
 * Copyright (c) 2014 Appiaries Corporation. All rights reserved.
 *******************************************************************************/
package com.appiaries.gallery.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import com.appiaries.gallery.R;
import com.appiaries.gallery.activities.MainActivity;
import com.appiaries.gallery.adapter.GridViewAdapter;
import com.appiaries.gallery.jsonmodels.Illustrations;
import com.appiaries.gallery.managers.IllustrationManager;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.app.ActionBar.Tab;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class ListFragment extends Fragment {
	private GridView gridView;
	private GridViewAdapter customGridAdapter;
	ProgressDialog progressBar;
	ArrayList<Illustrations> dataSource;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_list, container,
				false);
		getActivity().setTitle("一覧表示");

		gridView = (GridView) rootView.findViewById(R.id.gridView);

		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// Get Illustration object by item position
				Illustrations illustration = dataSource.get(position);

				ImageDetailFragment detailfragment = new ImageDetailFragment();

				Bundle bundle = new Bundle();
				bundle.putString("illustrationId", illustration.getId());
				detailfragment.setArguments(bundle);

				((MainActivity) getActivity()).addFragment(detailfragment);
			}
		});

		new GetImageAsyncTask().execute();

		return rootView;
	}

	@Override
	public void onStop() {
		super.onStop();
	}
	
	/* *********************************
	 * If button back in the Top screen, the exit application
	 */	
	public void onBackPressed() {
		getActivity().finish();
		getActivity().moveTaskToBack(true);
	}

	private class GetImageAsyncTask extends
			AsyncTask<Void, Void, List<Illustrations>> {

		@Override
		protected List<Illustrations> doInBackground(Void... params) {
			try {
				return IllustrationManager.getInstance().getIllustrationList(
						getActivity().getApplicationContext());
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
		protected void onPostExecute(List<Illustrations> result) {
			progressBar.dismiss();
			super.onPostExecute(result);
			if (result != null && result.size() > 0) {
				dataSource = (ArrayList<Illustrations>) result;

				customGridAdapter = new GridViewAdapter(getActivity(),
						R.layout.list_row, dataSource);
				gridView.setAdapter(customGridAdapter);

			} else {
				Log.d("ListFragment", "getImageListAsynctask fail");
			}
		}

	}
}
