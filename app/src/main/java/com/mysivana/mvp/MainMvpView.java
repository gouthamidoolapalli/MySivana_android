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
package com.mysivana.mvp;

public interface MainMvpView extends BaseMvpView {

    /**
     * When an API call is successfully executed, this method is called
     * @param object Response Object
     */
    void onDataSuccess(Object object, int apiRequestCode);

    /**
     * When there's some failure while API call execution/  parsing JSON this method is called
     * @param e Error Message
     */
    void onDataFailure(Throwable e);

}
