package com.nakhmedov.finance.util;

import com.nakhmedov.finance.constants.ContextConstants;

import java.util.Calendar;
import java.util.Date;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 4/28/17
 * Time: 1:33 PM
 * To change this template use File | Settings | File Templates
 */

public class AndroidUtil {

    public static boolean isMoreThanSelectedDays(Date thatDay, long givenDay) {
        Calendar thatDayCalendar = Calendar.getInstance();
        thatDayCalendar.setTime(thatDay);
        Calendar today = Calendar.getInstance();
        long diff = today.getTimeInMillis() - thatDayCalendar.getTimeInMillis(); //result in millis
        long days = diff / (24 * 60 * 60 * 1000);
        return days >= givenDay;
    }

    public static long getAlarmTime() {
        Calendar alarmCalendar = Calendar.getInstance();
        alarmCalendar.setTimeInMillis(System.currentTimeMillis());
        String alarmTimeText = ContextConstants.ALARM_TIME;
        String[] hours = alarmTimeText.split(":");
        int hour = Integer.parseInt(hours[0]);
        int minute = Integer.parseInt(hours[1]);
        alarmCalendar.add(Calendar.DAY_OF_MONTH, 1);
        alarmCalendar.set(Calendar.HOUR_OF_DAY, hour);
        alarmCalendar.set(Calendar.MINUTE, minute);

        return alarmCalendar.getTimeInMillis();
    }

}
