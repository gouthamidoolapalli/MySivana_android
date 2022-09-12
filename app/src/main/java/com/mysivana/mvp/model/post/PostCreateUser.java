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

import android.os.Parcel;
import android.os.Parcelable;

import com.squareup.moshi.Json;

public class PostCreateUser implements Parcelable {


    @Json(name = "userApiKey")
    private String userApiKey;
    @Json(name = "userId")
    private String userID;
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
    private Object imageUrl;
    @Json(name = "serviceProvider")
    private String serviceProvider;
    @Json(name = "password")
    private String password;
    @Json(name = "oldPassword")
    private String oldPassword;
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
    private String source = "Android";
    @Json(name = "userDeviceId")
    private Object userDeviceId;
    @Json(name = "deviceLocationId")
    private Object deviceLocationId;
    @Json(name = "transactionId")
    private Object transactionId;
    @Json(name = "sessionId")
    private Object sessionId;
    @Json(name = "ipAddress")
    private Object ipAddress;
    @Json(name = "userType")
    private String userType;
    @Json(name = "referredByCode")
    private  String referredByCode;

    public PostCreateUser() {

    }

    protected PostCreateUser(Parcel in) {
        userID  = in.readString();
        userApiKey = in.readString();
        fullName = in.readString();
        businessName = in.readString();
        email = in.readString();
        countryCode = in.readString();
        phoneNumber = in.readString();
        serviceProvider = in.readString();
        password = in.readString();
        oldPassword = in.readString();
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
        userType = in.readString();
        referredByCode = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userID);
        dest.writeString(userApiKey);
        dest.writeString(fullName);
        dest.writeString(businessName);
        dest.writeString(email);
        dest.writeString(countryCode);
        dest.writeString(phoneNumber);
        dest.writeString(serviceProvider);
        dest.writeString(password);
        dest.writeString(oldPassword);
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
        dest.writeString(userType);
        dest.writeString(referredByCode);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PostCreateUser> CREATOR = new Creator<PostCreateUser>() {
        @Override
        public PostCreateUser createFromParcel(Parcel in) {
            return new PostCreateUser(in);
        }

        @Override
        public PostCreateUser[] newArray(int size) {
            return new PostCreateUser[size];
        }
    };


    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getUserApiKey() {
        return userApiKey;
    }

    public void setUserApiKey(String userApiKey) {
        this.userApiKey = userApiKey;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
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

    public Object getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(Object imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(String serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Object getUserDeviceId() {
        return userDeviceId;
    }

    public void setUserDeviceId(Object userDeviceId) {
        this.userDeviceId = userDeviceId;
    }

    public Object getDeviceLocationId() {
        return deviceLocationId;
    }

    public void setDeviceLocationId(Object deviceLocationId) {
        this.deviceLocationId = deviceLocationId;
    }

    public Object getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Object transactionId) {
        this.transactionId = transactionId;
    }

    public Object getSessionId() {
        return sessionId;
    }

    public void setSessionId(Object sessionId) {
        this.sessionId = sessionId;
    }

    public Object getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(Object ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getReferredByCode() {
        return referredByCode;
    }

    public void setReferredByCode(String referredByCode) {
        this.referredByCode = referredByCode;
    }
}
