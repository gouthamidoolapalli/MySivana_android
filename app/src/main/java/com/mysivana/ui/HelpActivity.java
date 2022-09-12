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
package com.mysivana.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.mysivana.R;
import com.mysivana.custom.MSEditText;
import com.mysivana.mvp.BaseMvpActivity;
import com.mysivana.mvp.MainMvpPresenter;
import com.mysivana.mvp.MainMvpView;
import com.mysivana.mvp.model.GenericResponse;
import com.mysivana.mvp.model.HelpFeedbackRequest;
import com.mysivana.mvp.model.UserResponse;
import com.mysivana.util.AppConstants;
import com.mysivana.util.FirebaseConstants;
import com.mysivana.util.NetworkUtil;
import com.mysivana.util.PermissionUtil;
import com.mysivana.util.SharedPrefsUtils;
import com.mysivana.util.UIUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HelpActivity extends BaseMvpActivity<MainMvpPresenter> implements MainMvpView {
    @BindView(R.id.et_feedback)
    MSEditText etFeedback;
    @BindView(R.id.error_text)
    TextView mErrorText;
    @BindView(R.id.til_feedback)
    TextInputLayout mTILFeedback;

    String errorMessage;

    @Override
    protected int setContentLayoutId() {
        return R.layout.activity_help;
    }

    @Override
    protected void init() {
        mFirebaseAnalytics.setCurrentScreen(this, FirebaseConstants.LAUNCH_HELP,null);
        setSupportActionBar();
        errorMessage = getIntent().getStringExtra(AppConstants.ERROR_MESSAGE_FEEDBACK);
        mTILFeedback.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etFeedback, InputMethodManager.SHOW_IMPLICIT);
                return false;
            }
        });

    }

    @Override
    protected MainMvpPresenter createPresenter() {
        return new MainMvpPresenter(this);
    }

    @Override
    public void onDataSuccess(Object object, int apiRequestCode) {
        GenericResponse response = (GenericResponse) object;
        int code = response.getErrorCode();
        switch (code) {
            case AppConstants.SUCCESS_RESPONSE_CODE:
                UIUtils.showToast(this, response.getValue());
                finish();
                break;
            case AppConstants.BAD_CREDENTIALS_CODE:
                logOutFromApp();
                break;
            case AppConstants.HTTP_500_ERROR_CODE:
            case AppConstants.VALIDATION_ERROR_CODE:
                mErrorText.setVisibility(View.VISIBLE);
                mErrorText.setText(response.getValue());
                break;
        }

    }

    @Override
    public void onDataFailure(Throwable e) {
        mErrorText.setVisibility(View.VISIBLE);

        if (e instanceof HttpException) {
            //HTTP exceptions of non 200 type.
            int code = ((HttpException) e).code();
            if (code == 500) {
                if (errorCounter >= 5) {
                    submitTicket = new SubmitTicket(this, code + ", " + ((HttpException) e).response().toString());
                    submitTicket.execute(0);
                    errorCounter = 0;
                } else {
                    mErrorText.setText(getString(R.string.internal_server_error));
                    errorCounter++;
                }

            } else if (code == 401) {
                //Bad credentials error
                logOutFromApp();
            }
        } else if (e instanceof SocketTimeoutException) {
            //connection establishment failure exception (might have slow internet or server unavailable)
            mErrorText.setText(getString(R.string.something_wrong_try_again));
        } else if (e instanceof IOException) {
            //called when there's network problem
            mErrorText.setText(getString(R.string.internet_connection_unavailable));
        } else {
            //other messages
            mErrorText.setText(e.getMessage());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    public void onClickSend(View view) {
        String feedback = etFeedback.getText().toString();
        mErrorText.setVisibility(View.INVISIBLE);
        if (!TextUtils.isEmpty(feedback)) {

            UserResponse userResponse = SharedPrefsUtils.getLoggedUserObject(this);

            //log Analytics
            Bundle b = new Bundle();
            b.putString(FirebaseConstants.PARAM_USER_ID, userResponse.getValue().getUserId());
            mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_SEND_HELP,b);

            HelpFeedbackRequest request = new HelpFeedbackRequest();
            request.setUserId(String.valueOf(userResponse.getValue().getUserId()));
            if (errorMessage != null & !TextUtils.isEmpty(errorMessage)) {
                feedback = feedback + "\n Error message: " + errorMessage;
            }
            request.setMessage(feedback);
            request.setUserApiKey(userResponse.getValue().getUserApiKey());
            if (NetworkUtil.getConnectivityStatus(this)) {
                mErrorText.setVisibility(View.INVISIBLE);
                mPresenter.saveFeedbackDetails(request);
            }else{
                mErrorText.setVisibility(View.VISIBLE);
                mErrorText.setText(getString(R.string.internet_connection_unavailable));
            }
        } else {
            mErrorText.setText(getString(R.string.write_feedback));
            mErrorText.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.tv_call_us)
    public void onClickCallUs(View view) {
        //log Analytics
        UserResponse userResponse = SharedPrefsUtils.getLoggedUserObject(this);
        Bundle b = new Bundle();
        b.putString(FirebaseConstants.PARAM_USER_ID, userResponse.getValue().getUserId());
        mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_CALL,b);

        if (!PermissionUtil.hasPermission(this, android.Manifest.permission.CALL_PHONE)) {
            PermissionUtil.checkPermission(HelpActivity.this, android.Manifest.permission.CALL_PHONE, 1, "", "", new PermissionUtil.ReqPermissionCallback() {
                @SuppressLint("MissingPermission")
                @Override
                public void onResult(boolean success) {
                    if (success) {
                        callCustomerCare();
                    }
                }
            });
        } else {
            callCustomerCare();
        }
    }

    /**
     * Calls to the desired phone number as a helpline
     */
    @SuppressLint("MissingPermission")
    public void callCustomerCare() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:+918885053541"));
        startActivity(callIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.onRequestPermissionResult(this, requestCode, permissions, grantResults);
    }
}
