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
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.ListView;

import com.google.android.gms.location.places.Place;
import com.mysivana.R;
import com.mysivana.custom.AutoCompleteLocation;
import com.mysivana.mvp.BaseMvpActivity;
import com.mysivana.mvp.MainMvpPresenter;
import com.mysivana.mvp.MainMvpView;
import com.mysivana.mvp.model.PlaceData;
import com.mysivana.util.AppConstants;
import com.mysivana.util.FirebaseConstants;
import com.mysivana.util.NetworkUtil;
import com.mysivana.util.PermissionUtil;
import com.mysivana.util.SingleShotLocationProvider;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;

public class PickLocationActivity extends BaseMvpActivity<MainMvpPresenter> implements MainMvpView, AutoCompleteLocation.AutoCompleteLocationListener {


    @BindView(R.id.autocomplete_location)
    AutoCompleteLocation mAutoCompleteLocation;
    @BindView(R.id.lvLocations)
    ListView mLVLocations;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int setContentLayoutId() {
        return R.layout.activity_pick_location;
    }

    @Override
    protected void init() {
        setSupportActionBar();
        if (NetworkUtil.getConnectivityStatus(this)) {
            if (SingleShotLocationProvider.isGPSEnabled(this)) {
                requestPermissions();
            } else {
                promptGPSDialog();
            }
        } else {
            showMessage(getString(R.string.internet_check));
        }
        mAutoCompleteLocation.setAutoCompleteTextListener(this);
        mAutoCompleteLocation.setListView(mLVLocations);
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
            setCountryPreference();
        }

    }

    private void checkFineLocationPermission() {
        if (!PermissionUtil.hasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            PermissionUtil.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, 2, "", "", new PermissionUtil.ReqPermissionCallback() {
                @SuppressLint("MissingPermission")
                @Override
                public void onResult(boolean success) {
                    if (success) {
                        setCountryPreference();
                    }
                }
            });
        } else {
            setCountryPreference();
        }
    }

    /**
     * Country preference is set as the default Locale of the device.
     */
    public void setCountryPreference() {
        SingleShotLocationProvider.requestSingleUpdate(PickLocationActivity.this, new SingleShotLocationProvider.LocationCallback() {
            @Override
            public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                Geocoder gcd = new Geocoder(PickLocationActivity.this, Locale.getDefault());
                List<Address> addresses = null;

                try {
                    addresses = gcd.getFromLocation(location.latitude, location.longitude, 1);
                    if (addresses != null && addresses.size() > 0) {
                        String countryCode = addresses.get(0).getCountryCode();
                        mAutoCompleteLocation.createAutoCompleteFilter(countryCode);

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    public void onAttachedToWindow() {
    }


    @Override
    protected MainMvpPresenter createPresenter() {
        return new MainMvpPresenter(this);
    }


    @Override
    public void onTextClear() {
    }

    @Override
    public void onItemSelected(Place selectedPlace) {

        //Analytics Log
        Bundle b = new Bundle();
        mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_LOCATION_SELECTED, b);

        PlaceData placeData = new PlaceData();
        placeData.setAddress((String) selectedPlace.getAddress());
        placeData.setAttributions((String) selectedPlace.getAttributions());
        placeData.setId(selectedPlace.getId());
        placeData.setLatLng(selectedPlace.getLatLng());
        placeData.setLocale(selectedPlace.getLocale());
        placeData.setName((String) selectedPlace.getName());
        placeData.setPhoneNumber((String) selectedPlace.getPhoneNumber());
        placeData.setPlaceTypes(selectedPlace.getPlaceTypes());
        placeData.setViewport(selectedPlace.getViewport());
        placeData.setWebsiteUri(selectedPlace.getWebsiteUri());
        Intent intent = new Intent();
        intent.putExtra(AppConstants.BundleParams.PLACE, placeData);
        setResult(RESULT_OK, intent);
        finish();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.onRequestPermissionResult(this, requestCode, permissions, grantResults);
    }

    @Override
    public void onDataSuccess(Object object, int apiRequestCode) {

    }

    @Override
    public void onDataFailure(Throwable e) {

    }


}
