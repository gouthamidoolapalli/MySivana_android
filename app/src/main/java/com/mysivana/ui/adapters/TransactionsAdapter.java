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
package com.mysivana.ui.adapters;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mysivana.R;
import com.mysivana.custom.MSTextView;
import com.mysivana.custom.TimeLineView;
import com.mysivana.mvp.BaseMvpActivity;
import com.mysivana.mvp.model.Transactions;
import com.mysivana.mvp.model.UserResponse;
import com.mysivana.ui.ReportIssueActivity;
import com.mysivana.ui.TransactionReceiptActivity;
import com.mysivana.util.AppConstants;
import com.mysivana.util.CommonUtils;
import com.mysivana.util.FirebaseConstants;
import com.mysivana.util.SharedPrefsUtils;
import com.mysivana.util.UIUtils;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private Activity context;
    private List<Transactions.Value> transactionsList;
    private int lastExpandedPosition = 0;

    public TransactionsAdapter(Activity context) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.transaction_stepper, parent, false);
        return new TransactionsAdapter.ViewHolder(view);
    }

    /**
     * Used to update the list with given list of transactions.
     *
     * @param transactionsList data list to be updated in view.
     */
    public void updateData(List<Transactions.Value> transactionsList) {
        this.transactionsList = transactionsList;
        lastExpandedPosition = 0;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transactions.Value value = transactionsList.get(position);
        String status = value.getStatus();
        if (status.equalsIgnoreCase(AppConstants.TransactionStatus.PAID)) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins((int) context.getResources().getDimension(R.dimen.activity_horizontal_margin), (int) context.getResources().getDimension(R.dimen.dimen_5), 0, (int) context.getResources().getDimension(R.dimen.dimen_5));
            holder.mTVBitcoinValue.setLayoutParams(params);
            holder.merchantLayout.setVisibility(View.VISIBLE);
            holder.merchantName.setText(value.getServiceProvierFullName());
            holder.mTVLocation.setText(value.getCity());
            holder.mTVLocation.setVisibility(View.VISIBLE);
            holder.mTVLocation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location, 0, 0, 0);
            holder.detailsLayout.setVisibility(View.VISIBLE);
            String deliveryTime = value.getDeliveredTime();
            Date date = UIUtils.formatTransactionDate(deliveryTime != null ? deliveryTime : "");
            if (date != null) {
                String day = (String) DateFormat.format("dd", date);
                String month = (String) DateFormat.format("MM", date);
                String year = (String) DateFormat.format("yyyy", date);
                String hour = (String) DateFormat.format("HH", date);
                String minute = (String) DateFormat.format("mm", date);
                holder.date.setText(context.getString(R.string.format_transaction_date, day, month, year));
                holder.time.setText(context.getString(R.string.format_transaction_hour, hour, minute));
            }
        } else {
            UserResponse response = SharedPrefsUtils.getLoggedUserObject(context);
            if (response.getValue().getUserType() != null && response.getValue().getUserType().equalsIgnoreCase(AppConstants.USER_TYPE_SENDER_RECEIVER)) {
                if (status.equalsIgnoreCase(AppConstants.TransactionStatus.DEPOSIT_COMPLETED) || status.equalsIgnoreCase(AppConstants.TransactionStatus.PENDING_DEPOSIT)) {
                    holder.mTVLocation.setVisibility(View.VISIBLE);
                    holder.mTVLocation.setText(" Bank Deposit");
                    holder.mTVLocation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bank, 0, 0, 0);
                } else {
                    holder.mTVLocation.setVisibility(View.VISIBLE);
                    holder.mTVLocation.setText(" Cash Pickup");
                    holder.mTVLocation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cash_pickup, 0, 0, 0);
                }
            } else {

                if (!TextUtils.isEmpty(value.getCity())) {
                    holder.mTVLocation.setVisibility(View.VISIBLE);
                    holder.mTVLocation.setText(value.getCity());
                    holder.mTVLocation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location, 0, 0, 0);
                } else {
                    holder.mTVLocation.setVisibility(View.INVISIBLE);
                }
            }
            holder.merchantLayout.setVisibility(View.GONE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins((int) context.getResources().getDimension(R.dimen.activity_horizontal_margin), (int) context.getResources().getDimension(R.dimen.dimen_5), 0, (int) context.getResources().getDimension(R.dimen.dimen_5));
            holder.mTVBitcoinValue.setLayoutParams(params);
            String createdDate = value.getCreatedDate();
            Date date = UIUtils.formatTransactionDate(createdDate != null ? createdDate : "");
            if (date != null) {
                String day = (String) DateFormat.format("dd", date);
                String month = (String) DateFormat.format("MM", date);
                String year = (String) DateFormat.format("yyyy", date);
                String hour = (String) DateFormat.format("HH", date);
                String minute = (String) DateFormat.format("mm", date);
                holder.date.setText(context.getString(R.string.format_transaction_date, day, month, year));
                holder.time.setText(context.getString(R.string.format_transaction_hour, hour, minute));
            }
        }


        holder.mTVBitcoinValue.setText(String.format("%.8f", value.getTotalCrypto()));
        holder.rupeeFigure.setText(String.valueOf(value.getFiatValue()));
        holder.transactionCodeFigure.setText(value.getTransactionCode());
        holder.receiverName.setText(value.getReceiverFullName());
        if (value.getCryptoCode() != null) {
            switch (value.getCryptoCode()) {
                case AppConstants.CryptoCodes.BTC:
                    holder.bitcoinImg.setMarker(context.getResources().getDrawable(R.drawable.ic_bitcoin_original));
                    break;
                case AppConstants.CryptoCodes.ETH:
                    holder.bitcoinImg.setMarker(context.getResources().getDrawable(R.drawable.ic_ethereum_original));

                    break;
            }
        }
        else{
            holder.bitcoinImg.setMarker(context.getResources().getDrawable(R.drawable.ic_bitcoin_original));
        }



        if (transactionsList.get(position).isExpanded()) {
            holder.bitcoinImg.showBottomLine(true);
            holder.bitcoinImg.requestLayout();
            holder.showDetails.setVisibility(View.VISIBLE);
            holder.onClickExpandBtn.setRotation(-90.0f);
        } else {
            holder.bitcoinImg.showBottomLine(false);
            holder.bitcoinImg.requestLayout();
            holder.showDetails.setVisibility(View.GONE);
            holder.onClickExpandBtn.setRotation(90.0f);
        }
    }

    @Override
    public int getItemCount() {
        return transactionsList != null ? transactionsList.size() : 0;
    }


    /**
     * Class which hold the objects of list item and helps in binding them to RecyclerView
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.my_transaction_bar)
        LinearLayout mTransactionBar;
        @BindView(R.id.bitcoin_img)
        TimeLineView bitcoinImg;
        @BindView(R.id.tv_bitcoin_value)
        MSTextView mTVBitcoinValue;
        @BindView(R.id.location)
        MSTextView mTVLocation;
        @BindView(R.id.date)
        MSTextView date;
        @BindView(R.id.time)
        MSTextView time;
        @BindView(R.id.rupee_figure)
        MSTextView rupeeFigure;
        @BindView(R.id.merchant_name)
        MSTextView merchantName;
        @BindView(R.id.receiver_name)
        MSTextView receiverName;
        @BindView(R.id.transaction_code_figure)
        MSTextView transactionCodeFigure;
        @BindView(R.id.show_details)
        LinearLayout showDetails;
        @BindView(R.id.on_click_expand_btn)
        ImageView onClickExpandBtn;
        @BindView(R.id.view_transaction_receipt)
        Button viewTransactionReceipt;
        @BindView(R.id.report_issue)
        TextView reportIssue;
        @BindView(R.id.merchant_marker)
        TimeLineView merchantMarker;
        @BindView(R.id.merchant_layout)
        LinearLayout merchantLayout;
        @BindView(R.id.details_layout)
        LinearLayout detailsLayout;


        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mTransactionBar.setOnClickListener(this);
            viewTransactionReceipt.setOnClickListener(this);
            reportIssue.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.my_transaction_bar:
                    if (transactionsList.get(getAdapterPosition()).isExpanded()) {
                        transactionsList.get(getAdapterPosition()).setExpanded(false);

                    } else {
                        transactionsList.get(getAdapterPosition()).setExpanded(true);
                    }
                    if (lastExpandedPosition != getAdapterPosition()) {
                        transactionsList.get(lastExpandedPosition).setExpanded(false);
                        lastExpandedPosition = getAdapterPosition();
                    }
                    notifyDataSetChanged();
                    break;
                case R.id.view_transaction_receipt:
                    //Analytics Log
                    Bundle b = new Bundle();
                    b.putString(FirebaseConstants.PARAM_USER_ID, SharedPrefsUtils.getLoggedUserObject(context).getValue().getUserId());
                    ((BaseMvpActivity) context).getmFirebaseAnalytics().logEvent(FirebaseConstants.CLICK_VIEW_RECEIPT, b);

                    Bundle bundle = new Bundle();
                    bundle.putString(TransactionReceiptActivity.TRANSACTION_CODE, transactionsList.get(getAdapterPosition()).getTransactionCode());
                    bundle.putParcelable(TransactionReceiptActivity.TRANSACTION_DETAILS, transactionsList.get(getAdapterPosition()));
                    CommonUtils.startActivity(context, TransactionReceiptActivity.class, bundle);
                    break;
                case R.id.report_issue:
                    //Analytics Log
                    Bundle bun = new Bundle();
                    bun.putString(FirebaseConstants.PARAM_USER_ID, SharedPrefsUtils.getLoggedUserObject(context).getValue().getUserId());
                    ((BaseMvpActivity) context).getmFirebaseAnalytics().logEvent(FirebaseConstants.CLICK_HERE_REPORT, bun);

                    Bundle _bundle = new Bundle();
                    _bundle.putParcelable(ReportIssueActivity.TRANSACTION_DETAILS, transactionsList.get(getAdapterPosition()));
                    CommonUtils.startActivity(context, ReportIssueActivity.class, _bundle);

                    break;
            }
        }
    }
}
