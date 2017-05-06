package com.nakhmedov.finance.ui.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
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
import com.nakhmedov.finance.db.entity.Category;
import com.nakhmedov.finance.db.entity.DaoSession;
import com.nakhmedov.finance.db.entity.Term;
import com.nakhmedov.finance.db.entity.TermDao;
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
 * Date: 4/29/17
 * Time: 10:23 AM
 * To change this template use File | Settings | File Templates
 */

public class TermUpdateService extends Service {

    private static final String TAG = TermUpdateService.class.getCanonicalName();
    public static final String EXTRA_CATEGORY_ID = "category_id";
    private DaoSession daoSession;
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
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");

        if (intent != null) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    Gson gson = new GsonBuilder()
                            .setLenient()
                            .create();
//                    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//                    interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//                    OkHttpClient client = new OkHttpClient.Builder()
//                            .addInterceptor(interceptor)
//                            .build();
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(ContextConstants.BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create(gson))
//                            .client(client)
                            .build();

                    FinanceHttpService service = retrofit.create(FinanceHttpService.class);
                    final long categoryId = intent.getLongExtra(EXTRA_CATEGORY_ID, 0L);
                    service.listTermsBy(categoryId).enqueue(new Callback<JsonArray>() {
                        @Override
                        public void onResponse(Call<JsonArray> call, final Response<JsonArray> response) {
                            Log.i(TAG, "response = " + response.toString());
                            if (response.isSuccessful()) {
                                executorService.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        doProcess(response, categoryId);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonArray> call, Throwable throwable) {
                            Intent intent = new Intent(ActionNames.UPDATE_TERM_LIST);
                            intent.putExtra(ExtrasNames.TERM_UPDATE_RESULT, false);
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                            stopSelf();
                        }
                    });

                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(TermUpdateService.this)
                            .setSmallIcon(R.drawable.ic_app_ntfy)
                            .setContentTitle(getResources().getText(R.string.app_name))
                            .setContentText(getString(R.string.updating_terms))
                            .setWhen(System.currentTimeMillis());
                    Intent nIntent = getPackageManager().
                            getLaunchIntentForPackage(ContextConstants.PACKAGE_NAME);
                    PendingIntent pendingIntent = PendingIntent.getActivity(TermUpdateService.this, 0, nIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    notificationBuilder.setContentIntent(pendingIntent);
                    startForeground(ContextConstants.NTFY_TERM_ID, notificationBuilder.build());
                }
            });
        }

        return START_STICKY;
    }

    private void doProcess(Response<JsonArray> response, long categoryId) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);;
        JsonArray result = response.body();
        int count = result.size();
        for (int i = 0; i < count; i++) {
            Gson gsonObj = new Gson();
            Term term = gsonObj.fromJson(result.get(i), Term.class);
            Term localTerm = daoSession
                    .getTermDao()
                    .queryBuilder()
                    .where(TermDao.Properties.Id.eq(term.getId()))
                    .unique();
            if (localTerm == null) {
                daoSession
                        .getTermDao()
                        .insert(term);
            } else {
                localTerm.setName(term.getName());
                localTerm.setDescription(null);
                daoSession
                        .getTermDao()
                        .update(localTerm);
            }
            mBuilder
                    .setContentTitle(getResources().getText(R.string.app_name))
                    .setContentText(getString(R.string.updating_terms))
                    .setSmallIcon(R.drawable.ic_app_ntfy)
                    .setProgress(100, i*100/count, false);
            // Displays the progress bar for the first time.
            notificationManager.notify(ContextConstants.NTFY_TERM_ID, mBuilder.build());
        }
        Log.i(TAG, "doProcess term updated");
        Category category = daoSession
                .getCategoryDao()
                .load(categoryId);
        category.setLastTermsUpdateTime(new Date().getTime());
        daoSession.getCategoryDao().update(category);

        Intent intent = new Intent(ActionNames.UPDATE_TERM_LIST);
        intent.putExtra(ExtrasNames.TERM_UPDATE_RESULT, true);
        intent.putExtra(EXTRA_CATEGORY_ID, categoryId);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        stopForeground(true);
        if (executorService != null) {
            executorService.shutdown();
            executorService = null;
        }
    }
}
