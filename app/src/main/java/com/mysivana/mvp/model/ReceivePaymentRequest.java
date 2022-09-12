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
package com.mysivana.mvp.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.squareup.moshi.Json;

public class ReceivePaymentRequest implements Parcelable {

    @Json(name = "userId")
    private int userId;
    @Json(name = "userApiKey")
    private String userApiKey;
    @Json(name = "chargeCode")
    private String chargeCode;
    @Json(name = "cryptoCode")
    private String cryptoCode;
    @Json(name = "cryptoAddress")
    private String cryptoAddress;
    @Json(name = "createdAt")
    private String createdAt;
    @Json(name = "expiresAt")
    private String expiresAt;
    @Json(name = "fiatValue")
    private String fiatValue;
    @Json(name = "currency")
    private String currency;
    @Json(name = "cryptoValue")
    private String cryptoValue;
    @Json(name = "serviceCharge_inCrypto")
    private String serviceChargeInCrypto;
    @Json(name = "totalCrypto")
    private String totalCrypto;
    @Json(name = "status")
    private String status;
    @Json(name = "statusTime")
    private String statusTime;
    @Json(name = "transactionCode")
    private String transactionCode;
    @Json(name = "fullName")
    private String fullName;
    @Json(name = "email")
    private String email;
    @Json(name = "countryCode")
    private String countryCode;
    @Json(name = "phoneNumber")
    private String phoneNumber;
    @Json(name = "receiverId")
    private int receiverId;
    @Json(name = "transactionOtp")
    private String transactionOtp;
    @Json(name = "userType")
    private String userType;
    @Json(name = "otp")
    private String otp;
    @Json(name = "paymentType")
    private String paymentType;
    @Json(name = "receiverBankId")
    private String receiverBankId;
    @Json(name = "senderId")
    private int senderId;
    @Json(name = "transactionRequestId")
    private String transactionRequestId;

    public String getTransactionRequestId() {
        return transactionRequestId;
    }

    public void setTransactionRequestId(String transactionRequestId) {
        this.transactionRequestId = transactionRequestId;
    }

    public ReceivePaymentRequest() {

    }

    protected ReceivePaymentRequest(Parcel in) {
        userId = in.readInt();
        userApiKey = in.readString();
        chargeCode = in.readString();
        cryptoCode = in.readString();
        cryptoAddress = in.readString();
        createdAt = in.readString();
        expiresAt = in.readString();
        fiatValue = in.readString();
        currency = in.readString();
        cryptoValue = in.readString();
        serviceChargeInCrypto = in.readString();
        totalCrypto = in.readString();
        status = in.readString();
        statusTime = in.readString();
        transactionCode = in.readString();
        fullName = in.readString();
        email = in.readString();
        countryCode = in.readString();
        phoneNumber = in.readString();
        receiverId = in.readInt();
        transactionOtp = in.readString();
        userType = in.readString();
        otp = in.readString();
        paymentType = in.readString();
        receiverBankId = in.readString();
        senderId = in.readInt();
        transactionRequestId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(userId);
        dest.writeString(userApiKey);
        dest.writeString(chargeCode);
        dest.writeString(cryptoCode);
        dest.writeString(cryptoAddress);
        dest.writeString(createdAt);
        dest.writeString(expiresAt);
        dest.writeString(fiatValue);
        dest.writeString(currency);
        dest.writeString(cryptoValue);
        dest.writeString(serviceChargeInCrypto);
        dest.writeString(totalCrypto);
        dest.writeString(status);
        dest.writeString(statusTime);
        dest.writeString(transactionCode);
        dest.writeString(fullName);
        dest.writeString(email);
        dest.writeString(countryCode);
        dest.writeString(phoneNumber);
        dest.writeInt(receiverId);
        dest.writeString(transactionOtp);
        dest.writeString(userType);
        dest.writeString(otp);
        dest.writeString(paymentType);
        dest.writeString(receiverBankId);
        dest.writeInt(senderId);
        dest.writeString(transactionRequestId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ReceivePaymentRequest> CREATOR = new Creator<ReceivePaymentRequest>() {
        @Override
        public ReceivePaymentRequest createFromParcel(Parcel in) {
            return new ReceivePaymentRequest(in);
        }

        @Override
        public ReceivePaymentRequest[] newArray(int size) {
            return new ReceivePaymentRequest[size];
        }
    };

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserApiKey() {
        return userApiKey;
    }

    public String getCryptoCode() {
        return cryptoCode;
    }

    public void setCryptoCode(String cryptoCode) {
        this.cryptoCode = cryptoCode;
    }


    public void setUserApiKey(String userApiKey) {
        this.userApiKey = userApiKey;
    }

    public String getChargeCode() {
        return chargeCode;
    }

    public void setChargeCode(String chargeCode) {
        this.chargeCode = chargeCode;
    }

    public String getCryptoAddress() {
        return cryptoAddress;
    }

    public void setCryptoAddress(String cryptoAddress) {
        this.cryptoAddress = cryptoAddress;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(String expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getFiatValue() {
        return fiatValue;
    }

    public void setFiatValue(String fiatValue) {
        this.fiatValue = fiatValue;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCryptoValue() {
        return cryptoValue;
    }

    public void setCryptoValue(String cryptoValue) {
        this.cryptoValue = cryptoValue;
    }

    public String getServiceChargeInCrypto() {
        return serviceChargeInCrypto;
    }

    public void setServiceChargeInCrypto(String serviceChargeInCrypto) {
        this.serviceChargeInCrypto = serviceChargeInCrypto;
    }

    public String getTotalCrypto() {
        return totalCrypto;
    }

    public void setTotalCrypto(String totalCrypto) {
        this.totalCrypto = totalCrypto;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusTime() {
        return statusTime;
    }

    public void setStatusTime(String statusTime) {
        this.statusTime = statusTime;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public String getTransactionOtp() {
        return transactionOtp;
    }

    public void setTransactionOtp(String transactionOtp) {
        this.transactionOtp = transactionOtp;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }


    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }


    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }


    public String getReceiverBankId() {
        return receiverBankId;
    }

    public void setReceiverBankId(String receiverBankId) {
        this.receiverBankId = receiverBankId;
    }


    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }
}
