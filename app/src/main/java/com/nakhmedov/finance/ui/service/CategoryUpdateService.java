package com.nakhmedov.finance.ui.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.nakhmedov.finance.R;
import com.nakhmedov.finance.constants.ActionNames;
import com.nakhmedov.finance.constants.ContextConstants;
import com.nakhmedov.finance.constants.ExtrasNames;
import com.nakhmedov.finance.constants.PrefLab;
import com.nakhmedov.finance.db.entity.Category;
import com.nakhmedov.finance.db.entity.CategoryDao;
import com.nakhmedov.finance.db.entity.DaoSession;
import com.nakhmedov.finance.net.FinanceHttpService;
import com.nakhmedov.finance.ui.FinanceApp;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created with Android Studio
 * User: navruz
 * Date: 4/28/17
 * Time: 11:45 AM
 * To change this template use File | Settings | File Templates
 */

public class CategoryUpdateService extends Service {
    private String TAG = CategoryUpdateService.class.getCanonicalName();
    private DaoSession daoSession;
    private SharedPreferences prefs;
    private NotificationManager notificationManager;
    private ExecutorService executorService;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        if (executorService == null) {
            executorService = Executors.newCachedThreadPool();
        }

        daoSession = ((FinanceApp) getApplicationContext()).getDaoSession();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");

        executorService.execute(new Runnable() {
            @Override
            public void run() {
//                HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//
//                OkHttpClient client = new OkHttpClient.Builder()
//                        .addInterceptor(interceptor)
//                        .build();

                final Gson gson = new GsonBuilder()
                        .setLenient()
                        .create();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(ContextConstants.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create(gson))
//                        .client(client)
                        .build();
                FinanceHttpService httpService = retrofit.create(FinanceHttpService.class);
                httpService.listCategory().enqueue(new Callback<JsonArray>() {
                    @Override
                    public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                        Log.i(TAG, "response = " + response.toString());
                        if (response.isSuccessful()) {
                            doProcess(response);
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonArray> call, Throwable throwable) {
                        Log.e(TAG, "failed = " + throwable.getMessage());
                        Intent intent = new Intent(ActionNames.UPDATE_CATEGORY_LIST);
                        intent.putExtra(ExtrasNames.CATEGORY_UPDATE_RESULT, false);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                        throwable.printStackTrace();
                        stopSelf();
                    }
                });

                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(CategoryUpdateService.this)
                        .setSmallIcon(R.drawable.ic_app_ntfy)
                        .setContentTitle(getResources().getText(R.string.app_name))
                        .setContentText(getString(R.string.updating_categories))
                        .setWhen(System.currentTimeMillis());
                Intent nIntent = getPackageManager().
                        getLaunchIntentForPackage(ContextConstants.PACKAGE_NAME);
                PendingIntent pendingIntent = PendingIntent.getActivity(CategoryUpdateService.this, 0, nIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                notificationBuilder.setContentIntent(pendingIntent);
                startForeground(ContextConstants.NTFY_CATEGORY_ID, notificationBuilder.build());
            }
        });

        return START_STICKY;
    }

    private void doProcess(Response<JsonArray> response) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);;
        JsonArray result = response.body();
        int count = result.size();
        for (int i = 0; i < count; i++) {
            Gson gsonObj = new Gson();
            Category category = gsonObj.fromJson(result.get(i), Category.class);
            Category localCategory = daoSession
                    .getCategoryDao()
                    .queryBuilder()
                    .where(CategoryDao.Properties.Id.eq(category.getId()))
                    .unique();

            if (localCategory == null) {
                daoSession
                        .getCategoryDao()
                        .insert(category);
            } else {
                localCategory.setName(category.getName());
                daoSession
                        .getCategoryDao()
                        .update(localCategory);
            }
            mBuilder
                    .setContentTitle(getResources().getText(R.string.app_name))
                    .setContentText(getString(R.string.updating_categories))
                    .setSmallIcon(R.drawable.ic_app_ntfy)
                    .setProgress(100, i*100/count, false);
            // Displays the progress bar for the first time.
            notificationManager.notify(ContextConstants.NTFY_CATEGORY_ID, mBuilder.build());
        }
        Log.i(TAG, "doProcess category updated");
        prefs
            .edit()
            .putLong(PrefLab.CATEGORY_LAST_UPDATE, new Date().getTime())
            .apply();
        Intent intent = new Intent(ActionNames.UPDATE_CATEGORY_LIST);
        intent.putExtra(ExtrasNames.CATEGORY_UPDATE_RESULT, true);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
        stopForeground(true);
        if (executorService != null) {
            executorService.shutdown();
            executorService = null;
        }
    }
}
