package com.nakhmedov.finance.db;

import android.content.Context;

import com.nakhmedov.finance.db.migration.Migration;
import com.nakhmedov.finance.db.migration.MigrationV1;
import com.nakhmedov.finance.ui.entity.DaoMaster;

import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 4/8/17
 * Time: 10:20 PM
 * To change this template use File | Settings | File Templates
 */

public class DbUpgradeHelper extends DaoMaster.OpenHelper {
    public DbUpgradeHelper(Context context, String name) {
        super(context, name);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        List<Migration> migrations = getMigrations();
        for (Migration migration: migrations) {
            if (oldVersion < migration.getVersion()) {
                migration.runMigration(db);
            }
        }
    }

    private List<Migration> getMigrations() {
        List<Migration> migrations = new ArrayList<>();
        migrations.add(new MigrationV1());

        //Sorting Migrations by versions
        Comparator<Migration> comparator = new Comparator<Migration>() {
            @Override
            public int compare(Migration o1, Migration o2) {
                return o1.getVersion().compareTo(o2.getVersion());
            }
        };

        Collections.sort(migrations, comparator);

        return migrations;
    }


}
