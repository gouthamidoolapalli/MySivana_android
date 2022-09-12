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
package com.mysivana.ui.adapters;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mysivana.R;

public class WelcomePagerAdapter extends PagerAdapter {
    String[] welcomeNotes;
    Context context;
    int myImages[] = {R.drawable.sign_in_up, R.drawable.pay_btc, R.drawable.get_cash};
    String myTitles[], myDesc[];

    public WelcomePagerAdapter(Context context, String[] welcomeNotes) {
        this.context = context;
        this.welcomeNotes = welcomeNotes;
        myTitles = context.getResources().getStringArray(R.array.welcome_titles);
        myDesc = context.getResources().getStringArray(R.array.welcome_desc);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemview = inflater.inflate(R.layout.item_welcome, container, false);
        TextView mTitle = itemview.findViewById(R.id.tv_title);
        mTitle.setText(myTitles[position]);
        TextView mSubTitle = itemview.findViewById(R.id.tv_subtitle);
        mSubTitle.setText(myDesc[position]);
        ImageView tourImage = itemview.findViewById(R.id.image_view);
        tourImage.setImageResource(myImages[position]);
        ((ViewPager) container).addView(itemview);
        return itemview;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ConstraintLayout) object);
    }

    @Override
    public float getPageWidth(int position) {
        return 1f;
    }
}
