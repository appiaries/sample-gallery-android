//
// Copyright (c) 2015 Appiaries Corporation. All rights reserved.
//
package com.appiaries.sample.gallery.activities;

import java.util.HashMap;
import java.util.Stack;
import java.util.UUID;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.appiaries.sample.gallery.R;
import com.appiaries.sample.gallery.common.Constants;
import com.appiaries.sample.gallery.fragments.InputFragment;
import com.appiaries.sample.gallery.fragments.ListFragment;
import com.appiaries.sample.gallery.fragments.PlayFragment;
import com.appiaries.sample.gallery.fragments.SettingsFragment;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

	enum TabType {
		LIST, PLAY, ADD, SETTING
	}

	// Tab back stacks
	public HashMap<TabType, Stack<String>> backStacks;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Initialize Main Tab
		setupTabLayout(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == android.R.id.home) {
			// Handle Up button event
			this.onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// Show/hide ActionBar up button
	public void shouldDisplayHomeUp(boolean canback) {
		// Enable Up button only if there are entries in the back stack
		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(canback);
		}
	}

    // Initialize Tab layout
	private void setupTabLayout(Bundle savedInstanceState) {
		// Set back stacks
		if (savedInstanceState != null) {
			// Read back stacks after orientation change
			@SuppressWarnings("unchecked")
			HashMap<TabType, Stack<String>> serializable =
					(HashMap<TabType, Stack<String>>) savedInstanceState.getSerializable("backStacks");
			backStacks = serializable;

		} else {
			// Initialize back stacks on first run
			backStacks = new HashMap<TabType, Stack<String>>();
			backStacks.put(TabType.LIST, new Stack<String>());
			backStacks.put(TabType.PLAY, new Stack<String>());
			backStacks.put(TabType.ADD, new Stack<String>());
			backStacks.put(TabType.SETTING, new Stack<String>());
		}

		// Initialize ActionBar
		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			// Create tabs
			actionBar.addTab(actionBar.newTab().setTag(TabType.LIST).setText(R.string.tab__list).setTabListener(this));
			actionBar.addTab(actionBar.newTab().setTag(TabType.PLAY).setText(R.string.tab__play).setTabListener(this));
			actionBar.addTab(actionBar.newTab().setTag(TabType.ADD).setText(R.string.tab__add).setTabListener(this));
			actionBar.addTab(actionBar.newTab().setTag(TabType.SETTING).setText(R.string.tab__settings).setTabListener(this));
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Select proper stack
		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			Tab tab = actionBar.getSelectedTab();
			Stack<String> backStack = backStacks.get(tab.getTag());
			if (!backStack.isEmpty()) {
				// Restore topmost fragment (e.g. after application switch)
				String tag = backStack.peek();
				Fragment fragment = getFragmentManager().findFragmentByTag(tag);
				if (fragment.isDetached()) {
					FragmentTransaction ft = getFragmentManager().beginTransaction();
					ft.attach(fragment);
					ft.commit();
				}
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Select proper stack
		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			Tab tab = actionBar.getSelectedTab();
			Stack<String> backStack = backStacks.get(tab.getTag());
			if (!backStack.isEmpty()) {
				// Detach topmost fragment otherwise it will not be correctly
				// displayed
				// after orientation change
				String tag = backStack.peek();
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				Fragment fragment = getFragmentManager().findFragmentByTag(tag);
				ft.detach(fragment);
				ft.commit();
			}
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		// Restore selected tab
		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			int saved = savedInstanceState.getInt("tab", 0);
			if (saved != actionBar.getSelectedNavigationIndex()) {
				getActionBar().setSelectedNavigationItem(saved);
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// Save selected tab and all back stacks
		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			outState.putInt(Constants.BUNDLE_KEY_TAB, actionBar.getSelectedNavigationIndex());
			outState.putSerializable(Constants.BUNDLE_KEY_STACKS, backStacks);
		}
	}

	@Override
	public void onBackPressed() {
		// Select proper stack
		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			Tab tab = actionBar.getSelectedTab();
			Stack<String> backStack = backStacks.get(tab.getTag());
			// Remove a fragment from the stack
			String tag;

			hideKeyboard();
			if (backStack.isEmpty()) {
				shouldDisplayHomeUp(false);
				// Let application finish
				// super.onBackPressed();
			} else {
				// Show/hide ActionBar up button
				if (backStack.size() > 1) {
					shouldDisplayHomeUp(true);
				} else {
					shouldDisplayHomeUp(false);
				}

				if (!backStack.isEmpty()) {
					tag = backStack.pop();

					FragmentTransaction ft = getFragmentManager().beginTransaction();
					Fragment fragment = getFragmentManager().findFragmentByTag(tag);

					// Remove topmost fragment from back stack and forget it
					ft.remove(fragment);
					showFragment(backStack, ft);
					ft.commit();
				}
			}
		}
	}

//region TabListener implementations
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// Select proper stack
		Stack<String> backStack = backStacks.get(tab.getTag());
		backStack.clear();
		if (backStack.isEmpty()) {
			// If it is empty instantiate and add initial tab fragment
			Fragment fragment;
			switch ((TabType) tab.getTag()) {
                case LIST:
					hideKeyboard();
                    fragment = Fragment.instantiate(this, ListFragment.class.getName());
                    break;

                case PLAY:
                    hideKeyboard();
                    fragment = Fragment.instantiate(this, PlayFragment.class.getName());
                    break;

                case ADD:
                    hideKeyboard();
                    fragment = Fragment.instantiate(this, InputFragment.class.getName());
                    break;

                case SETTING:
                    hideKeyboard();
                    fragment = Fragment.instantiate(this, SettingsFragment.class.getName());
                    break;

                default:
                    throw new java.lang.IllegalArgumentException("Unknown Tab");
			}
			addFragment(fragment, backStack, ft);

			// hide up button
			shouldDisplayHomeUp(false);

		} else {
			// check to show/hide up button
			hideKeyboard();
			if (backStack.size() > 1) {
				shouldDisplayHomeUp(true);
			} else {
				shouldDisplayHomeUp(false);
			}

			// Show topmost fragment
			showFragment(backStack, ft);
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// Select proper stack
		Stack<String> backStack = backStacks.get(tab.getTag());
		// Get topmost fragment
		String tag = "";
		if (!backStack.isEmpty()) {
			tag = backStack.peek();
			Fragment fragment = getFragmentManager().findFragmentByTag(tag);
			// Detach it
			ft.detach(fragment);
		}

	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// Select proper stack
		Stack<String> backStack = backStacks.get(tab.getTag());

		if (backStack.size() > 1) {
			// ft.setCustomAnimations(R.anim.slide_from_right,
			// R.anim.slide_to_left);
			// Clean the stack leaving only initial fragment
			while (backStack.size() > 1) {
				// Pop topmost fragment
				String tag = backStack.pop();
				Fragment fragment = getFragmentManager().findFragmentByTag(tag);
				// Remove it
				ft.remove(fragment);
			}
		}
		showFragment(backStack, ft);
	}
//endregion

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Select proper stack
		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			Tab tab = actionBar.getSelectedTab();
			Stack<String> backStack = backStacks.get(tab.getTag());
			Fragment fragment = getFragmentManager().findFragmentByTag(backStack.peek());

			// Passing the Activity Result for the child fragment
			if (fragment != null) {
				fragment.onActivityResult(requestCode, resultCode, data);
			}
		}
	}

	// Show a fragment from the stack
	private void showFragment(Stack<String> backStack, FragmentTransaction ft) {
		// Peek topmost fragment from the stack
		String tag;
		if (!backStack.isEmpty()) {
			tag = backStack.peek();
			Fragment fragment = getFragmentManager().findFragmentByTag(tag);
			// and attach it
			ft.attach(fragment);
		} else {
			// If it is empty instantiate and add initial tab fragment
			Fragment fragment;
			ActionBar actionBar = getActionBar();
			if (actionBar != null) {
				TabType tabType = (TabType) actionBar.getSelectedTab().getTag();
				switch (tabType) {
					case LIST:
						hideKeyboard();
						fragment = Fragment.instantiate(this, ListFragment.class.getName());
						break;

					case PLAY:
						hideKeyboard();
						fragment = Fragment.instantiate(this, PlayFragment.class.getName());
						break;

					case ADD:
						hideKeyboard();
						fragment = Fragment.instantiate(this, InputFragment.class.getName());
						break;

					case SETTING:
						hideKeyboard();
						fragment = Fragment.instantiate(this, SettingsFragment.class.getName());
						break;

					default:
						throw new java.lang.IllegalArgumentException("Unknown Tab");
				}
				addFragment(fragment, backStack, ft);
			}

			// hide up button
			shouldDisplayHomeUp(false);
		}
	}

    // Add a fragment into current tab's backStack
	public void addFragment(Fragment fragment) {
		// Select proper stack
		ActionBar actionBar = getActionBar();
		if (actionBar != null) {
			Tab tab = actionBar.getSelectedTab();
			Stack<String> backStack = backStacks.get(tab.getTag());

			FragmentTransaction ft = getFragmentManager().beginTransaction();

			// Get topmost fragment
			String tag = backStack.peek();
			Fragment top = getFragmentManager().findFragmentByTag(tag);
			if (top != null) {
				ft.detach(top);
			}
			// Add new fragment
			addFragment(fragment, backStack, ft);
			ft.commit();
		}
	}

	private void addFragment(Fragment fragment, Stack<String> backStack, FragmentTransaction ft) {
		// Add fragment to back stack with unique tag
		String tag = UUID.randomUUID().toString();
		ft.add(android.R.id.content, fragment, tag);
		backStack.push(tag);
		shouldDisplayHomeUp((backStack.size() > 1));
	}

	public HashMap<TabType, Stack<String>> getBackStacks() {
		return this.backStacks;
	}

	public Fragment getFragmentByTag(String tag) {
		return getFragmentManager().findFragmentByTag(tag);
	}

	private void hideKeyboard() {
		if (getCurrentFocus() != null) {
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// do something on back.
			ActionBar actionBar = getActionBar();
			if (actionBar != null) {
				Tab tabSelected = actionBar.getSelectedTab();
				if (tabSelected.getText().equals(getString(R.string.tab__list))) {
					finish();
					moveTaskToBack(true);
				} else {
					getActionBar().selectTab(actionBar.newTab().setTag(TabType.LIST).setText(R.string.tab__list).setTabListener(this));
				}
			}
		}
		return super.onKeyDown(keyCode, event);
	}

}
