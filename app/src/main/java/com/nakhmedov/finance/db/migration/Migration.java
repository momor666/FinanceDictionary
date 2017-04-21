package com.nakhmedov.finance.db.migration;

import org.greenrobot.greendao.database.Database;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 4/8/17
 * Time: 11:15 PM
 * To change this template use File | Settings | File Templates
 */

public interface Migration {
    Integer getVersion();

    void runMigration(Database db);
}
