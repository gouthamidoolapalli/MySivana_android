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
package com.mysivana.custom;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.mysivana.R;
import com.mysivana.util.FontCache;
import com.mysivana.util.UIUtils;

public class AutoCompleteLocation extends AppCompatAutoCompleteTextView {

    private Drawable mCloseIcon;
    private Drawable mDotIcon;
    private GoogleApiClient mGoogleApiClient;
    private AutoCompleteAdapter mAutoCompleteAdapter;
    private AutoCompleteLocationListener mAutoCompleteLocationListener;
    private ListView mListView;
    private Drawable background;


    public interface AutoCompleteLocationListener {
        void onTextClear();

        void onItemSelected(Place selectedPlace);
    }


    public AutoCompleteLocation(Context context, AttributeSet attrs) {
        super(context, attrs);
        Resources resources = context.getResources();
        TypedArray typedArray =
                context.obtainStyledAttributes(attrs, R.styleable.AutoCompleteLocation, 0, 0);
        background =
                typedArray.getDrawable(R.styleable.AutoCompleteLocation_background_layout);
        if (background == null) {
            background = resources.getDrawable(R.drawable.bg_rounded_white);
        }
        String hintText = typedArray.getString(R.styleable.AutoCompleteLocation_hint_text);
        if (hintText == null) {
            hintText = resources.getString(R.string.default_hint_text);
        }
        int hintTextColor = typedArray.getColor(R.styleable.AutoCompleteLocation_hint_text_color,
                resources.getColor(R.color.colorHint));
        int textColor = typedArray.getColor(R.styleable.AutoCompleteLocation_text_color,
                resources.getColor(R.color.colorMineShaft));
        setTypeface(FontCache.getTypeface(getContext(), 0));
        typedArray.recycle();

        setBackground(background);
        setHint(hintText);
        setHintTextColor(hintTextColor);
        setTextColor(textColor);
        setMaxLines(resources.getInteger(R.integer.default_max_lines));

        mCloseIcon = context.getResources().getDrawable(R.drawable.ic_close);
        mDotIcon = context.getResources().getDrawable(R.drawable.black_radius);
        this.setCompoundDrawablesWithIntrinsicBounds(mDotIcon, null, null, null);
        mGoogleApiClient = new GoogleApiClient.Builder(context).addApi(Places.GEO_DATA_API)
                .addApi(AppIndex.API)
                .build();

    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mGoogleApiClient.connect();
        this.addTextChangedListener(mAutoCompleteTextWatcher);
        this.setOnTouchListener(mOnTouchListener);
        mListView.setOnItemClickListener(mAutocompleteClickListener);
        this.setAdapter(mAutoCompleteAdapter);
        mAutoCompleteAdapter = new AutoCompleteAdapter(getContext(), mGoogleApiClient, null, null);
        createAutoCompleteFilter("IN");
        mListView.setAdapter(mAutoCompleteAdapter);
        this.setDropDownHeight(0);
        this.setAdapter(mAutoCompleteAdapter);
    }

    public void createAutoCompleteFilter(String countryCode) {
        AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(Place.TYPE_COUNTRY)
                .setCountry(countryCode)
                .build();
        mAutoCompleteAdapter.setAutocompleteFilter(autocompleteFilter);
    }


    public void setListView(ListView listView) {
        this.mListView = listView;
    }


    public AutoCompleteAdapter getAdapter() {
        return mAutoCompleteAdapter;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!enabled) {
            this.setCompoundDrawablesWithIntrinsicBounds(mDotIcon, null, null, null);
        } else {
            this.setCompoundDrawablesWithIntrinsicBounds(mDotIcon, null,
                    AutoCompleteLocation.this.getText().toString().equals("") ? null : mCloseIcon, null);
        }
    }

    public void setAutoCompleteTextListener(AutoCompleteLocationListener autoCompleteLocationListener) {
        mAutoCompleteLocationListener = autoCompleteLocationListener;
    }

    private TextWatcher mAutoCompleteTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            setBackground(background);
            AutoCompleteLocation.this.setCompoundDrawablesWithIntrinsicBounds(mDotIcon, null,
                    AutoCompleteLocation.this.getText().toString().equals("") ? null : mCloseIcon, null);
            if (mAutoCompleteLocationListener != null) {
                mAutoCompleteLocationListener.onTextClear();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    };

    private OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getX()
                    > AutoCompleteLocation.this.getWidth()
                    - AutoCompleteLocation.this.getPaddingRight()
                    - mCloseIcon.getIntrinsicWidth()) {
                AutoCompleteLocation.this.setText("");
                AutoCompleteLocation.this.setCompoundDrawables(null, null, null, null);
                mAutoCompleteAdapter.getFilter().filter("");
                mAutoCompleteAdapter.notifyDataSetChanged();
            }
            return false;
        }
    };

    private AdapterView.OnItemClickListener mAutocompleteClickListener =
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    UIUtils.hideKeyboard(AutoCompleteLocation.this.getContext(), AutoCompleteLocation.this);
                    final AutocompletePrediction item = mAutoCompleteAdapter.getItem(position);
                    if (item != null) {
                        final String placeId = item.getPlaceId();
                        PendingResult<PlaceBuffer> placeResult =
                                Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
                        placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
                    }
                }
            };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback =
            new ResultCallback<PlaceBuffer>() {
                @Override
                public void onResult(@NonNull PlaceBuffer places) {
                    if (!places.getStatus().isSuccess()) {
                        places.release();
                        return;
                    }
                    final Place place = places.get(0);
                    if (mAutoCompleteLocationListener != null) {
                        mAutoCompleteLocationListener.onItemSelected(place);
                    }
                    places.release();
                }
            };
}
