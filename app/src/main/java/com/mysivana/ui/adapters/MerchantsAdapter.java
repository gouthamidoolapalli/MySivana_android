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
 * JUNE 06 2018      : BYNDR       : CREATED.
 * ------------------------------------------------------------------------
 *
 * ========================================================================
 */
package com.mysivana.ui.adapters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mysivana.R;
import com.mysivana.mvp.BaseMvpActivity;
import com.mysivana.mvp.model.Merchant;
import com.mysivana.mvp.model.UserResponse;
import com.mysivana.util.FirebaseConstants;
import com.mysivana.util.PermissionUtil;
import com.mysivana.util.SharedPrefsUtils;
import com.mysivana.util.SingleShotLocationProvider;
import com.mysivana.util.UIUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MerchantsAdapter extends RecyclerView.Adapter<MerchantsAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private List<Merchant.Value> merchantList;
    private Context context;

    public MerchantsAdapter(Context context) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
    }

    public void updateData(List<Merchant.Value> merchantList) {
        this.merchantList = merchantList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_merchant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Merchant.Value value = merchantList.get(position);
        holder.mBusinessName.setText(value.getBusinessName());

        holder.mBusinessAddress.setText(value.getAddress());
        holder.mEmail.setText(value.getEmail());
        holder.mName.setText(value.getFullName());
        holder.mBusinessName.setTag(value);
    }

    @Override
    public int getItemCount() {
        return merchantList != null ? merchantList.size() : 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_business_name)
        TextView mBusinessName;
        @BindView(R.id.tv_business_address)
        TextView mBusinessAddress;
        @BindView(R.id.tv_full_name)
        TextView mName;
        @BindView(R.id.tv_customer_email)
        TextView mEmail;
        @BindView(R.id.tv_contact)
        TextView mContact;
        @BindView(R.id.tv_direction)
        TextView mDirection;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mName.setEnabled(false);
            mEmail.setEnabled(false);
            mContact.setOnClickListener(this);
            mDirection.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            final Merchant.Value value = (Merchant.Value) mBusinessName.getTag();
            UserResponse userResponse = SharedPrefsUtils.getLoggedUserObject(context);
            switch (view.getId()) {
                case R.id.tv_contact:

                    //log Analytics
                    Bundle b = new Bundle();
                    b.putString(FirebaseConstants.PARAM_USER_ID, userResponse.getValue().getUserId());
                    ((BaseMvpActivity)context).mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_CALL_MERCHANT,b);

                    final Intent callintent = new Intent(Intent.ACTION_CALL);
                    callintent.setData(Uri.parse("tel:" + value.getCountryCode() + value.getPhoneNumber()));
                    PermissionUtil.checkPermission((Activity) context, Manifest.permission.CALL_PHONE, 1, "", "", new PermissionUtil.ReqPermissionCallback() {
                        @SuppressLint("MissingPermission")
                        @Override
                        public void onResult(boolean success) {
                            if (success) {
                                context.startActivity(callintent);
                            }
                        }
                    });
                    break;

                case R.id.tv_direction:

                    //log Analytics
                    Bundle bun = new Bundle();
                    bun.putString(FirebaseConstants.PARAM_USER_ID, userResponse.getValue().getUserId());
                    ((BaseMvpActivity)context).mFirebaseAnalytics.logEvent(FirebaseConstants.CLICK_GET_DIRECTIONS,bun);

                    if (SingleShotLocationProvider.isGPSEnabled(context)) {
                        PermissionUtil.checkPermission((Activity) context, Manifest.permission.ACCESS_FINE_LOCATION, 2, "", "", new PermissionUtil.ReqPermissionCallback() {
                            @SuppressLint("MissingPermission")
                            @Override
                            public void onResult(boolean success) {
                                if (success) {

                                    SingleShotLocationProvider.requestSingleUpdate(context, new SingleShotLocationProvider.LocationCallback() {
                                        @Override
                                        public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                                            // to create a route between current mTVLocation and desired address
                                            Uri navigationUri = Uri.parse("google.navigation:q=" + value.getAddress());
                                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, navigationUri);
                                            mapIntent.setPackage("com.google.android.apps.maps");
                                            context.startActivity(mapIntent);
                                        }
                                    });

                                } else {
                                }
                            }
                        });
                    } else {

                        UIUtils.showCustomDialog(context, UIUtils.getStringFromResource(context, R.string.location_service), UIUtils.getStringFromResource(context, R.string.gps_not_enabled),
                                UIUtils.getStringFromResource(context, R.string.go_to_location_settings), UIUtils.getStringFromResource(context, R.string.cancel), positiveCallback,
                                negativeCallback);

                    }

                    break;
            }
        }
    }

    public Merchant.Value getItem(int id) {
        return merchantList.get(id);
    }


    /**
     *  positive callback for GPS Dialog
     */
    public UIUtils.DialogButtonClickListener positiveCallback = new UIUtils.DialogButtonClickListener() {
        @Override
        public void onClickButton(Dialog d, View v) {
            d.dismiss();
            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            context.startActivity(myIntent);
        }
    };
    /**
     *  negative callback for GPS Dialog
     */
    UIUtils.DialogButtonClickListener negativeCallback = new UIUtils.DialogButtonClickListener() {
        @Override
        public void onClickButton(Dialog d, View v) {
            d.dismiss();
            UIUtils.showToast(context, UIUtils.getStringFromResource(context, R.string.gps_not_enabled_error));


        }
    };


}
