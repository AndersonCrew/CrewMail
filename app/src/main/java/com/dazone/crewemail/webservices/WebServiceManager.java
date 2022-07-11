package com.dazone.crewemail.webservices;

import android.annotation.SuppressLint;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dazone.crewemail.DaZoneApplication;
import com.dazone.crewemail.R;
import com.dazone.crewemail.data.ErrorData;
import com.dazone.crewemail.utils.Prefs;
import com.dazone.crewemail.utils.Statics;
import com.dazone.crewemail.utils.Urls;
import com.dazone.crewemail.utils.Util;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class WebServiceManager<T> {

    public WebServiceManager() {
    }

    @SuppressLint("TrulyRandom")
    public void doJsonObjectRequest(int requestMethod, final String url, final JSONObject bodyParam, final RequestListener<String> listener) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(requestMethod, url, bodyParam, response -> {
            try {
                JSONObject json = new JSONObject(response.getString("d"));

                int isSuccess;
                try {
                    isSuccess = json.getInt("success");
                } catch (Exception e) {
                    isSuccess = json.getBoolean("success") ? 1 : 0;
                }

                if (isSuccess == 1) {
                    try {
                        listener.onSuccess(json.getString("data"));
                    } catch (Exception e) {
                        listener.onSuccess(json.toString());
                        e.printStackTrace();
                    }
                } else {
                    ErrorData errorDto = new Gson().fromJson(json.getString("error"), ErrorData.class);
                    if (errorDto == null) {

                        errorDto = new ErrorData();
                        errorDto.setMessage(Util.getString(R.string.no_network_error));
                    } else {
                        if (errorDto.getCode() == 0 && !url.contains(Urls.URL_CHECK_DEVICE_TOKEN) && !url.contains(Urls.URL_CHECK_SESSION)
                                && !url.contains(Urls.URL_INSERT_PHONE_TOKEN)
                                && !url.contains(Urls.URL_GET_USER_INFO)) {
                        }
                    }

                    listener.onFailure(errorDto);
                }

            } catch (JSONException e) {

                ErrorData errorDto = new ErrorData();
                errorDto.setMessage(Util.getString(R.string.no_network_error));
                listener.onFailure(errorDto);
            }
        }, error -> {
            error.printStackTrace();
            ErrorData errorDto = new ErrorData();
            listener.onFailure(errorDto);
        });
        DaZoneApplication.getInstance().addToRequestQueue(jsonObjectRequest, url);
    }

    public interface RequestListener<T> {
        void onSuccess(T response);

        void onFailure(ErrorData error);
    }
}
