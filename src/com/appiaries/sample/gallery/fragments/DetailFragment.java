//
// Copyright (c) 2015 Appiaries Corporation. All rights reserved.
//
package com.appiaries.sample.gallery.fragments;

import java.util.ArrayList;
import java.util.List;
import com.appiaries.baas.sdk.ABException;
import com.appiaries.baas.sdk.ABResult;
import com.appiaries.baas.sdk.ResultCallback;
import com.appiaries.sample.gallery.R;
import com.appiaries.sample.gallery.common.Constants;
import com.appiaries.sample.gallery.common.PreferenceHelper;
import com.appiaries.sample.gallery.managers.DataManager;
import com.appiaries.sample.gallery.activities.MainActivity;
import com.appiaries.sample.gallery.adapter.ImagePagerAdapter;
import com.appiaries.sample.gallery.models.Illustration;
import com.appiaries.sample.gallery.models.IllustrationImage;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class DetailFragment extends BaseFragment {

	List<Illustration> mIllustrations;
	boolean mCommentHidden = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_detail, container, false);
        setupView(view);
		return view;
	}

    private void setupView(View view) {

        final Activity activity = getActivity();

        activity.setTitle(R.string.detail__title);

        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.view_pager);

        mCommentHidden = PreferenceHelper.loadCommentHidden(activity);

        mIllustrations = new ArrayList<>();

        Bundle bundle = getArguments();
        Illustration il = DataManager.getIllustration(bundle.getString(Constants.EXTRA_KEY_ILLUSTRATION_ID));

        mIllustrations.add(il);

        if(mIllustrations.size() > 0){
            ImagePagerAdapter adapter = new ImagePagerAdapter(activity, mIllustrations);
            viewPager.setAdapter(adapter);
        }

        final Button deleteButton      = (Button)   view.findViewById(R.id.button_delete);
        final Button editButton        = (Button)   view.findViewById(R.id.button_edit);
        final TextView textDescription = (TextView) view.findViewById(R.id.text_description);

        if (!mCommentHidden) {
            textDescription.setVisibility(View.VISIBLE);
            textDescription.setText(il != null ? il.getDescription() : null);
        } else {
            textDescription.setVisibility(View.INVISIBLE);
        }

        deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final int imagePosition = viewPager.getCurrentItem();
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage(R.string.detail__delete_confirm_message);
                builder.setNegativeButton(R.string.detail__ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final ProgressDialog progress = createAndShowProgressDialog(R.string.progress__deleting);
                        // --------------------------------
                        //  Delete illustration image
                        // --------------------------------
                        final Illustration il = mIllustrations.get(imagePosition);
                        IllustrationImage image = new IllustrationImage();
                        image.setID(il.getImageId());
                        image.delete(new ResultCallback<Void>() {
                            @Override
                            public void done(ABResult<Void> voidABResult, ABException e) {
                                if (e == null) {
                                    // --------------------------------
                                    //  Delete illustration
                                    // --------------------------------
                                    il.delete(new ResultCallback<Void>() {
                                        @Override
                                        public void done(ABResult<Void> voidABResult, ABException e) {
                                            progress.dismiss();
                                            if (e == null) {
                                                // back to ListFragment
                                                activity.onBackPressed();
                                            } else {
                                                showError(activity, e);
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } else {
                                    progress.dismiss();
                                    showError(activity, e);
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
                builder.setPositiveButton(R.string.detail__cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) { }
                });
                builder.show();
            }
        });

        editButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // change current image
                Illustration illustration = mIllustrations.get(viewPager.getCurrentItem());

                InputFragment addfragment = new InputFragment();

                Bundle bundle = new Bundle();
                bundle.putString(Constants.EXTRA_KEY_ILLUSTRATION_ID, illustration.getID());
                addfragment.setArguments(bundle);

                ((MainActivity) activity).addFragment(addfragment);
            }
        });
    }

}
