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

public class TransactionReceiptResponse implements Parcelable {

    @Json(name = "transactionFlag")
    private String transactionFlag;
    @Json(name = "httpStatusCode")
    private Integer httpStatusCode;
    @Json(name = "errorDescription")
    private String errorDescription;
    @Json(name = "status")
    private String status;
    @Json(name = "value")
    private Value value;

    protected TransactionReceiptResponse(Parcel in) {
        transactionFlag = in.readString();
        if (in.readByte() == 0) {
            httpStatusCode = null;
        } else {
            httpStatusCode = in.readInt();
        }
        errorDescription = in.readString();
        status = in.readString();
        value = in.readParcelable(Value.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(transactionFlag);
        if (httpStatusCode == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(httpStatusCode);
        }
        dest.writeString(errorDescription);
        dest.writeString(status);
        dest.writeParcelable(value, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TransactionReceiptResponse> CREATOR = new Creator<TransactionReceiptResponse>() {
        @Override
        public TransactionReceiptResponse createFromParcel(Parcel in) {
            return new TransactionReceiptResponse(in);
        }

        @Override
        public TransactionReceiptResponse[] newArray(int size) {
            return new TransactionReceiptResponse[size];
        }
    };

    public String getTransactionFlag() {
        return transactionFlag;
    }

    public void setTransactionFlag(String transactionFlag) {
        this.transactionFlag = transactionFlag;
    }

    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(Integer httpStatusCode) {
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

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }


    public static class Value implements Parcelable  {

        @Json(name = "transactionCode")
        private String transactionCode;
        @Json(name = "chargeCode")
        private String chargeCode;
        @Json(name = "fullName")
        private String fullName;
        @Json(name = "countryCode")
        private String countryCode;
        @Json(name = "phoneNumber")
        private String phoneNumber;
        @Json(name = "email")
        private String email;
        @Json(name = "receiverId")
        private Integer receiverId;
        @Json(name = "senderEmail")
        private String senderEmail;
        @Json(name = "senderId")
        private Integer senderId;
        @Json(name = "businessName")
        private String businessName;
        @Json(name = "address")
        private String address;
        @Json(name = "serviceProvider_FullName")
        private String serviceProviderFullName;
        @Json(name = "serviceProvider_CountryCode")
        private String serviceProviderCountryCode;
        @Json(name = "serviceProvider_PhoneNumber")
        private String serviceProviderPhoneNumber;
        @Json(name = "serviceProvider_Email")
        private String serviceProviderEmail;
        @Json(name = "serviceProviderId")
        private String serviceProviderId;
        @Json(name = "fiatValue")
        private Double fiatValue;
        @Json(name = "cryptoValue")
        private Double cryptoValue;
        @Json(name = "serviceCharge_Crypto")
        private Double serviceChargeCrypto;
        @Json(name = "totalCrypto")
        private Double totalCrypto;
        @Json(name = "status")
        private String status;
        @Json(name = "receiverBankId")
        private Integer receiverBankId;
        @Json(name = "accountNumber")
        private String accountNumber;
        @Json(name = "bankName")
        private String bankName;
        @Json(name = "bankCode")
        private String bankCode;
        @Json(name = "bankBranch")
        private String bankBranch;
        @Json(name = "bankCity")
        private String bankCity;
        @Json(name = "transactionDate")
        private String transactionDate;
        @Json(name = "cryptoCode")
        private String cryptoCode = "BTC";

        protected Value(Parcel in) {
            transactionCode = in.readString();
            chargeCode = in.readString();
            fullName = in.readString();
            countryCode = in.readString();
            phoneNumber = in.readString();
            email = in.readString();
            if (in.readByte() == 0) {
                receiverId = null;
            } else {
                receiverId = in.readInt();
            }
            senderEmail = in.readString();
            if (in.readByte() == 0) {
                senderId = null;
            } else {
                senderId = in.readInt();
            }
            businessName = in.readString();
            address = in.readString();
            serviceProviderFullName = in.readString();
            serviceProviderCountryCode = in.readString();
            serviceProviderPhoneNumber = in.readString();
            serviceProviderEmail = in.readString();
            serviceProviderId = in.readString();
            if (in.readByte() == 0) {
                fiatValue = null;
            } else {
                fiatValue = in.readDouble();
            }
            if (in.readByte() == 0) {
                cryptoValue = null;
            } else {
                cryptoValue = in.readDouble();
            }
            if (in.readByte() == 0) {
                serviceChargeCrypto = null;
            } else {
                serviceChargeCrypto = in.readDouble();
            }
            if (in.readByte() == 0) {
                totalCrypto = null;
            } else {
                totalCrypto = in.readDouble();
            }
            status = in.readString();
            if (in.readByte() == 0) {
                receiverBankId = null;
            } else {
                receiverBankId = in.readInt();
            }
            accountNumber = in.readString();
            bankName = in.readString();
            bankCode = in.readString();
            bankBranch = in.readString();
            bankCity = in.readString();
            transactionDate = in.readString();
            cryptoCode = in.readString();
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

        public String getChargeCode() {
            return chargeCode;
        }

        public void setChargeCode(String chargeCode) {
            this.chargeCode = chargeCode;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Integer getReceiverId() {
            return receiverId;
        }

        public void setReceiverId(Integer receiverId) {
            this.receiverId = receiverId;
        }

        public String getSenderEmail() {
            return senderEmail;
        }

        public void setSenderEmail(String senderEmail) {
            this.senderEmail = senderEmail;
        }

        public Integer getSenderId() {
            return senderId;
        }

        public void setSenderId(Integer senderId) {
            this.senderId = senderId;
        }

        public String getBusinessName() {
            return businessName;
        }

        public void setBusinessName(String businessName) {
            this.businessName = businessName;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getServiceProviderFullName() {
            return serviceProviderFullName;
        }

        public void setServiceProviderFullName(String serviceProviderFullName) {
            this.serviceProviderFullName = serviceProviderFullName;
        }

        public String getServiceProviderCountryCode() {
            return serviceProviderCountryCode;
        }

        public void setServiceProviderCountryCode(String serviceProviderCountryCode) {
            this.serviceProviderCountryCode = serviceProviderCountryCode;
        }

        public String getServiceProviderPhoneNumber() {
            return serviceProviderPhoneNumber;
        }

        public void setServiceProviderPhoneNumber(String serviceProviderPhoneNumber) {
            this.serviceProviderPhoneNumber = serviceProviderPhoneNumber;
        }

        public String getServiceProviderEmail() {
            return serviceProviderEmail;
        }

        public void setServiceProviderEmail(String serviceProviderEmail) {
            this.serviceProviderEmail = serviceProviderEmail;
        }


        public String getTransactionDate() {
            return transactionDate;
        }

        public void setTransactionDate(String transactionDate) {
            this.transactionDate = transactionDate;
        }

        public String getServiceProviderId() {
            return serviceProviderId;
        }

        public void setServiceProviderId(String serviceProviderId) {
            this.serviceProviderId = serviceProviderId;
        }

        public Double getFiatValue() {
            return fiatValue;
        }

        public void setFiatValue(Double fiatValue) {
            this.fiatValue = fiatValue;
        }

        public Double getCryptoValue() {
            return cryptoValue;
        }

        public void setCryptoValue(Double cryptoValue) {
            this.cryptoValue = cryptoValue;
        }

        public Double getServiceChargeCrypto() {
            return serviceChargeCrypto;
        }

        public void setServiceChargeCrypto(Double serviceChargeCrypto) {
            this.serviceChargeCrypto = serviceChargeCrypto;
        }

        public Double getTotalCrypto() {
            return totalCrypto;
        }

        public void setTotalCrypto(Double totalCrypto) {
            this.totalCrypto = totalCrypto;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Integer getReceiverBankId() {
            return receiverBankId;
        }

        public void setReceiverBankId(Integer receiverBankId) {
            this.receiverBankId = receiverBankId;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public void setAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
        }

        public String getBankName() {
            return bankName;
        }

        public void setBankName(String bankName) {
            this.bankName = bankName;
        }

        public String getBankCode() {
            return bankCode;
        }

        public void setBankCode(String bankCode) {
            this.bankCode = bankCode;
        }

        public String getBankBranch() {
            return bankBranch;
        }

        public void setBankBranch(String bankBranch) {
            this.bankBranch = bankBranch;
        }

        public String getBankCity() {
            return bankCity;
        }

        public void setBankCity(String bankCity) {
            this.bankCity = bankCity;
        }

        public String getCryptoCode() {
            return cryptoCode;
        }

        public void setCryptoCode(String cryptoCode) {
            this.cryptoCode = cryptoCode;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(transactionCode);
            parcel.writeString(chargeCode);
            parcel.writeString(fullName);
            parcel.writeString(countryCode);
            parcel.writeString(phoneNumber);
            parcel.writeString(email);
            if (receiverId == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(receiverId);
            }
            parcel.writeString(senderEmail);
            if (senderId == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(senderId);
            }
            parcel.writeString(businessName);
            parcel.writeString(address);
            parcel.writeString(serviceProviderFullName);
            parcel.writeString(serviceProviderCountryCode);
            parcel.writeString(serviceProviderPhoneNumber);
            parcel.writeString(serviceProviderEmail);
            parcel.writeString(serviceProviderId);
            if (fiatValue == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeDouble(fiatValue);
            }
            if (cryptoValue == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeDouble(cryptoValue);
            }
            if (serviceChargeCrypto == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeDouble(serviceChargeCrypto);
            }
            if (totalCrypto == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeDouble(totalCrypto);
            }
            parcel.writeString(status);
            if (receiverBankId == null) {
                parcel.writeByte((byte) 0);
            } else {
                parcel.writeByte((byte) 1);
                parcel.writeInt(receiverBankId);
            }
            parcel.writeString(accountNumber);
            parcel.writeString(bankName);
            parcel.writeString(bankCode);
            parcel.writeString(bankBranch);
            parcel.writeString(bankCity);
            parcel.writeString(transactionDate);
            parcel.writeString(cryptoCode);

        }
    }
}