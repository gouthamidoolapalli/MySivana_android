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

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.mysivana.R;
import com.mysivana.custom.MSPasswordEditText;
import com.mysivana.custom.MSTextView;
import com.mysivana.mvp.BaseMvpActivity;
import com.mysivana.mvp.MainMvpPresenter;
import com.mysivana.mvp.MainMvpView;
import com.mysivana.mvp.model.UserResponse;
import com.mysivana.mvp.model.post.PostUserLogin;
import com.mysivana.util.AppConstants;
import com.mysivana.util.CommonUtils;
import com.mysivana.util.CryptoUtil;
import com.mysivana.util.DeviceInfo;
import com.mysivana.util.FirebaseConstants;
import com.mysivana.util.NetworkUtil;
import com.mysivana.util.PermissionUtil;
import com.mysivana.util.SharedPrefsUtils;
import com.mysivana.util.UIUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;

import butterknife.BindView;

import static com.mysivana.util.AppConstants.USER_TYPE_MERCHANT;

public class LoginActivity extends BaseMvpActivity<MainMvpPresenter> implements MainMvpView {

    @BindView(R.id.et_email_phone)
    EditText mEmail;
    @BindView(R.id.et_password)
    MSPasswordEditText mPassword;
    private String mIMEINumber;
    @BindView(R.id.error_text)
    MSTextView mErrorText;
    @BindView(R.id.password_TIL)
    TextInputLayout passwordTIL;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int setContentLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void init() {
        mFirebaseAnalytics.setCurrentScreen(this, FirebaseConstants.LAUNCH_LOG_IN, null);

        mPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        requestPermissions();
    }

    public void requestPermissions() {
        if (!PermissionUtil.hasPermission(this, Manifest.permission.READ_PHONE_STATE)) {
            PermissionUtil.checkPermission(LoginActivity.this, Manifest.permission.READ_PHONE_STATE, 1, "", "", new PermissionUtil.ReqPermissionCallback() {
                @SuppressLint("MissingPermission")
                @Override
                public void onResult(boolean success) {
                    if (success) {
                        mIMEINumber = DeviceInfo.getIMEI(LoginActivity.this);
                    } else {
                        requestPermissions();
                    }

                }
            });
        } else {
            mIMEINumber = DeviceInfo.getIMEI(LoginActivity.this);
        }
    }

    @Override
    protected MainMvpPresenter createPresenter() {
        return new MainMvpPresenter(this);
    }

    public void onClickLogin(View view) {

        mErrorText.setVisibility(View.INVISIBLE);
        String emailPhone = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString();
        if (!TextUtils.isEmpty(emailPhone) && !TextUtils.isEmpty(password)) {
            if (!Patterns.EMAIL_ADDRESS.matcher(emailPhone).matches() && !CommonUtils.isPhoneNumber(emailPhone)) {
                mErrorText.setText(getString(R.string.validation_login_basic_invalid));
                mErrorText.setVisibility(View.VISIBLE);
                return;
            }
            PostUserLogin postUserLogin = new PostUserLogin();
            postUserLogin.setEmail(Patterns.EMAIL_ADDRESS.matcher(emailPhone).matches() ? emailPhone : null);
            postUserLogin.setPhoneNumber(CommonUtils.isPhoneNumber(emailPhone) ? emailPhone : null);
            postUserLogin.setPassword(CryptoUtil.encryptWithMD5(password));
            postUserLogin.setOsVersion(DeviceInfo.getOSVersion());
            postUserLogin.setApiVersion(DeviceInfo.getAppVersion(this));
            postUserLogin.setDeviceBrand(DeviceInfo.getBuildBrand());
            postUserLogin.setDeviceId(mIMEINumber);

            //Analytics log event
            Bundle b = new Bundle();
            b.putString(FirebaseConstants.PARAM_EMAIL, emailPhone);
            mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_SIGN_IN, b);

            if (NetworkUtil.getConnectivityStatus(this)) {
                mErrorText.setVisibility(View.INVISIBLE);
                mPresenter.doLogin(postUserLogin);

            } else {
                mErrorText.setVisibility(View.VISIBLE);
                mErrorText.setText(getString(R.string.internet_connection_unavailable));
            }
        } else {
            mErrorText.setVisibility(View.VISIBLE);
            mErrorText.setText(getString(R.string.validation_login_basic_invalid));
        }

    }

    private void clearData(){
        mEmail.setText("");
        mPassword.setText("");
        mErrorText.setVisibility(View.INVISIBLE);
    }


    public void onClickSignUp(View view) {
        //Analytics log event
        Bundle b = new Bundle();
        mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_SIGN_UP, b);

        clearData();
        showDailog(UIUtils.getStringFromResource(this, R.string.terms_conditions));

    }

    public void onClickForgotPassword(View view) {
        //Analytics log event
        Bundle b = new Bundle();
        mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_FORGOT_PWD, b);

        clearData();
        CommonUtils.startActivity(this, ForgotPasswordActivity.class);
    }

    /**
     * Needs to agree for the terms and conditions before creating an account with My Sivana
     *
     * @param stringFromResource header title
     */
    private void showDailog(String stringFromResource) {
        final Dialog dialog = new Dialog(this, R.style.AppTheme_FloatingDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_legal_section);

        ImageView mClose = dialog.findViewById(R.id.iv_close);
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        TextView titleHeader = dialog.findViewById(R.id.header_title);
        titleHeader.setText(stringFromResource);
        Button agreeBtn = dialog.findViewById(R.id.agree_btn);
        agreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                CommonUtils.startActivity(LoginActivity.this, CreateAccountActivity.class);
            }
        });

        WebView webView = dialog.findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewController(agreeBtn));
        webView.loadUrl("file:///android_asset/html/terms.html");


        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        dialog.show();
    }

    public class WebViewController extends WebViewClient {
        Button agreeBtn;

        public WebViewController(Button agreeBtn) {
            this.agreeBtn = agreeBtn;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            agreeBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void
    onDataSuccess(Object object, int apiRequestCode) {
        UserResponse userResponse = (UserResponse) object;
        int code = userResponse.getErrorCode();
        switch (code) {
            case AppConstants.SUCCESS_RESPONSE_CODE:
                UserResponse.Value value = userResponse.getValue();
                value.setOsVersion(DeviceInfo.getOSVersion());
                value.setApiVersion(DeviceInfo.getAppVersion(this));
                value.setDeviceBrand(DeviceInfo.getBuildBrand());
                value.setDeviceId(mIMEINumber);
                SharedPrefsUtils.saveLoggedUserObject(this, userResponse);
                if (value.getUserType().equalsIgnoreCase(USER_TYPE_MERCHANT)) {
                    CommonUtils.startActivity(this, DashboardMerchantActivity.class);
                } else {
                    CommonUtils.startActivity(this, DashboardUserActivity.class);
                }
                finish();

                break;
            case AppConstants.BAD_CREDENTIALS_CODE:
                logOutFromApp();
                break;
            case AppConstants.HTTP_500_ERROR_CODE:
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
        PermissionUtil.onRequestPermissionResult(LoginActivity.this, requestCode, permissions, grantResults);
    }
}
