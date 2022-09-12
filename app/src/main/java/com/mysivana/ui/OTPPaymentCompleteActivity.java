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
import android.widget.TextView;

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
import com.mysivana.mvp.model.ReceivePaymentRequest;
import com.mysivana.mvp.model.ReceivePaymentResponse;
import com.mysivana.mvp.model.UserResponse;
import com.mysivana.util.AppConstants;
import com.mysivana.util.CommonUtils;
import com.mysivana.util.FirebaseConstants;
import com.mysivana.util.NetworkUtil;
import com.mysivana.util.SharedPrefsUtils;
import com.mysivana.util.UIUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;

import static com.mysivana.ui.TransactionLookUpActivity.IS_FROM_LOOK_UP;

public class OTPPaymentCompleteActivity extends BaseMvpActivity<MainMvpPresenter> implements MainMvpView {

    private static final int REQUEST_OTP = 10;
    private static final int VALIDATE_OTP = 11;
    private FirebaseAuth mAuth;
    private final String TAG = OTPPaymentCompleteActivity.class.getSimpleName();
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String phoneNumber;
    private String mVerificationId;
    private ReceivePaymentRequest receivePaymentRequest;

    @BindView(R.id.tv_resend)
    TextView mResendTV;
    @BindView(R.id.otpview)
    MSOTPView mOTPView;

    private int resendOTPCount = 0;
    private UserResponse userResponse;
    private boolean isFromLookUp = false;

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

        mFirebaseAnalytics.setCurrentScreen(this, FirebaseConstants.LAUNCH_OTP_COMPLETE_PAY, null);
        Bundle bundle = getIntent().getExtras();
        receivePaymentRequest = bundle.getParcelable(AppConstants.BundleParams.PAYMENT_RESPONSE);
        isFromLookUp = bundle.getBoolean(IS_FROM_LOOK_UP, false);
        phoneNumber = receivePaymentRequest.getCountryCode() + receivePaymentRequest.getPhoneNumber();
        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted::::" + phoneAuthCredential);
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
                Log.d(TAG, "onCodeSent:::::" + verificationId);
                hideLoading();
                mVerificationId = verificationId;
                mResendToken = forceResendingToken;

                Bundle b = new Bundle();
                b.putString(FirebaseConstants.PARAM_OTP_TOKEN, mVerificationId);
                mFirebaseAnalytics.logEvent(FirebaseConstants.COMPLETE_PAY_OTP_TOKEN, b);

                if (resendOTPCount != 0) {
                    UIUtils.showToast(OTPPaymentCompleteActivity.this, getString(R.string.otp_resent));
                }
            }
        };
        userResponse = SharedPrefsUtils.getLoggedUserObject(this);
        sendOTP(phoneNumber);
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
    private void sendOTP(String phoneNumber) {
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
        if (resendOTPCount != 0) {
            mPresenter.generateOTP(receivePaymentRequest, REQUEST_OTP);
            mResendTV.setText(getString(R.string.otp_sent_email));
        } else {
            resendOTPCount = resendOTPCount + 1;
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    this,               // Activity (for callback binding)
                    mCallbacks,
                    token);
        }
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
                    if (otp == null || TextUtils.isEmpty(otp)) {
                        otp = credential.getSmsCode();
                    }
                    receivePaymentRequest.setTransactionOtp(otp);
                    if (NetworkUtil.getConnectivityStatus(OTPPaymentCompleteActivity.this))
                        mPresenter.completePayment(receivePaymentRequest, 1);
                    else
                        showMessage(getString(R.string.internet_check));
                } else {
                    if (resendOTPCount != 0) {
                        receivePaymentRequest.setOtp(mOTPView.getOTP());
                        mPresenter.validateOTP(receivePaymentRequest, VALIDATE_OTP);
                    } else {
                        mOTPView.clearOTP();
                        UIUtils.showToast(OTPPaymentCompleteActivity.this, getString(R.string.invalid_otp));
                    }

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
        } else {
            UIUtils.showToast(OTPPaymentCompleteActivity.this, getString(R.string.please_enter_otp));
        }
    }

    public void onClickResend(View view) {
        Bundle b = new Bundle();
        mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_RESEND_OTP, b);

        reSendOTP(phoneNumber, mResendToken);
    }


    @Override
    public void onDataSuccess(Object object, int apiRequestCode) {
        if (object instanceof ReceivePaymentResponse) {
            ReceivePaymentResponse receivePaymentResponse = (ReceivePaymentResponse) object;
            int code = receivePaymentResponse.getErrorCode();
            switch (code) {
                case AppConstants.SUCCESS_RESPONSE_CODE:
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(AppConstants.BundleParams.OTP_COMPLETE_ACTIVITY,
                            true);
                    bundle.putString(TransactionReceiptActivity.TRANSACTION_CODE, receivePaymentResponse.getValue().getTransactionCode());
                    bundle.putBoolean(IS_FROM_LOOK_UP, isFromLookUp);
                    CommonUtils.startActivity(OTPPaymentCompleteActivity.this, TransactionReceiptActivity.class, bundle);
                    finish();
                    break;
                case AppConstants.BAD_CREDENTIALS_CODE:
                    logOutFromApp();
                    break;
                case AppConstants.HTTP_500_ERROR_CODE:
                case AppConstants.VALIDATION_ERROR_CODE:
                    if (!TextUtils.isEmpty(receivePaymentResponse.getErrorDescription()))
                        UIUtils.showToast(this, receivePaymentResponse.getErrorDescription());
                    break;
            }
        } else if (object instanceof GenericResponse) {
            GenericResponse response = (GenericResponse) object;
            if (apiRequestCode == REQUEST_OTP) {
                UIUtils.showToast(OTPPaymentCompleteActivity.this, getString(R.string.otp_sent_email_success));
            } else if (apiRequestCode == VALIDATE_OTP) {
                int code = response.getErrorCode();
                if (code == AppConstants.SUCCESS_RESPONSE_CODE) {
                    receivePaymentRequest.setTransactionOtp(mOTPView.getOTP());
                    if (NetworkUtil.getConnectivityStatus(OTPPaymentCompleteActivity.this))
                        mPresenter.completePayment(receivePaymentRequest, 1);
                    else
                        showMessage(getString(R.string.internet_check));
                } else {
                    mOTPView.clearOTP();
                    UIUtils.showToast(OTPPaymentCompleteActivity.this, getString(R.string.invalid_otp));
                }
            }
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
