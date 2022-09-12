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
 * JUNE 06 2018      : BYNDR       : Shows all the pending and completed transactions of the user.
 * ------------------------------------------------------------------------
 *
 * ========================================================================
 */
package com.mysivana.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.mysivana.R;
import com.mysivana.mvp.BaseMvpActivity;
import com.mysivana.mvp.MainMvpPresenter;
import com.mysivana.mvp.MainMvpView;
import com.mysivana.mvp.model.Transactions;
import com.mysivana.mvp.model.UserResponse;
import com.mysivana.ui.adapters.TransactionsAdapter;
import com.mysivana.util.AppConstants;
import com.mysivana.util.FileHelper;
import com.mysivana.util.FirebaseConstants;
import com.mysivana.util.NetworkUtil;
import com.mysivana.util.SharedPrefsUtils;
import com.mysivana.util.UIUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TransactionListActivity extends BaseMvpActivity<MainMvpPresenter> implements MainMvpView, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.rv_transactions)
    RecyclerView mRVTransactions;
    @BindView(R.id.no_data_txt)
    TextView mNoDataTxt;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    private TransactionsAdapter adapter;
    private List<Transactions.Value> values, completedValues, pendingValues;
    UserResponse userResponse;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }


    @Override
    protected int setContentLayoutId() {
        return R.layout.activity_transactions_list;
    }

    @Override
    protected void init() {

        mFirebaseAnalytics.setCurrentScreen(this, FirebaseConstants.LAUNCH_TRANSACTION_LIST,null);
        setSupportActionBar();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRVTransactions.setLayoutManager(mLayoutManager);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(mRVTransactions.getContext(),
                mLayoutManager.getOrientation());
        mRVTransactions.addItemDecoration(mDividerItemDecoration);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setRefreshing(false);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorPrimary));
        //hiding refresh progress bar. Accessing it from SwipeRefreshLayout class.
        try {
            Field f = swipeRefreshLayout.getClass().getDeclaredField("mCircleView");
            f.setAccessible(true);
            ImageView img = (ImageView) f.get(swipeRefreshLayout);
            img.setAlpha(0.0f);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        userResponse = SharedPrefsUtils.getLoggedUserObject(this);

        pendingValues = new ArrayList<>();
        completedValues = new ArrayList<>();
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.white));
        setTabNames();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setData();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        adapter = new TransactionsAdapter(this);
        mRVTransactions.setAdapter(adapter);
        if (NetworkUtil.getConnectivityStatus(this)) {
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    fetchTransactionList();

                }
            });
        } else {
            getListFromFile();
        }

    }

    private void setTabNames() {
        LayoutInflater inflater = LayoutInflater.from(this);
        TextView pendingView = (TextView) inflater.inflate(R.layout.tab_item, null);
        pendingView.setText(getString(R.string.pending_transactions));
        tabLayout.getTabAt(0).setCustomView(pendingView);
        TextView completedView = (TextView) inflater.inflate(R.layout.tab_item, null);
        completedView.setText(getString(R.string.completed_transactions));
        tabLayout.getTabAt(1).setCustomView(completedView);

    }

    /**
     * fetch transaction list from API
     */
    private void fetchTransactionList() {
        if (NetworkUtil.getConnectivityStatus(this)) {
            swipeRefreshLayout.setRefreshing(false);
            mPresenter.getTransactionList(String.valueOf(userResponse.getValue().getUserId()), userResponse.getValue().getUserApiKey());
        }
    }

    /**
     * Getting transaction list from file when there's no network
     */
    private void getListFromFile() {
        if (FileHelper.readTransactionList(this) != null) {
            values = FileHelper.readTransactionList(this).getValue();

            if (values != null && values.size() > 0) {
                for (Transactions.Value itemValue : values) {
                    if (itemValue.getStatus() != null) {
                        if (itemValue.getStatus().equalsIgnoreCase(AppConstants.TransactionStatus.PAID)||
                                itemValue.getStatus().equalsIgnoreCase(AppConstants.TransactionStatus.DEPOSIT_COMPLETED)) {
                            completedValues.add(itemValue);
                        } else {
                            pendingValues.add(itemValue);
                        }
                    }
                }

            }
            Collections.reverse(completedValues);
            Collections.reverse(pendingValues);
            setData();
        } else {
            //no data stored in file too
            showMessage(R.string.internet_check);
        }
    }

    @Override
    protected MainMvpPresenter createPresenter() {
        return new MainMvpPresenter(this);
    }

    @Override
    public void onDataSuccess(Object object, int apiRequestCode) {
        Transactions transactions = (Transactions) object;
        int code = transactions.getErrorCode();
        switch (code) {
            case AppConstants.SUCCESS_RESPONSE_CODE:
                FileHelper.saveTransactionList(this, transactions);
                if (values != null) {
                    values.clear();
                    completedValues.clear();
                    pendingValues.clear();
                }
                values = transactions.getValue();
                if (values != null && values.size() > 0) {
                    for (Transactions.Value itemValue : values) {
                        if (itemValue.getStatus() != null) {
                            if (itemValue.getStatus().equalsIgnoreCase(AppConstants.TransactionStatus.PAID) || itemValue.getStatus().equalsIgnoreCase(AppConstants.TransactionStatus.DEPOSIT_COMPLETED)) {
                                completedValues.add(itemValue);
                            } else {
                                pendingValues.add(itemValue);
                            }
                        }
                    }

                }
                setData();

                break;
            case AppConstants.BAD_CREDENTIALS_CODE:
                logOutFromApp();
                break;
            case AppConstants.HTTP_500_ERROR_CODE:
            case AppConstants.VALIDATION_ERROR_CODE:
                if(!TextUtils.isEmpty(transactions.getErrorDescription()))
                    UIUtils.showToast(this, transactions.getErrorDescription());
                break;
        }
    }

    /**
     * Update data in RecyclerView accordingly.
     */
    private void setData() {


        int tabPos = tabLayout.getSelectedTabPosition();
        switch (tabPos) {
            case 0:
                //Analytics Log
                Bundle b = new Bundle();
                b.putString(FirebaseConstants.PARAM_USER_ID, userResponse.getValue().getUserId());
                mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_ONGOING, b);

                if (pendingValues != null && pendingValues.size() > 0) {
                    adapter.updateData(pendingValues);
                    mRVTransactions.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                    mNoDataTxt.setVisibility(View.GONE);
                } else {
                    mRVTransactions.setVisibility(View.GONE);
                    mNoDataTxt.setVisibility(View.VISIBLE);
                    showNoDataMessage("Pending");
                    swipeRefreshLayout.setVisibility(View.GONE);
                }
                break;
            case 1:
                //Analytics Log
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseConstants.PARAM_USER_ID, userResponse.getValue().getUserId());
                mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_COMPLETED, bundle);

                if (completedValues != null && completedValues.size() > 0) {
                    mNoDataTxt.setVisibility(View.GONE);
                    mRVTransactions.setVisibility(View.VISIBLE);
                    adapter.updateData(completedValues);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                } else {
                    mRVTransactions.setVisibility(View.GONE);
                    mNoDataTxt.setVisibility(View.VISIBLE);
                    showNoDataMessage("Completed");
                    swipeRefreshLayout.setVisibility(View.GONE);
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
            getListFromFile();
        } else if (e instanceof IOException) {
            //called when there's network problem
            getListFromFile();
        } else {
            //other messages
            showMessage(e.getMessage());
        }
    }

    /**
     * called when there are no transactions (both pending and completed).
     *
     * @param strType
     */
    private void showNoDataMessage(String strType) {
        SpannableString ss;
        if (strType.equalsIgnoreCase("Pending"))
            ss = new SpannableString(getString(R.string.no_pending));
        else
            ss = new SpannableString(getString(R.string.no_completed));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                finish();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(true);
            }
        };
        if (strType.equalsIgnoreCase("Pending"))
            ss.setSpan(clickableSpan, 25, 35, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        else
            ss.setSpan(clickableSpan, 27, 37, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        mNoDataTxt.setText(ss);
        mNoDataTxt.setMovementMethod(LinkMovementMethod.getInstance());
        mNoDataTxt.setHighlightColor(Color.TRANSPARENT);
    }

    @Override
    public void onRefresh() {

        fetchTransactionList();
    }
}
