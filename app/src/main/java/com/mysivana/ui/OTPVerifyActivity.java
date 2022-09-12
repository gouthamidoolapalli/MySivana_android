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
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.mysivana.R;
import com.mysivana.custom.MSOTPView;
import com.mysivana.mvp.BaseMvpActivity;
import com.mysivana.mvp.MainMvpPresenter;
import com.mysivana.mvp.MainMvpView;
import com.mysivana.mvp.model.GenericResponse;
import com.mysivana.mvp.model.post.PostCreateUser;
import com.mysivana.util.AppConstants;
import com.mysivana.util.CommonUtils;
import com.mysivana.util.FirebaseConstants;
import com.mysivana.util.NetworkUtil;
import com.mysivana.util.UIUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

import static com.mysivana.ui.WelcomeAfterLoginActivity.USER_TYPE;

public class OTPVerifyActivity extends BaseMvpActivity<MainMvpPresenter> implements MainMvpView {


    private FirebaseAuth mAuth;
    private final String TAG = OTPVerifyActivity.class.getSimpleName();
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String phoneNumber;
    private String mVerificationId;
    private PostCreateUser postCreateUser;

    @BindView(R.id.otpview)
    MSOTPView mOTPView;

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

        mFirebaseAnalytics.setCurrentScreen(this, FirebaseConstants.LAUNCH_OTP_SIGN_UP_PWD, null);
        Bundle bundle = getIntent().getExtras();
        postCreateUser = bundle.getParcelable(AppConstants.BundleParams.CREATE_USER);
        phoneNumber = postCreateUser.getCountryCode() + postCreateUser.getPhoneNumber();
        Log.d(TAG, "phoneNumber::::" + phoneNumber);
        mAuth = FirebaseAuth.getInstance();

        sendSMS(phoneNumber);

    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            Log.d(TAG, "onVerificationCompleted::::" + phoneAuthCredential);
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verification without
            //     user action.
            signInWithPhoneAuthCredential(phoneAuthCredential);
            hideLoading();
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Log.d(TAG, "onVerificationFailed::::" + e.getMessage());
            hideLoading();
            UIUtils.showToast(OTPVerifyActivity.this, "Verification Failed");
            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                Log.d(TAG, ":::::InValid Phone Number::::");
                // ...
            } else if (e instanceof FirebaseTooManyRequestsException) {
                Log.d(TAG, ":::::Too many requests sent to firebase::::");
            }
        }

        @Override
        public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            Log.d(TAG, "onCodeSent:::::" + verificationId);
            hideLoading();
            mVerificationId = verificationId;
            mResendToken = forceResendingToken;
            Bundle b = new Bundle();
            b.putString(FirebaseConstants.PARAM_OTP_TOKEN, mVerificationId);
            mFirebaseAnalytics.logEvent(FirebaseConstants.SIGN_UP_OTP_TOKEN, b);
        }
    };


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
                    System.out.println("otpCode written  is:::::"+otp);
                    postCreateUser.setOtpCode(otp);
                    if (NetworkUtil.getConnectivityStatus(OTPVerifyActivity.this))
                        mPresenter.doRegister(postCreateUser);
                    else
                        showMessage(getString(R.string.internet_check));
                } else {
                    mOTPView.clearOTP();
                    UIUtils.showToast(OTPVerifyActivity.this, getString(R.string.invalid_otp));
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
            UIUtils.showToast(OTPVerifyActivity.this, getString(R.string.please_enter_otp));
        }

    }

    public void onClickResend(View view) {
        Bundle b = new Bundle();
        mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_RESEND_OTP, b);

        reSendOTP(phoneNumber, mResendToken);
        UIUtils.showToast(OTPVerifyActivity.this, getString(R.string.otp_resent));
    }


    @Override
    public void onDataSuccess(Object object, int apiRequestCode) {
        hideLoading();
        GenericResponse genericResponse = (GenericResponse) object;

        int code = genericResponse.getErrorCode();
        switch (code) {
            case AppConstants.SUCCESS_RESPONSE_CODE:
//                UIUtils.showToast(OTPVerifyActivity.this, genericResponse.getStatus());
                Bundle b = new Bundle();
                b.putString(USER_TYPE, postCreateUser.getUserType());
                CommonUtils.startActivity(this, WelcomeAfterLoginActivity.class, b);
                finish();
                break;
            case AppConstants.BAD_CREDENTIALS_CODE:
                logOutFromApp();
                break;
            case AppConstants.HTTP_500_ERROR_CODE:
            case AppConstants.VALIDATION_ERROR_CODE:
                String errorString=genericResponse.getErrorDescription();
                if (TextUtils.isEmpty(errorString))
                    errorString = "Something went wrong. Please try again after sometime.";
                Intent intent = new Intent();
                intent.putExtra(AppConstants.BundleParams.ERROR, errorString);
                setResult(RESULT_OK, intent );
                finish();
                break;
        }


    }

    @Override
    public void onDataFailure(Throwable e) {
        if (e instanceof HttpException) {
            //HTTP exceptions of non 200 type.
            int code = ((HttpException) e).code();
            if (code == 500) {
                if (errorCounter >= 5) {
                    submitTicket = new SubmitTicket(this, code + ", " + ((HttpException) e).response().toString());
                    submitTicket.execute(0);
                    errorCounter = 0;
                } else {
                    showMessage(getString(R.string.internal_server_error));
                    errorCounter++;
                }

            } else if (code == 401) {
                //Bad credentials error
                logOutFromApp();
            }
        } else if (e instanceof SocketTimeoutException) {
            //connection establishment failure exception (might have slow internet or server unavailable)
            showMessage(getString(R.string.try_again));
        } else if (e instanceof IOException) {
            //called when there's network problem
            showMessage(getString(R.string.internet_check));
        } else {
            //other messages
            showMessage(e.getMessage());
        }
    }


}
