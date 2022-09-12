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
package com.mysivana.mvp.model.post;

import com.squareup.moshi.Json;

public class PostCryptoValue {

    @Json(name = "userId")
    private String userId;
    @Json(name = "userApiKey")
    private String userApiKey;
    @Json(name = "cryptoCode")
    private String cryptoCode;
    @Json(name = "inr")
    private Double inr;
    @Json(name = "inrToCrypto")
    private Double inrToCrypto;
    @Json(name = "cryptoValue")
    private Double cryptoValue;
    @Json(name = "totalCrypto")
    private Double totalCrypto;
    @Json(name = "serviceCharge_inCrypto")
    private Double serviceChargeInCrypto;
    @Json(name = "serviceCharge_inINR")
    private Integer serviceChargeInINR;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Double getInr() {
        return inr;
    }

    public void setInr(Double inr) {
        this.inr = inr;
    }

    public String getCryptoCode() {
        return cryptoCode;
    }

    public void setCryptoCode(String cryptoCode) {
        this.cryptoCode = cryptoCode;
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

    public Double getTotalBTC() {
        return totalCrypto;
    }

    public void setTotalBTC(Double totalBTC) {
        this.totalCrypto = totalBTC;
    }

    public Double getServiceChargeInCrypto() {
        return serviceChargeInCrypto;
    }

    public void setServiceChargeInCrypto(Double serviceChargeInCrypto) {
        this.serviceChargeInCrypto = serviceChargeInCrypto;
    }

    public Integer getServiceChargeInINR() {
        return serviceChargeInINR;
    }

    public void setServiceChargeInINR(Integer serviceChargeInINR) {
        this.serviceChargeInINR = serviceChargeInINR;
    }


    public String getUserApiKey() {
        return userApiKey;
    }

    public void setUserApiKey(String userApiKey) {
        this.userApiKey = userApiKey;
    }

}
