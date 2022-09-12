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
package com.mysivana.mvp;

import com.mysivana.MSApplication;
import com.mysivana.mvp.api.CallbackWrapper;
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

public class MainMvpPresenter extends BaseMvpPresenter<MainMvpView> {

    private static final int DEFAULT_CODE = -1;

    public MainMvpPresenter(MainMvpView mvpView) {
        attachView(mvpView);
    }

    public void getCryptoMenuList(String userId, String userApikey, final int statusCode) {
        baseMvpView.showLoading();
        Observable<CryptoMenuResponse> cryptoList = MSApplication.APIManager.mApiService.getCryptoList(userId, userApikey);
        addSubscription(cryptoList, new CallbackWrapper<CryptoMenuResponse>(baseMvpView) {
            @Override
            protected void onSuccess(CryptoMenuResponse cryptoMenuResponse) {
                baseMvpView.onDataSuccess(cryptoMenuResponse, statusCode);
            }

            @Override
            protected void onFailure(Throwable e) {
                baseMvpView.onDataFailure(e);
            }
        });
    }

    public void doLogin(PostUserLogin postUserLogin) {
        baseMvpView.showLoading();
        Observable<UserResponse> userLoginObservable = MSApplication.APIManager.mApiService.doLogin(postUserLogin);
        addSubscription(userLoginObservable, new CallbackWrapper<UserResponse>(baseMvpView) {
            @Override
            protected void onSuccess(UserResponse userResponse) {
                baseMvpView.onDataSuccess(userResponse, DEFAULT_CODE);
            }

            @Override
            protected void onFailure(Throwable e) {
                baseMvpView.onDataFailure(e);
            }
        });
    }

    public void viewMyProfile(PostUserLogin postUserLogin, final int apiRequestCode) {
        Observable<UserResponse> userLoginObservable = MSApplication.APIManager.mApiService.viewProfile(postUserLogin);
        addSubscription(userLoginObservable, new CallbackWrapper<UserResponse>(baseMvpView) {
            @Override
            protected void onSuccess(UserResponse userResponse) {
                baseMvpView.onDataSuccess(userResponse, apiRequestCode);
            }

            @Override
            protected void onFailure(Throwable e) {
                baseMvpView.onDataFailure(e);
            }
        });
    }

    public void doRegister(PostCreateUser postCreateUser) {
        baseMvpView.showLoading();
        Observable<GenericResponse> createUserObservable = MSApplication.APIManager.mApiService.doRegister(postCreateUser);
        addSubscription(createUserObservable, new CallbackWrapper<GenericResponse>(baseMvpView) {
            @Override
            protected void onSuccess(GenericResponse genericResponse) {
                baseMvpView.onDataSuccess(genericResponse, DEFAULT_CODE);
            }

            @Override
            protected void onFailure(Throwable e) {
                baseMvpView.onDataFailure(e);
            }
        });
    }

    public void changePassword(PostCreateUser postCreateUser) {
        baseMvpView.showLoading();
        Observable<GenericResponse> createUserObservable = MSApplication.APIManager.mApiService.changePassword(postCreateUser);
        addSubscription(createUserObservable, new CallbackWrapper<GenericResponse>(baseMvpView) {
            @Override
            protected void onSuccess(GenericResponse genericResponse) {
                baseMvpView.onDataSuccess(genericResponse, DEFAULT_CODE);
            }

            @Override
            protected void onFailure(Throwable e) {
                baseMvpView.onDataFailure(e);
            }
        });
    }

    public void checkUserExists(String email, String phoneNumber, String activityType) {
        baseMvpView.showLoading();
        Observable<UserResponse> checkUserExistsObservable = MSApplication.APIManager.mApiService.checkUserExists(email, phoneNumber, activityType);
        addSubscription(checkUserExistsObservable, new CallbackWrapper<UserResponse>(baseMvpView) {
            @Override
            protected void onSuccess(UserResponse userResponse) {
                baseMvpView.onDataSuccess(userResponse, DEFAULT_CODE);
            }

            @Override
            protected void onFailure(Throwable e) {
                baseMvpView.onDataFailure(e);
            }
        });
    }

    public void getMerchantList(String userId, String userApikey) {
        Observable<Merchant> userLoginResponseObservable = MSApplication.APIManager.mApiService.getMerchantList(userId, userApikey);
        addSubscription(userLoginResponseObservable, new CallbackWrapper<Merchant>(baseMvpView) {
            @Override
            protected void onSuccess(Merchant merchant) {
                baseMvpView.onDataSuccess(merchant, DEFAULT_CODE);
            }

            @Override
            protected void onFailure(Throwable e) {
                baseMvpView.onDataFailure(e);
            }
        });
    }

    public void getCryptoValue(PostCryptoValue postCryptoValue, final int apiRequestCode) {
        baseMvpView.showLoading();
        Observable<CryptoResponse> btcResponseObservable = MSApplication.APIManager.mApiService.getCryptoValue(postCryptoValue);
        addSubscription(btcResponseObservable, new CallbackWrapper<CryptoResponse>(baseMvpView) {
            @Override
            protected void onSuccess(CryptoResponse cryptoResponse) {
                baseMvpView.onDataSuccess(cryptoResponse, apiRequestCode);
            }

            @Override
            protected void onFailure(Throwable e) {

                baseMvpView.onDataFailure(e);
            }
        });
    }


    public void startPayment(StartPaymentRequest startPaymentRequest, final int apiRequestCode) {
        baseMvpView.showLoading();
        Observable<StartPaymentResponse> paymentResponseObservable = MSApplication.APIManager.mApiService.initiatePayment(startPaymentRequest);
        addSubscription(paymentResponseObservable, new CallbackWrapper<StartPaymentResponse>(baseMvpView) {
            @Override
            protected void onSuccess(StartPaymentResponse startPaymentResponse) {
                baseMvpView.onDataSuccess(startPaymentResponse, apiRequestCode);
            }

            @Override
            protected void onFailure(Throwable e) {
                baseMvpView.onDataFailure(e);
            }
        });
    }

    public void receivePayment(ReceivePaymentRequest receivePaymentRequest, final int apiRequestCode) {
        Observable<ReceivePaymentResponse> receivePaymentResponseObservable = MSApplication.APIManager.mApiService.receivePayment(receivePaymentRequest);
        addSubscription(receivePaymentResponseObservable, new CallbackWrapper<ReceivePaymentResponse>(baseMvpView) {
            @Override
            protected void onSuccess(ReceivePaymentResponse response) {
                baseMvpView.onDataSuccess(response, apiRequestCode);
            }

            @Override
            protected void onFailure(Throwable e) {
                baseMvpView.onDataFailure(e);
            }
        });
    }


    public void completePayment(ReceivePaymentRequest receivePaymentRequest, final int apiRequestCode) {
        baseMvpView.showLoading();
        Observable<ReceivePaymentResponse> receivePaymentResponseObservable = MSApplication.APIManager.mApiService.completePayment(receivePaymentRequest);
        addSubscription(receivePaymentResponseObservable, new CallbackWrapper<ReceivePaymentResponse>(baseMvpView) {
            @Override
            protected void onSuccess(ReceivePaymentResponse response) {
                baseMvpView.onDataSuccess(response, apiRequestCode);
            }

            @Override
            protected void onFailure(Throwable e) {
                baseMvpView.onDataFailure(e);
            }
        });
    }

    public void generateOTP(ReceivePaymentRequest receivePaymentRequest, final int apiRequestCode) {
        baseMvpView.showLoading();
        Observable<GenericResponse> genericResponseObservable = MSApplication.APIManager.mApiService
                .generateOTP(receivePaymentRequest);
        addSubscription(genericResponseObservable, new CallbackWrapper<GenericResponse>(baseMvpView) {
            @Override
            protected void onSuccess(GenericResponse response) {
                baseMvpView.onDataSuccess(response, apiRequestCode);
            }

            @Override
            protected void onFailure(Throwable e) {
                baseMvpView.onDataFailure(e);
            }
        });
    }

    public void validateOTP(ReceivePaymentRequest receivePaymentRequest, final int apiRequestCode) {
        baseMvpView.showLoading();
        Observable<GenericResponse> genericResponseObservable = MSApplication.APIManager.mApiService
                .validateOTP(receivePaymentRequest);
        addSubscription(genericResponseObservable, new CallbackWrapper<GenericResponse>(baseMvpView) {
            @Override
            protected void onSuccess(GenericResponse response) {
                baseMvpView.onDataSuccess(response, apiRequestCode);
            }

            @Override
            protected void onFailure(Throwable e) {
                baseMvpView.onDataFailure(e);
            }
        });
    }



    public void getBankDetails(String userID, String userApiKey, final int apiRequestCode) {
        baseMvpView.showLoading();
        Observable<ViewBankDetailsResponse> paymentResponseObservable = MSApplication.APIManager.mApiService.getBankDetails(userID, userApiKey);
        addSubscription(paymentResponseObservable, new CallbackWrapper<ViewBankDetailsResponse>(baseMvpView) {
            @Override
            protected void onSuccess(ViewBankDetailsResponse genericResponse) {
                baseMvpView.onDataSuccess(genericResponse, apiRequestCode);
            }

            @Override
            protected void onFailure(Throwable e) {
                baseMvpView.onDataFailure(e);
            }
        });
    }

    public void getBankName(BankDetails bankDetails, final int apiRequestCode) {
        baseMvpView.showLoading();
        Observable<BankNameResponse> responseObservable = MSApplication.APIManager.mApiService.getBankName(bankDetails);
        addSubscription(responseObservable, new CallbackWrapper<BankNameResponse>(baseMvpView) {
            @Override
            protected void onSuccess(BankNameResponse genericResponse) {
                baseMvpView.onDataSuccess(genericResponse, apiRequestCode);
            }

            @Override
            protected void onFailure(Throwable e) {
                baseMvpView.onDataFailure(e);
            }
        });
    }

    public void saveBankDetails(BankDetails bankDetails, final int apiRequestCode) {
        baseMvpView.showLoading();
        Observable<GenericResponse> paymentResponseObservable = MSApplication.APIManager.mApiService.saveBankDetails(bankDetails);
        addSubscription(paymentResponseObservable, new CallbackWrapper<GenericResponse>(baseMvpView) {
            @Override
            protected void onSuccess(GenericResponse genericResponse) {
                baseMvpView.onDataSuccess(genericResponse, apiRequestCode);
            }

            @Override
            protected void onFailure(Throwable e) {
                baseMvpView.onDataFailure(e);
            }
        });
    }


    public void saveFeedbackDetails(HelpFeedbackRequest helpFeedbackRequest) {
        baseMvpView.showLoading();
        Observable<GenericResponse> paymentResponseObservable = MSApplication.APIManager.mApiService.saveFeedbackDetails(helpFeedbackRequest);
        addSubscription(paymentResponseObservable, new CallbackWrapper<GenericResponse>(baseMvpView) {
            @Override
            protected void onSuccess(GenericResponse genericResponse) {
                baseMvpView.onDataSuccess(genericResponse, DEFAULT_CODE);
            }

            @Override
            protected void onFailure(Throwable e) {
                baseMvpView.onDataFailure(e);
            }
        });
    }

    public void sendReceipt(String userID, String userApiKey, String userType, String transactionCode, final int sendReceiptRequestCode) {
        baseMvpView.showLoading();
        Observable<GenericResponse> paymentResponseObservable = MSApplication.APIManager.mApiService.sendReceipt(userID, userApiKey, userType, transactionCode);
        addSubscription(paymentResponseObservable, new CallbackWrapper<GenericResponse>(baseMvpView) {
            @Override
            protected void onSuccess(GenericResponse genericResponse) {
                baseMvpView.onDataSuccess(genericResponse, sendReceiptRequestCode);
            }

            @Override
            protected void onFailure(Throwable e) {
                baseMvpView.onDataFailure(e);
            }
        });
    }


    public void sendReportIssue(HelpFeedbackRequest helpFeedbackRequest) {
        baseMvpView.showLoading();
        Observable<GenericResponse> paymentResponseObservable = MSApplication.APIManager.mApiService.reportAnIssue(helpFeedbackRequest);
        addSubscription(paymentResponseObservable, new CallbackWrapper<GenericResponse>(baseMvpView) {
            @Override
            protected void onSuccess(GenericResponse genericResponse) {
                baseMvpView.onDataSuccess(genericResponse, DEFAULT_CODE);
            }

            @Override
            protected void onFailure(Throwable e) {
                baseMvpView.onDataFailure(e);
            }
        });
    }

    public void getTransactionList(String userId, String userApiKey) {
        baseMvpView.showLoading();
        Observable<Transactions> transactionsObservable = MSApplication.APIManager.mApiService.getTransactionsList(userId, userApiKey);
        addSubscription(transactionsObservable, new CallbackWrapper<Transactions>(baseMvpView) {
            @Override
            protected void onSuccess(Transactions transactions) {
                baseMvpView.onDataSuccess(transactions, DEFAULT_CODE);
            }

            @Override
            protected void onFailure(Throwable e) {
                baseMvpView.onDataFailure(e);
            }
        });
    }

    public void getTransactionReceipt(String userID, String transactionCode, String userApiKey, final int transactionReceiptRequestCode) {
        baseMvpView.showLoading();
        Observable<TransactionReceiptResponse> transactionsObservable = MSApplication.APIManager.mApiService.getTransactionsReceipt(userID, transactionCode, userApiKey);
        addSubscription(transactionsObservable, new CallbackWrapper<TransactionReceiptResponse>(baseMvpView) {
            @Override
            protected void onSuccess(TransactionReceiptResponse transactionReceiptResponse) {
                baseMvpView.onDataSuccess(transactionReceiptResponse, transactionReceiptRequestCode);
            }

            @Override
            protected void onFailure(Throwable e) {
                baseMvpView.onDataFailure(e);
            }
        });
    }

    public void verifyEmail(String userId, String userApiKey, final int apiRequestCode) {
        baseMvpView.showLoading();
        Observable<GenericResponse> genericResponseObservable = MSApplication.APIManager.mApiService.verifyEmail(userId, userApiKey);
        addSubscription(genericResponseObservable, new CallbackWrapper<GenericResponse>(baseMvpView) {
            @Override
            protected void onSuccess(GenericResponse genericResponse) {
                baseMvpView.onDataSuccess(genericResponse, apiRequestCode);
            }

            @Override
            protected void onFailure(Throwable e) {
                baseMvpView.onDataFailure(e);
            }
        });
    }

    public void getAccountStatement(String userId, String userApiKey, final int apiRequestCode) {
        baseMvpView.showLoading();
        Observable<AccountStatementResponse> accountStatementResponseObservable = MSApplication.APIManager.mApiService.getBalanceStatement(userId, userApiKey);
        addSubscription(accountStatementResponseObservable, new CallbackWrapper<AccountStatementResponse>(baseMvpView) {
            @Override
            protected void onSuccess(AccountStatementResponse accountStatementResponse) {
                baseMvpView.onDataSuccess(accountStatementResponse, apiRequestCode);
            }

            @Override
            protected void onFailure(Throwable e) {
                baseMvpView.onDataFailure(e);
            }
        });
    }
    public void requestForStatement(String userId, String userApiKey, final int apiRequestCode) {
        baseMvpView.showLoading();
        Observable<GenericResponse> genericResponseObservable = MSApplication.APIManager.mApiService.requestForStatement(userId, userApiKey);
        addSubscription(genericResponseObservable, new CallbackWrapper<GenericResponse>(baseMvpView) {
            @Override
            protected void onSuccess(GenericResponse genericResponse) {
                baseMvpView.onDataSuccess(genericResponse, apiRequestCode);
            }

            @Override
            protected void onFailure(Throwable e) {
                baseMvpView.onDataFailure(e);
            }
        });
    }
    public void uploadImage(MultipartBody.Part image, RequestBody requestAPI, RequestBody requestUserID, final int apiRequestCode) {
        baseMvpView.showLoading();
        Observable<GenericResponse> genericResponseObservable = MSApplication.APIManager.mApiService.uploadProfileImage(image, requestAPI, requestUserID);
        addSubscription(genericResponseObservable, new CallbackWrapper<GenericResponse>(baseMvpView) {
            @Override
            protected void onSuccess(GenericResponse genericResponse) {
                baseMvpView.onDataSuccess(genericResponse, apiRequestCode);
            }

            @Override
            protected void onFailure(Throwable e) {
                baseMvpView.onDataFailure(e);
            }
        });
    }


    public void getReferralBoardList(String userId, String userApiKey) {
        baseMvpView.showLoading();
        Observable<ReferralBoardResponse> responseObservable = MSApplication.APIManager.mApiService.getReferralBoardList(userId, userApiKey);
        addSubscription(responseObservable, new CallbackWrapper<ReferralBoardResponse>(baseMvpView) {
            @Override
            protected void onSuccess(ReferralBoardResponse genericResponse) {
                baseMvpView.onDataSuccess(genericResponse, DEFAULT_CODE);
            }

            @Override
            protected void onFailure(Throwable e) {
                baseMvpView.onDataFailure(e);
            }
        });
    }


    public void registerFCMToken(String userId, String userApiKey, String token, String deviceId, final int apiRequestCode) {
        baseMvpView.showLoading();
        Observable<GenericResponse> genericResponseObservable = MSApplication.APIManager.mApiService.registerTokenToServer(userId, userApiKey, token, deviceId);
        addSubscription(genericResponseObservable, new CallbackWrapper<GenericResponse>(baseMvpView) {
            @Override
            protected void onSuccess(GenericResponse genericResponse) {
                baseMvpView.onDataSuccess(genericResponse, apiRequestCode);
            }

            @Override
            protected void onFailure(Throwable e) {
                baseMvpView.onDataFailure(e);
            }
        });
    }

    public void sendMoneyRequest(MultipartBody.Part body, RequestBody userId, RequestBody userApiKey, RequestBody requestAmount, RequestBody phoneNumber, RequestBody countryCode, RequestBody notes, RequestBody requestDepositType) {
        baseMvpView.showLoading();
        Observable<GenericResponse> genericResponseObservable = MSApplication.APIManager.mApiService.sendCashRequest( userId, userApiKey, requestAmount, phoneNumber,countryCode, body, notes, requestDepositType );
        addSubscription(genericResponseObservable, new CallbackWrapper<GenericResponse>(baseMvpView) {
            @Override
            protected void onSuccess(GenericResponse genericResponse) {
                baseMvpView.onDataSuccess(genericResponse, DEFAULT_CODE);
            }

            @Override
            protected void onFailure(Throwable e) {
                baseMvpView.onDataFailure(e);
            }
        });
    }

    public void notificationsList(String userId, String userApiKey) {
        baseMvpView.showLoading();
        Observable<NotificationListResponse> genericResponseObservable = MSApplication.APIManager.mApiService.getNotificationsList(userId, userApiKey);
        addSubscription(genericResponseObservable, new CallbackWrapper<NotificationListResponse>(baseMvpView) {
            @Override
            protected void onSuccess(NotificationListResponse genericResponse) {
                baseMvpView.onDataSuccess(genericResponse, DEFAULT_CODE);
            }

            @Override
            protected void onFailure(Throwable e) {
                baseMvpView.onDataFailure(e);
            }
        });
    }
}

