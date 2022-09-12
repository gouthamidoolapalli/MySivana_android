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
package com.mysivana.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import com.google.gson.Gson;
import com.mysivana.mvp.model.CryptoMenuResponse;
import com.mysivana.mvp.model.Merchant;
import com.mysivana.mvp.model.Transactions;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Helper class used to save data in files (Merchant list, transaction list and profilepicture)
 */
public class FileHelper {


    final static String TRANSACTION_LIST_TXT = "TransactionList.txt";
    final static String MENU_LIST_TXT = "MenuList.txt";
    final static String MERCHANT_LIST_TXT = "MerchantList.txt";
    final static String PROFILE_PICTURE = "IMG_";
    final static String TAG = FileHelper.class.getName();

    /**
     * @param context
     * @return
     */
    public static Transactions readTransactionList(Context context) {
        String line = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(context.getFilesDir(), TRANSACTION_LIST_TXT));
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            fileInputStream.close();
            line = stringBuilder.toString();

            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            Log.d(TAG, ex.getMessage());
        } catch (IOException ex) {
            Log.d(TAG, ex.getMessage());
        }
        Gson gson = new Gson();
        Transactions transactions = gson.fromJson(line, Transactions.class);
        return transactions;
    }

    public static boolean saveMenuList(Context context, CryptoMenuResponse cryptoMenuResponse) {
        try {
            File file = new File(context.getFilesDir(), MENU_LIST_TXT);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            Gson gson = new Gson();
            String data = gson.toJson(cryptoMenuResponse);
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            fileOutputStream.write((data).getBytes());

            return true;
        } catch (FileNotFoundException ex) {
            Log.d(TAG, ex.getMessage());
        } catch (IOException ex) {
            Log.d(TAG, ex.getMessage());
        }
        return false;


    }

    public static boolean saveTransactionList(Context context, Transactions transactions) {
        try {
            File file = new File(context.getFilesDir(), TRANSACTION_LIST_TXT);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            Gson gson = new Gson();
            String data = gson.toJson(transactions);
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            fileOutputStream.write((data).getBytes());

            return true;
        } catch (FileNotFoundException ex) {
            Log.d(TAG, ex.getMessage());
        } catch (IOException ex) {
            Log.d(TAG, ex.getMessage());
        }
        return false;


    }


    public static Merchant readMerchantList(Context context) {
        String line = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(context.getFilesDir(), MERCHANT_LIST_TXT));
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            fileInputStream.close();
            line = stringBuilder.toString();

            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            Log.d(TAG, ex.getMessage());
        } catch (IOException ex) {
            Log.d(TAG, ex.getMessage());
        }
        Gson gson = new Gson();
        Merchant merchant = gson.fromJson(line, Merchant.class);
        return merchant;
    }

    public static CryptoMenuResponse readMenuList(Context context) {
        String line = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(context.getFilesDir(), MENU_LIST_TXT));
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            fileInputStream.close();
            line = stringBuilder.toString();

            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            Log.d(TAG, ex.getMessage());
        } catch (IOException ex) {
            Log.d(TAG, ex.getMessage());
        }
        Gson gson = new Gson();
        CryptoMenuResponse response = gson.fromJson(line, CryptoMenuResponse.class);
        return response;
    }


    public static boolean saveMerchantList(Context context, Merchant merchant) {
        try {
            File file = new File(context.getFilesDir(), MERCHANT_LIST_TXT);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            Gson gson = new Gson();
            String data = gson.toJson(merchant);
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            fileOutputStream.write((data).getBytes());

            return true;
        } catch (FileNotFoundException ex) {
            Log.d(TAG, ex.getMessage());
        } catch (IOException ex) {
            Log.d(TAG, ex.getMessage());
        }
        return false;


    }

    public static boolean saveProfilePicture(Context context, Bitmap bitmap, String userID) {
        try {

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
            File file = new File(context.getFilesDir(), PROFILE_PICTURE + userID + ".jpg");
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            FileOutputStream fo;

            fo = new FileOutputStream(file);
            fo.write(bytes.toByteArray());
            fo.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;

    }

    public static Bitmap getProfilePicture(Context context, String userID) {
        Bitmap thumbnail = null;
        try {
            File file = new File(context.getFilesDir(), PROFILE_PICTURE + userID + ".jpg");
            FileInputStream fi = new FileInputStream(file);
            thumbnail = BitmapFactory.decodeStream(fi);
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }

        return thumbnail;
    }

    public static boolean isProfilePictureAvailable(Context context, String userID) {
        File file = new File(context.getFilesDir(), PROFILE_PICTURE + userID + ".jpg");
        return file.exists();
    }


    public static void clear(Context context) {
        File appDirectory = context.getFilesDir();
        if (appDirectory.isDirectory()) {
            String[] children = appDirectory.list();
            for (int i = 0; i < children.length; i++) {
                File file = new File(appDirectory, children[i]);
                if (file.getPath().contains(MERCHANT_LIST_TXT)) {
                    continue;
                }
                if (!file.getPath().contains(PROFILE_PICTURE))
                    file.delete();
            }

        }
    }



    public static Bitmap modifyOrientation(Bitmap bitmap, String image_absolute_path) throws IOException {
        ExifInterface ei = new ExifInterface(image_absolute_path);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotate(bitmap, 90);

            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotate(bitmap, 180);

            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotate(bitmap, 270);

            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                return flip(bitmap, true, false);

            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                return flip(bitmap, false, true);

            default:
                return bitmap;
        }
    }

    private static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
