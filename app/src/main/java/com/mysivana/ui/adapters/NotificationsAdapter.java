package com.mysivana.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mysivana.R;
import com.mysivana.mvp.model.NotificationListResponse;
import com.mysivana.mvp.model.UserResponse;
import com.mysivana.ui.DashboardMerchantActivity;
import com.mysivana.ui.DashboardUserActivity;
import com.mysivana.ui.NotificationAttachmentsActivity;
import com.mysivana.util.AppConstants;
import com.mysivana.util.CommonUtils;
import com.mysivana.util.SharedPrefsUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.mysivana.util.AppConstants.Notifications.RECEIVER_COUNTRY_CODE;
import static com.mysivana.util.AppConstants.Notifications.RECEIVER_EMAIL;
import static com.mysivana.util.AppConstants.Notifications.RECEIVER_FULL_NAME;
import static com.mysivana.util.AppConstants.Notifications.RECEIVER_PHONE_NUMBER;
import static com.mysivana.util.AppConstants.Notifications.REQUESTED_AMOUNT;
import static com.mysivana.util.AppConstants.Notifications.REQUESTED_DEPOSIT_TYPE;
import static com.mysivana.util.AppConstants.Notifications.TRANSACTION_REQUEST_ID;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {
    public static final String NOTES_KEY = "Notes";
    public static final String ATTACHMENT_KEY = "Attachment";
    public static final String CONTENT_TYPE = "ContentType";

    private LayoutInflater mInflater;
    List<NotificationListResponse.Value> notificationsList;
    Activity context;
    UserResponse userResponse;

    public NotificationsAdapter(Activity context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        userResponse = SharedPrefsUtils.getLoggedUserObject(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_notifications, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationListResponse.Value item = getItem(position);
        holder.notificationMessage.setText(item.getMessage() + "");
        if(item.getAttachmentUrl() != null && !item.getAttachmentUrl().isEmpty()){
            holder.requestedAttachment.setVisibility(View.VISIBLE);
        }else{
            holder.requestedAttachment.setVisibility(View.GONE);
        }
//        holder.requestedAmount.setText(item.getAmountInFiat() + "");
    }

    @Override
    public int getItemCount() {
        return (notificationsList != null && notificationsList.size() > 0) ? notificationsList.size() : 0;
    }

    public NotificationListResponse.Value getItem(int position) {
        return notificationsList.get(position);
    }


    public void setNotificationList(List<NotificationListResponse.Value> notificationsList) {
        this.notificationsList = notificationsList;
        notifyDataSetChanged();

    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.notification_message)
        TextView notificationMessage;
        @BindView(R.id.requested_attachment)
        ImageView requestedAttachment;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            requestedAttachment.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            NotificationListResponse.Value item = getItem(getAdapterPosition());
            switch (v.getId()) {
                case R.id.requested_attachment:
                    Bundle b = new Bundle();
                    b.putString(NOTES_KEY,item.getMessage());
                    b.putString(ATTACHMENT_KEY, item.getAttachmentUrl());
                    b.putString(CONTENT_TYPE,item.getContentType());
                    CommonUtils.startActivity(context, NotificationAttachmentsActivity.class,b);
                    break;
                default:

                    Intent intent;
                    if (userResponse.getValue().getUserType().equalsIgnoreCase(AppConstants.USER_TYPE_MERCHANT)) {
                        intent = new Intent(context, DashboardMerchantActivity.class);
                    } else {
                        intent = new Intent(context, DashboardUserActivity.class);
                    }
                    intent.putExtra(REQUESTED_AMOUNT, item.getAmountInFiat() + "");
                    intent.putExtra(RECEIVER_FULL_NAME, item.getReceiverFullName());
                    intent.putExtra(RECEIVER_PHONE_NUMBER, item.getReceiverPhoneNumber());
                    intent.putExtra(RECEIVER_COUNTRY_CODE, item.getReceiverCountryCode());
                    intent.putExtra(RECEIVER_EMAIL, item.getReceiverEmail());
                    intent.putExtra(TRANSACTION_REQUEST_ID, item.getTransactionRequestId() + "");
                    intent.putExtra(REQUESTED_DEPOSIT_TYPE, item.getPaymentType()+"");

                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);
                    context.finish();
                    break;
            }
        }
    }
}
