//
// Copyright (c) 2015 Appiaries Corporation. All rights reserved.
//
package com.appiaries.sample.gallery.common;

import java.io.File;

import android.content.Context;
import android.util.Log;

public class FileCache {
    private static final String TAG = FileCache.class.getSimpleName();
	  
    private File cacheDir;
  
    public FileCache(Context context){
        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            cacheDir = new File(android.os.Environment.getExternalStorageDirectory(), Constants.CACHE_DIR);
        } else {
            cacheDir = context.getCacheDir();
        }
        if (!cacheDir.exists()) {
            if (!cacheDir.mkdirs()) {
                Log.e(TAG, "Failed to create directories [cacheDir=" + cacheDir.getAbsolutePath() + "]");
            }
        }
    }
  
    public File getFile(String url) {
        String filename = String.valueOf(url.hashCode());
        return new File(cacheDir, filename);
    }
  
    public void clear() {
        File[] files = cacheDir.listFiles();
        if (files == null) {
            return;
        }
        for (File f : files) {
            if (!f.delete()) {
                Log.e(TAG, "Failed to delete a file [file=" + f.getAbsolutePath() + "]");
            }
        }
    }
  
}
