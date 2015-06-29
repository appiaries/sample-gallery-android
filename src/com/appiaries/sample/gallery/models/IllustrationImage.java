//
// Copyright (c) 2015 Appiaries Corporation. All rights reserved.
//
package com.appiaries.sample.gallery.models;

import com.appiaries.baas.sdk.ABCollection;
import com.appiaries.baas.sdk.ABFile;
import com.appiaries.baas.sdk.ABQuery;

@ABCollection("IllustrationImages")
public class IllustrationImage extends ABFile {

    @SuppressWarnings("unused")
    public static class Field extends ABFile.Field { }

    public IllustrationImage() {
        super("IllustrationImages");
    }

    @SuppressWarnings("unused")
    public static ABQuery query() {
        return ABQuery.query(IllustrationImage.class);
    }

}
