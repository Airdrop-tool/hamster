package com.cuongpq.hamster.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class DateUtil {

    private DateUtil() {
    }

    public static final String FORMAT_YEAR_YY = "YY";
    public static final String FORMAT_YEAR_YYYY = "YYYY";
    public static final String FORMAT_MONTH = "MM";
    public static final String FORMAT_SHORT_YEAR_MONTH = "YYMM";
    public static final String FORMAT_DAY_MONTH_YEAR = "dd/MM/yyyy";
    public static final String FORMAT_HOUR_MINUTE_DAY_MONTH_YEAR = "HH:mm dd/MM/yyyy";
    public static final String FORMAT_DAY_MONTH_YEAR_HOUR_MINUTE_SECOND = "dd-MM-yyyy HH:mm:ss";
    public static final String FORMAT_YEAR_MONTH_DAY = "YYYY-MM-DD";

    public static final Integer HOURS_OF_A_DAY = 24;

    /**
     * Lấy ngày giờ hiện tại hệ thống
     */
    public static Date getCurrentDate() {
        return Calendar.getInstance().getTime();
    }

    public static long getCurrentMillis() {
        return Calendar.getInstance().getTimeInMillis();
    }

    /**
     * Lấy năm hiện tại
     */
    public static Integer getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    /**
     * Convert Date to String
     *
     * @param date   the date input
     * @param format the format to convert
     * @return String
     */
    public static String toString(Date date, String format) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * Lấy giờ bắt đầu trong ngày
     */
    public static Date getStartDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * Lấy giờ cuối cùng trong ngày
     */
    public static Date getEndDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    /**
     * Lấy thời gian custom
     */
    public static Date getDateWithTime(Date date, int hour, int minute, int second, int millisecond) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, millisecond);
        return cal.getTime();
    }

    /**
     * Kiểm tra là thứ 2
     */
    public static boolean isMonday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return Objects.equals(cal.get(Calendar.DAY_OF_WEEK), Calendar.MONDAY);
    }

    /**
     * Cộng ngày
     */
    public static Date add(Date date, Integer days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }

    /**
     * Cộng giờ
     */
    public static Date addHours(Date date, Integer hours) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR, hours);
        return cal.getTime();
    }
}
