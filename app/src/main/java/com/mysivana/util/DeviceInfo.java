package com.mysivana.util;

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
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;

/**
 * Class which gives all the information about the device where the application is installed
 */
public class DeviceInfo {
    public static String getAndroidID(Context context) {
        String result = null;
        try {
            result = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;
    }

    /**
     * Gets model.
     *
     * @return the model
     */
    public static String getModel() {
        String result = null;
        try {
            result = Build.MODEL;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result == null || result.isEmpty()) {
            result = null;
        }
        return handleIllegalCharacterInResult(result.toString());
    }

    public static String handleIllegalCharacterInResult(String resultVal)

    {
        String result = resultVal;
        if (result.indexOf(" ") > 0) {
            result = result.replace(" ", "_");
        }
        return result;
    }

    /**
     * Gets build brand.
     *
     * @return the build brand
     */
    public static String getBuildBrand() {
        String result = null;
        try {
            result = Build.BRAND;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result == null || result.isEmpty()) {
            result = null;
        }
        return handleIllegalCharacterInResult(result.toString());
    }

    /**
     * Gets build host.
     *
     * @return the build host
     */
    public static String getBuildHost()

    {
        String result = null;
        try {
            result = Build.HOST;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result == null || result.isEmpty()) {
            result = null;
        }
        return result.toString();
    }

    /**
     * Gets build tags.
     *
     * @return the build tags
     */

    public static String getBuildTags()

    {
        String result = null;
        try {
            result = Build.TAGS;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result == null || result.isEmpty()) {
            result = null;
        }
        return result.toString();
    }

    /**
     * Gets build time.
     *
     * @return the build time
     */

    public static Long getBuildTime()

    {
        long result = 0;
        try {
            result = Build.TIME;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Gets build user.
     *
     * @return the build user
     */

    public static String getBuildUser()

    {
        String result = null;
        try {
            result = Build.USER;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result == null || result.isEmpty()) {
            result = null;
        }
        return result.toString();
    }

    ///////////////////////

    public static String getBuildVersionRelease()

    {
        String result = null;
        try {
            result = Build.VERSION.RELEASE;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result == null || result.isEmpty()) {
            result = null;
        }
        return result.toString();
    }

    /**
     * Gets screen display id.
     *
     * @return the screen display id
     */
    //TODO getScreenDisplayID
    @SuppressLint("ServiceCast")
    public static String getScreenDisplayID(Context context)

    {
        String result = null;
        try {
            Display display = (Display) context.getSystemService(Context.WINDOW_SERVICE);
            result = String.valueOf(display.getDisplayId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result == null || result.isEmpty()) {
            result = null;
        }
        return result.toString();
    }

    /**
     * Gets build version codename.
     *
     * @return the build version codename
     */

    public static String getBuildVersionCodename()

    {
        String result = null;
        try {
            result = Build.VERSION.CODENAME;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result == null || result.isEmpty()) {
            result = null;
        }
        return result.toString();
    }

    /**
     * Gets build version incremental.
     *
     * @return the build version incremental
     */

    public static String getBuildVersionIncremental()

    {
        String result = null;
        try {
            result = Build.VERSION.INCREMENTAL;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result == null || result.isEmpty()) {
            result = null;
        }
        return result.toString();
    }

    /**
     * Gets build version sdk.
     *
     * @return the build version sdk
     */

    public static int getBuildVersionSDK()

    {
        int result = 0;
        try {
            result = Build.VERSION.SDK_INT;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Gets build id.
     *
     * @return the build id
     */

    public static String getBuildID()

    {
        String result = null;
        try {
            result = Build.ID;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result == null || result.isEmpty()) {
            result = null;
        }
        return result.toString();
    }


    /**
     * Gets manufacturer.
     *
     * @return the manufacturer
     */

    public static String getManufacturer()

    {
        String result = null;
        try {
            result = Build.MANUFACTURER;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result == null || result.isEmpty()) {
            result = null;
        }
        return handleIllegalCharacterInResult(result.toString());
    }

    /**
     * Gets resolution.
     *
     * @return the resolution
     */
    //TODO
    @SuppressLint("ServiceCast")

    public static String getResolution(Context context)

    {
        String result = "";
        try {

            Display display = (Display) context.getSystemService(Context.WINDOW_SERVICE);

            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            result = "${metrics.heightPixels} x ${metrics.widthPixels}";
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result.isEmpty()) {
            result = "";
        }
        return result;
    }

    /**
     * Gets device.
     *
     * @return the device
     */

    public static String getDevice()

    {
        String result = null;
        try {
            result = Build.DEVICE;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result == null || result.isEmpty()) {
            result = null;
        }
        return result.toString();
    }


    /**
     * Gets os version.
     *
     * @return the os version
     */

    public static String getOSVersion()

    {
        String result = null;
        try {
            result = Build.VERSION.RELEASE;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result == null || result.isEmpty()) {
            result = null;
        }
        return result.toString();
    }


    /**
     * Gets imei.
     *
     * @return the imei
     */

    public static String getIMEI(Context context)

    {
        String result = null;
        boolean hasReadPhoneStatePermission = context.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            if (hasReadPhoneStatePermission) result = tm.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result == null || result.isEmpty()) {
            result = null;
        }
        return result.toString();
    }

    /**
     * Gets imsi.
     *
     * @return the imsi
     */

    public static String getIMSI(Context context)

    {
        String result = null;
        boolean hasReadPhoneStatePermission = context.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            if (hasReadPhoneStatePermission) result = tm.getSubscriberId();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result == null || result.isEmpty()) {
            result = null;
        }
        return result.toString();
    }


    /**
     * Gets phone no.
     *
     * @return the phone no
     */
    @SuppressLint("MissingPermission")

    public static String getPhoneNo(Context context) {
        String result = null;
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm.getLine1Number() != null) {
                result = tm.getLine1Number();
                if (result == "") {
                    result = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result.length() == 0) {
            result = null;
        }
        return result.toString();
    }

    /**
     * Gets product.
     *
     * @return the product
     */

    public static String getProduct()

    {
        String result = null;
        try {
            result = Build.PRODUCT;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result == null || result.isEmpty()) {
            result = null;
        }
        return result.toString();
    }

    /**
     * Gets fingerprint.
     *
     * @return the fingerprint
     */

    public static String getFingerprint()

    {
        String result = null;
        try {
            result = Build.FINGERPRINT;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result == null || result.isEmpty()) {
            result = null;
        }
        return result.toString();
    }

    /**
     * Gets hardware.
     *
     * @return the hardware
     */

    public static String getHardware()

    {
        String result = null;
        try {
            result = Build.HARDWARE;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result == null || result.isEmpty()) {
            result = null;
        }
        return result.toString();
    }

    /**
     * Gets radio ver.
     *
     * @return the radio ver
     */

    public static String getRadioVer()

    {
        String result = null;
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                result = Build.getRadioVersion();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result == null || result.isEmpty()) {
            result = null;
        }
        return result.toString();
    }


    /**
     * Gets app version.
     *
     * @return the app version
     */

    public static String getAppVersion(Context context) {
        String result = null;
        try {
            result = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (result == null || result.isEmpty()) {
            result = null;
        }
        return result.toString();
    }

    /**
     * Gets app version code.
     *
     * @return the app version code
     */

    public static String getAppVersionCode(Context context)

    {
        String result = null;
        try {
            result = String.valueOf(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (result == null || result.length() == 0) {
            result = null;
        }
        return result.toString();
    }

    /**
     * Gets package name.
     *
     * @return the package name
     */

    public static String getPackageName(Context context) {
        String result = null;
        try {
            result = context.getPackageName();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (result == null || result.isEmpty()) {
            result = null;
        }
        return result.toString();
    }

    /**
     * Is network available boolean.
     *
     * @return the boolean
     */
    //TODO
    @SuppressLint("ServiceCast")

    public static boolean isNetworkAvailable(Context context)

    {
        if (context.checkCallingOrSelfPermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED && context.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED) {
            NetworkInfo netInfo = (NetworkInfo) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return netInfo != null && netInfo.isConnected();
        }
        return false;
    }


}