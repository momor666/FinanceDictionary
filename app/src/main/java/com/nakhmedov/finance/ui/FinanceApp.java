package com.nakhmedov.finance.ui;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatDelegate;

import com.nakhmedov.finance.db.DbUpgradeHelper;
import com.nakhmedov.finance.db.entity.DaoMaster;
import com.nakhmedov.finance.db.entity.DaoSession;

import org.greenrobot.greendao.database.Database;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 4/2/17
 * Time: 12:57 AM
 * To change this template use File | Settings | File Templates
 */

public class FinanceApp extends Application {

    private static Context appContext;
    private DaoSession daoSession;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        appContext = getApplicationContext();

        DbUpgradeHelper helper = new DbUpgradeHelper(this, "finance-db");
        Database db = helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();

    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public static Context getAppContext() {
        return appContext;
    }

    public static FinanceApp getApplication(Context context) {
        if (context instanceof FinanceApp) {
            return (FinanceApp) context;
        }
        return (FinanceApp) context.getApplicationContext();
    }
}
