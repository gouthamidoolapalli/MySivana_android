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

import com.squareup.moshi.Json;
import com.squareup.moshi.Moshi;

import java.io.IOException;

import retrofit2.Response;

public class ErrorBody {

    public static int UNKNOWN_ERROR = 0;
    int code;
    @Json(name = "error")
    String message;

    @Override
    public String toString() {
        return "{code:" + code + ", message:" + message + "}";
    }

    public static ErrorBody parseError(Response response) {
        Moshi moshi = new Moshi.Builder().build();
        try {
            return moshi.adapter(ErrorBody.class).fromJson(response.errorBody().string());
        } catch (IOException ignored) {
            return null;
        }
    }

}
