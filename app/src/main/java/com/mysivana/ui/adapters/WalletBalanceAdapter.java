package com.mysivana.ui.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mysivana.R;
import com.mysivana.custom.MSTextView;
import com.mysivana.mvp.model.Payment;
import com.mysivana.ui.WalletBalanceActivity;
import com.mysivana.util.AppConstants;
import com.mysivana.util.UIUtils;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WalletBalanceAdapter extends RecyclerView.Adapter<WalletBalanceAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private Activity context;
    List<Payment> paymentDetailList;
    WalletBalanceActivity.RemoveEmptyPaymentDetails removeEmptyPaymentDetails;

    public WalletBalanceAdapter(Activity context, WalletBalanceActivity.RemoveEmptyPaymentDetails removeEmptyPaymentDetails) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.removeEmptyPaymentDetails = removeEmptyPaymentDetails;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_wallet_balnce, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Payment paymentItem = paymentDetailList.get(position);
        holder.transactionCode.setText("Code: " + paymentItem.getCode());
        String deliveryTime = paymentItem.getDate();
        Date date = UIUtils.formatTransactionDate(deliveryTime != null ? deliveryTime : "");
        if (date != null) {
            String day = (String) DateFormat.format("dd", date);
            String month = (String) DateFormat.format("MMMM", date);
            String year = (String) DateFormat.format("yyyy", date);
            holder.transactionDate.setText(day + " " + month + " " + year);
        }
        List<Payment.PaymentDetail> paymentDetailsList = paymentItem.getPaymentDetails();
        if (paymentDetailsList != null && paymentDetailsList.size() > 0) {
            if (paymentItem.getPaymentType().equalsIgnoreCase(AppConstants.PaymentType.REMITTANCE)) {
                holder.imageView.setImageResource(R.drawable.ic_paidicon);

                float cashPaidAmt = paymentDetailsList.get(0).getValue();
                holder.cashPaidText.setText(paymentDetailsList.get(0).getPaymentType());
                holder.cashPaidValue.setText("+" + String.format("%.02f", (cashPaidAmt != 0) ? cashPaidAmt : "00.00"));

                holder.serviceChargeText.setVisibility(View.VISIBLE);
                holder.serviceChargeText.setText(paymentDetailsList.get(1).getPaymentType());
                float serviceChargeAmt = paymentDetailsList.get(1).getValue();
                holder.serviceChargeValue.setText("+" + String.format("%.02f", (serviceChargeAmt != 0) ? serviceChargeAmt : "00.00"));
                holder.serviceChargeValue.setVisibility(View.VISIBLE);
            } else {
                holder.imageView.setImageResource(R.drawable.ic_receivedicon);

                holder.cashPaidText.setText(paymentDetailsList.get(0).getPaymentType());
                float cashPaidAmt = paymentDetailsList.get(0).getValue();
                holder.cashPaidValue.setText("-" + String.format("%.02f", (cashPaidAmt != 0) ? cashPaidAmt : "00.00"));
                holder.serviceChargeValue.setVisibility(View.GONE);
                holder.serviceChargeText.setVisibility(View.GONE);
            }
        } else {
            removeEmptyPaymentDetails.removeEmptyObject(paymentDetailsList);
            notifyDataSetChanged();
        }

    }

    @Override
    public int getItemCount() {
        return (paymentDetailList != null && !paymentDetailList.isEmpty()) ? paymentDetailList.size() : 0;
    }

    public void updateData(List<Payment> paymentDetailList) {
        this.paymentDetailList = paymentDetailList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.image_view)
        ImageView imageView;
        @BindView(R.id.cash_paid_value)
        MSTextView cashPaidValue;
        @BindView(R.id.service_charge_value)
        MSTextView serviceChargeValue;
        @BindView(R.id.transaction_code)
        MSTextView transactionCode;
        @BindView(R.id.transaction_date)
        MSTextView transactionDate;
        @BindView(R.id.code_layout)
        LinearLayout codeLayout;
        @BindView(R.id.cash_paid_text)
        MSTextView cashPaidText;
        @BindView(R.id.service_charge_text)
        MSTextView serviceChargeText;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
