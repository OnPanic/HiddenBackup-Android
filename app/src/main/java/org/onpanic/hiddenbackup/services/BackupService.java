package org.onpanic.hiddenbackup.services;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.onpanic.hiddenbackup.R;
import org.onpanic.hiddenbackup.constants.HiddenBackupConstants;
import org.onpanic.hiddenbackup.providers.DirsProvider;

import java.io.File;
import java.io.IOException;

import info.guardianproject.netcipher.client.StrongBuilder;
import info.guardianproject.netcipher.client.StrongOkHttpClientBuilder;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BackupService extends Service implements StrongBuilder.Callback<OkHttpClient> {
    private final String TAG = "BackupService";

    private Intent mIntent;
    private int mStartId;
    private OkHttpClient httpClient;
    private String mUrl;

    public BackupService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mIntent = intent;
        mStartId = startId;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String onion = preferences.getString(getString(R.string.pref_server_onion), null);
        String port = preferences.getString(getString(R.string.pref_server_port), null);

        if (port != null && onion != null) {
            mUrl = "http://" + onion + ":" + port;
            try {
                StrongOkHttpClientBuilder
                        .forMaxSecurity(this)
                        .build(this);
            } catch (Exception e) {
                e.printStackTrace();
                stopSelf(startId);
            }
        } else {
            stopSelf(startId);
        }

        return Service.START_STICKY;
    }

    private void fullBackup() {
        ContentResolver cr = getContentResolver();

        String[] mProjection = new String[]{
                DirsProvider.Dir._ID,
                DirsProvider.Dir.PATH,
                DirsProvider.Dir.SCHEDULED,
                DirsProvider.Dir.ENABLED
        };

        String where = DirsProvider.Dir.ENABLED + "=1 AND " + DirsProvider.Dir.SCHEDULED + "=1";
        Cursor files = cr.query(DirsProvider.CONTENT_URI, mProjection, where, null, null);

        if (files != null) {
            while (files.moveToNext()) {
                File current = new File(files.getString(files.getColumnIndex(DirsProvider.Dir.PATH)));

                if (current.exists()) {
                    for (File f : current.listFiles()) {
                        fileBackup(f);
                    }
                } else {
                    cr.delete(DirsProvider.CONTENT_URI,
                            DirsProvider.Dir._ID + "=" + files.getInt(files.getColumnIndex(DirsProvider.Dir._ID)),
                            null);
                }
            }

            files.close();
        }
    }

    private void fileBackup(File file) {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                fileBackup(f);
            }
        } else {
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file",
                            file.getName(),
                            RequestBody.create(MediaType.parse("multipart/form-data;"), file)
                    )
                    .build();

            Request request = new Request.Builder()
                    .url(mUrl)
                    .post(requestBody)
                    .build();
            try {
                Response response = httpClient.newCall(request).execute();
                // response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onConnected(OkHttpClient okHttpClient) {
        Log.d(TAG, "onConnected");

        httpClient = okHttpClient;

        final String action = mIntent.getAction();

        if (action.equals(HiddenBackupConstants.FULL_BACKUP)) {
            fullBackup();
        } else if (action.equals(HiddenBackupConstants.FILE_BACKUP)) {
            String fileName = mIntent.getStringExtra(DirsProvider.Dir.PATH);
            if (fileName != null) {
                File file = new File(fileName);
                if (file.exists() && !file.isDirectory()) {
                    fileBackup(file);
                }
            }
        }

        LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(this);
        broadcaster.sendBroadcast(new Intent(HiddenBackupConstants.BACKUP_FINISH));

        stopSelf(mStartId);
    }

    @Override
    public void onConnectionException(Exception e) {
        Log.d(TAG, "onConnectionException");
        stopSelf(mStartId);
    }

    @Override
    public void onTimeout() {
        Log.d(TAG, "onTimeout");
        stopSelf(mStartId);
    }

    @Override
    public void onInvalid() {
        Log.d(TAG, "onInvalid");
        stopSelf(mStartId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
