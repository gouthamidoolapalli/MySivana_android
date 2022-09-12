package com.mysivana.mvp.model;

import com.squareup.moshi.Json;

import java.util.List;

public class CryptoMenuResponse {

    @Json(name = "transactionFlag")
    private String transactionFlag;
    @Json(name = "httpStatusCode")
    private int httpStatusCode;
    @Json(name = "errorDescription")
    private String errorDescription;
    @Json(name = "status")
    private String status;
    @Json(name = "value")
    private List<Value> value = null;

    public String getTransactionFlag() {
        return transactionFlag;
    }

    public void setTransactionFlag(String transactionFlag) {
        this.transactionFlag = transactionFlag;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
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

    public List<Value> getValue() {
        return value;
    }

    public void setValue(List<Value> value) {
        this.value = value;
    }

    public static class Value {

        @Json(name = "cryptoCode")
        private String cryptoCode;
        @Json(name = "cryptoName")
        private String cryptoName;

        public String getCryptoCode() {
            return cryptoCode;
        }

        public void setCryptoCode(String cryptoCode) {
            this.cryptoCode = cryptoCode;
        }

        public String getCryptoName() {
            return cryptoName;
        }

        public void setCryptoName(String cryptoName) {
            this.cryptoName = cryptoName;
        }

    }
}
