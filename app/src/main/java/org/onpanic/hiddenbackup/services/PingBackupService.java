package org.onpanic.hiddenbackup.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.onpanic.hiddenbackup.R;
import org.onpanic.hiddenbackup.constants.HiddenBackupConstants;

import java.io.IOException;

import info.guardianproject.netcipher.client.StrongBuilder;
import info.guardianproject.netcipher.client.StrongOkHttpClientBuilder;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PingBackupService extends Service implements StrongBuilder.Callback<OkHttpClient> {
    private final String TAG = "PingBackupService";

    private int mStartId;
    private HttpUrl mUrl;
    private LocalBroadcastManager broadcaster;
    private Intent mResponse;

    public PingBackupService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mStartId = startId;
        broadcaster = LocalBroadcastManager.getInstance(this);

        mResponse = new Intent(HiddenBackupConstants.PING_SERVER_RESPONSE);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String onion = preferences.getString(getString(R.string.pref_server_onion), null);
        String port = preferences.getString(getString(R.string.pref_server_port), null);

        if (port != null && onion != null) {
            mUrl = new HttpUrl.Builder()
                    .scheme("http")
                    .host(onion)
                    .port(Integer.parseInt(port))
                    .build();

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

    @Override
    public void onConnected(final OkHttpClient okHttpClient) {
        Log.d(TAG, "onConnected");

        new Thread() {
            @Override
            public void run() {
                Request request = new Request.Builder()
                        .url(mUrl)
                        .get()
                        .build();
                try {
                    Response response = okHttpClient.newCall(request).execute();
                    JSONObject response_json = new JSONObject(response.body().string());
                    if (response_json.getBoolean("running")) {
                        mResponse.putExtra(HiddenBackupConstants.BACKUP_SERVER_STATUS, HiddenBackupConstants.BACKUP_SERVER_ONLINE);
                    } else {
                        mResponse.putExtra(HiddenBackupConstants.BACKUP_SERVER_STATUS, HiddenBackupConstants.BACKUP_SERVER_OFFLINE);
                    }
                } catch (IOException | JSONException e) {
                    mResponse.putExtra(HiddenBackupConstants.BACKUP_SERVER_STATUS, HiddenBackupConstants.BACKUP_SERVER_OFFLINE);
                    e.printStackTrace();
                }

                broadcaster.sendBroadcast(mResponse);
                PingBackupService.this.stopSelf(mStartId);
            }
        }.start();
    }

    @Override
    public void onConnectionException(Exception e) {
        Log.d(TAG, "onConnectionException");
        e.printStackTrace();
        mResponse.putExtra(HiddenBackupConstants.BACKUP_SERVER_STATUS, HiddenBackupConstants.BACKUP_SERVER_OFFLINE);
        broadcaster.sendBroadcast(mResponse);
        stopSelf(mStartId);
    }

    @Override
    public void onTimeout() {
        Log.d(TAG, "onTimeout");
        mResponse.putExtra(HiddenBackupConstants.BACKUP_SERVER_STATUS, HiddenBackupConstants.BACKUP_SERVER_OFFLINE);
        broadcaster.sendBroadcast(mResponse);
        stopSelf(mStartId);
    }

    @Override
    public void onInvalid() {
        Log.d(TAG, "onInvalid");
        mResponse.putExtra(HiddenBackupConstants.BACKUP_SERVER_STATUS, HiddenBackupConstants.BACKUP_SERVER_OFFLINE);
        broadcaster.sendBroadcast(mResponse);
        stopSelf(mStartId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
