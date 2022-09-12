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

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.mysivana.R;
import com.mysivana.custom.MSTextView;
import com.mysivana.mvp.BaseMvpActivity;
import com.mysivana.mvp.MainMvpPresenter;
import com.mysivana.mvp.MainMvpView;
import com.mysivana.mvp.model.GenericResponse;
import com.mysivana.mvp.model.post.PostCreateUser;
import com.mysivana.util.AppConstants;
import com.mysivana.util.CommonUtils;
import com.mysivana.util.CryptoUtil;
import com.mysivana.util.FirebaseConstants;
import com.mysivana.util.NetworkUtil;
import com.mysivana.util.SharedPrefsUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;

import butterknife.BindView;

public class NewPasswordActivity extends BaseMvpActivity<MainMvpPresenter> implements MainMvpView {

    @BindView(R.id.et_password)
    EditText mPassword;
    @BindView(R.id.et_confirmpassword)
    EditText mConfirmPassword;

    @BindView(R.id.screen_title)
    MSTextView mScreenTitle;
    @BindView(R.id.ti_current_password)
    TextInputLayout mCurrentPasswordTIL;
    @BindView(R.id.et_current_password)
    EditText mCurrentPassword;
    @BindView(R.id.current_password_divider)
    View mCurrentPasswordDivider;
    @BindView(R.id.error_text)
    MSTextView mErrorText;

    private PostCreateUser postCreateUser;
    private String navigateFrom;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int setContentLayoutId() {
        return R.layout.activity_new_password;
    }

    @Override
    protected void init() {
        setSupportActionBar();
        Bundle bundle = getIntent().getExtras();
        postCreateUser = bundle.getParcelable(AppConstants.BundleParams.CREATE_USER);
        navigateFrom = bundle.getString(AppConstants.NAVIGATE_FROM);
        mScreenTitle.setText(navigateFrom);
        mFirebaseAnalytics.setCurrentScreen(this, FirebaseConstants.LAUNCH_NEW_PWD,null);

        if (navigateFrom.equalsIgnoreCase(UserProfileActivity.CHANGE_PASSWORD)) {
            showCurrentPasswordLayout();
        } else {
            hideCurrentPasswordLayout();
        }

        mCurrentPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mCurrentPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        mPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        mConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

    }

    /**
     * Hide Current Password Layout
     */
    private void hideCurrentPasswordLayout() {
        mCurrentPasswordTIL.setVisibility(View.GONE);
        mCurrentPasswordDivider.setVisibility(View.GONE);
    }

    /**
     * Show Current Password Layout
     */
    private void showCurrentPasswordLayout() {
        mCurrentPasswordTIL.setVisibility(View.VISIBLE);
        mCurrentPasswordDivider.setVisibility(View.VISIBLE);
    }

    @Override
    protected MainMvpPresenter createPresenter() {
        return new MainMvpPresenter(this);
    }

    public void onClickSave(View v) {
        mErrorText.setVisibility(View.INVISIBLE);
        String password = mPassword.getText().toString();
        String confirmPassword = mConfirmPassword.getText().toString();
        String currentPassword = mCurrentPassword.getText().toString();

        if (navigateFrom.equalsIgnoreCase(UserProfileActivity.CHANGE_PASSWORD)) {

            if (!TextUtils.isEmpty(currentPassword)) {

                Bundle b = new Bundle();
                mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_SEND_NEW_PWD, b);


                postCreateUser.setOldPassword(CryptoUtil.encryptWithMD5(currentPassword));
                validatePassword(password, confirmPassword);
            } else {
                mErrorText.setVisibility(View.VISIBLE);
                mErrorText.setText(getString(R.string.all_fields_mandatory));
            }
        } else {
            validatePassword(password, confirmPassword);
        }
    }

    /**
     * validate the password with following conditions
     *
     * @param password        password given
     * @param confirmPassword confirmed password
     */
    private void validatePassword(String password, String confirmPassword) {

        if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(password)) {
            if (password.length() < 6) {
                mErrorText.setVisibility(View.VISIBLE);
                mErrorText.setText(getString(R.string.validation_password_min_char));
            } else if (!password.equals(confirmPassword)) {
                mErrorText.setVisibility(View.VISIBLE);
                mErrorText.setText(getString(R.string.validation_confirm_password));
            } else {
                postCreateUser.setPassword(CryptoUtil.encryptWithMD5(password));
                if (navigateFrom.equalsIgnoreCase(UserProfileActivity.CHANGE_PASSWORD)) {
                    postCreateUser.setUserApiKey(SharedPrefsUtils.getUserApiKey(this));
                }
                if (NetworkUtil.getConnectivityStatus(this)) {
                    mErrorText.setVisibility(View.INVISIBLE);
                    mPresenter.changePassword(postCreateUser);
                } else {
                    mErrorText.setVisibility(View.VISIBLE);
                    mErrorText.setText(getString(R.string.internet_connection_unavailable));
                }
            }
        } else {
            mErrorText.setVisibility(View.VISIBLE);
            mErrorText.setText(getString(R.string.all_fields_mandatory));
        }
    }

    @Override
    public void onDataSuccess(Object object, int apiRequestCode) {
        GenericResponse genericResponse = (GenericResponse) object;
        mErrorText.setVisibility(View.INVISIBLE);
        int code = genericResponse.getErrorCode();
        switch (code) {
            case AppConstants.SUCCESS_RESPONSE_CODE:
                if (!navigateFrom.equalsIgnoreCase(UserProfileActivity.CHANGE_PASSWORD)) {
//                    UIUtils.showToast(NewPasswordActivity.this, genericResponse.getValue());
                    CommonUtils.startActivity(this, LoginActivity.class, null, Intent.FLAG_ACTIVITY_CLEAR_TOP, CommonUtils.DEFAULT);
                }
                finish();
                break;
            case AppConstants.BAD_CREDENTIALS_CODE:
                logOutFromApp();
                break;
            case AppConstants.HTTP_500_ERROR_CODE:
            case AppConstants.VALIDATION_ERROR_CODE:
                mErrorText.setVisibility(View.VISIBLE);
                mErrorText.setText(genericResponse.getErrorDescription());
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
}
