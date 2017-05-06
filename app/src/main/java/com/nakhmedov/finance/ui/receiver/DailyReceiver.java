package com.nakhmedov.finance.ui.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.nakhmedov.finance.R;
import com.nakhmedov.finance.constants.ContextConstants;
import com.nakhmedov.finance.db.entity.Category;
import com.nakhmedov.finance.db.entity.CategoryDao;
import com.nakhmedov.finance.db.entity.DaoSession;
import com.nakhmedov.finance.db.entity.Term;
import com.nakhmedov.finance.db.entity.TermDao;
import com.nakhmedov.finance.ui.FinanceApp;
import com.nakhmedov.finance.ui.activity.SelectedTermActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 4/7/17
 * Time: 5:08 PM
 * To change this template use File | Settings | File Templates
 */

public class DailyReceiver extends BroadcastReceiver {
    private static final String TAG = DailyReceiver.class.getCanonicalName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive");

        DaoSession daoSession = ((FinanceApp) context.getApplicationContext()).getDaoSession();
        List<Category> starredCategories = daoSession
                .getCategoryDao()
                .queryBuilder()
                .where(CategoryDao.Properties.Starred.eq(true))
                .list();
        List<Term> termList = new ArrayList<>(1000);
        if (starredCategories.size() == 0) {
            termList = daoSession
                    .getTermDao()
                    .loadAll();
        } else {
            for (Category starredCategory: starredCategories) {
                termList.addAll(
                        daoSession
                            .getTermDao()
                            .queryBuilder()
                            .where(TermDao.Properties.CategoryId.eq(starredCategory.getId()))
                            .list()
                );
            }
        }

        if (termList.size() > 0) {
            Random random = new Random();
            int randIndex = random.nextInt(termList.size() - 1);

            Term dailyTerm = termList.get(randIndex);
            Log.i(TAG, "dailyTerm = " + dailyTerm.getName());

            Intent termIntent = new Intent(context, SelectedTermActivity.class);
            termIntent.putExtra(SelectedTermActivity.EXTRA_TERM_ID, dailyTerm.getId());
            termIntent.putExtra(SelectedTermActivity.EXTRA_CATEGORY_ID, dailyTerm.getCategoryId());
            termIntent.putExtra(SelectedTermActivity.EXTRA_DAILY_ALARM, true);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 102, termIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_app_ntfy)
                    .setContentTitle(context.getString(R.string.day_term))
                    .setContentText(dailyTerm.getName())
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(dailyTerm.getDescription()))
                    .setWhen(System.currentTimeMillis())
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(ContextConstants.NTFY_DAILY_ID, notificationBuilder.build());
        }
    }
}
