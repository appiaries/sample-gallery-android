//
// Copyright (c) 2015 Appiaries Corporation. All rights reserved.
//
package com.appiaries.sample.gallery.managers;

import com.appiaries.sample.gallery.models.Illustration;

import java.util.ArrayList;
import java.util.List;

public class DataManager {

    private static List<Illustration> sIllustrationList = new ArrayList<>();

    public static Illustration getIllustration(final String illustrationId) {
        for (Illustration ill : sIllustrationList) {
            if (ill.getID().equals(illustrationId)) {
                return ill;
            }
        }
        return null;
    }

    public static List<Illustration> getIllustrationList() {
        return sIllustrationList;
    }

    public static void setIllustrationList(final List<Illustration> list) {
        sIllustrationList = list;
    }

}
