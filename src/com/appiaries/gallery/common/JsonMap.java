/*******************************************************************************
 * Copyright (c) 2014 Appiaries Corporation. All rights reserved.
 *******************************************************************************/
package com.appiaries.gallery.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

class JsonMap extends HashMap<Object, Object> {
    ObjectMapper mMapper = new ObjectMapper();

    public JsonMap() {
        super();
    }

    public JsonMap(Map<?, ?> map) {
        super(map);
    }

    public JsonMap(String jsonString) throws IOException {
        Map<?, ?> map = mMapper.readValue(jsonString, Map.class);
        this.putAll(map);
    }

    String toJson() throws IOException {
        String str = mMapper.writeValueAsString(this);
        return str;
    }

    private static final long serialVersionUID = -5249594819717358863L;
}	