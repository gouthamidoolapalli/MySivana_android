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

import com.squareup.moshi.Json;

public class StartPaymentRequest {

    @Json(name = "userId")
    private int userId;
    @Json(name = "userApiKey")
    private String userApiKey;
    @Json(name = "chargeCode")
    private String chargeCode;
    @Json(name = "cryptoAddress")
    private String cryptoAddress;
    @Json(name = "cryptoCode")
    private String cryptoCode;
    @Json(name = "createdAt")
    private String createdAt;
    @Json(name = "expiresAt")
    private String expiresAt;
    @Json(name = "fiatValue")
    private String fiatValue;
    @Json(name = "currency")
    private String currency = "INR";
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
    @Json(name = "fullName")
    private String fullName;
    @Json(name = "email")
    private String email;
    @Json(name = "phoneNumber")
    private String phoneNumber;
    @Json(name = "countryCode")
    private String countryCode = "+91";
    @Json(name = "userType")
    private String userType;
    @Json(name = "receiverId")
    private String receiverId;
    @Json(name = "paymentType")
    private String paymentType;


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCryptoCode() {
        return cryptoCode;
    }

    public void setCryptoCode(String cryptoCode) {
        this.cryptoCode = cryptoCode;
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

    public Object getCryptoValue() {
        return cryptoValue;
    }

    public String getUserApiKey() {
        return userApiKey;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

}
