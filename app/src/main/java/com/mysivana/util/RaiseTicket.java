/**
 * Copyright MySivana LLC
 *
 * (C) Copyright MySivana LLC	All rights reserved.
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
 * DATE				: PROGRAMMER  : DESCRIPTION
 * ========================================================================
 * JUNE 01 2018		: BYNDR       : CREATED.
 * ------------------------------------------------------------------------
 *
 * ========================================================================
 */
package com.mysivana.util;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
/**
 * util class to create tickets for runtime failures
 * for technical team to check, this uses Freshdesk ticketing tool
 * api call passing the error json returned from the server
 */
public class RaiseTicket {


    public static final String FRESH_DESK_API_ENDPOINT = "https://mysivana.freshdesk.com/api/v2/tickets";
    public static final String FRESH_DESK_API_KEY = "m4mqow5fu4gW0YLRLW0";
    public static final String HEADER_CONTENT_TYPE_KEY = "Content-Type";
    public static final String HEADER_CONTENT_USER_AGENT_KEY = "User-Agent";
    public static final String HEADER_CONTENT_USER_AGENT_VALUE = "Mozilla/5.0";
    public static final String HEADER_CONTENT_TYPE_VALUE_JSON = "application/json";
    public static final String FRESHDESK_LOGIN = "c2F0aXNoQG15c2l2YW5hLmNvbTpzaXZhbmFAMTIz";
    public static final int FRESHDESK_STATUS_OPEN = 2;
    public static final int FRESHDESK_SOURCE = 8;
    public static final int FRESHDESK_PRIORITY_HIGH = 3;
    public static final String API_REQUEST_METHOD_POST = "POST";

    private static final String TAG = RaiseTicket.class.getSimpleName();

    /**
     * method to create JSON request for Freshdesk api call to create tickets for
     * App or Transaction feedback / issues
     *
     * @param email     email in this case it will be MySivana support team email id
     * @param message   error message which app caught while running the process
     * @return JSON     request json as string that will be submitted to Freshdesk api
     */
    private static String parseJsonForTicket(String email, String message) {
        try {
            if (message != null && email != null) {
                JSONObject request = new JSONObject();
                request.put("description", message);
                request.put("subject", "App Failed - Issue Details !");
                request.put("priority", FRESHDESK_PRIORITY_HIGH);
                request.put("email", email);
                request.put("status", FRESHDESK_STATUS_OPEN);
                request.put("source", FRESHDESK_SOURCE);

                JSONArray array = new JSONArray();
                array.put(1, "feedback@mysivana.com");
                array.put(2, "satish@mysivana.com");
                array.put(3, "sam@mysivana.com");

                request.put("cc_emails", array);
                return request.toString();
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * method to capture user feedback on the MySivana (Android / IOS)
     *
     * @param errorMessage The error message which is sent from server
     * @param userId       user ID must be email ID only
     */
    public static void submitTicket(String errorMessage, String userId) {
        String response = "";

        if (errorMessage != null && userId != null) {
            try {
                URL url = new URL(FRESH_DESK_API_ENDPOINT);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod(API_REQUEST_METHOD_POST);
                conn.setRequestProperty("apikey", FRESH_DESK_API_KEY);
                conn.setRequestProperty(HEADER_CONTENT_TYPE_KEY, HEADER_CONTENT_TYPE_VALUE_JSON);
                conn.setRequestProperty(HEADER_CONTENT_USER_AGENT_KEY, HEADER_CONTENT_USER_AGENT_VALUE);
                conn.setDoOutput(true);
                conn.setDoInput(true);

                String encodedAuthorization = FRESHDESK_LOGIN;
                conn.setRequestProperty("Authorization", "Basic " + encodedAuthorization);

                String requestJson = parseJsonForTicket(userId, errorMessage);

                OutputStream os = conn.getOutputStream();
                os.write(requestJson.getBytes());
                os.close();

                if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + conn.getResponseCode());
                }
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));

                String output;
                Log.i(TAG, "Output from Server .... \n");
                while ((output = br.readLine()) != null) {
                    response += output;
                }
                Log.i(TAG, response);

                conn.disconnect();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

