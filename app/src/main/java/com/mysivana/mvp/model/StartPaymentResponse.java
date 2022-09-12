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

public class StartPaymentResponse {

    @Json(name = "transactionFlag")
    private String transactionFlag;
    @Json(name = "httpStatusCode")
    private int errorCode;
    @Json(name = "errorDescription")
    private String errorDescription;
    @Json(name = "status")
    private String status;
    @Json(name = "value")
    private Value value;

    public String getTransactionFlag() {
        return transactionFlag;
    }

    public void setTransactionFlag(String transactionFlag) {
        this.transactionFlag = transactionFlag;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public static class Value {

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
        @Json(name = "receiverId")
        private int receiverId;
        @Json(name = "transactionOtp")
        private String transactionOtp;
        @Json(name = "bankDetailsAvailable")
        private String bankDetailsAvailable;

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getUserApiKey() {
            return userApiKey;
        }

        public void setUserApiKey(String userApiKey) {
            this.userApiKey = userApiKey;
        }

        public String getCryptoCode() {
            return cryptoCode;
        }

        public void setCryptoCode(String cryptoCode) {
            this.cryptoCode = cryptoCode;
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


        public String getBankDetailsAvailable() {
            return bankDetailsAvailable;
        }

        public void setBankDetailsAvailable(String bankDetailsAvailable) {
            this.bankDetailsAvailable = bankDetailsAvailable;
        }

    }
}
