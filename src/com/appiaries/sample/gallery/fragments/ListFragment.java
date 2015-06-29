//
// Copyright (c) 2015 Appiaries Corporation. All rights reserved.
//
package com.appiaries.sample.gallery.fragments;

import java.util.List;

import com.appiaries.baas.sdk.AB;
import com.appiaries.baas.sdk.ABException;
import com.appiaries.baas.sdk.ABQuery;
import com.appiaries.baas.sdk.ABResult;
import com.appiaries.baas.sdk.ABStatus;
import com.appiaries.baas.sdk.ResultCallback;
import com.appiaries.sample.gallery.R;
import com.appiaries.sample.gallery.activities.MainActivity;
import com.appiaries.sample.gallery.adapter.GridViewAdapter;
import com.appiaries.sample.gallery.common.Constants;
import com.appiaries.sample.gallery.managers.DataManager;
import com.appiaries.sample.gallery.models.Illustration;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;


public class ListFragment extends BaseFragment {
	private GridView mGridView;
	private GridViewAdapter mGridAdapter;
	List<Illustration> mDataSource;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_list, container, false);
        setupView(view);

        final Activity activity = getActivity();
        final ProgressDialog progress = createAndShowProgressDialog(R.string.progress__loading);
        ABQuery query = Illustration.query().orderBy(Illustration.Field.UPDATED, ABQuery.SortDirection.DESC);
        AB.DBService.findWithQuery(query, new ResultCallback<List<Illustration>>() {
            @Override
            public void done(ABResult<List<Illustration>> result, ABException e) {
                progress.dismiss();
                if (e == null) {
                    int code = result.getCode();
                    if (code == ABStatus.OK) {
                        List<Illustration> foundArray = result.getData();
						DataManager.setIllustrationList(foundArray);
                        mDataSource = foundArray;
                        mGridAdapter = new GridViewAdapter(activity, R.layout.list_item, mDataSource);
                        mGridView.setAdapter(mGridAdapter);
                    } else {
                        showUnexpectedStatusCodeError(activity, code);
                    }
                } else {
                    showError(activity, e);
                    e.printStackTrace();
                }
            }
        });

        return view;
	}

    private void setupView(View view) {
        final Activity activity = getActivity();

        activity.setTitle(R.string.list__title);

        mGridView = (GridView) view.findViewById(R.id.gridview);

        mGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				Illustration il = mDataSource.get(position);
				DetailFragment detailFragment = new DetailFragment();

                Bundle bundle = new Bundle();
                bundle.putString(Constants.EXTRA_KEY_ILLUSTRATION_ID, il.getID());
                detailFragment.setArguments(bundle);

                ((MainActivity)activity).addFragment(detailFragment);
            }
        });
    }

}
