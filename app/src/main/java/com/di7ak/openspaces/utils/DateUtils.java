package com.di7ak.openspaces.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import com.di7ak.openspaces.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtils {
    private static boolean isToday(Long milliseconds) {
        Calendar c = Calendar.getInstance();

        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        Date today = c.getTime();

        c.setTimeInMillis(milliseconds);
        Date date = c.getTime();

        return !date.before(today);
    }

    public static boolean isOneDay(Long milliseconds, Long milliseconds2) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(milliseconds2);

        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        Date today = c.getTime();

        c.setTimeInMillis(milliseconds);
        Date date = c.getTime();

        return !date.before(today);
    }

    private static boolean isCurrentYear(Long milliseconds) {
        Calendar c = Calendar.getInstance();

        int currentYear = c.get(Calendar.YEAR);
        c.setTimeInMillis(milliseconds);
        int oldYear = c.get(Calendar.YEAR);

        return currentYear == oldYear;
    }

    @SuppressLint("SimpleDateFormat")
    public static String format(Long time) {
        String format = isToday(time) ? "HH:mm" : isCurrentYear(time) ? "dd MMMM HH:mm" : "dd MMMM yyyy";
        Date date = new Date();
        date.setTime(time);
        return new SimpleDateFormat(format).format(date);
    }

    @SuppressLint("SimpleDateFormat")
    public static String formatDateOnly(Long time, Context context) {
        if(isToday(time)) return context.getString(R.string.today);
        String format = isCurrentYear(time) ? "dd MMMM" : "dd MMMM yyyy";
        Date date = new Date();
        date.setTime(time);
        return new SimpleDateFormat(format).format(date);
    }


    @SuppressLint("SimpleDateFormat")
    public  static String formatShort(Long time) {
        String format = isToday(time) ? "HH:mm" : isCurrentYear(time) ? "dd MMM " : "dd MMM yyyy";
        Date date = new Date();
        date.setTime(time);
        return new SimpleDateFormat(format).format(date);
    }

    @SuppressLint("SimpleDateFormat")
    public  static String formatMessage(Long time) {
        String format = "HH:mm";
        Date date = new Date();
        date.setTime(time);
        return new SimpleDateFormat(format).format(date);
    }

    private static long getMinutesRight(Long milliseconds) {
        long left =  System.currentTimeMillis() - milliseconds;
        return left / (1000 * 60);
    }

    private static long getMinutesLeft(Long milliseconds) {
        long left = milliseconds - System.currentTimeMillis();
        return left / (1000 * 60);
    }

    public static String formatAdverts(Context context, Long time, TimeUnit timeUnit) {
        if(TimeUnit.SECONDS == timeUnit) time *= 1000;

        long minutes = getMinutesRight(time);
        long hours = minutes / 60;
        long days = hours / 24;
        if(minutes < 1) return context.getString(R.string.advert_date_now);
        else if(minutes < 60) {
            if(minutes > 10 && minutes < 20) return context.getString(R.string.advert_date_minutes_ago_many, Long.toString(minutes));
            else if(minutes % 10 == 1) return context.getString(R.string.advert_date_minutes_ago_one, Long.toString(minutes));
            else if(minutes % 10 == 0) return context.getString(R.string.advert_date_minutes_ago_many, Long.toString(minutes));
            else if(minutes % 10 == 2 || minutes % 10 == 3 || minutes % 10 == 4) return context.getString(R.string.advert_date_minutes_ago_few, Long.toString(minutes));
            else return context.getString(R.string.advert_date_minutes_ago_many, Long.toString(minutes));
        } else if(minutes < 60 * 24) {
            if(hours > 10 && hours < 20) return context.getString(R.string.advert_date_hour_ago_many, Long.toString(hours));
            else if(hours % 10 == 1) return context.getString(R.string.advert_date_hour_ago_one, Long.toString(hours));
            else if(hours % 10 == 0) return context.getString(R.string.advert_date_hour_ago_many, Long.toString(hours));
            else if(hours % 10 == 2 || hours % 10 == 3 || hours % 10 == 4) return context.getString(R.string.advert_date_hour_ago_few, Long.toString(hours));
            else return context.getString(R.string.advert_date_hour_ago_many, Long.toString(hours));
        } else if(hours < 24 * 2) return context.getString(R.string.advert_date_day_yesterday);
        else if(days > 31) return context.getString(R.string.advert_date_long_ago);
        else return formatShort(time);
    }

    public static String formatLeft(Context context, Long time) {
        long minutes = getMinutesLeft(time);
        long hours = minutes / 60;
        if (minutes < 60) {
            if (minutes > 10 && minutes < 20)
                return context.getString(R.string.advert_date_minutes_left_many, Long.toString(minutes));
            else if (minutes % 10 == 1)
                return context.getString(R.string.advert_date_minutes_left_one, Long.toString(minutes));
            else if (minutes % 10 == 0)
                return context.getString(R.string.advert_date_minutes_left_many, Long.toString(minutes));
            else if (minutes % 10 == 2 || minutes % 10 == 3 || minutes % 10 == 4)
                return context.getString(R.string.advert_date_minutes_left_few, Long.toString(minutes));
            else
                return context.getString(R.string.advert_date_minutes_left_many, Long.toString(minutes));
        } else if (minutes < 60 * 24) {
            if (hours > 10 && hours < 20)
                return context.getString(R.string.advert_date_hour_left_many, Long.toString(hours));
            else if (hours % 10 == 1)
                return context.getString(R.string.advert_date_hour_left_one, Long.toString(hours));
            else if (hours % 10 == 0)
                return context.getString(R.string.advert_date_hour_left_many, Long.toString(hours));
            else if (hours % 10 == 2 || hours % 10 == 3 || hours % 10 == 4)
                return context.getString(R.string.advert_date_hour_left_few, Long.toString(hours));
            else
                return context.getString(R.string.advert_date_hour_left_many, Long.toString(hours));
        } else return formatShort(time);
    }

    public static String formatUser(Context context, Long time, TimeUnit timeUnit) {
        return context.getString(R.string.last_visit, formatAdverts(context, time, timeUnit).toLowerCase());
    }
}