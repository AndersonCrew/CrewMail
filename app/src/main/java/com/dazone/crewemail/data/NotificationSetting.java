package com.dazone.crewemail.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NotificationSetting implements Serializable {
    @SerializedName("NotificationOptions")
    private String notificationOptions;

    @SerializedName("DeviceNo")
    private int DeviceNo;

    @SerializedName("UserNo")
    private int UserNo;

    @SerializedName("TimezoneOffset")
    private int TimezoneOffset;

    @SerializedName("RegDate")
    private String RegDate;

    @SerializedName("DeviceID")
    private String DeviceID;

    @SerializedName("OSVersion")
    private String OSVersion;

    public String getNotificationOptions() {
        return notificationOptions;
    }

    public int getDeviceNo() {
        return DeviceNo;
    }

    public int getUserNo() {
        return UserNo;
    }

    public int getTimezoneOffset() {
        return TimezoneOffset;
    }

    public String getRegDate() {
        return RegDate;
    }

    public String getDeviceID() {
        return DeviceID;
    }

    public String getOSVersion() {
        return OSVersion;
    }

    public class NotificationOptions {
        @SerializedName("starttime")
        private String starttime;

        @SerializedName("endtime")
        private String endtime;

        @SerializedName("enabled")
        private boolean enabled;

        @SerializedName("notitime")
        private boolean notitime;

        public String getStarttime() {
            return starttime;
        }

        public String getEndtime() {
            return endtime;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public boolean isNotitime() {
            return notitime;
        }
    }
}


