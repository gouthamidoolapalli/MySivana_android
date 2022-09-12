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
package com.mysivana.mvp.model;

import com.squareup.moshi.Json;

public class CryptoResponse {

    @Json(name = "transactionFlag")
    private String transactionFlag;
    @Json(name = "httpStatusCode")
    private Integer errorCode;
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

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
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
        private String userId;
        @Json(name = "userApiKey")
        private String userApiKey;
        @Json(name = "cryptoCode")
        private String cryptoCode;
        @Json(name = "inr")
        private Integer inr;
        @Json(name = "inrToCrypto")
        private Double inrToCrypto;
        @Json(name = "cryptoValue")
        private Double cryptoValue;
        @Json(name = "serviceCharge_inCrypto")
        private Double serviceChargeInCrypto;
        @Json(name = "totalCrypto")
        private Double totalCrypto;
        @Json(name = "serviceCharge_inINR")
        private Double serviceChargeInINR;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
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

        public Integer getInr() {
            return inr;
        }

        public void setInr(Integer inr) {
            this.inr = inr;
        }

        public Double getInrToCrypto() {
            return inrToCrypto;
        }

        public void setInrToCrypto(Double inrToCrypto) {
            this.inrToCrypto = inrToCrypto;
        }

        public Double getCryptoValue() {
            return cryptoValue;
        }

        public void setCryptoValue(Double cryptoValue) {
            this.cryptoValue = cryptoValue;
        }

        public Double getServiceChargeInCrypto() {
            return serviceChargeInCrypto;
        }

        public void setServiceChargeInCrypto(Double serviceChargeInCrypto) {
            this.serviceChargeInCrypto = serviceChargeInCrypto;
        }

        public Double getTotalCrypto() {
            return totalCrypto;
        }

        public void setTotalCrypto(Double totalCrypto) {
            this.totalCrypto = totalCrypto;
        }

        public Double getServiceChargeInINR() {
            return serviceChargeInINR;
        }

        public void setServiceChargeInINR(Double serviceChargeInINR) {
            this.serviceChargeInINR = serviceChargeInINR;
        }

    }
}