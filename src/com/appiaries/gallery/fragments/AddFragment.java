/*******************************************************************************
 * Copyright (c) 2014 Appiaries Corporation. All rights reserved.
 *******************************************************************************/
package com.appiaries.gallery.fragments;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import com.appiaries.APISException;
import com.appiaries.APISFileData;
import com.appiaries.APISResult;
import com.appiaries.gallery.R;
import com.appiaries.gallery.common.Constants;
import com.appiaries.gallery.jsonmodels.Illustrations;
import com.appiaries.gallery.managers.IllustrationManager;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.app.FragmentManager.BackStackEntry;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AddFragment extends Fragment {

	public static final int PICKFILE_RESULT_CODE = 1;

	private String filePath;

	private EditText txtDescription = null;

	private Button btnAdd = null;

	private Illustrations illusObj = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_add, container,
				false);

		Bundle bundle = getArguments();
		if (bundle != null) {
			String illustrationId = bundle.getString("illustrationId");

			illusObj = IllustrationManager.getInstance().getIllustrationObject(
					illustrationId);
		}

		Button btnChooseFile = (Button) rootView.findViewById(R.id.btn_browse);
		btnChooseFile.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");
				startActivityForResult(intent, PICKFILE_RESULT_CODE);
			}
		});

		btnAdd = (Button) rootView.findViewById(R.id.btn_Add);

		txtDescription = (EditText) rootView.findViewById(R.id.txt_description);

		// Edit mode
		if (illusObj != null) {
			getActivity().setTitle("画像変更");

			btnAdd.setText("変更");

			txtDescription.setText(illusObj.getDescription());
		} else // Add new mode
		{
			getActivity().setTitle("画像追加");

			btnAdd.setText("登録");

			// txtDescription.setText("");
		}

		btnAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// check if selected file is not null in case of add new
				if (txtDescription.getText() != null
						&& txtDescription.getText().toString().length() > 100) {
					callingDialog("確認", "コメントが全角50文字を超えています。");
				} else if (txtDescription.getText() != null
						&& TextUtils.isEmpty(txtDescription.getText())) {
					callingDialog("確認", "コメントが入力されていません");
				} else {
					if (illusObj == null) {
						if (filePath != null && !filePath.equals("")) {
							new UploadFileAsyncTask().execute();
						}
						else{
							callingDialog("確認", "画像が選択されていません");
						}
					} else {
						new UploadFileAsyncTask().execute();
					}
				}

			}
		});

		return rootView;
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onPause() {
		super.onPause();
		// resetForm();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case PICKFILE_RESULT_CODE:
			if (data != null) // check if user selected a file
			{
				filePath = getRealPathFromUri(getActivity() ,data.getData());
				Log.d("Selected file", filePath);
			}
			

			break;
		}
	}
	
	public static String getRealPathFromUri(Context context, Uri contentUri) {
	    Cursor cursor = null;
	    try {
	        String[] proj = { MediaStore.Images.Media.DATA };
	        cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
	        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	        cursor.moveToFirst();
	        return cursor.getString(column_index);
	    } finally {
	        if (cursor != null) {
	            cursor.close();
	        }
	    }
	}

	private void resetForm() {
		getActivity().setTitle("画像追加");
		btnAdd.setText("登録");
		this.filePath = "";
		this.txtDescription.setText("");
		this.illusObj = null;
	}

	private class UploadFileAsyncTask extends AsyncTask<Void, Void, Integer> {
		ProgressDialog progressDlg = new ProgressDialog(getActivity());

		@Override
		protected void onPreExecute() {
			progressDlg.setMessage("Uploading...");
			progressDlg.setCancelable(false);
			progressDlg.setCanceledOnTouchOutside(false);
			progressDlg.show();
		}

		@Override
		protected Integer doInBackground(Void... params) {
			File file = null;
			String fileName = "";
			String objectName = "";
			String mimeType = "";
			byte[] data = null;

			if (filePath != null && !filePath.equals("")) {
				file = new File(filePath);

				if (file.exists () && file.isFile ()) {
					fileName = file.getName();

					objectName = "obj_" + System.currentTimeMillis();

					Uri selectedUri = Uri.fromFile(file);
					String fileExtension = MimeTypeMap
							.getFileExtensionFromUrl(selectedUri.toString());

					mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
							fileExtension);

					ByteArrayOutputStream out = new ByteArrayOutputStream();
					FileInputStream in = null;
					try {
						in = new FileInputStream(filePath);
						byte[] buff = new byte[1024];
						while (in.read(buff) > 0) {
							out.write(buff);
						}
						data = out.toByteArray();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						try {
							in.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						try {
							out.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					// Upload file using APIS
					try {
						// Add new image
						if (illusObj == null) {
							APISResult result = APISFileData.registData(
									Constants.ILLUSTRATION_IMAGES_ID, objectName,
									fileName, data, mimeType, "");

							if (result.getResponseCode() == 201) {
								// Insert create an Illustration object
								HashMap<String, Object> illustrationData = new HashMap<String, Object>();

								illustrationData.put("description", txtDescription
										.getText().toString());
								illustrationData.put("image_id", objectName);

								return IllustrationManager.getInstance().registData(
										illustrationData);
							}
						} else // Update current image
						{
							// check if user selected new one to replace current image
							// => update current image by new one.
							if (filePath != null && !filePath.equals("")) {

								// Upload image file
								APISResult result = APISFileData.registData(
										Constants.ILLUSTRATION_IMAGES_ID, objectName,
										fileName, data, mimeType, "");

								if (result.getResponseCode() == 201
										|| result.getResponseCode() == 204) {

									// Update an Illustration object
									HashMap<String, Object> illustrationData = new HashMap<String, Object>();

									illustrationData.put("description", txtDescription
											.getText().toString());

									illustrationData.put("image_id", objectName);

									return IllustrationManager.getInstance()
											.updateData(illusObj.getId(),
													illustrationData);
								}
							} else {
								// Update an Illustration object only
								HashMap<String, Object> illustrationData = new HashMap<String, Object>();

								illustrationData.put("description", txtDescription
										.getText().toString());

								illustrationData
										.put("image_id", illusObj.getImageURI());

								return IllustrationManager.getInstance().updateData(
										illusObj.getId(), illustrationData);
							}
						}

					} catch (APISException e) {
						e.printStackTrace();
					}

				}			
			}
			return -1;
		}

		@Override
		protected void onPostExecute(Integer result) {
			progressDlg.dismiss();

			if (result == 201 || result == 204) {
				if (result == 201) {
					resetForm();
				}
				Log.d("Upload Status", "Success!");

				// in case of add new => back to List screen
				// if (illusObj == null) {
				// Back to List tab
				ActionBar actionBar = getActivity().getActionBar();
				actionBar.setSelectedNavigationItem(0);
				getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
			
				InputMethodManager imm = (InputMethodManager) getActivity()
						.getSystemService(getActivity().INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(txtDescription.getWindowToken(), 0);
			    
				// } else // in case of edit => back to previous fragment
				// {
				// getActivity().onBackPressed();
				// }
			}
		}
	}


	private void callingDialog(String dialogTitle, String dialogContent) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		TextView customTitle = new TextView(getActivity());
		customTitle.setText(dialogTitle);
		customTitle.setPadding(10, 10, 10, 10);
		customTitle.setGravity(Gravity.CENTER);
		customTitle.setTextSize(20);

		builder.setCustomTitle(customTitle);

		TextView customMessage = new TextView(getActivity());

		customMessage.setPadding(10, 40, 10, 40);
		customMessage.setText(dialogContent);
		customMessage.setGravity(Gravity.CENTER_HORIZONTAL);
		customMessage.setTextSize(20);

		builder.setView(customMessage);

		// handle cancel button click
		builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		});

		builder.show();
	}
	
	private void clearBackStack() {
	FragmentManager manager = getActivity().getFragmentManager();
		if (manager.getBackStackEntryCount() > 0) {
			FragmentManager.BackStackEntry first = (BackStackEntry) manager
					.getBackStackEntryAt(0);
			manager.popBackStack(null,
					FragmentManager.POP_BACK_STACK_INCLUSIVE);
		}
	}
}
