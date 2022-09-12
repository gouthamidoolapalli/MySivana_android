/**
 * Copyright MySivana LLC
 * <p>
 * (C) Copyright MySivana LLC   All rights reserved.
 * <p>
 * NOTICE:  All information contained herein or attendant hereto is,
 * and remains, the property of MySivana LLC.  Many of the
 * intellectual and technical concepts contained herein are
 * proprietary to MySivana LLC. Any dissemination of this
 * information or reproduction of this material is strictly
 * forbidden unless prior written permission is obtained
 * from MySivana LLC.
 * <p>
 * ------------------------------------------------------------------------
 * <p>
 * ========================================================================
 * Revision History
 * ========================================================================
 * DATE             : PROGRAMMER  : DESCRIPTION
 * ========================================================================
 * JUNE 06 2018      : BYNDR       : CREATED.
 * ------------------------------------------------------------------------
 * <p>
 * ========================================================================
 */
package com.mysivana.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.mysivana.mvp.model.UserResponse;

import java.util.Set;

/**
 * A pack of helpful getter and setter methods for reading/writing to {@link SharedPreferences}.
 */
final public class SharedPrefsUtils {
    private SharedPrefsUtils() {
    }

    /**
     * Helper method to retrieve a String value from {@link SharedPreferences}.
     *
     * @param context a {@link Context} object.
     * @param key
     * @return The value from shared preferences, or null if the value could not be read.
     */
    public static String getStringPreference(Context context, String key) {
        String value = null;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            value = preferences.getString(key, null);
        }
        return value;
    }

    /**
     * Helper method to write a String value to {@link SharedPreferences}.
     *
     * @param context a {@link Context} object.
     * @param key
     * @param value
     * @return true if the new value was successfully written to persistent storage.
     */
    public static boolean setStringPreference(Context context, String key, String value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null && !TextUtils.isEmpty(key)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(key, value);
            return editor.commit();
        }
        return false;
    }



    /**
     * Helper method to retrieve a float value from {@link SharedPreferences}.
     *
     * @param context      a {@link Context} object.
     * @param key
     * @param defaultValue A default to return if the value could not be read.
     * @return The value from shared preferences, or the provided default.
     */
    public static float getFloatPreference(Context context, String key, float defaultValue) {
        float value = defaultValue;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            value = preferences.getFloat(key, defaultValue);
        }
        return value;
    }

    /**
     * Helper method to write a float value to {@link SharedPreferences}.
     *
     * @param context a {@link Context} object.
     * @param key
     * @param value
     * @return true if the new value was successfully written to persistent storage.
     */
    public static boolean setFloatPreference(Context context, String key, float value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putFloat(key, value);
            return editor.commit();
        }
        return false;
    }

    /**
     * Helper method to retrieve a long value from {@link SharedPreferences}.
     *
     * @param context      a {@link Context} object.
     * @param key
     * @param defaultValue A default to return if the value could not be read.
     * @return The value from shared preferences, or the provided default.
     */
    public static long getLongPreference(Context context, String key, long defaultValue) {
        long value = defaultValue;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            value = preferences.getLong(key, defaultValue);
        }
        return value;
    }

    /**
     * Helper method to write a long value to {@link SharedPreferences}.
     *
     * @param context a {@link Context} object.
     * @param key
     * @param value
     * @return true if the new value was successfully written to persistent storage.
     */
    public static boolean setLongPreference(Context context, String key, long value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(key, value);
            return editor.commit();
        }
        return false;
    }

    /**
     * Helper method to retrieve an integer value from {@link SharedPreferences}.
     *
     * @param context      a {@link Context} object.
     * @param key
     * @param defaultValue A default to return if the value could not be read.
     * @return The value from shared preferences, or the provided default.
     */
    public static int getIntegerPreference(Context context, String key, int defaultValue) {
        int value = defaultValue;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            value = preferences.getInt(key, defaultValue);
        }
        return value;
    }

    /**
     * Helper method to write an integer value to {@link SharedPreferences}.
     *
     * @param context a {@link Context} object.
     * @param key
     * @param value
     * @return true if the new value was successfully written to persistent storage.
     */
    public static boolean setIntegerPreference(Context context, String key, int value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(key, value);
            return editor.commit();
        }
        return false;
    }

    /**
     * Helper method to retrieve a boolean value from {@link SharedPreferences}.
     *
     * @param context      a {@link Context} object.
     * @param key
     * @param defaultValue A default to return if the value could not be read.
     * @return The value from shared preferences, or the provided default.
     */
    public static boolean getBooleanPreference(Context context, String key, boolean defaultValue) {
        boolean value = defaultValue;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            value = preferences.getBoolean(key, defaultValue);
        }
        return value;
    }


    /**
     * Helper method to write a boolean value to {@link SharedPreferences}.
     *
     * @param context a {@link Context} object.
     * @param key
     * @param value
     * @return true if the new value was successfully written to persistent storage.
     */
    public static boolean setBooleanPreference(Context context, String key, boolean value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(key, value);
            return editor.commit();
        }
        return false;
    }

    /**
     * Helper method to retrieve a StringSet value from {@link SharedPreferences}.
     *
     * @param context      a {@link Context} object.
     * @param key
     * @param defaultValue A default to return if the value could not be read.
     * @return The value from shared preferences, or the provided default.
     */
    public static Set<String> getStringSetPreference(Context context, String key, Set<String> defaultValue) {
        Set<String> value = defaultValue;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            value = preferences.getStringSet(key, defaultValue);
        }
        return value;
    }

    /**
     * Helper method to write a boolean value to {@link SharedPreferences}.
     *
     * @param context a {@link Context} object.
     * @param key
     * @param value
     * @return true if the new value was successfully written to persistent storage.
     */
    public static boolean setStringSetPreference(Context context, String key, Set<String> value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putStringSet(key, value);
            return editor.commit();
        }
        return false;
    }

    /**
     * @param context
     * @param userResponse
     */
    public static void saveLoggedUserObject(Context context, UserResponse userResponse) {
        Gson gson = new Gson();
        String userObject = gson.toJson(userResponse);
        setStringPreference(context, AppConstants.Preferences.USER_OBJECT, userObject);
    }

    /**
     * @param context
     * @return
     */
    public static UserResponse getLoggedUserObject(Context context) {
        Gson gson = new Gson();
        String userJson = getStringPreference(context, AppConstants.Preferences.USER_OBJECT);
        UserResponse userResponse = gson.fromJson(userJson, UserResponse.class);
        return userResponse;
    }

    public static void saveReferrerCode(Context context, String referralCode) {
        setStringPreference(context, AppConstants.Preferences.REFERRAL_CODE, referralCode);
    }

    public static String getReferrerCode(Context context) {
        String s = getStringPreference(context, AppConstants.Preferences.REFERRAL_CODE);
        if (s == null)
            s = "";

        return s;
    }

    /**
     * @param context
     * @param userApiKey
     */
    public static void saveUserApiKey(Context context, String userApiKey) {
        Gson gson = new Gson();
        String userJson = getStringPreference(context, AppConstants.Preferences.USER_OBJECT);
        UserResponse userResponse = gson.fromJson(userJson, UserResponse.class);
        userResponse.getValue().setUserApiKey(userApiKey);
        String userObject = gson.toJson(userResponse);
        setStringPreference(context, AppConstants.Preferences.USER_OBJECT, userObject);
    }

    /**
     * @param context
     */
    public static void saveBankDetailsDone(Context context) {
        Gson gson = new Gson();
        String userJson = getStringPreference(context, AppConstants.Preferences.USER_OBJECT);
        UserResponse userResponse = gson.fromJson(userJson, UserResponse.class);
        userResponse.getValue().setBankDetailsAvailable("1");
        String userObject = gson.toJson(userResponse);
        setStringPreference(context, AppConstants.Preferences.USER_OBJECT, userObject);
    }


    /**
     * @param context
     * @return
     */
    public static String getUserApiKey(Context context) {
        Gson gson = new Gson();
        String userJson = getStringPreference(context, AppConstants.Preferences.USER_OBJECT);
        UserResponse userResponse = gson.fromJson(userJson, UserResponse.class);
        return userResponse.getValue().getUserApiKey();
    }


    /**
     * @param context
     * @param fcmToken
     */
    public static void saveFCMToken(Context context, String fcmToken) {
        setStringPreference(context, AppConstants.Preferences.FCM_TOKEN, fcmToken);
    }

    /**
     * @param context
     * @return
     */
    public static String getFCMToken(Context context) {
        return getStringPreference(context, AppConstants.Preferences.FCM_TOKEN);
    }

    /**
     * @param context
     * @return
     */
    public static int getNotificationCount(Context context) {
        return getIntegerPreference(context, AppConstants.Preferences.NOTIFICATION_COUNT,0);
    }


    /**
     * @param context
     * @param count
     */
    public static void setNotificationCount(Context context, int count) {
        setIntegerPreference(context, AppConstants.Preferences.NOTIFICATION_COUNT, count);
    }


    /**
     * @param context
     * @param list
     */
    public static void saveNotificationObject(Context context, String list) {
        setStringPreference(context, AppConstants.Preferences.NOTIFICATION_OBJECT, list);
    }

    /**
     * @param context
     * @return
     */
    public static String getNotificationObject(Context context) {
        return getStringPreference(context, AppConstants.Preferences.NOTIFICATION_OBJECT);
    }

    /**
     * @param context
     */
    public static void clear(Context context) {
        FileHelper.clear(context);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.commit();
        }
    }
}
