package com.dazone.crewemail.Service;

import android.util.Log;

import com.dazone.crewemail.DaZoneApplication;
import com.dazone.crewemail.interfaces.pushlishProgressInterface;
import com.dazone.crewemail.utils.TimeUtils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

public class fileUploadDownload {
    public static String uploadFileToServer(String filePath, String fileName, pushlishProgressInterface pushlishProgressInterface) {
        pushlishProgressInterface pushlishProgressInter;
        String rootFile = DaZoneApplication.getInstance().getPrefs().getServerSite() + "/UI/MobileMail3/MobileFileUpload.ashx?";
        pushlishProgressInter = pushlishProgressInterface;
        File sourceFile = new File(filePath);

        String url = rootFile +
                "sessionId=" + DaZoneApplication.getInstance().getPrefs().getAccessToken() + "&languageCode=" +
                Locale.getDefault().getLanguage().toUpperCase() + "&timeZoneOffset=" +
                TimeUtils.getTimezoneOffsetInMinutes();

        String boundary = "7dfe519300448";
        String delimiter = "\r\n--" + boundary + "\r\n";

        try {
            String postDataBuilder = delimiter +
                    "Content-Disposition: form-data; name=\"UploadFile\"; filename=\"" + fileName + "\"\r\n" +
                    "Content-Type: application/octet-stream" + "\r\n" +
                    "\r\n";
            InputStream inputStream;
            InputStreamReader inputStreamReader;
            BufferedReader bufferedReader;
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            final FileInputStream in = new FileInputStream(filePath);
            DataOutputStream out = new DataOutputStream(new BufferedOutputStream(conn.getOutputStream()));
            out.writeUTF(postDataBuilder);
            out.flush();

            byte[] buffer = new byte[1024];
            int readLength;
            long progress = 0;
            int nCurPercent = 0;
            while ((readLength = in.read(buffer)) != -1) {
                out.write(buffer, 0, readLength);
                out.flush();
                Log.d("fileUploadDownload", "progress" + progress + "sourceFile" + sourceFile.length());

                progress += readLength;
                int percent = (int) (((double) progress / (double) in.getChannel().size()) * 100);

                if (percent != nCurPercent && percent > 1) {
                    nCurPercent = percent;
                    pushlishProgressInter.push(nCurPercent, sourceFile.getName());
                }
            }

            out.writeBytes("\r\n--" + boundary + "--\r\n");
            out.flush();
            out.close();
            in.close();

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = conn.getInputStream();
                inputStreamReader = new InputStreamReader(inputStream);
                bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String s;

                while ((s = bufferedReader.readLine()) != null) {
                    sb.append(s);
                }

                return sb.toString();
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return "";
    }
}
