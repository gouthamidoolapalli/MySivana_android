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

public class GenericResponse {

    @Json(name = "transactionFlag")
    private String transactionFlag;
    @Json(name = "httpStatusCode")
    private int errorCode;
    @Json(name = "errorDescription")
    private String errorDescription;
    @Json(name = "status")
    private String status;
    @Json(name = "value")
    private String value;

    public String  getTransactionFlag() {
        return transactionFlag;
    }

    public void setTransactionFlag(String  transactionFlag) {
        this.transactionFlag = transactionFlag;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String  getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String  errorDescription) {
        this.errorDescription = errorDescription;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}


