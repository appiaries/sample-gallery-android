//
// Copyright (c) 2015 Appiaries Corporation. All rights reserved.
//
package com.appiaries.sample.gallery.fragments;

import java.util.Arrays;

import com.appiaries.sample.gallery.R;
import com.appiaries.sample.gallery.common.PreferenceHelper;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Switch;

public class SettingsFragment extends Fragment {

    private static class ViewHolder {
        public Spinner spinnerInterval;
        public Switch switchComment;
    }

	ViewHolder mViewHolder;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        setupView(view);
        return view;
	}
	
    private void setupView(View view) {

        final Activity activity = getActivity();

        activity.setTitle(R.string.settings__title);

        mViewHolder = new ViewHolder();
        mViewHolder.spinnerInterval = (Spinner)view.findViewById(R.id.spinnerTimeInterval);

        final ArrayAdapter<String> spinnerArrayAdapter =
                new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.intervals));
        mViewHolder.spinnerInterval.setAdapter(spinnerArrayAdapter);

        final String unit = getString(R.string.settings__display_interval_unit);
        int interval = PreferenceHelper.loadDisplayInterval(activity);
        if (interval >= 0) {
            int index = Arrays.asList(getResources().getStringArray(R.array.intervals)).indexOf(interval + unit);
            mViewHolder.spinnerInterval.setSelection(index);
        } else {
            mViewHolder.spinnerInterval.setSelection(0);
        }

        final String unsetValue = getString(R.string.settings__display_interval_unset);
        mViewHolder.spinnerInterval.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedInterval = spinnerArrayAdapter.getItem(position);
                if (!TextUtils.isEmpty(selectedInterval) && !selectedInterval.equals(unsetValue)) {
                    selectedInterval = selectedInterval.replace(unit, "");
                    PreferenceHelper.saveDisplayInterval(activity, Integer.parseInt(selectedInterval));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        mViewHolder.switchComment = (Switch)view.findViewById(R.id.swtComment);
        boolean commentHidden = PreferenceHelper.loadCommentHidden(activity);
        mViewHolder.switchComment.setChecked(!commentHidden);
        mViewHolder.switchComment.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PreferenceHelper.saveCommentHidden(activity, !isChecked);
            }
        });
    }

}
