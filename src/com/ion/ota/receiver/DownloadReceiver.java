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
package com.ion.ota.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ion.ota.misc.Constants;
import com.ion.ota.misc.UpdateInfo;
import com.ion.ota.service.DownloadCompleteIntentService;
import com.ion.ota.service.DownloadService;

public class DownloadReceiver extends BroadcastReceiver {
    public static final String ACTION_START_DOWNLOAD = "com.ion.ota.action.START_DOWNLOAD";
    public static final String EXTRA_UPDATE_INFO = "update_info";
    public static final String ACTION_DOWNLOAD_STARTED = "com.ion.ota.action.DOWNLOAD_STARTED";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (ACTION_START_DOWNLOAD.equals(action)) {
            UpdateInfo ui = intent.getParcelableExtra(EXTRA_UPDATE_INFO);
            handleStartDownload(context, ui);
        } else if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            handleDownloadComplete(context, id);
        }
    }

    private void handleStartDownload(Context context, UpdateInfo ui) {
        DownloadService.start(context, ui);
    }

    private void handleDownloadComplete(Context context, long id) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        long enqueued = prefs.getLong(Constants.DOWNLOAD_ID, -1);
        String fileName = prefs.getString(Constants.DOWNLOAD_NAME, null);
        if (enqueued < 0 || id < 0 || id != enqueued || fileName == null) {
            return;
        }

        // Send off to DownloadCompleteIntentService
        Intent intent = new Intent(context, DownloadCompleteIntentService.class);
        intent.putExtra(Constants.DOWNLOAD_ID, id);
        intent.putExtra(Constants.DOWNLOAD_NAME, fileName);
        context.startForegroundService(intent);

        // Clear the shared prefs
        prefs.edit()
                .remove(Constants.DOWNLOAD_ID)
                .remove(Constants.DOWNLOAD_NAME)
                .apply();
    }
}
