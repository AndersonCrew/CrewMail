package com.dazone.crewemail.utils;

import android.content.Context;
import android.util.Log;

import com.dazone.crewemail.R;
import com.dazone.crewemail.data.MailData;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class TimeUtils {

    public static final Calendar FIRST_DAY_OF_TIME;
    public static final Calendar LAST_DAY_OF_TIME;
    public static final int DAYS_OF_TIME;
    public static final int MONTHS_OF_TIME;

    static {
        FIRST_DAY_OF_TIME = Calendar.getInstance();
        FIRST_DAY_OF_TIME.set(Calendar.getInstance().get(Calendar.YEAR) - 100, Calendar.JANUARY, 1);
        LAST_DAY_OF_TIME = Calendar.getInstance();
        LAST_DAY_OF_TIME.set(Calendar.getInstance().get(Calendar.YEAR) + 100, Calendar.DECEMBER, 31);
        DAYS_OF_TIME = (int) ((LAST_DAY_OF_TIME.getTimeInMillis() - FIRST_DAY_OF_TIME.getTimeInMillis()) / (24 * 60 * 60 * 1000));
        MONTHS_OF_TIME = (LAST_DAY_OF_TIME.get(Calendar.YEAR) - FIRST_DAY_OF_TIME.get(Calendar.YEAR)) * 12 + LAST_DAY_OF_TIME.get(Calendar.MONTH) - FIRST_DAY_OF_TIME.get(Calendar.MONTH);
    }

    public static String showTimeWithoutTimeZone(long date, String defaultPattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(defaultPattern, Locale.getDefault());
        return simpleDateFormat.format(new Date(date));
    }

    /**
     * CONVERT TIME TO STRING - NOTIFICATION SETTING
     */
    public static String getStrFromTime(int hours, int minutes) {
        /** CHECK AM/PM */
        String result = hours < 13 ? "AM " : "PM ";
        /** CHECK HOURS */
        result += hours < 10 ? "0" + hours + ":" : hours + ":";
        /** CHECK MINUTES */
        result += minutes < 10 ? "0" + minutes : minutes;
        return result;
    }

    /**
     * CONVERT STRING TO TIME - NOTIFICATION SETTING
     */
    public static Calendar getTimeFromStr(String strTime) {
        String hours = strTime.substring(3, 5);
        String minutes = strTime.substring(6, 8);
        int intHours = Integer.parseInt(hours);
        int intMinutes = Integer.parseInt(minutes);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, intHours);
        calendar.set(Calendar.MINUTE, intMinutes);
        return calendar;
    }

    /**
     * COMPARE TIME
     */
    public static boolean compareTime(String strFromTime, String strToTime) {
        Calendar calFromTime = getTimeFromStr(strFromTime);
        Calendar calToTime = getTimeFromStr(strToTime);
        int hourFromTime = calFromTime.get(Calendar.HOUR_OF_DAY);
        int minuteFromTime = calFromTime.get(Calendar.MINUTE);
        int hourToTime = calToTime.get(Calendar.HOUR_OF_DAY);
        int minuteToTime = calToTime.get(Calendar.MINUTE);

        if ((hourToTime == hourFromTime)) {
            if (minuteToTime > minuteFromTime) {
                return true;
            }
        } else {
            if (hourToTime > hourFromTime) {
                return true;
            }
        }
        return false;
    }

    /**
     * CHECK TIME BETWEEN
     */
    public static boolean isBetweenTime(String strFromTime, String strToTime) {
        Calendar calendar = Calendar.getInstance();
        Calendar calFromTime = getTimeFromStr(strFromTime);
        Calendar calToTime = getTimeFromStr(strToTime);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int hourFromTime = calFromTime.get(Calendar.HOUR_OF_DAY);
        int minuteFromTime = calFromTime.get(Calendar.MINUTE);
        int hourToTime = calToTime.get(Calendar.HOUR_OF_DAY);
        int minuteToTime = calToTime.get(Calendar.MINUTE);
        if (hourFromTime == hour || hour == hourToTime) {
            if (hourFromTime == hour && hour == hourToTime) {
                if (minuteFromTime <= minute && minute <= minuteToTime) {
                    return true;
                } else {
                    return false;
                }
            } else if (hourFromTime == hour && hour != hourToTime) {
                if (minuteFromTime <= minute) {
                    return true;
                } else {
                    return false;
                }
            } else if (hourFromTime != hour && hour == hourToTime) {
                if (minute <= minuteToTime) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            if (hourFromTime < hour && hour < hourToTime) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static String displayTimeWithoutOffset(String timeString) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(Statics.DATE_FORMAT_YYYY_MM_DD, Locale.getDefault());
            return formatter.format(new Date(getTime(timeString)));
        } catch (Exception e) {
            return "";
        }
    }

    public static long getTime(String timeString) {
        try {
            long time;

            if (timeString.contains("(")) {
                timeString = timeString.replace("/Date(", "");
                int plusIndex = timeString.indexOf("+");
                int minusIndex = timeString.indexOf("-");
                if (plusIndex != -1) {
                    time = Long.valueOf(timeString.substring(0, plusIndex));
                    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                    cal.setTimeInMillis(time);
                    cal.add(Calendar.HOUR_OF_DAY, Integer.parseInt(timeString.substring(plusIndex + 1, plusIndex + 3)));
                    cal.add(Calendar.MINUTE, Integer.parseInt(timeString.substring(plusIndex + 3, plusIndex + 5)));
                    Calendar tCal = Calendar.getInstance();
                    tCal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
                    time = tCal.getTimeInMillis();
                } else if (minusIndex != -1) {
                    time = Long.valueOf(timeString.substring(0, minusIndex));
                    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                    cal.setTimeInMillis(time);
                    cal.setTimeZone(TimeZone.getDefault());
                    cal.add(Calendar.HOUR_OF_DAY, -Integer.parseInt(timeString.substring(minusIndex + 1, minusIndex + 3)));
                    cal.add(Calendar.MINUTE, -Integer.parseInt(timeString.substring(minusIndex + 3, minusIndex + 5)));
                    Calendar tCal = Calendar.getInstance();
                    tCal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
                    time = tCal.getTimeInMillis();
                } else {
                    time = Long.valueOf(timeString.substring(0, timeString.indexOf(")")));
                }
            } else {
                time = Long.valueOf(timeString);
            }

            return time;
        } catch (Exception e) {
            Log.d("lchTest", e.toString());
            return 0;
        }
    }

    public static String getFormattedTime(long date, String defaultPattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(defaultPattern, Locale.getDefault());
        return simpleDateFormat.format(new Date(date));
    }

    public static String getTimezoneOffsetInMinutes() {
        TimeZone tz = TimeZone.getDefault();
        int offsetMinutes = tz.getRawOffset() / 60000;
        String sign = "";
        if (offsetMinutes < 0) {
            sign = "-";
            offsetMinutes = -offsetMinutes;
        }
        return sign + "" + offsetMinutes;
    }

    public static String getTimeStamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Timestamp(System.currentTimeMillis()));
    }

    /**
     * @param context    application context
     * @param timeString with format "/Date(1450746095000)/"
     * @return Today hh:mm aa || yesterday hh:mm aa || yyyy-MM-dd hh:mm aa
     */
    public static String displayTimeWithoutOffset(Context context, String timeString, int task) {
        try {
            String tempTimeString = timeString;
            tempTimeString = tempTimeString.replace("/Date(", "");
            tempTimeString = tempTimeString.replace(")/", "");
            long time = Long.valueOf(tempTimeString);
            return displayTimeWithoutOffset(context, time, task);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * format time
     *
     * @param context application context
     * @param time    long in milliseconds
     * @return Today hh:mm aa || yesterday hh:mm aa || yyyy-MM-dd hh:mm aa
     */
    //task - 0: EN
    //task - 1: KO
    public static String displayTimeWithoutOffset(Context context, long time, int task) {
        SimpleDateFormat formatter;
        if (task == 0) {
//            formatter = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
            formatter = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
            formatter.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        } else {
//            formatter = new SimpleDateFormat("aa hh:mm", Locale.getDefault());
            formatter = new SimpleDateFormat("aa hh:mm", Locale.KOREA);
            formatter.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        }
        int type = (int) getTimeForMail(time);
        String dateString;
        switch (type) {
            case -2:
                dateString = context.getString(R.string.today) + " " + formatter.format(new Date(time)).toLowerCase();
                break;
            case -3:
                dateString = context.getString(R.string.yesterday) + " " + formatter.format(new Date(time)).toLowerCase();
                break;
            default:

                if (task == 0) {
                    formatter.applyLocalizedPattern("yyyy-MM-dd hh:mm aa");
                } else {
                    formatter.applyLocalizedPattern("yyyy-MM-dd aa hh:mm");
                }
                dateString = formatter.format(new Date(time)).toLowerCase();
                break;
        }
        return dateString;
    }


    //1: today
    //2: Yesterday
    //0: default

    public static int getYearNote(long date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        return cal.get(Calendar.YEAR);
    }

    public static int getMonthNote(long date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        return cal.get(Calendar.MONTH);
    }

    public static int getDateNote(long date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    //-2: today
    //-3: Yesterday
    //-4: this month
    //-5: last Month
    //-1: default
    public static long getTimeForMail(long time) {
        int date = -1;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        if (cal.get(Calendar.YEAR) == getYearNote(time)) {
            if (cal.get(Calendar.MONTH) == getMonthNote(time)) {
                int temp = cal.get(Calendar.DAY_OF_MONTH) - getDateNote(time);
                if (cal.get(Calendar.DAY_OF_MONTH) == getDateNote(time)) {
                    date = -2;
                } else if (temp == 1) {
                    date = -3;
                } else {
                    date = -4;
                }
            } else if (cal.get(Calendar.MONTH) - 1 == getMonthNote(time)) {
                date = -5;
            }
        } else if (cal.get(Calendar.YEAR) == getYearNote(time) + 1) {
            if (cal.get(Calendar.MONTH) == 0 && getMonthNote(time) == 11) {
                date = -5;
            }
        }
        return date;
    }

    public static long getTimeFromString(String timeString) {
        long time;
        try {
            String tempTimeString = timeString;
            tempTimeString = tempTimeString.replace("/Date(", "");
            tempTimeString = tempTimeString.replace(")/", "");
            time = Long.valueOf(tempTimeString);
        } catch (Exception e) {
            return 0;
        }
        return time;
    }

    public static List<MailData> CheckDateTime(List<MailData> list) {
        List<MailData> listTemp = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            MailData mailData = list.get(i);
            if (i == 0) {
                if (mailData.getMailNo() > 0) {
                    MailData mailDataTemp = new MailData();
                    String time = "";
                    if (!new PreferenceUtilities().getDomain().equals("dazone")) {
                        time = TimeUtils.getTimeWithTimezone(mailData.getRegisterDate());
                    } else {
                        time = mailData.getRegisterDate();

                    }
                    mailDataTemp.setMailNo(getTimeForMail(getTimeFromString(time)));
                    mailDataTemp.setRegisterDate(mailData.getRegisterDate());
                    listTemp.add(mailDataTemp);
                    listTemp.add(mailData);
                } else {
                    listTemp.add(mailData);
                }
            } else {
                long noTemp;
                long noTemp2;
                if (mailData.getMailNo() > 0) {
                    if (!new PreferenceUtilities().getDomain().equals("dazone")) {
                        noTemp = getTimeForMail(getTimeFromString(getTimeWithTimezone(list.get(i - 1).getRegisterDate())));
                        noTemp2 = getTimeForMail(getTimeFromString(getTimeWithTimezone(mailData.getRegisterDate())));
                    } else {
                        noTemp = getTimeForMail(getTimeFromString(list.get(i - 1).getRegisterDate()));
                        noTemp2 = getTimeForMail(getTimeFromString(mailData.getRegisterDate()));//                    }

                    }
                    if (noTemp != noTemp2) {
                        MailData mailDataTemp = new MailData();
                        mailDataTemp.setMailNo(noTemp2);
                        mailDataTemp.setRegisterDate(mailData.getRegisterDate());
                        listTemp.add(mailDataTemp);
                        listTemp.add(mailData);
                    } else {
                        int month, monthTemp;

                        if (!new PreferenceUtilities().getDomain().equals("dazone")) {
                            month = getMonthNote(getTimeFromString(getTimeWithTimezone(list.get(i - 1).getRegisterDate())));
                            monthTemp = getMonthNote(getTimeFromString(getTimeWithTimezone(mailData.getRegisterDate())));
                        } else {
                            month = getMonthNote(getTimeFromString(list.get(i - 1).getRegisterDate()));
                            monthTemp = getMonthNote(getTimeFromString(mailData.getRegisterDate()));
                        }

                        if (month != monthTemp) {
                            MailData mailDataTemp = new MailData();
                            mailDataTemp.setMailNo(noTemp2);
                            mailDataTemp.setRegisterDate(mailData.getRegisterDate());
                            listTemp.add(mailDataTemp);
                            listTemp.add(mailData);
                        } else {
                            listTemp.add(mailData);
                        }
                    }
                } else {
                    listTemp.add(mailData);
                }
            }
        }
        return listTemp;
    }

    //Get Month from date long
    public static String getMonth(long date) throws ParseException {
        String monthName;
        String dateNote = Util.parseMili2Date(date, Statics.DATE_MONTH);
        String yearNote = Util.parseMili2Date(date, Statics.DATE_YEAR);
        monthName = dateNote + " " + yearNote;
        return monthName;
    }

    public static String GetMonth(long type, String time) {
        String month = "";
        try {
            if (type == -2) {
                month = Util.getString(R.string.string_today);
            } else if (type == -3) {
                month = Util.getString(R.string.string_yesterday);
            } else if (type == -4) {
                month = Util.getString(R.string.string_this_month);
            } else if (type == -5) {
                month = Util.getString(R.string.last_month);
            } else {
                month = getMonth(getTimeFromString(time));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return month;
    }

    public static String getTimeWithTimezone(String timeString) {
        timeString = timeString.replace("+0900", "");
        return timeString;
    }

}
