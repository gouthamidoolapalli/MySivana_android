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
package com.mysivana.mvp.api;

public interface APISettings {

    String RESPONSE_STATUS_SUCCESS = "SUCCESS";

    String CRYPTO_LIST = "cryptoList";
    String LOGIN = "userLogin";
    String MERCHANT_LIST = "merchantList";
    String REGISTER = "userSignUp";
    String CHECK_USER_EXISTS = "checkUserExists";
    String CHANGE_PASSWORD = "changePassword";
    String CRYPTO_VALUE = "cryptoValue";
    String START_PAYMENT = "startPayment";
    String RECEIVE_PAYMENT = "receivePayment";
    String COMPLETE_PAYMENT = "completedPayment";
    String GENERATE_OTP = "generateOtp";
    String VALIDATE_OTP = "validateOtp";
    String SAVE_BANK_DETAILS = "saveBankInfo";
    String VIEW_BANK_DETAILS = "viewBankInfo";
    String TRANSACTION_LIST = "transactionList";
    String TRANSACTION_RECEIPT = "transactionReceipt";
    String HELP_FEEDBACK = "appFeeback";
    String REPORT_ISSUE = "tranIssue";
    String SEND_RECEIPT = "sendReceiptByEmail";
    String GET_BANK_NAME = "ifscLookup";
    String USER_PROFILE = "userProfile";
    String VERIFY_EMAIL = "resendVerficationEmail";
    String UPLOAD_PROFILE_IMAGE = "saveUserDP";
    String MERCHANT_BALANACE_STATEMENT = "accountStatement";
    String MERCHANT_REQUEST_STATEMENT = "requestStatementByEmail";
    String REFERRAL_BOARD = "referralBoard";
    String REGISTER_FCM_TOKEN = "savePushNotificationTokenNum";
    String SEND_CASH_REQUEST = "requestMoney";
    String GET_NOTIFICATIONS_LIST = "notificationList";
}