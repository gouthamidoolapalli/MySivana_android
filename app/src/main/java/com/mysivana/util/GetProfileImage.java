package com.mysivana.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.mysivana.R;

import java.io.IOException;
import java.net.URL;

public class GetProfileImage extends AsyncTask<String, String, Bitmap> {
    String url;
    ImageView mProfileImg;
    Context context;
    String userId;

    public GetProfileImage(Context context, String url, ImageView mProfileImg,String userId) {
        this.context = context;
        this.url = url;
        this.mProfileImg = mProfileImg;
        this.userId = userId;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProfileImg.setImageResource(R.drawable.ic_user_avatar);
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        Bitmap imageBitmap = null;
        try {
            URL _url = new URL(url);
            imageBitmap = BitmapFactory.decodeStream(_url.openConnection().getInputStream());
            FileHelper.saveProfilePicture(context, imageBitmap, userId);
        } catch (IOException e) {
            System.out.println(e);
        }
        return imageBitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        mProfileImg.setImageBitmap(bitmap);
    }

}
