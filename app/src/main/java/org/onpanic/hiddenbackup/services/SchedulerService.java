package org.onpanic.hiddenbackup.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import org.onpanic.hiddenbackup.constants.HiddenBackupConstants;

public class SchedulerService extends Service {
    private LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver stopReceiver;

    public SchedulerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {

        stopReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                localBroadcastManager.unregisterReceiver(stopReceiver);
                stopSelf(startId);
            }
        };

        localBroadcastManager.registerReceiver(
                stopReceiver, new IntentFilter(HiddenBackupConstants.ACTION_STOP_SCHEDULER));

        return Service.START_STICKY;
    }
}
