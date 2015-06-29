//
// Copyright (c) 2015 Appiaries Corporation. All rights reserved.
//
package com.appiaries.sample.gallery.common;

import android.content.Context;
import android.content.SharedPreferences;

@SuppressWarnings("unused")
public class PreferenceHelper {

    public static final String PREFS_NAME = "GalleryPrefs";

    public static final String PREF_KEY_DISPLAY_COMMENT_HIDDEN = "commentHidden";
    public static final String PREF_KEY_DISPLAY_INTERVAL = "displayInterval";


    public static void saveCommentHidden(Context context, boolean hidden) {
        SharedPreferences pref = getPreference(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(PreferenceHelper.PREF_KEY_DISPLAY_COMMENT_HIDDEN, hidden);
        editor.apply();
    }
    public static boolean loadCommentHidden(Context context) {
        SharedPreferences pref = getPreference(context);
        return pref.getBoolean(PreferenceHelper.PREF_KEY_DISPLAY_COMMENT_HIDDEN, false);
    }

    public static void saveDisplayInterval(Context context, int interval) {
        SharedPreferences pref = getPreference(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(PreferenceHelper.PREF_KEY_DISPLAY_INTERVAL, interval);
        editor.apply();
    }
    public static int loadDisplayInterval(Context context) {
        SharedPreferences pref = getPreference(context);
        return pref.getInt(PreferenceHelper.PREF_KEY_DISPLAY_INTERVAL, 0);
    }

    public static String loadString(Context context, String key) {
        SharedPreferences pref = getPreference(context);
        return pref.getString(key, "");
    }

    public static void saveString(Context context, String key, String value){
        SharedPreferences pref = getPreference(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private static SharedPreferences getPreference(Context context) {
        return context.getSharedPreferences(PreferenceHelper.PREFS_NAME, 0);
    }

}
