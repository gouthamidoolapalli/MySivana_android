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

import java.util.List;

public class Transactions implements Parcelable {

    @Json(name = "transactionFlag")
    private String transactionFlag;
    @Json(name = "httpStatusCode")
    private int errorCode;
    @Json(name = "errorDescription")
    private String errorDescription;
    @Json(name = "status")
    private String status;
    @Json(name = "value")
    private List<Value> value = null;

    public Transactions(Parcel in) {
        transactionFlag = in.readString();
        errorCode = in.readInt();
        errorDescription = in.readString();
        status = in.readString();
        value = in.createTypedArrayList(Value.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(transactionFlag);
        dest.writeInt(errorCode);
        dest.writeString(errorDescription);
        dest.writeString(status);
        dest.writeTypedList(value);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Transactions> CREATOR = new Creator<Transactions>() {
        @Override
        public Transactions createFromParcel(Parcel in) {
            return new Transactions(in);
        }

        @Override
        public Transactions[] newArray(int size) {
            return new Transactions[size];
        }
    };

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

    public List<Value> getValue() {
        return value;
    }

    public void setValue(List<Value> value) {
        this.value = value;
    }


    public static class Value implements Parcelable {

        @Json(name = "transactionCode")
        private String transactionCode;
        @Json(name = "totalCrypto")
        private Double totalCrypto;
        @Json(name = "fiatValue")
        private int fiatValue;
        @Json(name = "deliveredTime")
        private String deliveredTime;
        @Json(name = "city")
        private String city;
        @Json(name = "receiver_FullName")
        private String receiverFullName;
        @Json(name = "serviceProvier_FullName")
        private String serviceProvierFullName;
        @Json(name = "status")
        private String status;
        @Json(name = "createdDate")
        private String createdDate;
        private boolean isExpanded;
        @Json(name = "cryptoCode")
        private String cryptoCode = "BTC";

        public Value() {

        }

        public Value(Parcel in) {
            transactionCode = in.readString();
            if (in.readByte() == 0) {
                totalCrypto = null;
            } else {
                totalCrypto = in.readDouble();
            }
            fiatValue = in.readInt();
            deliveredTime = in.readString();
            city = in.readString();
            receiverFullName = in.readString();
            serviceProvierFullName = in.readString();
            status = in.readString();
            createdDate = in.readString();
            isExpanded = in.readByte() != 0;
            cryptoCode = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(transactionCode);
            if (totalCrypto == null) {
                dest.writeByte((byte) 0);
            } else {
                dest.writeByte((byte) 1);
                dest.writeDouble(totalCrypto);
            }
            dest.writeInt(fiatValue);
            dest.writeString(deliveredTime);
            dest.writeString(city);
            dest.writeString(receiverFullName);
            dest.writeString(serviceProvierFullName);
            dest.writeString(status);
            dest.writeString(createdDate);
            dest.writeByte((byte) (isExpanded ? 1 : 0));
            dest.writeString(cryptoCode);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Value> CREATOR = new Creator<Value>() {
            @Override
            public Value createFromParcel(Parcel in) {
                return new Value(in);
            }

            @Override
            public Value[] newArray(int size) {
                return new Value[size];
            }
        };

        public String getTransactionCode() {
            return transactionCode;
        }

        public void setTransactionCode(String transactionCode) {
            this.transactionCode = transactionCode;
        }

        public Double getTotalCrypto() {
            return totalCrypto;
        }

        public void setTotalCrypto(Double totalCrypto) {
            this.totalCrypto = totalCrypto;
        }

        public int getFiatValue() {
            return fiatValue;
        }

        public void setFiatValue(int fiatValue) {
            this.fiatValue = fiatValue;
        }

        public String getDeliveredTime() {
            return deliveredTime;
        }

        public void setDeliveredTime(String deliveredTime) {
            this.deliveredTime = deliveredTime;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getReceiverFullName() {
            return receiverFullName;
        }

        public void setReceiverFullName(String receiverFullName) {
            this.receiverFullName = receiverFullName;
        }

        public String getServiceProvierFullName() {
            return serviceProvierFullName;
        }

        public void setServiceProvierFullName(String serviceProvierFullName) {
            this.serviceProvierFullName = serviceProvierFullName;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCreatedDate() {
            return createdDate;
        }

        public void setCreatedDate(String createdDate) {
            this.createdDate = createdDate;
        }

        public boolean isExpanded() {
            return isExpanded;
        }

        public void setExpanded(boolean expanded) {
            isExpanded = expanded;
        }

        public String getCryptoCode() {
            return cryptoCode;
        }

        public void setCryptoCode(String cryptoCode) {
            this.cryptoCode = cryptoCode;
        }



    }
}