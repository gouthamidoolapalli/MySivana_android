/**
 * Copyright MySivana LLC
 *
 * (C) Copyright MySivana LLC   All rights reserved.
 *
 * NOTICE:  All information contained herein or attendant hereto is,
 *          and remains, the property of MySivana LLC.  Many of the
 *          intellectual and technical concepts contained herein are
 *          proprietary to MySivana LLC. Any dissemination of this
 *          information or reproduction of this material is strictly
 *          forbidden unless prior written permission is obtained
 *          from MySivana LLC.
 *
 * ------------------------------------------------------------------------
 *
 * ========================================================================
 * Revision History
 * ========================================================================
 * DATE             : PROGRAMMER  : DESCRIPTION
 * ========================================================================
 * JUNE 06 2018      : BYNDR       : CREATED.
 * ------------------------------------------------------------------------
 *
 * ========================================================================
 */
package com.mysivana.util;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.google.gson.Gson;
import com.mysivana.R;
import com.mysivana.ui.NotificationsActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

public class CommonUtils {

    public static final int DEFAULT = -1;
    public static final int LEFT_TO_RIGHT = 1;
    public static final int TOP_TO_BOTTOM = 2;


    /**
     * @param object
     * @return
     */
    public static String convertToJson(Object object) {
        Gson gson = new Gson();
        String toJson = gson.toJson(object);
        return toJson;
    }

    public static Dialog showLoadingDialog(Context context) {
        Dialog progressDialog = new Dialog(context, R.style.AppTheme_ProgressDialog);
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        progressDialog.setContentView(R.layout.progress_dialog);
        return progressDialog;
    }

    public static void startActivity(Activity context, Class<?> className) {
        startActivity(context, className, null, -1, DEFAULT);
    }

    public static void startActivity(Activity context, Class<?> className, Bundle extras) {
        startActivity(context, className, extras, -1, DEFAULT);
    }


    public static void startActivityForResult(Activity context, Class<?> className, int requestCode) {
        startActivityForResult(context, className, null, -1, DEFAULT, requestCode);
    }

    public static void startActivityForResult(Activity context, Class<?> className, Bundle extras, int requestCode) {
        startActivityForResult(context, className, extras, -1, DEFAULT, requestCode);
    }

    public static void startActivity(Activity context, Class<?> className, Bundle extras, int flags,
                                     int transitionType) {
        Intent intent = new Intent(context, className);
        if (extras != null)
            intent.putExtras(extras);
        if (flags != -1)
            intent.addFlags(flags);
        context.startActivity(intent);
        switch (transitionType) {
            case DEFAULT:
            case LEFT_TO_RIGHT:
                context.overridePendingTransition(R.anim.slide_in_right, android.R.anim.fade_out);
        }
    }


    public static void startActivityForResult(Activity context, Class<?> className, Bundle extras, int flags,
                                              int transitionType, int requestCode) {
        Intent intent = new Intent(context, className);
        if (extras != null)
            intent.putExtras(extras);
        if (flags != -1)
            intent.addFlags(flags);
        context.startActivityForResult(intent, requestCode);
        switch (transitionType) {
            case DEFAULT:
            case LEFT_TO_RIGHT:
                context.overridePendingTransition(R.anim.slide_in_right, android.R.anim.fade_out);
        }
    }

    public static boolean isPhoneNumber(String input) {
        if (input.matches("\\d+(?:\\.\\d+)?")) {
            return true;
        }
        return false;
    }
    public static boolean isIFSCCode(String input) {
        if (input.matches("^[A-Za-z]{4}[a-zA-Z0-9]{7}$")) {
            return true;
        }
        return false;
    }

    public static boolean appInstalledOrNot(Context context, String uri) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

    public static void clearNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager)context. getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        SharedPrefsUtils.setNotificationCount(context,0);
    }
}