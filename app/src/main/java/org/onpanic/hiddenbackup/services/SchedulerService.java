package org.onpanic.hiddenbackup.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import org.onpanic.hiddenbackup.R;
import org.onpanic.hiddenbackup.constants.HiddenBackupConstants;

import java.util.Calendar;

public class SchedulerService extends Service {
    private LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver stopReceiver;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    public SchedulerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        String action = intent.getAction();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (action.equals(HiddenBackupConstants.ACTION_START_SCHEDULER)) {

            Intent service = new Intent(this, SchedulerService.class);
            service.setAction(HiddenBackupConstants.FULL_BACKUP);
            alarmIntent = PendingIntent.getBroadcast(this, 0, service, 0);

            String[] time = preferences.getString(getString(R.string.pref_scheduler_time), "00:00").split(":");

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time[0]));
            calendar.set(Calendar.MINUTE, Integer.parseInt(time[1]));

            alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, alarmIntent);

            stopReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    localBroadcastManager.unregisterReceiver(stopReceiver);
                    alarmMgr.cancel(alarmIntent);
                    stopSelf(startId);
                }
            };

            localBroadcastManager.registerReceiver(
                    stopReceiver, new IntentFilter(HiddenBackupConstants.ACTION_STOP_SCHEDULER));

        } else if (action.equals(HiddenBackupConstants.SCHEDULED_BACKUP)) {
            Intent backup = new Intent(this, BackupService.class);
            backup.setAction(HiddenBackupConstants.FULL_BACKUP);
            startService(backup);
        }

        return Service.START_STICKY;
    }
}
