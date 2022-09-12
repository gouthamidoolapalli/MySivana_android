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
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.mysivana.R;
import com.mysivana.mvp.BaseMvpActivity;
import com.mysivana.mvp.MainMvpPresenter;
import com.mysivana.mvp.MainMvpView;
import com.mysivana.mvp.model.Merchant;
import com.mysivana.ui.adapters.MerchantsAdapter;
import com.mysivana.util.AppConstants;
import com.mysivana.util.FileHelper;
import com.mysivana.util.FirebaseConstants;
import com.mysivana.util.NetworkUtil;
import com.mysivana.util.PermissionUtil;
import com.mysivana.util.SharedPrefsUtils;
import com.mysivana.util.SingleShotLocationProvider;
import com.mysivana.util.UIUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;

public class MerchantListingActivity extends BaseMvpActivity<MainMvpPresenter> implements MainMvpView {

    @BindView(R.id.rv_merchants)
    RecyclerView mRVMerchants;
    @BindView(R.id.no_data_txt)
    TextView mNoDataTxt;

    private MerchantsAdapter merchantsAdapter;

    private double currentLatitude = 0;
    private double currentLongitude = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int setContentLayoutId() {
        return R.layout.activity_merchant_listing;
    }

    @Override
    protected void init() {
        mFirebaseAnalytics.setCurrentScreen(this, FirebaseConstants.LAUNCH_MERCHANT_LIST,null);
        setSupportActionBar();
        mRVMerchants.setLayoutManager(new LinearLayoutManager(this));
        merchantsAdapter = new MerchantsAdapter(this);
        mRVMerchants.setAdapter(merchantsAdapter);


        if (SingleShotLocationProvider.isGPSEnabled(this)) {
            requestPermissions();
        } else {
            UIUtils.showCustomDialog(this, UIUtils.getStringFromResource(this, R.string.location_service), UIUtils.getStringFromResource(this, R.string.gps_not_enabled),
                    UIUtils.getStringFromResource(this, R.string.go_to_location_settings), UIUtils.getStringFromResource(this, R.string.cancel), positiveCallback,
                    _negativeCallback);

        }

    }

    private void requestPermissions() {
        if (!PermissionUtil.hasPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            PermissionUtil.checkPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION, 2, "", "", new PermissionUtil.ReqPermissionCallback() {
                @SuppressLint("MissingPermission")
                @Override
                public void onResult(boolean success) {
                    if (success) {
                        checkFineLocationPermission();
                    }
                }
            });
        } else {
            getMerchantList();
        }
    }

    private void checkFineLocationPermission() {
        if (!PermissionUtil.hasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            PermissionUtil.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, 2, "", "", new PermissionUtil.ReqPermissionCallback() {
                @SuppressLint("MissingPermission")
                @Override
                public void onResult(boolean success) {
                    getMerchantList();
                }
            });
        } else {
            getMerchantList();
        }
    }


    /**
     * Gets the current location's latlng and uses for sorting the response of merchant list.
     * Save details in a file to use when there's no network.
     */
    private void getMerchantList() {
        getListFromFile();
        if(FileHelper.readMerchantList(MerchantListingActivity.this) == null)
            showLoading();
        if (PermissionUtil.hasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) || PermissionUtil.hasPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION))
            SingleShotLocationProvider.requestSingleUpdate(MerchantListingActivity.this, new SingleShotLocationProvider.LocationCallback() {
                @Override
                public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                    currentLatitude = location.latitude;
                    currentLongitude = location.longitude;
                    if (NetworkUtil.getConnectivityStatus(MerchantListingActivity.this)) {
                        mPresenter.getMerchantList(SharedPrefsUtils.getLoggedUserObject(MerchantListingActivity.this).getValue().getUserId(),
                                SharedPrefsUtils.getLoggedUserObject(MerchantListingActivity.this).getValue().getUserApiKey());
                    }
                }
            });
    }

    /**
     * negative callback for GPS Dialog
     */
    UIUtils.DialogButtonClickListener _negativeCallback = new UIUtils.DialogButtonClickListener() {
        @Override
        public void onClickButton(Dialog d, View v) {
            d.dismiss();
            UIUtils.showToast(MerchantListingActivity.this, UIUtils.getStringFromResource(MerchantListingActivity.this, R.string.gps_not_enabled_error));
            getListFromFile();
        }
    };

    /**
     * sort the merchant list by distance
     *
     * @param merchant Object which holds list of merchants
     */
    private void sortMerchantList(Merchant merchant) {
        List<Merchant.Value> values = merchant.getValue();
        if (currentLatitude != 0 && currentLongitude != 0) {
            for (Merchant.Value value : values) {
                double distance = UIUtils.findDistance(currentLatitude, currentLongitude, Double.parseDouble(value.getLatitude()), Double.parseDouble(value.getLongitute()));
                value.setDistanceFromCurrentLocation(distance);
            }
            Collections.sort(values, new Comparator<Merchant.Value>() {
                @Override
                public int compare(Merchant.Value lhs, Merchant.Value rhs) {
                    return (lhs.getDistanceFromCurrentLocation()).compareTo(rhs.getDistanceFromCurrentLocation());
                }
            });
        }
        merchantsAdapter.updateData(values);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GPS_SETTINGS)
            requestPermissions();
    }

    @Override
    protected MainMvpPresenter createPresenter() {
        return new MainMvpPresenter(this);
    }


    @Override
    public void onDataSuccess(Object object, int apiRequestCode) {
        final Merchant merchant = (Merchant) object;
        int code = merchant.getErrorCode();
        switch (code) {
            case AppConstants.SUCCESS_RESPONSE_CODE:
                FileHelper.saveMerchantList(this, merchant);
                List<Merchant.Value> value = merchant.getValue();
                if (value != null && value.size() > 0) {
                    mRVMerchants.setVisibility(View.VISIBLE);
                    mNoDataTxt.setVisibility(View.GONE);
                    sortMerchantList(merchant);
                    FileHelper.saveMerchantList(MerchantListingActivity.this, merchant);
                } else {
                    mRVMerchants.setVisibility(View.GONE);
                    mNoDataTxt.setVisibility(View.VISIBLE);
                    mNoDataTxt.setText(getString(R.string.no_merchants));
                }
                break;
            case AppConstants.BAD_CREDENTIALS_CODE:
                logOutFromApp();
                break;
            case AppConstants.HTTP_500_ERROR_CODE:
            case AppConstants.VALIDATION_ERROR_CODE:
                if (!TextUtils.isEmpty(merchant.getErrorDescription()))
                    UIUtils.showToast(this, merchant.getErrorDescription());
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

    private void getListFromFile() {
        if (FileHelper.readMerchantList(this) != null)
            sortMerchantList(FileHelper.readMerchantList(this));
        else if (!NetworkUtil.getConnectivityStatus(this)) {
            showMessage(getString(R.string.internet_check));
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.onRequestPermissionResult(this, requestCode, permissions, grantResults);
    }
}
