package com.dazone.crewemail.webservices;

import android.text.TextUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class WebClient {

    private final static ObjectMapper mJSONObjectMapper = new ObjectMapper();

    private static final String SERVICE_URL_LOGIN_V2 = "/UI/WebService/WebServiceCenter.asmx/Login_v2";
    private static final String SERVICE_URL_LOGOUT_V2 = "/UI/WebService/WebServiceCenter.asmx/Logout_v2";
    private static final String SERVICE_URL_CHECK_SESSION_USER_V2 = "/UI/WebService/WebServiceCenter.asmx/CheckSessionUser_v2";
    private static final String SERVICE_URL_HAS_APPLICATION_V2 = "/UI/WebService/WebServiceCenter.asmx/HasApplication_v2";

    public interface OnWebClientListener {
        void onSuccess(JsonNode jsonNode);
        void onFailure();
    }

    public static String getJSONStringFromUrl(String methodUrl, Map<String, Object> params) {
        HttpURLConnection urlConnection = null;

        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;

        try {
            URL url = new URL(methodUrl);
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.addRequestProperty("Content-Type", "application/json");

            OutputStream outputStream = urlConnection.getOutputStream();
            outputStream.write(mJSONObjectMapper.writeValueAsString(params).getBytes());
            outputStream.flush();
            outputStream.close();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                inputStreamReader = new InputStreamReader(inputStream);
                bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String s;

                while ((s = bufferedReader.readLine()) != null) {
                    sb.append(s);
                }

                return sb.toString();
            }

            return "";
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (urlConnection != null) {
                try {
                    urlConnection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return "";
    }

    public static void Login_v2(String languageCode, int timeZoneOffset, String companyDomain, String userID, String password, String mobileOSVersion, String _domain, OnWebClientListener listener) {
        Map<String, Object> mapParams = new HashMap<>();
        mapParams.put("languageCode", languageCode);
        mapParams.put("timeZoneOffset", timeZoneOffset);
        mapParams.put("companyDomain", companyDomain);
        mapParams.put("userID", userID);
        mapParams.put("password", password);
        mapParams.put("mobileOSVersion", mobileOSVersion);

        String result = getJSONStringFromUrl(_domain + SERVICE_URL_LOGIN_V2, mapParams);

        if (TextUtils.isEmpty(result)) {
            listener.onFailure();
        } else {
            try {
                listener.onSuccess(mJSONObjectMapper.readTree(result).get("d"));
            } catch (Exception e) {
                e.printStackTrace();
                listener.onFailure();
            }
        }
    }

    public static void Logout_v2(String sessionId, String _domain, OnWebClientListener listener) {
        Map<String, Object> mapParams = new HashMap<>();
        mapParams.put("sessionId", sessionId);

        String result = getJSONStringFromUrl(_domain + SERVICE_URL_LOGOUT_V2, mapParams);

        if (TextUtils.isEmpty(result)) {
            listener.onFailure();
        } else {
            try {
                listener.onSuccess(mJSONObjectMapper.readTree(result).get("d"));
            } catch (Exception e) {
                e.printStackTrace();
                listener.onFailure();
            }
        }
    }

    public static void CheckSessionUser_v2(String languageCode, int timeZoneOffset, String sessionId, String _domain, OnWebClientListener listener) {
        Map<String, Object> mapParams = new HashMap<>();
        mapParams.put("languageCode", languageCode);
        mapParams.put("timeZoneOffset", timeZoneOffset);
        mapParams.put("sessionId", sessionId);

        String result = getJSONStringFromUrl(_domain + SERVICE_URL_CHECK_SESSION_USER_V2, mapParams);

        if (TextUtils.isEmpty(result)) {
            listener.onFailure();
        } else {
            try {
                listener.onSuccess(mJSONObjectMapper.readTree(result).get("d"));
            } catch (Exception e) {
                e.printStackTrace();
                listener.onFailure();
            }
        }
    }

    public static void HasApplication_v2(String languageCode, int timeZoneOffset, String projectCode, String _domain, OnWebClientListener listener) {
        Map<String, Object> mapParams = new HashMap<>();
        mapParams.put("languageCode", languageCode);
        mapParams.put("timeZoneOffset", timeZoneOffset);
        mapParams.put("projectCode", projectCode);

        String result = getJSONStringFromUrl(_domain + SERVICE_URL_HAS_APPLICATION_V2, mapParams);

        if (TextUtils.isEmpty(result)) {
            listener.onFailure();
        }

        try {
            listener.onSuccess(mJSONObjectMapper.readTree(result).get("d"));
        } catch (Exception e) {
            e.printStackTrace();
            listener.onFailure();
        }
    }
}