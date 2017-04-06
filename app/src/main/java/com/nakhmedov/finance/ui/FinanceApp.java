package com.nakhmedov.finance.ui;

import android.app.Application;

import com.nakhmedov.finance.ui.entity.DaoMaster;
import com.nakhmedov.finance.ui.entity.DaoMaster.DevOpenHelper;
import com.nakhmedov.finance.ui.entity.DaoSession;

import org.greenrobot.greendao.database.Database;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 4/2/17
 * Time: 12:57 AM
 * To change this template use File | Settings | File Templates
 */

public class FinanceApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();


        DevOpenHelper helper = new DevOpenHelper(this, "finance-db");
        Database db = helper.getWritableDb();
        DaoSession daoSession = new DaoMaster(db).newSession();

    }
}
