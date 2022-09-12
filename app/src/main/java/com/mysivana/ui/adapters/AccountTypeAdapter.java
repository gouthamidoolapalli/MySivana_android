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

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.mysivana.R;

import java.util.List;

public class AccountTypeAdapter extends ArrayAdapter<String> implements SpinnerAdapter {

    private final LayoutInflater mInflater;
    List<String> accountList;

    public AccountTypeAdapter(@NonNull Context context, List<String> accountList) {
        super(context, R.layout.layout_acc_type_item);
        mInflater = LayoutInflater.from(getContext());
        this.accountList = accountList;
    }

    @Override
    public int getCount() {
        return accountList.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return accountList.get(position);
    }

    @Override
    public int getPosition(@Nullable String item) {
        return accountList.indexOf(item);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String accType = getItem(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_acc_type_item, parent, false);
        }
        TextView mAccItem = (TextView) convertView.findViewById(R.id.acc_item);
        mAccItem.setText(accType);
        return convertView;
    }


}
