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
package com.mysivana.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import com.mysivana.R;
import com.mysivana.mvp.BaseMvpActivity;
import com.mysivana.mvp.MainMvpPresenter;
import com.mysivana.mvp.MainMvpView;
import com.mysivana.util.AppConstants;
import com.mysivana.util.CommonUtils;
import com.mysivana.util.FirebaseConstants;

import butterknife.BindView;

public class WelcomeAfterLoginActivity extends BaseMvpActivity<MainMvpPresenter> implements MainMvpView {
    @BindView(R.id.btn_sign_in)
    TextView mBtnSignIn;
    @BindView(R.id.note_1)
    TextView mNote1;
    @BindView(R.id.note_2)
    TextView mNote2;
    @BindView(R.id.note_3)
    TextView mNote3;


    public static final String USER_TYPE = "UserType";
    String getMail;

    @Override
    protected int setContentLayoutId() {
        return R.layout.activity_welcome_note;
    }


    @Override
    protected void init() {

        mFirebaseAnalytics.setCurrentScreen(this, FirebaseConstants.LAUNCH_WELCOME_PAGE, null);
        getMail = getString(R.string.mail_feedback);

        String userType = getIntent().getStringExtra(USER_TYPE);
        if (userType.equalsIgnoreCase(AppConstants.USER_TYPE_MERCHANT)) {
            mNote1.setText(getString(R.string.welcome_note_merchant_1));
            mNote2.setText(getString(R.string.welcome_note_merchant_2));
            String note3Text = getString(R.string.welcome_note_merchant_3, getMail);
            int thisLinkIndex = note3Text.indexOf(getMail);
            SpannableString spannableString = new SpannableString(note3Text);
            spannableString.setSpan(new ClickableSpan() {
                                        @Override
                                        public void onClick(View widget) {
                                            sendEmail();
                                        }

                                        @Override
                                        public void updateDrawState(TextPaint ds) {
                                            super.updateDrawState(ds);
                                            ds.setUnderlineText(true);
                                        }
                                    },
                    thisLinkIndex, thisLinkIndex + getMail.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            mNote3.setText(spannableString, TextView.BufferType.SPANNABLE);
            mNote3.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            mNote1.setText(getString(R.string.welcome_note_1));
            mNote2.setText(getString(R.string.welcome_note_2));
            String note3Text = getString(R.string.welcome_note_3, getMail);
            int thisLinkIndex = note3Text.indexOf(getMail);
            SpannableString spannableString = new SpannableString(note3Text);
            spannableString.setSpan(new ClickableSpan() {
                                        @Override
                                        public void onClick(View widget) {
                                            sendEmail();
                                        }

                                        @Override
                                        public void updateDrawState(TextPaint ds) {
                                            super.updateDrawState(ds);
                                            ds.setUnderlineText(true);
                                        }
                                    },
                    thisLinkIndex, thisLinkIndex + getMail.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            mNote3.setText(spannableString, TextView.BufferType.SPANNABLE);
            mNote3.setMovementMethod(LinkMovementMethod.getInstance());
        }
        mBtnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_SIGN_IN, new Bundle());
                CommonUtils.startActivity(WelcomeAfterLoginActivity.this, LoginActivity.class, null, Intent.FLAG_ACTIVITY_CLEAR_TOP, CommonUtils.DEFAULT);
                finish();
            }
        });

    }

    private void sendEmail() {
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{
                getMail
        });
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "My Sivana feedback - Regarding");
        emailIntent.setType("message/rfc822");

        try {
            startActivity(Intent.createChooser(emailIntent,
                    "Send email using..."));
        } catch (Exception ex) {

        }

    }


    @Override
    protected MainMvpPresenter createPresenter() {
        return new MainMvpPresenter(this);
    }

    @Override
    public void onDataSuccess(Object object, int apiRequestCode) {

    }

    @Override
    public void onDataFailure(Throwable e) {

    }

    @Override
    public void onBackPressed() {
        mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_SIGN_IN, new Bundle());
        CommonUtils.startActivity(WelcomeAfterLoginActivity.this, LoginActivity.class, null, Intent.FLAG_ACTIVITY_CLEAR_TOP, CommonUtils.DEFAULT);
        finish();
    }
}
