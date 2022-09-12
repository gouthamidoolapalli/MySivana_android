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
package com.mysivana.util;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtil {

    private static String TAG = CryptoUtil.class.getSimpleName();
    // passPhrase used for Data Encryption
    private static String passPhrase;
    private static final String exponentHexStr = "10001";

    private static final String ALGORITHM = "AES";
    public static final String KEY = "25432A462D4A614E645267556B587032";


    private static String generatePassPhrase() {
        String key1 = String.valueOf(Math.random());
        String key2 = String.valueOf(Math.random());
        passPhrase = key1.substring(2, 10) + key2.substring(2, 10);
        return passPhrase;
    }

    /**
     * @param keyAlgorithm
     * @param passPhrase
     */
    private static SecretKeySpec generateKey(String passPhrase, String keyAlgorithm) {
        return new SecretKeySpec(passPhrase.getBytes(), keyAlgorithm);
    }


    /**
     * @param inputString
     */
    public static String encrypt(String inputString)

    {
        SecretKeySpec aesKey = generateKey(KEY,ALGORITHM);
        String encryptedBase64String = "";
        try {
            IvParameterSpec ivSpec = new IvParameterSpec("1234567898765432".getBytes());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(1, aesKey, ivSpec);
            byte[] encryptedBytes = cipher.doFinal(inputString.getBytes());
            System.out.println("Encrypted Bytes : " + encryptedBytes);
            encryptedBase64String = Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
        } catch (NoSuchAlgorithmException nsaEx) {
            nsaEx.printStackTrace();
        } catch (NoSuchPaddingException nspEx) {
            nspEx.printStackTrace();
        } catch (InvalidKeyException ikEx) {
            ikEx.printStackTrace();
        } catch (BadPaddingException bpEx) {
            bpEx.printStackTrace();
        } catch (IllegalBlockSizeException ibsEx) {
            ibsEx.printStackTrace();
        } catch (InvalidAlgorithmParameterException iapEx) {
            iapEx.printStackTrace();
        }

        return encryptedBase64String;
    }

    /**
     * @param encryptedBase64String
     * @param key
     */
    public static String decrypt(String encryptedBase64String, SecretKeySpec key)

    {
        SecretKeySpec aesKey = key;
        String decryptedString = "";
        try {
            byte[] encryptedBytes = Base64.decode(encryptedBase64String, Base64.DEFAULT);
            IvParameterSpec ivSpec = new IvParameterSpec("1234567898765432".getBytes());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(2, aesKey, ivSpec);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            decryptedString = new String(decryptedBytes);
        } catch (NoSuchAlgorithmException nsaEx) {
            nsaEx.printStackTrace();
        } catch (NoSuchPaddingException nspEx) {
            nspEx.printStackTrace();
        } catch (InvalidKeyException ikEx) {
            ikEx.printStackTrace();
        } catch (BadPaddingException bpEx) {
            bpEx.printStackTrace();
        } catch (IllegalBlockSizeException ibsEx) {
            ibsEx.printStackTrace();
        } catch (InvalidAlgorithmParameterException iapEx) {
            iapEx.printStackTrace();
        }

        return decryptedString;
    }

    /**
     * This method is used to generate encryptedPassword for setAESPassword API
     *
     * @param dataPublicKey
     */
    public static String encryptedPassword(String dataPublicKey)

    {
        if (TextUtils.isEmpty(passPhrase)) {
            generatePassPhrase();
        }
        byte[] pubKeyBytes = Base64.decode(dataPublicKey, Base64.DEFAULT);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(pubKeyBytes);
        PublicKey publicKey = null;
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            Certificate cert = certFactory.generateCertificate(inputStream);
            publicKey = cert.getPublicKey();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

        try {
            if (cipher != null)
                cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        byte[] encryptedBytes = new byte[0];
        try {
            encryptedBytes = cipher.doFinal(passPhrase.getBytes());
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        String encryptedBytesVal = new String(org.apache.commons.codec.binary.Base64.encodeBase64(encryptedBytes));
        Log.v(TAG, encryptedBytesVal);
        return encryptedBytesVal;
    }

    public static String encryptPassword(String plainPassword, String serverPublicKey, String serverRandom) throws Exception {
        if (TextUtils.isEmpty(plainPassword)) {
            return null;
        }
        byte[] dataBytes = buildPKCS15BlockForPinVerify(plainPassword, serverRandom);

        RSAObfuscation rsaObfuscation = new RSAObfuscation();
        rsaObfuscation.setPublic(serverPublicKey, exponentHexStr);

        return rsaObfuscation.encryptNativeBytes(dataBytes);
    }

    private static byte[] buildPKCS15BlockForPinVerify(String pin, String random) {
        if (pin.length() > 30) {
            return null;
        }
        // block size is 128 bytes according to spec
        byte[] bytes = new byte[128];

        // convert the PIN to bytes from string
        byte[] PINBytes = pin.getBytes();

        // now generate the 30 byte password portion
        byte[] passwordBytes = new byte[30];
        for (int i = 0; i < 30; i++) {
            if (i < PINBytes.length)
                passwordBytes[i] = PINBytes[i];
            else
                passwordBytes[i] = (byte) 0xFF;
        }

        // convert the random number to bytes from string
        byte RandomBytes[] = fromHexString(random);//random.getBytes();

        int zeros = 128 - RandomBytes.length - passwordBytes.length;
        byte[] bytesPad = randomBytes(zeros);  //this is for random bytes
        for (int i = 0; i < zeros; i++) {
            if (bytesPad[i] == 0x00) {
                // arbitrarily replace with 0x27 for now
                bytesPad[i] = 0x27;
            }
        }

        bytesPad[0] = 0x00;
        bytesPad[1] = 0x02;
        bytesPad[10] = 0x00;

        System.arraycopy(bytesPad, 0, bytes, 0, bytesPad.length);
        System.arraycopy(RandomBytes, 0, bytes, bytesPad.length, RandomBytes.length);
        System.arraycopy(passwordBytes, 0, bytes, bytesPad.length + RandomBytes.length, passwordBytes.length);

        return bytes;
    }

    private static byte[] fromHexString(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private static byte[] randomBytes(int C) {
        byte[] A = new byte[C];
        for (int B = 0; B < C; B++) {
            A[B] = (byte) Math.ceil(Math.random() * 255);
        }
        return A;
    }

    /**
     * method convert the password to one way hash using MD5
     * @param pass as String
     */
    public static String encryptWithMD5(String pass) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] passBytes = pass.getBytes();
            md.reset();
            byte[] digested = md.digest(passBytes);
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < digested.length; i++) {
                sb.append(Integer.toHexString((digested[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            Log.i(CryptoUtil.TAG,"Couldn't encrypt the password : " + ex.getMessage());
        }
        return null;
    }

}