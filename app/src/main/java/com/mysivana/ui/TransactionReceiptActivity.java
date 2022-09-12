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
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.CardView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.mysivana.R;
import com.mysivana.custom.MSTextView;
import com.mysivana.mvp.BaseMvpActivity;
import com.mysivana.mvp.MainMvpPresenter;
import com.mysivana.mvp.MainMvpView;
import com.mysivana.mvp.model.GenericResponse;
import com.mysivana.mvp.model.TransactionReceiptResponse;
import com.mysivana.mvp.model.Transactions;
import com.mysivana.mvp.model.UserResponse;
import com.mysivana.mvp.model.post.PostUserLogin;
import com.mysivana.util.AppConstants;
import com.mysivana.util.CommonUtils;
import com.mysivana.util.DeviceInfo;
import com.mysivana.util.FirebaseConstants;
import com.mysivana.util.NetworkUtil;
import com.mysivana.util.PermissionUtil;
import com.mysivana.util.SharedPrefsUtils;
import com.mysivana.util.UIUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.mysivana.ui.TransactionLookUpActivity.IS_FROM_LOOK_UP;
import static com.mysivana.util.AppConstants.USER_TYPE_MERCHANT;

public class TransactionReceiptActivity extends BaseMvpActivity<MainMvpPresenter> implements MainMvpView {

    public static final String TRANSACTION_CODE = "TransactionCode";
    public static final String TRANSACTION_DETAILS = "TransactionDetails";
    String transactionCode;

    @BindView(R.id.recepient_name_tv)
    MSTextView mRecepientNameTv;
    @BindView(R.id.transaction_code_tv)
    MSTextView mTransactionCodeTv;
    @BindView(R.id.phone_tv)
    MSTextView mPhoneTv;
    @BindView(R.id.email_tv)
    MSTextView mEmailTv;
    @BindView(R.id.tv_amount_inr)
    MSTextView mTvAmountInr;
    @BindView(R.id.tv_btc_resp)
    MSTextView mTvBtcResp;
    @BindView(R.id.tv_service_charge)
    MSTextView mTvServiceCharge;
    @BindView(R.id.tv_total_pay)
    MSTextView mTvTotalPay;
    @BindView(R.id.transaction_receipt_card)
    CardView mTransactionReceiptCard;
    @BindView(R.id.service_provider_name)
    MSTextView mServiceProviderName;
    @BindView(R.id.service_provider_address)
    MSTextView mServiceProviderAddress;
    @BindView(R.id.service_provider_details)
    MSTextView mServiceProviderDetails;
    @BindView(R.id.transaction_status)
    MSTextView mTransactionStatus;
    @BindView(R.id.coinValueTitle)
    MSTextView mCoinValueTitle;
    @BindView(R.id.coinChargeTitle)
    MSTextView mCoinChargeTitle;
    @BindView(R.id.coinTotalTitle)
    MSTextView mCoinTotalTitle;
    @BindView(R.id.coinValueImage)
    ImageView mCoinValueImage;
    @BindView(R.id.coinChargeImage)
    ImageView mCoinChargeImage;
    @BindView(R.id.coinTotalImage)
    ImageView mCoinTotalImage;
    @BindView(R.id.root_view)
    CoordinatorLayout mRootView;
    @BindView(R.id.btn_send_receipt)
    Button mBtnSend;
    @BindView(R.id.service_provider_layout)
    LinearLayout serviceProviderLayout;

    boolean isFromDashboard = false;
    boolean isFromLookUp = false;
    public static final int TRANSACTION_RECEIPT_REQUEST_CODE = 134;
    public static final int SEND_RECEIPT_REQUEST_CODE = 135;
    public static final int USER_PROFILE = 136;
    UserResponse userResponse;
    Transactions.Value transactionDetails;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    @Override
    protected int setContentLayoutId() {
        return R.layout.activity_transaction_receipt;
    }

    @Override
    protected void init() {
        mFirebaseAnalytics.setCurrentScreen(this, FirebaseConstants.LAUNCH_TRANSACTION_RECEIPT, null);

        setSupportActionBar();
        mRootView.setVisibility(View.GONE);
        Intent intent = getIntent();
        transactionCode = intent.getStringExtra(TRANSACTION_CODE);
        isFromDashboard = intent.getBooleanExtra(DashboardUserActivity.DASHBOARD_PAYMENT_COMPLETED, false);
        isFromLookUp = intent.getBooleanExtra(IS_FROM_LOOK_UP, false);
        userResponse = SharedPrefsUtils.getLoggedUserObject(this);
        if (NetworkUtil.getConnectivityStatus(this)) {
            mPresenter.getTransactionReceipt(String.valueOf(userResponse.getValue().getUserId()), transactionCode, userResponse.getValue().getUserApiKey(), TRANSACTION_RECEIPT_REQUEST_CODE);
        } else {
            showMessage(getString(R.string.internet_check));
        }
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Analytics Log
                Bundle b = new Bundle();
                b.putString(FirebaseConstants.PARAM_USER_ID, userResponse.getValue().getUserId());
                mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_SEND_RECEIPT, b);

                if (NetworkUtil.getConnectivityStatus(TransactionReceiptActivity.this))
                    mPresenter.sendReceipt(userResponse.getValue().getUserId(), userResponse.getValue().getUserApiKey(),
                            userResponse.getValue().getUserType(), transactionCode, SEND_RECEIPT_REQUEST_CODE);
                else
                    showMessage(getString(R.string.internet_check));
            }
        });
        Bundle bundle = intent.getExtras();

        if (bundle.containsKey(AppConstants.BundleParams.OTP_COMPLETE_ACTIVITY) &&
                bundle.getBoolean(AppConstants.BundleParams.OTP_COMPLETE_ACTIVITY) && userResponse
                .getValue().getUserType()
                != null &&
                userResponse.getValue().getUserType().equalsIgnoreCase
                        (USER_TYPE_MERCHANT)) {
            updateMerchantDetails();
        }

    }


    /**
     * Render details on UI
     */
    private void updateMerchantDetails() {

        if (NetworkUtil.getConnectivityStatus(this)) {
            PostUserLogin postUserLogin = new PostUserLogin();
            postUserLogin.setEmail(userResponse.getValue().getEmail());
            postUserLogin.setPhoneNumber(userResponse.getValue().getPhoneNumber());
            postUserLogin.setOsVersion(DeviceInfo.getOSVersion());
            postUserLogin.setApiVersion(DeviceInfo.getAppVersion(this));
            postUserLogin.setDeviceBrand(DeviceInfo.getBuildBrand());
            postUserLogin.setDeviceId(DeviceInfo.getIMEI(TransactionReceiptActivity.this));
            postUserLogin.setCountryCode(userResponse.getValue().getCountryCode());
            postUserLogin.setUserId(userResponse.getValue().getUserId());
            postUserLogin.setUserApiKey(userResponse.getValue().getUserApiKey());
            mPresenter.viewMyProfile(postUserLogin, USER_PROFILE);
        }


    }

    @Override
    protected MainMvpPresenter createPresenter() {
        return new MainMvpPresenter(this);
    }

    @Override
    public void onDataSuccess(Object object, int apiRequestCode) {
        hideLoading();
        switch (apiRequestCode) {

            case TRANSACTION_RECEIPT_REQUEST_CODE:
                TransactionReceiptResponse response = (TransactionReceiptResponse) object;
                int code = response.getHttpStatusCode();
                switch (code) {
                    case AppConstants.SUCCESS_RESPONSE_CODE:
                        final TransactionReceiptResponse.Value value = response.getValue();
                        mRootView.setVisibility(View.VISIBLE);
                        mRecepientNameTv.setText(value.getFullName());
                        mTransactionCodeTv.setText(value.getTransactionCode());
                        mPhoneTv.setText(value.getCountryCode() + " " + value.getPhoneNumber());
                        mEmailTv.setText(value.getEmail());
                        String status = value.getStatus();
                        String statusDefault = getString(R.string.transaction_status);
                        Spannable wordToSpan = new SpannableString(statusDefault + status);
                        if (status != null && !TextUtils.isEmpty(status) && status.equalsIgnoreCase(AppConstants.TransactionStatus.PAID)) {
                            wordToSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)), statusDefault.length(), wordToSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        } else {
                            wordToSpan.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.error)), statusDefault.length(), wordToSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        mTransactionStatus.setText(wordToSpan);
                        mTvAmountInr.setText(String.valueOf(value.getFiatValue()));
                        mTvBtcResp.setText(String.format("%.8f", value.getCryptoValue()));
                        mTvServiceCharge.setText(String.format("%.8f", value.getServiceChargeCrypto()));
                        mTvTotalPay.setText(String.format("%.8f", value.getTotalCrypto()));
                        if (value.getStatus().equalsIgnoreCase(AppConstants.TransactionStatus.PAID)) {
                            mServiceProviderAddress.setVisibility(View.VISIBLE);
                            mServiceProviderDetails.setVisibility(View.VISIBLE);
                            mServiceProviderName.setText(value.getBusinessName());
                            mServiceProviderAddress.setText(value.getAddress());
                            mServiceProviderDetails.setText(value.getServiceProviderFullName() + "\n" + value.getServiceProviderCountryCode() + " " + value.getServiceProviderPhoneNumber() + "\n" + value.getServiceProviderEmail());
                        } else if (value.getStatus().equalsIgnoreCase(AppConstants.TransactionStatus.NOT_PAID)) {
                            mServiceProviderName.setText(UIUtils.getStringFromResource(TransactionReceiptActivity.this, R.string.visit_nearby_merchant));
                            mServiceProviderAddress.setVisibility(View.GONE);
                            mServiceProviderDetails.setVisibility(View.GONE);
                        } else if (value.getStatus().equalsIgnoreCase(AppConstants.TransactionStatus.PENDING_DEPOSIT)) {
                            ClickableSpan clickableSpan = new ClickableSpan() {
                                @Override
                                public void onClick(View textView) {
                                    //Analytics Log
                                    Bundle b = new Bundle();
                                    b.putString(FirebaseConstants.PARAM_USER_ID, userResponse.getValue().getUserId());
                                    mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_HERE_REPORT, b);

                                    Bundle bundle = new Bundle();
                                    if (transactionDetails == null) {
                                        transactionDetails = new Transactions.Value();
                                        transactionDetails.setReceiverFullName(value.getFullName());
                                        transactionDetails.setFiatValue(value.getFiatValue().intValue());
                                        transactionDetails.setDeliveredTime(value.getTransactionDate());
                                        transactionDetails.setCity(value.getBankCity());
                                        transactionDetails.setServiceProvierFullName(value.getServiceProviderFullName());
                                        transactionDetails.setStatus(value.getStatus());
                                        transactionDetails.setTotalCrypto(value.getTotalCrypto());
                                        transactionDetails.setTransactionCode(value.getTransactionCode());
                                        transactionDetails.setCreatedDate(value.getTransactionDate());
                                    }
                                    bundle.putParcelable(ReportIssueActivity.TRANSACTION_DETAILS, transactionDetails);

                                    CommonUtils.startActivity(TransactionReceiptActivity.this, ReportIssueActivity.class, bundle);
                                }

                                @Override
                                public void updateDrawState(TextPaint ds) {
                                    super.updateDrawState(ds);
                                    ds.setUnderlineText(true);
                                }
                            };
                            String thisLink = getString(R.string.click_here);
                            String yourString = getString(R.string.initiated_bank_deposit, thisLink);
                            int thisLinkIndex = yourString.indexOf(thisLink);
                            SpannableString spannableString = new SpannableString(yourString);
                            spannableString.setSpan(clickableSpan,
                                    thisLinkIndex, thisLinkIndex + thisLink.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                            mServiceProviderName.setText(spannableString, TextView.BufferType.SPANNABLE);
                            mServiceProviderName.setMovementMethod(LinkMovementMethod.getInstance());
                            mServiceProviderAddress.setVisibility(View.GONE);
                            mServiceProviderDetails.setVisibility(View.GONE);
                            serviceProviderLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            serviceProviderLayout.setPadding(0, 0, TransactionReceiptActivity.this.getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin), 0);
                            mTransactionStatus.setGravity(Gravity.CENTER_HORIZONTAL);
                            if (value.getBankCode() != null && !TextUtils.isEmpty(value.getBankCode())) {
                                mServiceProviderDetails.setVisibility(View.VISIBLE);
                                mServiceProviderDetails.setText(value.getAccountNumber() + "\n" + value.getBankName() + "\n" + value.getBankBranch() + "\n" + value.getBankCode());

                            }
                        } else {
                            ClickableSpan clickableSpan = new ClickableSpan() {
                                @Override
                                public void onClick(View textView) {

                                    //Analytics Log
                                    Bundle b = new Bundle();
                                    b.putString(FirebaseConstants.PARAM_USER_ID, userResponse.getValue().getUserId());
                                    mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_HERE_REPORT, b);


                                    Bundle bundle = new Bundle();
                                    bundle.putParcelable(ReportIssueActivity.TRANSACTION_DETAILS, transactionDetails);
                                    CommonUtils.startActivity(TransactionReceiptActivity.this, ReportIssueActivity.class, bundle);
                                }

                                @Override
                                public void updateDrawState(TextPaint ds) {
                                    super.updateDrawState(ds);
                                    ds.setUnderlineText(true);
                                }
                            };
                            String thisLink = getString(R.string.click_here);
                            String yourString = getString(R.string.amount_credited, thisLink);
                            int thisLinkIndex = yourString.indexOf(thisLink);
                            SpannableString spannableString = new SpannableString(yourString);
                            spannableString.setSpan(clickableSpan,
                                    thisLinkIndex, thisLinkIndex + thisLink.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            mServiceProviderName.setText(spannableString, TextView.BufferType.SPANNABLE);
                            mServiceProviderName.setMovementMethod(LinkMovementMethod.getInstance());

                            mServiceProviderAddress.setVisibility(View.GONE);
                            mServiceProviderDetails.setVisibility(View.GONE);
                            serviceProviderLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            serviceProviderLayout.setPadding(0, 0, TransactionReceiptActivity.this.getResources().getDimensionPixelOffset(R.dimen.activity_horizontal_margin), 0);
                            mTransactionStatus.setGravity(Gravity.CENTER_HORIZONTAL);
                            if (value.getBankCode() != null && !TextUtils.isEmpty(value.getBankCode())) {
                                mServiceProviderDetails.setVisibility(View.VISIBLE);
                                mServiceProviderDetails.setText(value.getAccountNumber() + "\n" + value.getBankName() + "\n" + value.getBankBranch() + "\n" + value.getBankCode());
                            }
                        }
                        if(value.getCryptoCode() == null || value.getCryptoCode().equalsIgnoreCase(AppConstants.CryptoCodes.BTC)){
                            mCoinValueTitle.setText(R.string.btc_value);
                            mCoinChargeTitle.setText(R.string.service_charge_in_btc);
                            mCoinTotalTitle.setText(R.string.total_btc_paid);
                            mCoinValueImage.setImageResource(R.drawable.ic_bitcoin_black);
                            mCoinChargeImage.setImageResource(R.drawable.ic_bitcoin_black);
                            mCoinTotalImage.setImageResource(R.drawable.ic_bitcoin_black);
                        }else{
                            mCoinValueTitle.setText(R.string.eth_value);
                            mCoinChargeTitle.setText(R.string.service_charge_in_eth);
                            mCoinTotalTitle.setText(R.string.total_eth_paid);
                            mCoinValueImage.setImageResource(R.drawable.ic_ethereum);
                            mCoinChargeImage.setImageResource(R.drawable.ic_ethereum);
                            mCoinTotalImage.setImageResource(R.drawable.ic_ethereum);
                        }

                        break;
                    case AppConstants.BAD_CREDENTIALS_CODE:
                        logOutFromApp();
                        break;
                    case AppConstants.HTTP_500_ERROR_CODE:
                    case AppConstants.VALIDATION_ERROR_CODE:
                        if (!TextUtils.isEmpty(response.getErrorDescription()))
                            UIUtils.showToast(this, response.getErrorDescription());
                        break;
                }


                break;
            case SEND_RECEIPT_REQUEST_CODE:
                GenericResponse genericResponse = (GenericResponse) object;
                int _code = genericResponse.getErrorCode();
                switch (_code) {
                    case AppConstants.SUCCESS_RESPONSE_CODE:
                        UIUtils.showToast(this, genericResponse.getValue());
                        break;
                    case AppConstants.BAD_CREDENTIALS_CODE:
                        logOutFromApp();
                        break;
                    case AppConstants.HTTP_500_ERROR_CODE:
                    case AppConstants.VALIDATION_ERROR_CODE:
                        if (!TextUtils.isEmpty(genericResponse.getErrorDescription()))
                            UIUtils.showToast(this, genericResponse.getErrorDescription());
                        break;
                }

                break;
            case USER_PROFILE:
                UserResponse userResponseObj = (UserResponse) object;
                UserResponse.Value value = userResponseObj.getValue();
                value.setOsVersion(DeviceInfo.getOSVersion());
                value.setApiVersion(DeviceInfo.getAppVersion(this));
                value.setDeviceBrand(DeviceInfo.getBuildBrand());
                value.setDeviceId(DeviceInfo.getIMEI(this));
                SharedPrefsUtils.saveLoggedUserObject(this, userResponseObj);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.onRequestPermissionResult(this, requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        if (isFromDashboard) {
            setResult(RESULT_OK);
            finish();
        } else if (isFromLookUp) {
            CommonUtils.startActivity(this, DashboardMerchantActivity.class, null, Intent.FLAG_ACTIVITY_CLEAR_TOP, CommonUtils.DEFAULT);
            finish();
        } else {
            super.onBackPressed();
        }
    }
}
