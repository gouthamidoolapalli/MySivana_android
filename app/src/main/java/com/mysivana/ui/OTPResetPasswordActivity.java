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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mysivana.R;
import com.mysivana.custom.MSOTPView;
import com.mysivana.mvp.BaseMvpActivity;
import com.mysivana.mvp.MainMvpPresenter;
import com.mysivana.mvp.MainMvpView;
import com.mysivana.mvp.model.post.PostCreateUser;
import com.mysivana.util.AppConstants;
import com.mysivana.util.CommonUtils;
import com.mysivana.util.FirebaseConstants;
import com.mysivana.util.UIUtils;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;

public class OTPResetPasswordActivity extends BaseMvpActivity<MainMvpPresenter> implements MainMvpView {


    private FirebaseAuth mAuth;
    private final String TAG = OTPVerifyActivity.class.getSimpleName();
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String phoneNumber;
    private String mVerificationId;
    private PostCreateUser postCreateUser;

    @BindView(R.id.otpview)
    MSOTPView mOTPView;

    public static final String FORGOT_PASSWORD = "Forgot Password";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int setContentLayoutId() {
        return R.layout.activity_otp_verify;
    }

    @Override
    protected void init() {


        mFirebaseAnalytics.setCurrentScreen(this, FirebaseConstants.LAUNCH_OTP_FORGOT_PWD, null);

        Bundle bundle = getIntent().getExtras();
        postCreateUser = bundle.getParcelable(AppConstants.BundleParams.CREATE_USER);
        String countryCode = postCreateUser.getCountryCode() != null ? postCreateUser.getCountryCode() : "+91";
        phoneNumber = countryCode + postCreateUser.getPhoneNumber();
        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted:" + phoneAuthCredential);
                signInWithPhoneAuthCredential(phoneAuthCredential);
                hideLoading();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.d(TAG, "FirebaseException::::" + e.getMessage());
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Log.d(TAG, "Invalid phone number:::::");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Log.d(TAG, "FirebaseTooManyRequestsException:::::");
                }
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                hideLoading();
                mVerificationId = verificationId;
                mResendToken = forceResendingToken;
                Bundle b = new Bundle();
                b.putString(FirebaseConstants.PARAM_OTP_TOKEN, mVerificationId);
                mFirebaseAnalytics.logEvent(FirebaseConstants.OTP_TOKEN, b);
            }
        };

        sendSMS(phoneNumber);

    }

    @Override
    protected MainMvpPresenter createPresenter() {
        return new MainMvpPresenter(this);
    }

    /**
     * trying to verify phone number and send OTP
     *
     * @param phoneNumber phone number with country code
     */
    private void sendSMS(String phoneNumber) {
        showLoading();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);
    }


    /**
     * trying to verify phone number and send OTP
     *
     * @param phoneNumber phone number with country code
     */
    private void reSendOTP(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        showLoading();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,
                token);
    }


    /**
     * called when a code is sent to the user to verify and signIn using those credentials
     *
     * @param verificationId verification id received in callback
     * @param code           user entered code
     */
    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    /**
     * SignIn when code is sent to the user or user is auto verified
     *
     * @param credential PhoneAuthCredential which is taken from OnVerificationStateChangedCallbacks callback or created from code
     */
    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                hideLoading();
                if (task.isSuccessful()) {
                    String otp = mOTPView.getOTP();
                    if(otp == null || TextUtils.isEmpty(otp)){
                        otp = credential.getSmsCode();
                    }
                    postCreateUser.setOtpCode(otp);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(AppConstants.BundleParams.CREATE_USER, postCreateUser);
                    bundle.putString(AppConstants.NAVIGATE_FROM, FORGOT_PASSWORD);
                    CommonUtils.startActivity(OTPResetPasswordActivity.this, NewPasswordActivity.class, bundle);
                    finish();
                } else {
                    mOTPView.clearOTP();
                    UIUtils.showToast(OTPResetPasswordActivity.this, getString(R.string.invalid_otp));
                }
            }
        });
    }

    public void onClickContinue(View view) {
        Bundle b = new Bundle();
        mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_CONTINUE_OTP, b);

        String code = mOTPView.getOTP();
        if (!TextUtils.isEmpty(code)) {
            showLoading();
            verifyPhoneNumberWithCode(mVerificationId, code);
        }else {
            UIUtils.showToast(OTPResetPasswordActivity.this, getString(R.string.please_enter_otp));
        }
    }

    public void onClickResend(View view) {
        Bundle b = new Bundle();
        mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_RESEND_OTP, b);
        reSendOTP(phoneNumber, mResendToken);
        UIUtils.showToast(OTPResetPasswordActivity.this, getString(R.string.otp_resent));
    }


    @Override
    public void onDataSuccess(Object object, int apiRequestCode) {

    }

    @Override
    public void onDataFailure(Throwable e) {
    }


}
