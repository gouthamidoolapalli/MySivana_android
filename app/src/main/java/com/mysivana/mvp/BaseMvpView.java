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

import android.content.Context;

public interface BaseMvpView {

    Context getContext();

    /**
     * Shows the error
     * @param error error message
     */
    void showError(String error);

    /**
     * show error message
     * @param stringResId resource ID from strings.xml
     */
    void showError(int stringResId);

    /**
     * shows something wrong message
     * @param srtResId resource ID from strings.xml
     */
    void showMessage(int srtResId);

    /**
     * shows something wrong message with click here
     * @param message message to be shown.
     */
    void showMessage(String message);

    /**
     * load progress bar
     */
    void showLoading();

    /**
     * hide progress bar
     */
    void hideLoading();


}
