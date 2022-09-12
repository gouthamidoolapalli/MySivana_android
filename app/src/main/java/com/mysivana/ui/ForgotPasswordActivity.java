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

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.mysivana.R;
import com.mysivana.custom.MSTextView;
import com.mysivana.mvp.BaseMvpActivity;
import com.mysivana.mvp.MainMvpPresenter;
import com.mysivana.mvp.MainMvpView;
import com.mysivana.mvp.model.UserResponse;
import com.mysivana.mvp.model.post.PostCreateUser;
import com.mysivana.util.AppConstants;
import com.mysivana.util.CommonUtils;
import com.mysivana.util.DeviceInfo;
import com.mysivana.util.FirebaseConstants;
import com.mysivana.util.NetworkUtil;
import com.mysivana.util.PermissionUtil;

import java.io.IOException;
import java.net.SocketTimeoutException;

import butterknife.BindView;

public class ForgotPasswordActivity extends BaseMvpActivity<MainMvpPresenter> implements MainMvpView {


    @BindView(R.id.et_email_phone)
    EditText mEmail;
    @BindView(R.id.error_text)
    MSTextView mErrorText;


    private String mIMEINumber;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int setContentLayoutId() {
        return R.layout.activity_forget_password;
    }

    @Override
    protected void init() {
        mFirebaseAnalytics.setCurrentScreen(this, FirebaseConstants.LAUNCH_FORGOT_PWD,null);

        setSupportActionBar();
        requestPermissions();
    }

    @Override
    protected MainMvpPresenter createPresenter() {
        return new MainMvpPresenter(this);
    }

    /**
     * requesting permission
     */
    public void requestPermissions() {
        PermissionUtil.checkPermission(ForgotPasswordActivity.this, Manifest.permission.READ_PHONE_STATE, 1, "", "", new PermissionUtil.ReqPermissionCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onResult(boolean success) {
                if (success) {
                    mIMEINumber = DeviceInfo.getIMEI(ForgotPasswordActivity.this);
                }
            }
        });
    }

    public void onClickSend(View view) {
        mErrorText.setVisibility(View.INVISIBLE);
        String emailPhone = mEmail.getText().toString();
        if (!TextUtils.isEmpty(emailPhone)) {
            if (!Patterns.EMAIL_ADDRESS.matcher(emailPhone).matches() && !CommonUtils.isPhoneNumber(emailPhone)) {
                mErrorText.setVisibility(View.VISIBLE);
                mErrorText.setText(getString(R.string.validation_reset_email));
                return;
            }
            String email = (Patterns.EMAIL_ADDRESS.matcher(emailPhone).matches() ? emailPhone : "");
            String phoneNumber = CommonUtils.isPhoneNumber(emailPhone) ? emailPhone : "";
            if (mIMEINumber == null || mIMEINumber.isEmpty()) {
                requestPermissions();
                return;
            }


            //Log Analytics
            Bundle b = new Bundle();
            b.putString(FirebaseAnalytics.Param.ITEM_ID,email);
            mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_SEND_PWD, b);

            if (NetworkUtil.getConnectivityStatus(this)) {
                mErrorText.setVisibility(View.INVISIBLE);
                mPresenter.checkUserExists(email, phoneNumber, AppConstants.ActivityType.FORGOT_PASSWORD);
            }else{
                mErrorText.setVisibility(View.VISIBLE);
                mErrorText.setText(getString(R.string.internet_connection_unavailable));
            }
        } else {
            mErrorText.setVisibility(View.VISIBLE);
            mErrorText.setText(getString(R.string.validation_reset_email));
        }

    }


    @Override
    public void onDataSuccess(Object object, int apiRequestCode) {
        mErrorText.setVisibility(View.INVISIBLE);
        UserResponse userResponse = (UserResponse) object;
        int code = userResponse.getErrorCode();
        switch (code) {
            case AppConstants.SUCCESS_RESPONSE_CODE:
                PostCreateUser postCreateUser = new PostCreateUser();
                postCreateUser.setUserID(String.valueOf(userResponse.getValue().getUserId()));
                postCreateUser.setUserApiKey(userResponse.getValue().getUserApiKey());
                postCreateUser.setEmail(userResponse.getValue().getEmail());
                postCreateUser.setPhoneNumber(userResponse.getValue().getPhoneNumber());
                postCreateUser.setCountryCode(userResponse.getValue().getCountryCode());
                postCreateUser.setOsVersion(DeviceInfo.getOSVersion());
                postCreateUser.setApiVersion(DeviceInfo.getAppVersion(this));
                postCreateUser.setDeviceBrand(DeviceInfo.getBuildBrand());
                postCreateUser.setDeviceId(mIMEINumber);
                postCreateUser.setActivityType(AppConstants.ActivityType.FORGOT_PASSWORD);
                Bundle bundle = new Bundle();
                bundle.putParcelable(AppConstants.BundleParams.CREATE_USER, postCreateUser);
                CommonUtils.startActivity(this, OTPResetPasswordActivity.class, bundle);

                break;
            case AppConstants.VALIDATION_ERROR_CODE:
                mErrorText.setVisibility(View.VISIBLE);
                mErrorText.setText(userResponse.getErrorDescription());
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.onRequestPermissionResult(ForgotPasswordActivity.this, requestCode, permissions, grantResults);
    }

}
