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
 * JUNE 06 2018      : BYNDR       : navigates to transaction look up where merchant can enter transaction code of user to complete transaction.
 * ------------------------------------------------------------------------
 * <p>
 * ========================================================================
 */
package com.mysivana.ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.mysivana.R;
import com.mysivana.custom.MSButton;
import com.mysivana.custom.MSEditText;
import com.mysivana.custom.MSTextView;
import com.mysivana.mvp.BaseMvpActivity;
import com.mysivana.mvp.MainMvpPresenter;
import com.mysivana.mvp.MainMvpView;
import com.mysivana.mvp.model.ReceivePaymentRequest;
import com.mysivana.mvp.model.TransactionReceiptResponse;
import com.mysivana.mvp.model.UserResponse;
import com.mysivana.util.AppConstants;
import com.mysivana.util.CommonUtils;
import com.mysivana.util.FirebaseConstants;
import com.mysivana.util.NetworkUtil;
import com.mysivana.util.SharedPrefsUtils;
import com.mysivana.util.TransactionCodeTextWatcher;
import com.mysivana.util.UIUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.mysivana.util.AppConstants.USER_TYPE_MERCHANT;

public class TransactionLookUpActivity extends BaseMvpActivity<MainMvpPresenter> implements MainMvpView {

    @BindView(R.id.et_transaction_code)
    MSEditText etTransactionCode;
    @BindView(R.id.et_rupee_value)
    MSTextView etRupeeValue;
    @BindView(R.id.et_fullname)
    MSTextView etFullname;
    @BindView(R.id.et_phone)
    MSTextView etPhone;
    @BindView(R.id.et_email)
    MSTextView etEmail;
    @BindView(R.id.phone_email_layout)
    LinearLayout phoneEmailLayout;
    @BindView(R.id.tv_btc_value)
    MSTextView tvBtcValue;
    @BindView(R.id.et_status)
    MSTextView etStatus;
    @BindView(R.id.btn_pay_cash)
    MSButton btnPayCash;

    TransactionReceiptResponse.Value value;
    UserResponse userResponse;
    private static final int REQUEST_ADD_BANK_DETAILS_TRANSACTION = 122;
    public static final String IS_FROM_LOOK_UP = "IsFromLookUp";

    @Override
    protected int setContentLayoutId() {
        return R.layout.activity_transaction_lookup;
    }

    @Override
    protected void onResume() {
        super.onResume();
        userResponse = SharedPrefsUtils.getLoggedUserObject(this);
    }

    @Override
    protected void init() {

        mFirebaseAnalytics.setCurrentScreen(this, FirebaseConstants.LAUNCH_TRANSACTION_LOOKUP,null);
        setSupportActionBar();
        userResponse = SharedPrefsUtils.getLoggedUserObject(this);
        etTransactionCode.addTextChangedListener(new TransactionCodeTextWatcher(etTransactionCode, new TransactionCodeTextWatcher.OnEnteredCallback() {
            @Override
            public void onEntered() {
                String transactionCode = etTransactionCode.getText().toString();
                UserResponse userResponse = SharedPrefsUtils.getLoggedUserObject(TransactionLookUpActivity.this);
                if (NetworkUtil.getConnectivityStatus(TransactionLookUpActivity.this)) {
                    mPresenter.getTransactionReceipt(String.valueOf(userResponse.getValue().getUserId()), transactionCode,
                            userResponse.getValue().getUserApiKey(), TransactionReceiptActivity.TRANSACTION_RECEIPT_REQUEST_CODE);
                } else {
                    showMessage(getString(R.string.internet_check));
                }
            }
        }));
    }


    @Override
    protected MainMvpPresenter createPresenter() {
        return new MainMvpPresenter(this);
    }

    @Override
    public void onDataSuccess(Object object, int apiRequestCode) {
        hideLoading();
        TransactionReceiptResponse response = (TransactionReceiptResponse) object;
        int code = response.getHttpStatusCode();
        switch (code) {
            case AppConstants.SUCCESS_RESPONSE_CODE:
                value = response.getValue();
                etFullname.setText(value.getFullName());
                etTransactionCode.setText(value.getTransactionCode());
                etPhone.setText(value.getPhoneNumber());
                etEmail.setText(value.getEmail());
                etRupeeValue.setText(String.valueOf(value.getFiatValue()));
                etStatus.setText(value.getStatus());
                tvBtcValue.setText(String.format("%.8f", value.getTotalCrypto()));
                if (value.getCryptoCode() == null || value.getCryptoCode().equalsIgnoreCase(AppConstants.CryptoCodes.BTC)) {
                    tvBtcValue.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bitcoin_original,0,0,0);
                } else {
                    tvBtcValue.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ethereum_original,0,0,0);
                }
                if (value.getStatus().equalsIgnoreCase(AppConstants.TransactionStatus.NOT_PAID)) {
                    btnPayCash.setVisibility(View.VISIBLE);
                    btnPayCash.setText(UIUtils.getStringFromResource(this, R.string.pay_cash));
                } else {
                    btnPayCash.setVisibility(View.GONE);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }


    public void onClickPayment(View view) {
        if (validatePayment()) {
            //log Analytics
            Bundle b = new Bundle();
            b.putString(FirebaseConstants.PARAM_USER_ID, userResponse.getValue().getUserId());
            mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_PAY_CASH,b);

            Bundle bundle = new Bundle();
            ReceivePaymentRequest request = new ReceivePaymentRequest();
            request.setCryptoAddress(value.getAddress());
            request.setCryptoValue(value.getCryptoValue() + "");
            request.setReceiverId(value.getReceiverId());
            request.setSenderId(value.getSenderId());
            request.setServiceChargeInCrypto(value.getServiceChargeCrypto() + "");
            request.setTotalCrypto(value.getTotalCrypto() + "");
            request.setUserId(Integer.parseInt(userResponse.getValue().getUserId()));
            request.setUserApiKey(userResponse.getValue().getUserApiKey());
            request.setFiatValue(value.getFiatValue() + "");
            request.setFullName(value.getFullName());
            request.setCountryCode(value.getCountryCode());
            request.setEmail(value.getEmail());
            request.setPhoneNumber(value.getPhoneNumber());
            request.setTransactionCode(value.getTransactionCode());
            request.setStatus(value.getStatus());
            request.setCryptoCode(value.getCryptoCode());
            request.setUserType(AppConstants.USER_TYPE_MERCHANT);
            request.setPaymentType(AppConstants.DEPOSIT_CASH);
            bundle.putParcelable(AppConstants.BundleParams.PAYMENT_RESPONSE, request);
            bundle.putBoolean(IS_FROM_LOOK_UP,true);
            CommonUtils.startActivity(this, OTPPaymentCompleteActivity.class, bundle);

        }
    }

    /**
     * validates transaction code & asks merchant to add bank info if he hasn't added
     *
     * @return false if the text field is not validated
     */
    private boolean validatePayment() {
        String transCode = etTransactionCode.getText().toString();
        if (TextUtils.isEmpty(transCode)) {
            UIUtils.showToast(this, getString(R.string.enter_transaction_code_here));
            return false;
        } else if (userResponse.getValue().getUserType().equalsIgnoreCase(USER_TYPE_MERCHANT)) {
            if (TextUtils.isEmpty(userResponse.getValue().getBankDetailsAvailable()) ||
                    (!TextUtils.isEmpty(userResponse.getValue().getBankDetailsAvailable()) &&
                            userResponse.getValue().getBankDetailsAvailable().equalsIgnoreCase("0"))) {
                showAddBankDetails();
                return false;
            }

        }
        return true;
    }


    /**
     * Used to force the merchant to add bank details before he initiates a payment
     */
    private void showAddBankDetails() {
        final Dialog dialog = new Dialog(this, R.style.AppTheme_FloatingDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm_bank_details);
        TextInputLayout mTILPassword = dialog.findViewById(R.id.til_password);
        mTILPassword.setVisibility(View.GONE);
        Button mProceed = dialog.findViewById(R.id.proceed_btn);
        mProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //log Analytics
                Bundle b = new Bundle();
                b.putString(FirebaseConstants.PARAM_USER_ID, userResponse.getValue().getUserId());
                mFirebaseAnalytics.logEvent(FirebaseConstants.ADD_BANK_DETAILS,b);

                CommonUtils.startActivityForResult(TransactionLookUpActivity.this, AddBankDetailsActivity.class, REQUEST_ADD_BANK_DETAILS_TRANSACTION);
                dialog.dismiss();
            }
        });
        TextView mDesc = dialog.findViewById(R.id.warning_msg);
        mDesc.setText(getString(R.string.cant_pay));
        ImageView mCancel = dialog.findViewById(R.id.cancel_btn);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        TextView mTitle = dialog.findViewById(R.id.header_title);
        mTitle.setText(getString(R.string.add_bank_details));

        Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADD_BANK_DETAILS_TRANSACTION) {
            if (resultCode == RESULT_OK) {
                loadBankDetails();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loadBankDetails() {
        userResponse.getValue().setBankDetailsAvailable("1");
        SharedPrefsUtils.saveLoggedUserObject(TransactionLookUpActivity.this, userResponse);
    }

}
