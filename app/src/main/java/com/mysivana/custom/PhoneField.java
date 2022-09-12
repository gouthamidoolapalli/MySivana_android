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
 * MAY 06 2018      : BYNDR       : CREATED.
 * ------------------------------------------------------------------------
 *
 * ========================================================================
 */
package com.mysivana.custom;

import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.mysivana.R;
import com.mysivana.mvp.model.Country;
import com.mysivana.ui.adapters.CountriesAdapter;
import com.mysivana.util.Countries;
import com.mysivana.util.FontCache;
import com.mysivana.util.UIUtils;
/**
 * PhoneField is a custom view for phone numbers with the corresponding
 * country flag, and it uses libphonenumber to validate the phone number.
 */
public abstract class PhoneField extends LinearLayout {

    private Spinner mSpinner;

    private EditText mEditText;

    private Country mCountry;

    private PhoneNumberUtil mPhoneUtil = PhoneNumberUtil.getInstance();

    private int mDefaultCountryPosition = 0;

    /**
     * Instantiates a new Phone field.
     *
     * @param context the context
     */
    public PhoneField(Context context) {
        this(context, null);
    }

    /**
     * Instantiates a new Phone field.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public PhoneField(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Instantiates a new Phone field.
     *
     * @param context      the context
     * @param attrs        the attrs
     * @param defStyleAttr the def style attr
     */
    public PhoneField(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(getContext(), getLayoutResId(), this);
        updateLayoutAttributes();
        prepareView();
    }

    /**
     * Prepare view.
     */
    protected void prepareView() {
        mSpinner = (Spinner) findViewWithTag(getResources().getString(R.string.com_lamudi_phonefield_flag_spinner));
        mEditText = (EditText) findViewWithTag(getResources().getString(R.string.com_lamudi_phonefield_edittext));
        mEditText.setBackgroundResource(0);
        mEditText.setTypeface(FontCache.getTypeface(getContext(), 0));
        mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        mEditText.setInputType(InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_FLAG_SIGNED);
        if (mSpinner == null || mEditText == null) {
            throw new IllegalStateException("Please provide a valid xml layout");
        }

        final CountriesAdapter adapter = new CountriesAdapter(getContext(), Countries.COUNTRIES);
        mSpinner.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });

        final TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int cursorPosition, int before, int count) {
                if (mCountry != null && mCountry.getDialCode() == 1) {
                    mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
                    if (before == 0 && count == 1) {  //Entering values

                        String val = s.toString();
                        String a = "";
                        String b = "";
                        String c = "";
                        if (val != null && val.length() > 0) {
                            val = val.replace("-", "");
                            if (val.length() >= 3) {
                                a = val.substring(0, 3);
                            } else if (val.length() < 3) {
                                a = val.substring(0, val.length());
                            }
                            if (val.length() >= 6) {
                                b = val.substring(3, 6);
                                c = val.substring(6, val.length());
                            } else if (val.length() > 3 && val.length() < 6) {
                                b = val.substring(3, val.length());
                            }
                            StringBuffer stringBuffer = new StringBuffer();
                            if (a != null && a.length() > 0) {
                                stringBuffer.append(a);

                            }
                            if (b != null && b.length() > 0) {
                                stringBuffer.append("-");
                                stringBuffer.append(b);

                            }
                            if (c != null && c.length() > 0) {
                                stringBuffer.append("-");
                                stringBuffer.append(c);
                            }

                            mEditText.removeTextChangedListener(this);
                            mEditText.setText(stringBuffer.toString());
                            if (cursorPosition == 3 || cursorPosition == 7) {
                                cursorPosition = cursorPosition + 2;
                            } else {
                                cursorPosition = cursorPosition + 1;
                            }
                            if (cursorPosition <= mEditText.getText().toString().length()) {
                                mEditText.setSelection(cursorPosition);
                            } else {
                                mEditText.setSelection(mEditText.getText().toString().length());
                            }
                            mEditText.addTextChangedListener(this);
                        } else {
                            mEditText.removeTextChangedListener(this);
                            mEditText.setText("");
                            mEditText.addTextChangedListener(this);
                        }

                    }

                    if (before == 1 && count == 0) {  //Deleting values

                        String val = s.toString();
                        String a = "";
                        String b = "";
                        String c = "";

                        if (val != null && val.length() > 0) {
                            val = val.replace("-", "");
                            if (cursorPosition == 3) {
                                val = removeCharAt(val, cursorPosition - 1, s.toString().length() - 1);
                            } else if (cursorPosition == 7) {
                                val = removeCharAt(val, cursorPosition - 2, s.toString().length() - 2);
                            }
                            if (val.length() >= 3) {
                                a = val.substring(0, 3);
                            } else if (val.length() < 3) {
                                a = val.substring(0, val.length());
                            }
                            if (val.length() >= 6) {
                                b = val.substring(3, 6);
                                c = val.substring(6, val.length());
                            } else if (val.length() > 3 && val.length() < 6) {
                                b = val.substring(3, val.length());
                            }
                            StringBuffer stringBuffer = new StringBuffer();
                            if (a != null && a.length() > 0) {
                                stringBuffer.append(a);

                            }
                            if (b != null && b.length() > 0) {
                                stringBuffer.append("-");
                                stringBuffer.append(b);

                            }
                            if (c != null && c.length() > 0) {
                                stringBuffer.append("-");
                                stringBuffer.append(c);
                            }
                            mEditText.removeTextChangedListener(this);
                            mEditText.setText(stringBuffer.toString());
                            if (cursorPosition == 3 || cursorPosition == 7) {
                                cursorPosition = cursorPosition - 1;
                            }
                            if (cursorPosition <= mEditText.getText().toString().length()) {
                                mEditText.setSelection(cursorPosition);
                            } else {
                                mEditText.setSelection(mEditText.getText().toString().length());
                            }
                            mEditText.addTextChangedListener(this);
                        } else {
                            mEditText.removeTextChangedListener(this);
                            mEditText.setText("");
                            mEditText.addTextChangedListener(this);
                        }

                    }

                } else {
                    mEditText.removeTextChangedListener(this);
                    mEditText.setText(UIUtils.formatIndianNumber(mEditText.getText().toString()));
                    cursorPosition = cursorPosition+1;
                    if (cursorPosition <= mEditText.getText().toString().length()) {
                        mEditText.setSelection(cursorPosition);
                    } else {
                        mEditText.setSelection(mEditText.getText().toString().length());
                    }
                    mEditText.addTextChangedListener(this);
                    mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String rawNumber = s.toString();
//                if (rawNumber.isEmpty()) {
//                    mSpinner.setSelection(mDefaultCountryPosition);
//                } else {
                    if (rawNumber.startsWith("00")) {
                        rawNumber = rawNumber.replaceFirst("00", "+");
                        mEditText.removeTextChangedListener(this);
                        mEditText.setText(rawNumber);
                        mEditText.addTextChangedListener(this);
                        mEditText.setSelection(rawNumber.length());
                    }
                    try {
                        Phonenumber.PhoneNumber number = parsePhoneNumber(rawNumber);
                        if (mCountry == null || mCountry.getDialCode() != number.getCountryCode()) {
                            selectCountry(number.getCountryCode());
                        }
                    } catch (NumberParseException ignored) {
                    }
//                }
            }
        };

        mEditText.addTextChangedListener(textWatcher);

        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCountry = adapter.getItem(position);
                if (mCountry.getDialCode() == 1) {
                    mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(12)});
                    String text = UIUtils.formatUSNumber(mEditText.getText().toString());
                    mEditText.setText("");
                    mEditText.append(text);

                } else {
                    mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                    String text = UIUtils.formatIndianNumber(mEditText.getText().toString());
                    mEditText.setText("");
                    mEditText.append(text);

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mCountry = null;
            }
        });

    }

    public static String removeCharAt(String s, int pos, int length) {

        String value = "";
        if (length > pos) {
            value = s.substring(pos + 1);
        }
        return s.substring(0, pos) + value;
    }





    /**
     * Gets spinner.
     *
     * @return the spinner
     */
    public Spinner getSpinner() {
        return mSpinner;
    }

    /**
     * Gets edit text.
     *
     * @return the edit text
     */
    public EditText getEditText() {
        return mEditText;
    }

    /**
     * Checks whether the entered phone number is valid or not.
     *
     * @return a boolean that indicates whether the number is of a valid pattern
     */
    public boolean isValid() {
        try {
            return mPhoneUtil.isValidNumber(parsePhoneNumber(getRawInput()));
        } catch (NumberParseException e) {
            return false;
        }
    }

    private Phonenumber.PhoneNumber parsePhoneNumber(String number) throws NumberParseException {
        String defaultRegion = mCountry != null ? mCountry.getCode().toUpperCase() : "";
        return mPhoneUtil.parseAndKeepRawInput(number, defaultRegion);
    }

    /**
     * Gets phone number.
     *
     * @return the phone number
     */
    public String getPhoneNumber() {
        try {
            Phonenumber.PhoneNumber number = parsePhoneNumber(getRawInput());
            return mPhoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.E164);
        } catch (NumberParseException ignored) {
        }
        return getRawInput();
    }

    /**
     * Gets country code.
     *
     * @return the country code
     */
    public String getCountryCode() {
        try {
            return String.valueOf("+" + mCountry.getDialCode());
        } catch (Exception ignored) {
        }
        return "";
    }

    /**
     * Sets default country.
     *
     * @param countryCode the country code
     */
    public void setDefaultCountry(String countryCode) {
        for (int i = 0; i < Countries.COUNTRIES.size(); i++) {
            Country country = Countries.COUNTRIES.get(i);
            if (country.getCode().equalsIgnoreCase(countryCode)) {
                mCountry = country;
                mDefaultCountryPosition = i;
                mSpinner.setSelection(i);
            }
        }
    }

    private void selectCountry(int dialCode) {
        for (int i = 0; i < Countries.COUNTRIES.size(); i++) {
            Country country = Countries.COUNTRIES.get(i);
            if (country.getDialCode() == dialCode) {
                mCountry = country;
                mSpinner.setSelection(i);
            }
        }
    }

    /**
     * Sets phone number.
     *
     * @param rawNumber the raw number
     */
    public void setPhoneNumber(String rawNumber) {
        try {
            Phonenumber.PhoneNumber number = parsePhoneNumber(rawNumber);
            if (mCountry == null || mCountry.getDialCode() != number.getCountryCode()) {
                selectCountry(number.getCountryCode());
            }
            mEditText.setText(mPhoneUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.NATIONAL));
        } catch (NumberParseException ignored) {
        }
    }

    private void hideKeyboard() {
        ((InputMethodManager) getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    /**
     * Update layout attributes.
     */
    protected abstract void updateLayoutAttributes();

    /**
     * Gets layout res id.
     *
     * @return the layout res id
     */
    public abstract int getLayoutResId();

    /**
     * Sets hint.
     *
     * @param resId the res id
     */
    public void setHint(int resId) {
        mEditText.setHint(resId);
    }

    /**
     * Gets raw input.
     *
     * @return the raw input
     */
    public String getRawInput() {
        return mEditText.getText().toString();
    }

    /**
     * Sets error.
     *
     * @param error the error
     */
    public void setError(String error) {
        mEditText.setError(error);
    }

    /**
     * Sets text color.
     *
     * @param resId the res id
     */
    public void setTextColor(int resId) {
        mEditText.setTextColor(resId);
    }

}
