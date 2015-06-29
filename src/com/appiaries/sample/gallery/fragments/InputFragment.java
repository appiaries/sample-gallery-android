//
// Copyright (c) 2015 Appiaries Corporation. All rights reserved.
//
package com.appiaries.sample.gallery.fragments;

import com.appiaries.baas.sdk.AB;
import com.appiaries.baas.sdk.ABException;
import com.appiaries.baas.sdk.ABResult;
import com.appiaries.baas.sdk.ABStatus;
import com.appiaries.sample.gallery.R;
import com.appiaries.sample.gallery.common.Constants;
import com.appiaries.sample.gallery.managers.DataManager;
import com.appiaries.sample.gallery.models.Illustration;
import com.appiaries.sample.gallery.models.IllustrationImage;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class InputFragment extends BaseFragment {
    private static final String TAG = InputFragment.class.getSimpleName();

	public static final int PICK_RESULT_CODE = 1;

	private String mFilePath;
	private EditText mEditDescription = null;
	private Button mSubmitButton = null;
	private Illustration mIllustration = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_input, container, false);
        setupView(view);
        return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case PICK_RESULT_CODE:
				if (data != null) { // check if user selected a file
					mFilePath = getRealPathFromUri(getActivity() ,data.getData());
					Log.d(TAG, "Selected file: " + mFilePath);
				}
				break;
		}
	}

	/*
	public static String getRealPathFromUri(Context context, Uri contentUri) {
		String path = null;
	    Cursor cursor = null;
	    try {
			String[] projection = {MediaStore.Images.Media.DATA};
			cursor = context.getContentResolver().query(contentUri, projection, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(projection[0]);
			path = cursor.getString(columnIndex);
			Log.d(TAG, "path: " + path);
		} catch (Exception e) {
			e.printStackTrace();
	    } finally {
	        if (cursor != null) {
	            cursor.close();
	        }
	    }
		return path;
	}
	*/

	public static String getRealPathFromUri(Context context, Uri contentUri) {
		String path = null;
		Cursor cursor = null;
		try {
			String wholeID = DocumentsContract.getDocumentId(contentUri);
			String id = wholeID.split(":")[1];
			String[] columns = {MediaStore.Images.Media.DATA};
			String sel = MediaStore.Images.Media._ID + "=?";
			cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, sel, new String[]{id}, null);
			int index = cursor.getColumnIndex(columns[0]);
			if (cursor.moveToFirst()) {
				path = cursor.getString(index);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return path;
	}

    private void setupView(View view) {

        final Activity activity = getActivity();

        Bundle bundle = getArguments();
        if (bundle != null) {
            String illustrationId = bundle.getString(Constants.EXTRA_KEY_ILLUSTRATION_ID);
            mIllustration = DataManager.getIllustration(illustrationId);
        }

        Button fileButton = (Button) view.findViewById(R.id.button_file);
        fileButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");
				startActivityForResult(intent, PICK_RESULT_CODE);
			}
		});

        mSubmitButton = (Button) view.findViewById(R.id.button_submit);
        mEditDescription = (EditText) view.findViewById(R.id.edit_description);

		boolean isNew = mIllustration == null;
        if (isNew) {
			activity.setTitle(R.string.input__title);
			mSubmitButton.setText(R.string.input__submit_button);
        } else {
			activity.setTitle(R.string.input__title_in_edit_mode);
			mSubmitButton.setText(R.string.input__submit_button_in_edit_mode);
			mEditDescription.setText(mIllustration.getDescription());
        }

        mSubmitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				boolean isNew = mIllustration == null;

				// Input Validation
				//>> description
				final String description = mEditDescription.getText() != null ? mEditDescription.getText().toString() : null;
				if (TextUtils.isEmpty(description)) {
					callingDialog(R.string.error__title, R.string.message_error__empty_description);
					return;
				}
				if (description.length() > 100) {
					callingDialog(R.string.error__title, R.string.message_error__too_long_description);
					return;
				}
				//>> filePath
				if (isNew) {
					if (TextUtils.isEmpty(mFilePath)) {
						callingDialog(R.string.error__title, R.string.message_error__required_image_file);
						return;
					}
				}

				if (isNew) {

					final ProgressDialog progress = createAndShowProgressDialog(R.string.progress__creating);

					Handler handler = new Handler();
					Runnable runnable = new Runnable() {
						@Override
						public void run() {

							try {
								// --------------------------------
								//  Create illustration image
								// --------------------------------
								IllustrationImage image = new IllustrationImage();
								image.setID("obj_" + System.currentTimeMillis());
								image.loadData(mFilePath);
								ABResult<IllustrationImage> createImageResult = image.saveSynchronously();
								IllustrationImage createdImage = createImageResult.getData();
								Log.d(TAG, "Illustration image created: " + createdImage);
								// --------------------------------
								//  Create illustration
								// --------------------------------
								Illustration il = new Illustration();
								il.setImageId(createdImage.getID());
								il.setDescription(description);
								ABResult<Illustration> createResult = il.saveSynchronously();
								Illustration created = createResult.getData();
								Log.d(TAG, "Illustration created: " + created);
								if (createResult.getCode() == ABStatus.CREATED) {
									resetForm();
								}

								// in case of add new => back to List screen
								ActionBar actionBar = activity.getActionBar();
								if (actionBar != null) {
									actionBar.setSelectedNavigationItem(0);
									activity.getActionBar().setDisplayHomeAsUpEnabled(false);
								}

								InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
								imm.hideSoftInputFromWindow(mEditDescription.getWindowToken(), 0);

							} catch (ABException e) {
								progress.dismiss();
								showError(activity, e);
								e.printStackTrace();
							} finally {
								progress.dismiss();
							}
						}
					};
					handler.postDelayed(runnable, 2000);

				} else {

					final ProgressDialog progress = createAndShowProgressDialog(R.string.progress__updating);

					Handler handler = new Handler();
					Runnable runnable = new Runnable() {
						@Override
						public void run() {

							IllustrationImage image = new IllustrationImage();
							image.setID(mIllustration.getImageId());
							try {
								if (!TextUtils.isEmpty(mFilePath)) {
									// --------------------------------
									//  Fetch illustration image
									// --------------------------------
									ABResult<IllustrationImage> fetchImageResult = AB.FileService.fetchSynchronously(image);
									IllustrationImage fetchedImage = fetchImageResult.getData();
									Log.d(TAG, "Illustration image fetched: " + fetchedImage);
									// --------------------------------
									//  Update illustration image
									// --------------------------------
									fetchedImage.loadData(mFilePath);
									ABResult<IllustrationImage> updateImageResult = fetchedImage.saveSynchronously();
									IllustrationImage updatedImage = updateImageResult.getData();
									Log.d(TAG, "Illustration image updated: " + updatedImage);
								}
								// --------------------------------
								//  Update illustration
								// --------------------------------
								mIllustration.setDescription(mEditDescription.getText().toString());
								ABResult<Illustration> updateResult = mIllustration.saveSynchronously();
								Illustration updated = updateResult.getData();
								Log.d(TAG, "Illustration updated: " + updated);

								if (updateResult.getCode() == ABStatus.CREATED) {
									resetForm();
								}

								// in case of add new => back to List screen
								ActionBar actionBar = activity.getActionBar();
								if (actionBar != null) {
									actionBar.setSelectedNavigationItem(0);
									activity.getActionBar().setDisplayHomeAsUpEnabled(false);
								}

								InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
								imm.hideSoftInputFromWindow(mEditDescription.getWindowToken(), 0);

							} catch (ABException e) {
								progress.dismiss();
								showError(activity, e);
								e.printStackTrace();
							} finally {
								progress.dismiss();
							}
						}
					};
					handler.postDelayed(runnable, 2000);
				}
			}
		});
    }

	private void resetForm() {
		Activity activity = getActivity();
		activity.setTitle(R.string.input__title);
		mSubmitButton.setText(R.string.input__submit_button);
		this.mFilePath = "";
		this.mEditDescription.setText("");
		this.mIllustration = null;
	}

	private void callingDialog(int dialogTitleResourceID, int dialogContentResourceID) {
		callingDialog(getString(dialogTitleResourceID), getString(dialogContentResourceID));
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
		builder.setNegativeButton(R.string.input__ok_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) { }
		});

		builder.show();
	}

}
