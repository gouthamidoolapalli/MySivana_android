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
package com.mysivana.mvp.model;

import android.content.Context;
import android.content.res.Resources;

import java.util.Locale;

public class Country {

  private final String mCode;

  private final String mName;

  private final int mDialCode;

  public Country(String code, String name, int dialCode) {
    mCode = code;
    mName = name;
    mDialCode = dialCode;
  }

  public String getCode() {
    return mCode;
  }

  public String getName() {
    return mName;
  }

  public int getDialCode() {
    return mDialCode;
  }

  public String getDisplayName() {
    return new Locale("", mCode).getDisplayCountry(Locale.US);
  }

  public int getResId(Context context) {
    String name = String.format("country_flag_%s",mCode.toLowerCase());
    final Resources resources = context.getResources();
    return resources.getIdentifier(name, "drawable", context.getPackageName());
  }
}
