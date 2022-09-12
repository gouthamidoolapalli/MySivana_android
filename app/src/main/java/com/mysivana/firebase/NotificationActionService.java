package com.mysivana.firebase;

import android.app.IntentService;
import android.content.Intent;

import com.mysivana.ui.DashboardMerchantActivity;
import com.mysivana.ui.DashboardUserActivity;
import com.mysivana.util.AppConstants;
import com.mysivana.util.SharedPrefsUtils;

import static com.mysivana.util.AppConstants.Notifications.RECEIVER_COUNTRY_CODE;
import static com.mysivana.util.AppConstants.Notifications.RECEIVER_EMAIL;
import static com.mysivana.util.AppConstants.Notifications.RECEIVER_FULL_NAME;
import static com.mysivana.util.AppConstants.Notifications.RECEIVER_PHONE_NUMBER;
import static com.mysivana.util.AppConstants.Notifications.REQUESTED_AMOUNT;
import static com.mysivana.util.AppConstants.Notifications.REQUESTED_DEPOSIT_TYPE;
import static com.mysivana.util.AppConstants.Notifications.REQUESTED_SENDER_USERTYPE;
import static com.mysivana.util.AppConstants.Notifications.TRANSACTION_REQUEST_ID;

public class NotificationActionService extends IntentService {

    private String requestedAmount, receiverEmail, receiverName, receiverPhone, receiverCountryCode, transactionRequestId, senderUserType, paymentType;
    private int notificationCount;

    public NotificationActionService() {
        super(NotificationActionService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        receiverName = intent.getStringExtra(RECEIVER_FULL_NAME);
        receiverEmail = intent.getStringExtra(RECEIVER_EMAIL);
        receiverPhone = intent.getStringExtra(RECEIVER_PHONE_NUMBER);
        receiverCountryCode = intent.getStringExtra(RECEIVER_COUNTRY_CODE);
        requestedAmount = intent.getStringExtra(REQUESTED_AMOUNT);
        transactionRequestId = intent.getStringExtra(TRANSACTION_REQUEST_ID);
        senderUserType = intent.getStringExtra(REQUESTED_SENDER_USERTYPE);
        paymentType = intent.getStringExtra(REQUESTED_DEPOSIT_TYPE);
        Intent _intent;
        if (senderUserType.equalsIgnoreCase(AppConstants.USER_TYPE_MERCHANT)) {
            _intent = new Intent(this, DashboardMerchantActivity.class);
        } else {
            _intent = new Intent(this, DashboardUserActivity.class);
        }
        _intent.putExtra(REQUESTED_AMOUNT, requestedAmount);
        _intent.putExtra(RECEIVER_FULL_NAME, receiverName);
        _intent.putExtra(RECEIVER_PHONE_NUMBER, receiverPhone);
        _intent.putExtra(RECEIVER_COUNTRY_CODE, receiverCountryCode);
        _intent.putExtra(RECEIVER_EMAIL, receiverEmail);
        _intent.putExtra(TRANSACTION_REQUEST_ID, transactionRequestId);
        _intent.putExtra(REQUESTED_DEPOSIT_TYPE, paymentType);
        _intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(_intent);

        notificationCount = SharedPrefsUtils.getNotificationCount(this);
        if (notificationCount > 0) {
            notificationCount--;
        }
        SharedPrefsUtils.setNotificationCount(this, notificationCount);

        //notify activity with count
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(AppConstants.INTENT_ACTION_NEW_NOTIFICATION);
        sendBroadcast(broadcastIntent);
    }
}
