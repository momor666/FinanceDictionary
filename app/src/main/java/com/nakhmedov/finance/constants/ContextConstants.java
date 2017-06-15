package com.nakhmedov.finance.constants;

import com.nakhmedov.finance.ui.FinanceApp;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 4/7/17
 * Time: 4:50 PM
 * To change this template use File | Settings | File Templates
 */

public class ContextConstants {

    public static final String PACKAGE_NAME = FinanceApp.getAppContext().getPackageName();
    public static final String BASE_URL = "http://52.25.65.23/api/";
    public static final String CONVERTER_URL = "https://www.google.com/finance/";

    public static final int TERM_TYPE = 101;
    public static final int RECENT_TYPE = 102;

    public static final String ALARM_TIME = "09:00";
    //    Notifications
    public static final int NTFY_TERM_ID = 10;
    public static final int NTFY_CATEGORY_ID = 11;
    public static final int NTFY_DAILY_ID = 12;
}
