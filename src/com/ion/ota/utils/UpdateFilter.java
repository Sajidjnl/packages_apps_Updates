/*
 * Copyright (C) 2012 The CyanogenMod Project
 * Copyright (C) 2017 The LineageOS Project
 * Copyright (C) 2018 Pixel Experience (jhenrique09)
 *
 * * Licensed under the GNU GPLv2 license
 *
 * The text of the license can be found in the LICENSE file
 * or at https://www.gnu.org/licenses/gpl-2.0.txt
 */
package com.ion.ota.utils;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Filename Filter for getting only Files that matches the Given Extensions
 * Extensions can be split with |
 * Example: .zip|.md5sum
 */
public class UpdateFilter implements FilenameFilter {
    private final String[] mExtension;

    public UpdateFilter(String extensions) {
        mExtension = extensions.split("\\|");
    }

    public boolean accept(File dir, String name) {
        for (String extension : mExtension) {
            if (name.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
}
