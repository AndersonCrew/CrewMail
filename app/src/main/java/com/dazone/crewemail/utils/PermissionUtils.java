package com.dazone.crewemail.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtils {

    public static boolean checkPermisstion(Context context, String[] permissions) {
        for (String permisstion : permissions) {
            if (ContextCompat.checkSelfPermission(context, permisstion)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    public static void setPermisstions(Activity activity, int REQUEST_CODE, String[] permisstions) {
        ActivityCompat.requestPermissions(activity, permisstions, REQUEST_CODE);
    }

    public static final String READ_EXTERNAL = Manifest.permission.READ_EXTERNAL_STORAGE;
    public static final String WRITE_EXTERNAL = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String[] READ_WRITE_EXTERNAL = {READ_EXTERNAL, WRITE_EXTERNAL};
}
