package com.mysivana.ui;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.mysivana.R;
import com.mysivana.mvp.BaseMvpActivity;
import com.mysivana.mvp.MainMvpPresenter;
import com.mysivana.mvp.MainMvpView;
import com.mysivana.mvp.model.AccountStatementResponse;
import com.mysivana.mvp.model.GenericResponse;
import com.mysivana.mvp.model.Payment;
import com.mysivana.mvp.model.UserResponse;
import com.mysivana.ui.adapters.WalletBalanceAdapter;
import com.mysivana.util.AppConstants;
import com.mysivana.util.CommonUtils;
import com.mysivana.util.FirebaseConstants;
import com.mysivana.util.SharedPrefsUtils;
import com.mysivana.util.UIUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

import butterknife.BindView;

public class WalletBalanceActivity extends BaseMvpActivity<MainMvpPresenter> implements MainMvpView {
    @BindView(R.id.bal_title)
    TextView balTitle;
    @BindView(R.id.wallet_balance)
    TextView walletBalance;
    @BindView(R.id.request_stmt)
    TextView requestStmt;
    @BindView(R.id.balance_list)
    RecyclerView balanceList;
    @BindView(R.id.no_data_txt)
    TextView noDataTxt;

    private static final int REQUEST_GET_ACCOUNT_HISTORY = 401;
    private static final int REQUEST_GET_STATEMENT_IN_EMAIL = 402;
    List<Payment> paymentDetailList;
    WalletBalanceAdapter adapter;
    UserResponse userResponse;

    @Override
    protected int setContentLayoutId() {
        return R.layout.activity_wallet_balance;
    }

    @Override
    protected void init() {
        mFirebaseAnalytics.setCurrentScreen(this, FirebaseConstants.LAUNCH_WALLET_BALANCE, null);

        setSupportActionBar();
        noDataTxt.setVisibility(View.GONE);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        balanceList.setLayoutManager(mLayoutManager);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(balanceList.getContext(),
                mLayoutManager.getOrientation());
        balanceList.addItemDecoration(mDividerItemDecoration);
        userResponse = SharedPrefsUtils.getLoggedUserObject(this);
        float bankBalance = userResponse.getValue().getBankBalance();
        walletBalance.setText("INR " + String.format("%.02f", bankBalance));
        adapter = new WalletBalanceAdapter(WalletBalanceActivity.this, new RemoveEmptyPaymentDetails() {
            @Override
            public void removeEmptyObject(List<Payment.PaymentDetail> paymentDetails) {
                paymentDetailList.remove(paymentDetails);
            }
        });
        balanceList.setAdapter(adapter);
        mPresenter.getAccountStatement(userResponse.getValue().getUserId(), userResponse.getValue().getUserApiKey(), REQUEST_GET_ACCOUNT_HISTORY);
        requestStmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (paymentDetailList != null && paymentDetailList.size() > 0) {
                    //log Analytics
                    Bundle b = new Bundle();
                    b.putString(FirebaseConstants.PARAM_USER_ID, userResponse.getValue().getUserId());
                    mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_REQUEST_STATEMENT, b);

                    mPresenter.requestForStatement(userResponse.getValue().getUserId(), userResponse.getValue().getUserApiKey(), REQUEST_GET_STATEMENT_IN_EMAIL);
                }
            }
        });
    }

    private void showNoDataText() {
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                //log Analytics
                Bundle b = new Bundle();
                b.putString(FirebaseConstants.PARAM_USER_ID, userResponse.getValue().getUserId());
                mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_HELP, b);

                Bundle bundle = new Bundle();
                CommonUtils.startActivity(WalletBalanceActivity.this, HelpActivity.class, bundle);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
            }
        };
        String thisLink = getString(R.string.click_here);
        String yourString = getString(R.string.no_transactions, thisLink);
        int thisLinkIndex = yourString.indexOf(thisLink);
        SpannableString spannableString = new SpannableString(yourString);
        spannableString.setSpan(clickableSpan,
                thisLinkIndex, thisLinkIndex + thisLink.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        noDataTxt.setText(spannableString, TextView.BufferType.SPANNABLE);
        noDataTxt.setMovementMethod(LinkMovementMethod.getInstance());
        noDataTxt.setVisibility(View.VISIBLE);
    }

    @Override
    protected MainMvpPresenter createPresenter() {
        return new MainMvpPresenter(this);
    }

    @Override
    public void onDataSuccess(Object object, int apiRequestCode) {
        switch (apiRequestCode) {
            case REQUEST_GET_ACCOUNT_HISTORY:
                AccountStatementResponse response = (AccountStatementResponse) object;
                int code = response.getHttpStatusCode();
                switch (code) {
                    case AppConstants.SUCCESS_RESPONSE_CODE:
                        if (response.getValue() != null) {
                            paymentDetailList = response.getValue().getPayments();
                            float bankBalance = response.getValue().getBalance();
                            walletBalance.setText("INR " + String.format("%.02f", bankBalance));
                            userResponse.getValue().setBankBalance(bankBalance);
                            SharedPrefsUtils.saveLoggedUserObject(WalletBalanceActivity.this, userResponse);

                            if (paymentDetailList != null && paymentDetailList.size() > 0) {
                                adapter.updateData(paymentDetailList);
                                noDataTxt.setVisibility(View.GONE);
                            } else {
                                showNoDataText();
                            }

                        } else {
                            showNoDataText();
                        }
                        break;
                    case AppConstants.BAD_CREDENTIALS_CODE:
                        //Bad credentials error
                        logOutFromApp();

                        break;
                    case AppConstants.HTTP_500_ERROR_CODE:
                    case AppConstants.VALIDATION_ERROR_CODE:
                        UIUtils.showToast(this, response.getErrorDescription());
                        break;
                }
                break;

            case REQUEST_GET_STATEMENT_IN_EMAIL:
                GenericResponse genericResponse = (GenericResponse) object;
                int responseCode = genericResponse.getErrorCode();
                switch (responseCode) {
                    case AppConstants.SUCCESS_RESPONSE_CODE:
                        if (genericResponse.getValue() != null && !genericResponse.getValue().isEmpty())
                            UIUtils.showToast(this, genericResponse.getValue());
                        break;
                    case AppConstants.BAD_CREDENTIALS_CODE:
                        //Bad credentials error
                        logOutFromApp();

                        break;
                    case AppConstants.HTTP_500_ERROR_CODE:
                    case AppConstants.VALIDATION_ERROR_CODE:
                        UIUtils.showToast(this, genericResponse.getErrorDescription());
                        break;
                }
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


    public interface RemoveEmptyPaymentDetails{
        public void removeEmptyObject(List<Payment.PaymentDetail> paymentDetails);
    }

}
