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

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.mysivana.R;
import com.mysivana.custom.MSEditText;
import com.mysivana.custom.MSTextView;
import com.mysivana.custom.TimeLineView;
import com.mysivana.mvp.BaseMvpActivity;
import com.mysivana.mvp.MainMvpPresenter;
import com.mysivana.mvp.MainMvpView;
import com.mysivana.mvp.model.GenericResponse;
import com.mysivana.mvp.model.HelpFeedbackRequest;
import com.mysivana.mvp.model.Transactions;
import com.mysivana.mvp.model.UserResponse;
import com.mysivana.util.AppConstants;
import com.mysivana.util.FirebaseConstants;
import com.mysivana.util.NetworkUtil;
import com.mysivana.util.SharedPrefsUtils;
import com.mysivana.util.UIUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReportIssueActivity extends BaseMvpActivity<MainMvpPresenter> implements MainMvpView {
    @BindView(R.id.bitcoin_img)
    TimeLineView bitcoinImg;
    @BindView(R.id.tv_bitcoin_value)
    MSTextView tvBitcoinValue;
    @BindView(R.id.location)
    MSTextView location;
    @BindView(R.id.date)
    MSTextView date;
    @BindView(R.id.time)
    MSTextView time;
    @BindView(R.id.on_click_expand_btn)
    ImageView onClickExpandBtn;
    @BindView(R.id.my_transaction_bar)
    LinearLayout myTransactionBar;
    @BindView(R.id.rupee_marker)
    TimeLineView rupeeMarker;
    @BindView(R.id.rupee_figure)
    MSTextView rupeeFigure;
    @BindView(R.id.merchant_marker)
    TimeLineView merchantMarker;
    @BindView(R.id.merchant_name)
    MSTextView merchantName;
    @BindView(R.id.merchant)
    MSTextView merchant;
    @BindView(R.id.receiver_marker)
    TimeLineView receiverMarker;
    @BindView(R.id.receiver_name)
    MSTextView receiverName;
    @BindView(R.id.receiver)
    MSTextView receiver;
    @BindView(R.id.transaction_marker)
    TimeLineView transactionMarker;
    @BindView(R.id.transaction_code_figure)
    MSTextView transactionCodeFigure;
    @BindView(R.id.transaction)
    MSTextView transaction;
    @BindView(R.id.et_feedback)
    MSEditText etFeedback;
    @BindView(R.id.show_details)
    LinearLayout showDetails;
    @BindView(R.id.error_text)
    TextView mErrorText;
    @BindView(R.id.til_feedback)
    TextInputLayout mTILFeedback;
    @BindView(R.id.merchant_layout)
    LinearLayout merchantLayout;

    Transactions.Value transactionDetails;

    public static final String TRANSACTION_DETAILS = "TransactionDetails";

    @Override
    protected int setContentLayoutId() {
        return R.layout.activity_report_issue;
    }

    @Override
    protected void init() {

        mFirebaseAnalytics.setCurrentScreen(this, FirebaseConstants.LAUNCH_REPORT_ISSUE,null);

        setSupportActionBar();
        transactionDetails = getIntent().getParcelableExtra(TRANSACTION_DETAILS);
        mTILFeedback.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etFeedback, InputMethodManager.SHOW_IMPLICIT);
                return false;
            }
        });
        loadDetails();
    }

    /**
     * loading the details to screen UI
     */
    private void loadDetails() {
        tvBitcoinValue.setText(String.format("%.8f", transactionDetails.getTotalCrypto()));
        if (!TextUtils.isEmpty(transactionDetails.getCity()))
            location.setText(transactionDetails.getCity());
        rupeeFigure.setText(String.valueOf(transactionDetails.getFiatValue()));
        transactionCodeFigure.setText(transactionDetails.getTransactionCode());
        receiverName.setText(transactionDetails.getReceiverFullName());
        merchantName.setText(transactionDetails.getServiceProvierFullName());
        showDetails.setVisibility(View.VISIBLE);
        String status = transactionDetails.getStatus();
        if (status.equalsIgnoreCase(AppConstants.TransactionStatus.PAID)) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins((int) getResources().getDimension(R.dimen.activity_horizontal_margin), (int) getResources().getDimension(R.dimen.dimen_5), 0, (int) getResources().getDimension(R.dimen.dimen_5));
            tvBitcoinValue.setLayoutParams(params);
            merchantLayout.setVisibility(View.VISIBLE);
            merchantName.setText(transactionDetails.getServiceProvierFullName());
            location.setText(transactionDetails.getCity());
            location.setVisibility(View.VISIBLE);

            String deliveryTime = transactionDetails.getDeliveredTime();
            Date mDate = UIUtils.formatTransactionDate(deliveryTime != null ? deliveryTime : "");
            if (mDate != null) {
                String day = (String) DateFormat.format("dd", mDate);
                String month = (String) DateFormat.format("MM", mDate);
                String year = (String) DateFormat.format("yyyy", mDate);
                String hour = (String) DateFormat.format("HH", mDate);
                String minute = (String) DateFormat.format("mm", mDate);
                date.setText(getString(R.string.format_transaction_date, day, month, year));
                time.setText(getString(R.string.format_transaction_hour, hour, minute));
            }
        } else {
            UserResponse response = SharedPrefsUtils.getLoggedUserObject(this);


            if (response.getValue().getUserType()!= null && response.getValue().getUserType().equalsIgnoreCase(AppConstants.USER_TYPE_SENDER_RECEIVER)) {
                if(status.equalsIgnoreCase(AppConstants.TransactionStatus.DEPOSIT_COMPLETED)||status.equalsIgnoreCase(AppConstants.TransactionStatus.PENDING_DEPOSIT)){
                    location.setVisibility(View.VISIBLE);
                    location.setText("Bank Deposit");
                    location.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bank,0,0,0);
                }else {
                    location.setVisibility(View.INVISIBLE);
                }
            } else {
                location.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(transactionDetails.getCity())) {
                    location.setText(transactionDetails.getCity());
                    location.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location, 0, 0, 0);
                }else{
                    location.setText("");
                }
            }


            merchantLayout.setVisibility(View.GONE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins((int) getResources().getDimension(R.dimen.activity_horizontal_margin), (int) getResources().getDimension(R.dimen.dimen_5), 0, (int) getResources().getDimension(R.dimen.dimen_5));
            tvBitcoinValue.setLayoutParams(params);
            String createdDate = transactionDetails.getCreatedDate();
            Date mdate = UIUtils.formatTransactionDate(createdDate != null ? createdDate : "");
            if (mdate != null) {
                String day = (String) DateFormat.format("dd", mdate);
                String month = (String) DateFormat.format("MM", mdate);
                String year = (String) DateFormat.format("yyyy", mdate);
                String hour = (String) DateFormat.format("HH", mdate);
                String minute = (String) DateFormat.format("mm", mdate);
                date.setText(getString(R.string.format_transaction_date, day, month, year));
                time.setText(getString(R.string.format_transaction_hour, hour, minute));
            }
        }
        tvBitcoinValue.setText(String.format("%.8f", transactionDetails.getTotalCrypto()));
        rupeeFigure.setText(String.valueOf(transactionDetails.getFiatValue()));
        transactionCodeFigure.setText(transactionDetails.getTransactionCode());
        receiverName.setText(transactionDetails.getReceiverFullName());

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
            HelpFeedbackRequest request = new HelpFeedbackRequest();
            request.setUserId(String.valueOf(userResponse.getValue().getUserId()));
            request.setMessage(feedback);
            request.setUserApiKey(userResponse.getValue().getUserApiKey());
            request.setTransactionCode(transactionDetails.getTransactionCode());

            //log Analytics
            Bundle b = new Bundle();
            b.putString(FirebaseConstants.PARAM_USER_ID, userResponse.getValue().getUserId());
            mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_SEND_REPORT,b);

            if (NetworkUtil.getConnectivityStatus(this)) {
                mErrorText.setVisibility(View.INVISIBLE);
                mPresenter.sendReportIssue(request);
            } else {
                mErrorText.setVisibility(View.VISIBLE);
                mErrorText.setText(getString(R.string.internet_connection_unavailable));
            }
        } else {
            mErrorText.setText(getString(R.string.write_feedback));
            mErrorText.setVisibility(View.VISIBLE);
        }
    }
}
