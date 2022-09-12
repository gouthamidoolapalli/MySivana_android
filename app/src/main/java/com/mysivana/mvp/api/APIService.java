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

import com.mysivana.mvp.model.AccountStatementResponse;
import com.mysivana.mvp.model.BankDetails;
import com.mysivana.mvp.model.BankNameResponse;
import com.mysivana.mvp.model.CryptoMenuResponse;
import com.mysivana.mvp.model.CryptoResponse;
import com.mysivana.mvp.model.GenericResponse;
import com.mysivana.mvp.model.HelpFeedbackRequest;
import com.mysivana.mvp.model.Merchant;
import com.mysivana.mvp.model.NotificationListResponse;
import com.mysivana.mvp.model.ReceivePaymentRequest;
import com.mysivana.mvp.model.ReceivePaymentResponse;
import com.mysivana.mvp.model.ReferralBoardResponse;
import com.mysivana.mvp.model.StartPaymentRequest;
import com.mysivana.mvp.model.StartPaymentResponse;
import com.mysivana.mvp.model.TransactionReceiptResponse;
import com.mysivana.mvp.model.Transactions;
import com.mysivana.mvp.model.UserResponse;
import com.mysivana.mvp.model.ViewBankDetailsResponse;
import com.mysivana.mvp.model.post.PostCreateUser;
import com.mysivana.mvp.model.post.PostCryptoValue;
import com.mysivana.mvp.model.post.PostUserLogin;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface APIService {

    @GET(APISettings.CRYPTO_LIST)
    Observable<CryptoMenuResponse> getCryptoList(@Query("userId") String userId, @Query("userApiKey") String userApiKey);

    @GET(APISettings.MERCHANT_LIST)
    Observable<Merchant> getMerchantList(@Query("userId") String userId, @Query("userApiKey") String userApiKey);

    @POST(APISettings.REGISTER)
    Observable<GenericResponse> doRegister(@Body PostCreateUser postCreateUser);

    @POST(APISettings.LOGIN)
    Observable<UserResponse> doLogin(@Body PostUserLogin postUserLogin);

    @POST(APISettings.CHANGE_PASSWORD)
    Observable<GenericResponse> changePassword(@Body PostCreateUser postCreateUser);

    @GET(APISettings.CHECK_USER_EXISTS)
    Observable<UserResponse> checkUserExists(@Query("email") String email, @Query("phoneNumber") String phoneNumber, @Query("activityType") String activityType);

    @POST(APISettings.CRYPTO_VALUE)
    Observable<CryptoResponse> getCryptoValue(@Body PostCryptoValue postCryptoValue);

    @POST(APISettings.START_PAYMENT)
    Observable<StartPaymentResponse> initiatePayment(@Body StartPaymentRequest startPaymentRequest);

    @POST(APISettings.SAVE_BANK_DETAILS)
    Observable<GenericResponse> saveBankDetails(@Body BankDetails bankDetails);

    @POST(APISettings.VIEW_BANK_DETAILS)
    Observable<ViewBankDetailsResponse> getBankDetails(@Query("userId") String userID, @Query("userApiKey") String userApiKey);


    @GET(APISettings.TRANSACTION_LIST)
    Observable<Transactions> getTransactionsList(@Query("userId") String userId, @Query("userApiKey") String userApiKey);

    @GET(APISettings.TRANSACTION_RECEIPT)
    Observable<TransactionReceiptResponse> getTransactionsReceipt(@Query("userId") String userId, @Query("transactionCode") String transactionCode,
                                                                  @Query("userApiKey") String userApiKey);

    @POST(APISettings.HELP_FEEDBACK)
    Observable<GenericResponse> saveFeedbackDetails(@Body HelpFeedbackRequest helpFeedbackRequest);

    @POST(APISettings.REPORT_ISSUE)
    Observable<GenericResponse> reportAnIssue(@Body HelpFeedbackRequest helpFeedbackRequest);

    @POST(APISettings.RECEIVE_PAYMENT)
    Observable<ReceivePaymentResponse> receivePayment(@Body ReceivePaymentRequest receivePaymentRequest);

    @POST(APISettings.COMPLETE_PAYMENT)
    Observable<ReceivePaymentResponse> completePayment(@Body ReceivePaymentRequest receivePaymentRequest);

    @POST(APISettings.GENERATE_OTP)
    Observable<GenericResponse> generateOTP(@Body ReceivePaymentRequest receivePaymentRequest);

    @POST(APISettings.VALIDATE_OTP)
    Observable<GenericResponse> validateOTP(@Body ReceivePaymentRequest receivePaymentRequest);

    @GET(APISettings.SEND_RECEIPT)
    Observable<GenericResponse> sendReceipt(@Query("userId") String userID, @Query("userApiKey") String userApiKey, @Query("userType") String userType, @Query("transactionCode") String transactionCode);

    @POST(APISettings.GET_BANK_NAME)
    Observable<BankNameResponse> getBankName(@Body BankDetails bankDetails);

    @POST(APISettings.USER_PROFILE)
    Observable<UserResponse> viewProfile(@Body PostUserLogin postUserLogin);

    @GET(APISettings.VERIFY_EMAIL)
    Observable<GenericResponse> verifyEmail(@Query("userId") String userID, @Query("userApiKey") String userApiKey);

    @GET(APISettings.MERCHANT_BALANACE_STATEMENT)
    Observable<AccountStatementResponse> getBalanceStatement(@Query("userId") String userID, @Query("userApiKey") String userApiKey);

    @GET(APISettings.MERCHANT_REQUEST_STATEMENT)
    Observable<GenericResponse> requestForStatement(@Query("userId") String userID, @Query("userApiKey") String userApiKey);

    @POST(APISettings.REFERRAL_BOARD)
    Observable<ReferralBoardResponse> getReferralBoardList(@Query("userId") String userID, @Query("userApiKey") String userApiKey);

    @Multipart
    @POST(APISettings.UPLOAD_PROFILE_IMAGE)
    Observable<GenericResponse> uploadProfileImage(@Part MultipartBody.Part image, @Part("userApiKey")RequestBody userApiKey, @Part("userId") RequestBody userId);

    @POST(APISettings.REGISTER_FCM_TOKEN)
    Observable<GenericResponse> registerTokenToServer(@Query("userId") String userID, @Query("userApiKey") String userApiKey, @Query("fireBaseTokenNumber") String fireBaseTokenNumber, @Query("deviceId")String deviceId);

    @Multipart
    @POST(APISettings.SEND_CASH_REQUEST)
    Observable<GenericResponse> sendCashRequest(@Part("userId") RequestBody userID, @Part("userApiKey") RequestBody userApiKey, @Part("requestAmount") RequestBody requestAmount, @Part("phoneNumber") RequestBody phoneNumber, @Part("countryCode") RequestBody countryCode, @Part MultipartBody.Part file, @Part("notes") RequestBody notes, @Part("paymentType")RequestBody requestDepositType);

    @GET(APISettings.GET_NOTIFICATIONS_LIST)
    Observable<NotificationListResponse> getNotificationsList(@Query("userId") String userID, @Query("userApiKey") String userApiKey);
}
