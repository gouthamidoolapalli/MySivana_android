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

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;
/**
 * Set Font style nd family to the text in application
 */
public class FontCache {

    public static final int LIGHT = 0;
    public static final int ROMAN = 1;
    public static final int MEDIUM = 2;
    public static final int BOLD = 3;

    private static HashMap<String, Typeface> mFontCache = new HashMap<>();


    /**
     * @param context
     * @param typeValue
     * @return
     */
    public static Typeface getTypeface(Context context, int typeValue) {
        Typeface typeface = null;
        String typefaceName = getFontName(typeValue);
        if (mFontCache.containsKey(typefaceName)) {
            typeface = mFontCache.get(typefaceName);
        } else {
            typeface = Typeface.createFromAsset(context.getAssets(), "fonts/" + typefaceName);
            mFontCache.put(typefaceName, typeface);
        }
        return typeface;
    }

    /**
     * @param typeValue
     * @return
     */
    private static String getFontName(int typeValue) {
        switch (typeValue) {

            case LIGHT:
                return "museosans_300.ttf";
            case ROMAN:
                return "museosans_500.ttf";
            case MEDIUM:
                return "museosans_700.ttf";
            case BOLD:
                return "museosans_900.ttf";
            default:
                return "museosans_300.ttf";
        }
    }
}
