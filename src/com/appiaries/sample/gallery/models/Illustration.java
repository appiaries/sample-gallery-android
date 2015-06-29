//
// Copyright (c) 2015 Appiaries Corporation. All rights reserved.
//
package com.appiaries.sample.gallery.models;

import com.appiaries.baas.sdk.AB;
import com.appiaries.baas.sdk.ABCollection;
import com.appiaries.baas.sdk.ABDBObject;
import com.appiaries.baas.sdk.ABField;
import com.appiaries.baas.sdk.ABQuery;

@ABCollection("Illustrations")
public class Illustration extends ABDBObject {
    private static final long serialVersionUID = -6555441627576638110L;

    public static class Field extends ABDBObject.Field {
        public static final ABField DESCRIPTION = new ABField("description", String.class);
        public static final ABField IMAGE_ID    = new ABField("image_id", String.class);
    }

    public Illustration() {
        super("Illustrations");
    }

    public static ABQuery query() {
        return ABQuery.query(Illustration.class);
    }

    public String getDescription() {
        return get(Field.DESCRIPTION);
    }
    public void setDescription(String description) {
        put(Field.DESCRIPTION, description);
    }

    public String getImageId() {
        return get(Field.IMAGE_ID);
    }
    public void setImageId(String imageId) {
        put(Field.IMAGE_ID, imageId);
    }

    public String getImageUrl() {
        return String.format("https://api-datastore.appiaries.com/v1/bin/%s/%s/%s/%s/_bin",
                AB.Config.getDatastoreID(), AB.Config.getApplicationID(), "IllustrationImages", get(Field.IMAGE_ID));
    }
}
