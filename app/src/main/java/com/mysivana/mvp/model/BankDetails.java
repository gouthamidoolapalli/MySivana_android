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

public class BankDetails implements Parcelable {

    @Json(name = "userId")
    private Integer userId;
    @Json(name = "userApiKey")
    private String userApiKey;
    @Json(name = "bankName")
    private String bankName;
    @Json(name = "bankBranch")
    private String bankBranch;
    @Json(name = "bankAddress")
    private String bankAddress;
    @Json(name = "bankCity")
    private String bankCity;
    @Json(name = "bankCode")
    private String bankCode;
    @Json(name = "accountNumber")
    private String accountNumber;
    @Json(name = "upiAddress")
    private String upiAddress;
    @Json(name = "password")
    private String password;
    @Json(name = "accountType")
    private String accountType;
    @Json(name = "transactionFlag")
    private boolean transactionFlag;
    @Json(name = "receiverId")
    private String receiverId;

    protected BankDetails(Parcel in) {
        if (in.readByte() == 0) {
            userId = null;
        } else {
            userId = in.readInt();
        }
        userApiKey = in.readString();
        bankName = in.readString();
        bankBranch = in.readString();
        bankAddress = in.readString();
        bankCity = in.readString();
        bankCode = in.readString();
        accountNumber = in.readString();
        upiAddress = in.readString();
        password = in.readString();
        accountType = in.readString();
        transactionFlag = in.readByte() != 0;
        receiverId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (userId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(userId);
        }
        dest.writeString(userApiKey);
        dest.writeString(bankName);
        dest.writeString(bankBranch);
        dest.writeString(bankAddress);
        dest.writeString(bankCity);
        dest.writeString(bankCode);
        dest.writeString(accountNumber);
        dest.writeString(upiAddress);
        dest.writeString(password);
        dest.writeString(accountType);
        dest.writeByte((byte) (transactionFlag ? 1 : 0));
        dest.writeString(receiverId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BankDetails> CREATOR = new Creator<BankDetails>() {
        @Override
        public BankDetails createFromParcel(Parcel in) {
            return new BankDetails(in);
        }

        @Override
        public BankDetails[] newArray(int size) {
            return new BankDetails[size];
        }
    };

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public BankDetails() {

    }


    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserApiKey() {
        return userApiKey;
    }

    public void setUserApiKey(String userApiKey) {
        this.userApiKey = userApiKey;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getUpiAddress() {
        return upiAddress;
    }

    public void setUpiAddress(String upiAddress) {
        this.upiAddress = upiAddress;
    }

    public String getBankBranch() {
        return bankBranch;
    }

    public void setBankBranch(String bankBranch) {
        this.bankBranch = bankBranch;
    }


    public String getBankAddress() {
        return bankAddress;
    }

    public void setBankAddress(String bankAddress) {
        this.bankAddress = bankAddress;
    }

    public String getBankCity() {
        return bankCity;
    }

    public void setBankCity(String bankCity) {
        this.bankCity = bankCity;
    }


    public boolean isTransactionFlag() {
        return transactionFlag;
    }

    public void setTransactionFlag(boolean transactionFlag) {
        this.transactionFlag = transactionFlag;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }


}
