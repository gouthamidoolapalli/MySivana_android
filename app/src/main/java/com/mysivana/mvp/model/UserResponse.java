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

import android.os.Parcel;
import android.os.Parcelable;

import com.squareup.moshi.Json;

public class UserResponse implements Parcelable {

    @Json(name = "transactionFlag")
    private String transactionFlag;
    @Json(name = "httpStatusCode")
    private int errorCode;
    @Json(name = "errorDescription")
    private String errorDescription;
    @Json(name = "status")
    private String status;
    @Json(name = "value")
    private Value value;

    protected UserResponse(Parcel in) {
        transactionFlag = in.readString();
        errorCode = in.readInt();
        errorDescription = in.readString();
        status = in.readString();
        value = in.readParcelable(Value.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(transactionFlag);
        dest.writeInt(errorCode);
        dest.writeString(errorDescription);
        dest.writeString(status);
        dest.writeParcelable(value, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserResponse> CREATOR = new Creator<UserResponse>() {
        @Override
        public UserResponse createFromParcel(Parcel in) {
            return new UserResponse(in);
        }

        @Override
        public UserResponse[] newArray(int size) {
            return new UserResponse[size];
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

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }


public static class Value implements Parcelable{

    @Json(name = "userId")
    private String userId;
    @Json(name = "userApiKey")
    private String userApiKey;
    @Json(name = "fullName")
    private String fullName;
    @Json(name = "businessName")
    private String businessName;
    @Json(name = "email")
    private String email;
    @Json(name = "countryCode")
    private String countryCode;
    @Json(name = "phoneNumber")
    private String phoneNumber;
    @Json(name = "imageUrl")
    private String imageUrl;
    @Json(name = "serviceProvider")
    private String serviceProvider;
    @Json(name = "oldPassword")
    private String oldPassword;
    @Json(name = "password")
    private String password;
    @Json(name = "userType")
    private String userType;
    @Json(name = "otpCode")
    private String otpCode;
    @Json(name = "deviceId")
    private String deviceId;
    @Json(name = "deviceBrand")
    private String deviceBrand;
    @Json(name = "osVersion")
    private String osVersion;
    @Json(name = "apiVersion")
    private String apiVersion;
    @Json(name = "address")
    private String address;
    @Json(name = "city")
    private String city;
    @Json(name = "state")
    private String state;
    @Json(name = "country")
    private String country;
    @Json(name = "pincode")
    private String pincode;
    @Json(name = "latitude")
    private String latitude;
    @Json(name = "longitude")
    private String longitude;
    @Json(name = "activityType")
    private String activityType;
    @Json(name = "source")
    private String source;
    @Json(name = "userDeviceId")
    private String userDeviceId;
    @Json(name = "deviceLocationId")
    private String deviceLocationId;
    @Json(name = "transactionId")
    private String transactionId;
    @Json(name = "sessionId")
    private String sessionId;
    @Json(name = "ipAddress")
    private String ipAddress;
    @Json(name = "isPhoneVerified")
    private String isPhoneVerified;
    @Json(name = "isEmailVerified")
    private String isEmailVerified;
    @Json(name = "bankDetailsAvailable")
    private String bankDetailsAvailable;
    @Json(name = "bankBalance")
    private float bankBalance;
    @Json(name = "referralCode")
    private String referralCode;
    @Json(name = "referredByCode")
    private  String referredByCode;


    @Json(name = "sivaTokens")
    private  String sivaToken;


    protected Value(Parcel in) {
        userId = in.readString();
        userApiKey = in.readString();
        fullName = in.readString();
        businessName = in.readString();
        email = in.readString();
        countryCode = in.readString();
        phoneNumber = in.readString();
        imageUrl = in.readString();
        serviceProvider = in.readString();
        oldPassword = in.readString();
        password = in.readString();
        userType = in.readString();
        otpCode = in.readString();
        deviceId = in.readString();
        deviceBrand = in.readString();
        osVersion = in.readString();
        apiVersion = in.readString();
        address = in.readString();
        city = in.readString();
        state = in.readString();
        country = in.readString();
        pincode = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        activityType = in.readString();
        source = in.readString();
        userDeviceId = in.readString();
        deviceLocationId = in.readString();
        transactionId = in.readString();
        sessionId = in.readString();
        ipAddress = in.readString();
        isPhoneVerified = in.readString();
        isEmailVerified = in.readString();
        bankDetailsAvailable = in.readString();
        bankBalance = in.readFloat();
        referralCode = in.readString();
        referredByCode = in.readString();
        sivaToken = in.readString();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userId);
        dest.writeString(userApiKey);
        dest.writeString(fullName);
        dest.writeString(businessName);
        dest.writeString(email);
        dest.writeString(countryCode);
        dest.writeString(phoneNumber);
        dest.writeString(imageUrl);
        dest.writeString(serviceProvider);
        dest.writeString(oldPassword);
        dest.writeString(password);
        dest.writeString(userType);
        dest.writeString(otpCode);
        dest.writeString(deviceId);
        dest.writeString(deviceBrand);
        dest.writeString(osVersion);
        dest.writeString(apiVersion);
        dest.writeString(address);
        dest.writeString(city);
        dest.writeString(state);
        dest.writeString(country);
        dest.writeString(pincode);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(activityType);
        dest.writeString(source);
        dest.writeString(userDeviceId);
        dest.writeString(deviceLocationId);
        dest.writeString(transactionId);
        dest.writeString(sessionId);
        dest.writeString(ipAddress);
        dest.writeString(isPhoneVerified);
        dest.writeString(isEmailVerified);
        dest.writeString(bankDetailsAvailable);
        dest.writeFloat(bankBalance);
        dest.writeString(referralCode);
        dest.writeString(referredByCode);
        dest.writeString(sivaToken);
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(String serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceBrand() {
        return deviceBrand;
    }

    public void setDeviceBrand(String deviceBrand) {
        this.deviceBrand = deviceBrand;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUserDeviceId() {
        return userDeviceId;
    }

    public void setUserDeviceId(String userDeviceId) {
        this.userDeviceId = userDeviceId;
    }

    public String getDeviceLocationId() {
        return deviceLocationId;
    }

    public void setDeviceLocationId(String deviceLocationId) {
        this.deviceLocationId = deviceLocationId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getIsPhoneVerified() {
        return isPhoneVerified;
    }

    public void setIsPhoneVerified(String isPhoneVerified) {
        this.isPhoneVerified = isPhoneVerified;
    }

    public String getIsEmailVerified() {
        return isEmailVerified;
    }

    public void setIsEmailVerified(String isEmailVerified) {
        this.isEmailVerified = isEmailVerified;
    }

    public String getBankDetailsAvailable() {
        return bankDetailsAvailable;
    }

    public void setBankDetailsAvailable(String bankDetailsAvailable) {
        this.bankDetailsAvailable = bankDetailsAvailable;
    }

    public float getBankBalance() {
        return bankBalance;
    }

    public void setBankBalance(float bankBalance) {
        this.bankBalance = bankBalance;
    }

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public String getReferredByCode() {
        return referredByCode;
    }

    public void setReferredByCode(String referredByCode) {
        this.referredByCode = referredByCode;
    }

    public String getSivaToken() {
        return sivaToken;
    }

    public void setSivaToken(String sivaToken) {
        this.sivaToken = sivaToken;
    }
}
}