package com.dazone.crewemail.utils;

import android.graphics.Bitmap;
import android.webkit.MimeTypeMap;

import com.dazone.crewemail.BuildConfig;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * Created by Sherry on 12/8/15.
 */
public class Statics {
    public static final String LOG_TAG = ">>>sss CrewEmail";
    public static final int ACTIVITY_HANDLER_NEXT_ACTIVITY = 1111;
    public static final int ACTIVITY_HANDLER_START_UPDATE = 1112;

    /**
     * KEY PREFERENCES
     */
    public static final String KEY_PREFERENCES_PIN = "KEY_PREFERENCES_PIN";
    public static final String KEY_PREFERENCES_ADJUST_TO_SCREEN_WIDTH = "KEY_PREFERENCES_ADJUST_TO_SCREEN_WIDTH";
    public static final String KEY_PREFERENCES_NOTIFICATION_NEW_MAIL = "KEY_PREFERENCES_NOTIFICATION_NEW_MAIL";
    public static final String KEY_PREFERENCES_NOTIFICATION_SOUND = "KEY_PREFERENCES_NOTIFICATION_SOUND";
    public static final String KEY_PREFERENCES_NOTIFICATION_VIBRATE = "KEY_PREFERENCES_NOTIFICATION_VIBRATE";
    public static final String KEY_PREFERENCES_NOTIFICATION_TIME = "KEY_PREFERENCES_NOTIFICATION_TIME";
    public static final String KEY_PREFERENCES_NOTIFICATION_TIME_TO_TIME = "KEY_PREFERENCES_NOTIFICATION_TIME_TO_TIME";
    public static final String KEY_PREFERENCES_NOTIFICATION_TIME_FROM_TIME = "KEY_PREFERENCES_NOTIFICATION_TIME_FROM_TIME";
    public static final String KEY_DEVICE_ID = "KEY_DEVICE_ID";

    public static final String yyyy_MM_dd_HH_mm_ss_SSS = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * KEY INTENT
     */
    public static final String KEY_INTENT_TYPE_PIN = "KEY_INTENT_TYPE_PIN";

    /**
     * TYPE PIN
     */
    public static final int TYPE_PIN_SET = 1;
    public static final int TYPE_PIN_REMOVE = 2;
    public static final int TYPE_PIN_CHANGE = 3;
    public static final int TYPE_PIN_CONFIRM = 4;

    public static final int TYPE_OUTBOX = 3599;
    //DB
    public static final int DATABASE_VERSION = 8;
    public static final String DATABASE_NAME = "crew_email_1.db";

    public static final int REQUEST_TIMEOUT_MS = 15000;

    public static final boolean ENABLE_DEBUG = BuildConfig.DEBUG;

    public static final int DEFAULT_GET_NEW_DISPLAY_LIST_COUNT = 15;

    public static final String PREFS_KEY_SESSION_ERROR = "session_error";


    public static final String FORMAT_DATE_SESSION_EXPIRE = "EEEEEEE, dd MMM yyyy HH:mm:ss";

    public static final int FILE_PICKER_SELECT = 100;

    //file type
    public static final String IMAGE_JPG = ".jpg";
    public static final String IMAGE_JPEG = ".jpeg";
    public static final String IMAGE_PNG = ".png";
    public static final String IMAGE_GIF = ".gif";
    public static final String AUDIO_MP3 = ".mp3";
    public static final String AUDIO_WMA = ".wma";
    public static final String AUDIO_AMR = ".amr";
    public static final String VIDEO_MP4 = ".mp4";
    public static final String FILE_PDF = ".pdf";
    public static final String FILE_DOCX = ".docx";
    public static final String FILE_DOC = ".doc";
    public static final String FILE_XLS = ".xls";
    public static final String FILE_XLSX = ".xlsx";
    public static final String FILE_PPTX = ".pptx";
    public static final String FILE_PPT = ".ppt";
    public static final String FILE_ZIP = ".zip";
    public static final String FILE_RAR = ".rar";
    public static final String FILE_APK = ".apk";
    public static final String MIME_TYPE_AUDIO = "audio/*";
    public static final String MIME_TYPE_IMAGE = "image/*";
    public static final String MIME_TYPE_VIDEO = "video/*";
    public static final String MIME_TYPE_APP = "file/*";

    /********
     * activity request code
     ********/
    public static final int EMAIL_DETAIL_ACTIVITY = 200;
    public static final int EMAIL_MOVE_ACTIVITY = 201;
    public static final int EMAIL_FILTER_ADDRESS_ACTIVITY = 202;
    public static final int EMAIL_FILTER_SENDER_ACTIVITY = 203;
    // Go to Organization activity start with 300 - 399
    public static final int ORGANIZATION_TO_ACTIVITY = 300;
    public static final int ORGANIZATION_CC_ACTIVITY = 301;
    public static final int ORGANIZATION_BCC_ACTIVITY = 302;
    public static final int ORGANIZATION_DISPLAY_SELECTED_ACTIVITY = 303;

    //final public static String GOOGLE_SENDER_ID_MAIL = "537469459942";//AIzaSyAioHyUAEvdJ4GRDJEkJoxZVnCWMdOJezs
    final public static String GOOGLE_SENDER_ID_MAIL = "120079230335";
    public static final String DATE_FORMAT_PICTURE = "yyyyMMdd_HHmmss";
    public static final String DATE_MONTH = "MMMM";
    public static final String DATE_YEAR = "yyyy";

    public static final String SAVE_BOX_NO_PREF = "save_box_no";
    public static final String SAVE_BOX_NO_PREF_V2 = "save_box_no_v2";
    public static final long TIME_DELAY_LOAD_MORE = 1500;
    public static final DisplayImageOptions options2 = new DisplayImageOptions.Builder()
            .resetViewBeforeLoading(true)
            .cacheOnDisk(true).cacheInMemory(false)
            .imageScaleType(ImageScaleType.NONE_SAFE)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .considerExifParams(true)
            .displayer(new FadeInBitmapDisplayer(0))
            .considerExifParams(true)
            .displayer(new RoundedBitmapDisplayer(90))
            .build();

    public static final int USER_LOGOUT = 0;
    public static String DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd";
    public static String getMimeType(String url) {
        String type = null;
        String extension = url.substring(url.lastIndexOf(".") + 1);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }
}
