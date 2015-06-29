//
// Copyright (c) 2015 Appiaries Corporation. All rights reserved.
//
package com.appiaries.sample.gallery.fragments;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;

import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class PlayFragment extends BaseFragment {
    private static final String TAG = PlayFragment.class.getSimpleName();

	List<Illustration> mIllustrations;
	int mPlayTime = 5000;
	boolean mCommentHidden = false;
    Timer mTimer;
    ViewPager mViewPager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_play, container, false);
        setupView(view);

        mViewPager = (ViewPager)view.findViewById(R.id.view_pager);
		final Handler handler = new Handler();
		final Runnable update = new Runnable() {
			int currentPage;

			public void run() {
				currentPage = mViewPager.getCurrentItem();
				Log.d(TAG, "current page: " + currentPage);
				if (currentPage == mIllustrations.size() - 1) {
					mViewPager.setCurrentItem(0, true);
				} else {
					mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
				}
			}
		};

		mTimer = new Timer();
		mTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				handler.post(update);
			}
		}, 3000, mPlayTime);

		return view;
	}

	@Override
	public void onStop() {
		super.onStop();
        mTimer.cancel();
	}

    private void setupView(View view) {

        final Activity activity = getActivity();

        activity.setTitle(R.string.play__title);

        int interval = PreferenceHelper.loadDisplayInterval(activity);
        if (interval > 0) {
            mPlayTime = interval * 1000;
        }

        mCommentHidden = PreferenceHelper.loadCommentHidden(activity);

        mIllustrations = DataManager.getIllustrationList();

        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        if (mIllustrations.size() > 0) {
            ImagePagerAdapter adapter = new ImagePagerAdapter(activity, mIllustrations);
            viewPager.setAdapter(adapter);
        }

        final Button nextButton = (Button) view.findViewById(R.id.button_next);
        final Button prevButton = (Button) view.findViewById(R.id.button_prev);

        nextButton.setVisibility(View.INVISIBLE);
        prevButton.setVisibility(View.INVISIBLE);

        final Button deleteButton      = (Button)   view.findViewById(R.id.button_delete);
        final Button editButton        = (Button)   view.findViewById(R.id.button_edit);
        final TextView textDescription = (TextView) view.findViewById(R.id.text_description);

        textDescription.setVisibility(mCommentHidden ? View.INVISIBLE : View.VISIBLE);

        nextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = viewPager.getCurrentItem();
                if (currentPosition < mIllustrations.size() - 1) {
                    viewPager.setCurrentItem(currentPosition + 1, true);
                }
            }
        });

        prevButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = viewPager.getCurrentItem();
                if (currentPosition > 0) {
                    viewPager.setCurrentItem(currentPosition - 1, true);
                }
            }
        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) { }

            @Override
            public void onPageScrolled(int position, float arg1, int arg2) {
                textDescription.setText(mIllustrations.get(position).getDescription());

                // do not show Arrow buttons if there is only one item
                if (mIllustrations.size() <= 1) {
                    nextButton.setVisibility(View.INVISIBLE);
                    prevButton.setVisibility(View.INVISIBLE);
                } else if (position == mIllustrations.size() - 1) { // current position is the last item on the image list
                    nextButton.setVisibility(View.INVISIBLE);
                    prevButton.setVisibility(View.VISIBLE);
                } else if (position == 0) { // current position is the first item on the image list
                    nextButton.setVisibility(View.VISIBLE);
                    prevButton.setVisibility(View.INVISIBLE);
                } else {
                    nextButton.setVisibility(View.VISIBLE);
                    prevButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int arg0) { }
        });

        deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimer.cancel(); //NOTE: pause timer

                final int imagePosition = viewPager.getCurrentItem();
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage(R.string.play__delete_confirm_message);
                builder.setNegativeButton(R.string.play__ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final ProgressDialog progress = createAndShowProgressDialog("Deleting...");
                        // --------------------------------
                        //  Delete IllustrationImage
                        // --------------------------------
                        final Illustration il = mIllustrations.get(imagePosition);
                        IllustrationImage image = new IllustrationImage();
                        image.setID(il.getImageId());
                        image.delete(new ResultCallback<Void>() {
                            @Override
                            public void done(ABResult<Void> imageDeleteResult, ABException e) {
                                if (e == null) {
                                    // --------------------------------
                                    //  Delete Illustration
                                    // --------------------------------
                                    il.delete(new ResultCallback<Void>() {
                                        @Override
                                        public void done(ABResult<Void> illResult, ABException e) {
                                            progress.dismiss();
                                            if (e == null) {
                                                ActionBar actionBar = activity.getActionBar();
                                                if (actionBar != null) {
                                                    actionBar.setSelectedNavigationItem(0);
                                                }
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
                builder.setPositiveButton(R.string.play__cancel_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        //NOTE: resume timer

                        final Handler handler = new Handler();
                        final Runnable update = new Runnable() {
                            int currentPage;

                            public void run() {
                                currentPage = mViewPager.getCurrentItem();
                                Log.d(TAG, "current page: " + currentPage);
                                if (currentPage == mIllustrations.size() - 1) {
                                    mViewPager.setCurrentItem(0, true);
                                } else {
                                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
                                }
                            }
                        };

                        mTimer = new Timer();
                        mTimer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                handler.post(update);
                            }
                        }, 3000, mPlayTime);
                    }
                });
                builder.show();
            }
        });

        editButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Illustration il = mIllustrations.get(viewPager.getCurrentItem());
                InputFragment fragment = new InputFragment();

                Bundle bundle = new Bundle();
                bundle.putString(Constants.EXTRA_KEY_ILLUSTRATION_ID, il.getID());
                fragment.setArguments(bundle);

                ((MainActivity) activity).addFragment(fragment);
            }
        });
    }

}
