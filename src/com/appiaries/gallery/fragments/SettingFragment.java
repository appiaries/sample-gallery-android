/*******************************************************************************
 * Copyright (c) 2014 Appiaries Corporation. All rights reserved.
 *******************************************************************************/
package com.appiaries.gallery.fragments;

import java.util.Arrays;

import com.appiaries.gallery.R;
import com.appiaries.gallery.common.APIHelper;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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

public class SettingFragment extends Fragment {	
	PlanetHolder planetHolder;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_setting, container,
				false);		
		
		getActivity().setTitle("設定");
				
		planetHolder = new PlanetHolder();
		planetHolder.spinnerInterval = (Spinner)rootView.findViewById(R.id.spinnerTimeInterval);

		final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.intervalArray));
		planetHolder.spinnerInterval.setAdapter(spinnerArrayAdapter);
		
		if(APIHelper.getStringInLocalStorage(getActivity(), "interval") != null && !APIHelper.getStringInLocalStorage(getActivity(), "interval").equals("")){
			int index = Arrays.asList(getResources().getStringArray(R.array.intervalArray)).indexOf(APIHelper.getStringInLocalStorage(getActivity(), "interval") +"秒");
			planetHolder.spinnerInterval.setSelection(index);
		}else{
			planetHolder.spinnerInterval.setSelection(0);
		}
		
		
		planetHolder.spinnerInterval.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				String intervalSelected = spinnerArrayAdapter.getItem(position);
				if(!TextUtils.isEmpty(intervalSelected) && !intervalSelected.equals("画像の表示間隔")){
					intervalSelected = intervalSelected.replace("秒","");
					APIHelper.setStringToLocalStorage(getActivity(), "interval", intervalSelected);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
		
		planetHolder.swtComment = (Switch)rootView.findViewById(R.id.swtComment);
		if(APIHelper.getStringInLocalStorage(getActivity(), "comment") != null && !APIHelper.getStringInLocalStorage(getActivity(), "comment").equals("")){
			int commentFlag = Integer.parseInt(APIHelper.getStringInLocalStorage(getActivity(), "comment"));
			if(commentFlag == 1){
				planetHolder.swtComment.setChecked(true);
			}else{
				planetHolder.swtComment.setChecked(false);
			}
		}else{
			planetHolder.swtComment.setChecked(true);
		}
		
		planetHolder.swtComment.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					APIHelper.setStringToLocalStorage(getActivity(), "comment","1");
				}else{
					APIHelper.setStringToLocalStorage(getActivity(), "comment","0");
				}
			}
		});
		return rootView;
	}
	
	@Override
	public void onStop() {		
		super.onStop();
		
	}
	private static class PlanetHolder {
		public Spinner spinnerInterval;
		public Switch swtComment;
	}
}
