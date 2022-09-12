package com.mysivana.ui;

import android.app.NotificationManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.mysivana.R;
import com.mysivana.mvp.BaseMvpActivity;
import com.mysivana.mvp.MainMvpPresenter;
import com.mysivana.mvp.MainMvpView;
import com.mysivana.mvp.model.GenericResponse;
import com.mysivana.mvp.model.NotificationListResponse;
import com.mysivana.mvp.model.UserResponse;
import com.mysivana.ui.adapters.NotificationsAdapter;
import com.mysivana.util.AppConstants;
import com.mysivana.util.CommonUtils;
import com.mysivana.util.FirebaseConstants;
import com.mysivana.util.SharedPrefsUtils;
import com.mysivana.util.UIUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationsActivity extends BaseMvpActivity<MainMvpPresenter> implements MainMvpView {
    @BindView(R.id.notification_rcv)
    RecyclerView notificationRcv;
    @BindView(R.id.no_data_txt)
    TextView mNoDataTxt;

    List<NotificationListResponse.Value> notificationsList;
    UserResponse userResponse;
    NotificationsAdapter adapter;

    @Override
    protected int setContentLayoutId() {
        return R.layout.activity_notifications;
    }

    @Override
    protected void init() {
        setSupportActionBar();
        mFirebaseAnalytics.setCurrentScreen(this, FirebaseConstants.LAUNCH_NOTIFICATION_SCREEN, null);
        userResponse = SharedPrefsUtils.getLoggedUserObject(this);

        notificationRcv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationsAdapter(this);
        notificationRcv.setAdapter(adapter);

        CommonUtils.clearNotifications(this);

        mPresenter.notificationsList(userResponse.getValue().getUserId(),userResponse.getValue().getUserApiKey());


    }

    @Override
    protected MainMvpPresenter createPresenter() {
        return new MainMvpPresenter(this);
    }

    @Override
    public void onDataSuccess(Object object, int apiRequestCode) {
        NotificationListResponse response = (NotificationListResponse) object;
        int code = response.getHttpStatusCode();
        switch (code) {
            case AppConstants.SUCCESS_RESPONSE_CODE:
                notificationsList = response.getValue();
                if(notificationsList != null && notificationsList.size()>0){
                    mNoDataTxt.setVisibility(View.GONE);
                    notificationRcv.setVisibility(View.VISIBLE);
                    adapter.setNotificationList(notificationsList);
                }else{
                    mNoDataTxt.setText(getString(R.string.no_notifications));
                    mNoDataTxt.setVisibility(View.VISIBLE);
                    notificationRcv.setVisibility(View.GONE);
                }
                break;
            case AppConstants.BAD_CREDENTIALS_CODE:
                logOutFromApp();
                break;
            case AppConstants.HTTP_500_ERROR_CODE:
            case AppConstants.VALIDATION_ERROR_CODE:
                UIUtils.showToastOnDashboard(this, response.getErrorDescription());
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
