package com.nakhmedov.finance.db.migration;

import org.greenrobot.greendao.database.Database;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 4/8/17
 * Time: 11:14 PM
 * To change this template use File | Settings | File Templates
 */

public class MigrationV1 implements Migration {
    @Override
    public Integer getVersion() {
        return 1;
    }

    @Override
    public void runMigration(Database db) {
        /*
            Execute sql queries like
        - UserDao.createTable(db, false);
        - db.execSQL("ALTER TABLE " + UserDao.TABLENAME + " ADD COLUMN " +
                      UserDao.Properties.Age.columnName + " INTEGER");
        */

    }
}
