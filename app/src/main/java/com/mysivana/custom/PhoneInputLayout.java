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
 * JUNE 06 2018      : BYNDR       : Implementation of PhoneField that uses {@link TextInputLayout}.
 * ------------------------------------------------------------------------
 *
 * ========================================================================
 */
package com.mysivana.custom;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;

import com.mysivana.R;
import com.mysivana.util.FontCache;

public class PhoneInputLayout extends PhoneField {

  private TextInputLayout mTextInputLayout;

  public PhoneInputLayout(Context context) {
    this(context, null);
  }

  public PhoneInputLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public PhoneInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override
  protected void updateLayoutAttributes() {
    setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT));
    setGravity(Gravity.TOP);
    setOrientation(HORIZONTAL);
  }

  @Override
  protected void prepareView() {
    super.prepareView();
    mTextInputLayout = (TextInputLayout) findViewWithTag(getResources().getString(R.string.com_lamudi_phonefield_til_phone));
    mTextInputLayout.setTypeface(FontCache.getTypeface(getContext(), 0));
  }

  @Override
  public int getLayoutResId() {
    return R.layout.phone_text_input_layout;
  }

  @Override
  public void setHint(int resId) {
    mTextInputLayout.setHint(getContext().getString(resId));
  }

  @Override
  public void setError(String error) {
    if (error == null || error.length() == 0) {
      mTextInputLayout.setErrorEnabled(false);
    } else {
      mTextInputLayout.setErrorEnabled(true);
    }
    mTextInputLayout.setError(error);
  }

  public TextInputLayout getTextInputLayout() {
    return mTextInputLayout;
  }
}
