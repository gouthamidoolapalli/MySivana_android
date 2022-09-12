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
package com.mysivana.util;

public interface AppConstants {
    String COOKIES = "COOKIES";
    String USER_TYPE_MERCHANT = "MERCHANT";
    String USER_TYPE_SENDER_RECEIVER = "SENDER/RECEIVER";
    String DEPOSIT_BANK = "BANK DEPOSIT";
    String DEPOSIT_CASH = "PICKUP CASH";
    String ERROR_MESSAGE_FEEDBACK = "ErrorMessageFeedback";
    int SUCCESS_RESPONSE_CODE = 200;
    int BAD_CREDENTIALS_CODE = 401;
    int VALIDATION_ERROR_CODE = 400;
    int HTTP_500_ERROR_CODE = 500;
    int USER_DOES_NOT_EXIST = 404;

    String INTENT_ACTION_NEW_NOTIFICATION = "Action.Notification";

    String NAVIGATE_FROM = "NavigateFrom";

    interface BundleParams {
        String PLACE = "place";
        String CREATE_USER = "createUser";
        String PAYMENT_RESPONSE = "paymentResponse";
        String OTP_COMPLETE_ACTIVITY = "otpcompleteactivity";
        String ERROR = "error";
        String THEME = "theme";
    }

    interface Preferences {
        String USER_OBJECT = "User";
        String REFERRAL_CODE = "ReferralCode";
        String FCM_TOKEN = "FCMToken";
        String NOTIFICATION_COUNT = "NotificationCount";
        String NOTIFICATION_OBJECT = "NotificationObject";
    }

    interface CryptoCodes {
        String ETH = "ETH";
        String BTC = "BTC";
    }

    interface ActivityType {
        String SIGN_UP = "SIGN UP";
        String FORGOT_PASSWORD = "FORGOT PASSWORD";
        String CHANGE_PASSWORD = "CHANGE PASSWORD";
    }

    interface TransactionStatus {
        String PAID = "PAID";
        String DEPOSIT_COMPLETED = "DEPOSIT COMPLETED";
        String NOT_PAID = "NOT PAID";
        String PENDING_DEPOSIT = "PENDING DEPOSIT";
    }

    interface PaymentType {
        String REMITTANCE = "REMITTANCE";
        String DEPOSITED = "DEPOSITED";
    }

    interface AmountType {
        String CASH_PAID = "Cash Paid";
        String SERVICE_CHARGE = "Service Charge";
        String CASH_RECEIVED = "Cash Received";
    }

    interface Notifications {
        String REQUESTED_SENDER_USERTYPE = "RequestedSenderUserType";
        String REQUESTED_AMOUNT = "RequestedAmount";
        String RECEIVER_FULL_NAME = "ReceiverFullName";
        String RECEIVER_PHONE_NUMBER = "ReceiverPhone";
        String RECEIVER_COUNTRY_CODE = "ReceiverCountryCode";
        String RECEIVER_EMAIL = "ReceiverEmail";
        String TRANSACTION_REQUEST_ID = "TransactionRequestID";
        String REQUESTED_DEPOSIT_TYPE = "PaymentType";
        String NOTIFICATIONS_OBJECT = "NotificationsObject";
    }

}
