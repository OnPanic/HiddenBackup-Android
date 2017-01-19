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
import org.onpanic.hiddenbackup.notifications.TriggerNotification;

import java.util.Calendar;

public class SchedulerService extends Service {
    private LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver stopReceiver;
    private BroadcastReceiver backupFinish;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private SharedPreferences preferences;

    public SchedulerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        String action = intent.getAction();

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

            backupFinish = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    localBroadcastManager.unregisterReceiver(backupFinish);
                    if (preferences.getBoolean(getString(R.string.pref_runned_notification), false)) {
                        TriggerNotification notification = new TriggerNotification(getApplicationContext());
                        notification.show();
                    }

                }
            };

            localBroadcastManager.registerReceiver(
                    backupFinish, new IntentFilter(HiddenBackupConstants.BACKUP_FINISH));

            Intent backup = new Intent(this, BackupService.class);
            backup.setAction(HiddenBackupConstants.FULL_BACKUP);
            startService(backup);
        }

        return Service.START_STICKY;
    }
}
