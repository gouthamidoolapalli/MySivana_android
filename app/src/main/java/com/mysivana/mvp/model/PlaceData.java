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

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;
import java.util.Locale;

public class PlaceData implements Parcelable {

    private String address;
    private String subAddress;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private String attributions;
    private String id;
    private LatLng latLng;
    private Locale locale;
    private String name;
    private String phoneNumber;
    private List<Integer> placeTypes;
    private LatLngBounds viewport;
    private Uri websiteUri;


    public PlaceData() {

    }


    protected PlaceData(Parcel in) {
        address = in.readString();
        subAddress = in.readString();
        city = in.readString();
        state = in.readString();
        country = in.readString();
        postalCode = in.readString();
        attributions = in.readString();
        id = in.readString();
        latLng = in.readParcelable(LatLng.class.getClassLoader());
        name = in.readString();
        phoneNumber = in.readString();
        viewport = in.readParcelable(LatLngBounds.class.getClassLoader());
        websiteUri = in.readParcelable(Uri.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeString(subAddress);
        dest.writeString(city);
        dest.writeString(state);
        dest.writeString(country);
        dest.writeString(postalCode);
        dest.writeString(attributions);
        dest.writeString(id);
        dest.writeParcelable(latLng, flags);
        dest.writeString(name);
        dest.writeString(phoneNumber);
        dest.writeParcelable(viewport, flags);
        dest.writeParcelable(websiteUri, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PlaceData> CREATOR = new Creator<PlaceData>() {
        @Override
        public PlaceData createFromParcel(Parcel in) {
            return new PlaceData(in);
        }

        @Override
        public PlaceData[] newArray(int size) {
            return new PlaceData[size];
        }
    };

    public String getSubAddress() {
        return subAddress;
    }

    public void setSubAddress(String subAddress) {
        this.subAddress = subAddress;
    }

    public String getAddress() {
        return address;
    }

    public CharSequence getAttributions() {
        return attributions;
    }

    public String getId() {
        return id;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public Locale getLocale() {
        return locale;
    }

    public CharSequence getName() {
        return name;
    }

    public CharSequence getPhoneNumber() {
        return phoneNumber;
    }

    public List<Integer> getPlaceTypes() {
        return placeTypes;
    }

    public LatLngBounds getViewport() {
        return viewport;
    }

    public Uri getWebsiteUri() {
        return websiteUri;
    }


    public void setAddress(String address) {
        this.address = address;
    }

    public void setAttributions(String attributions) {
        this.attributions = attributions;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPlaceTypes(List<Integer> placeTypes) {
        this.placeTypes = placeTypes;
    }

    public void setViewport(LatLngBounds viewport) {
        this.viewport = viewport;
    }

    public void setWebsiteUri(Uri websiteUri) {
        this.websiteUri = websiteUri;
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

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

}
