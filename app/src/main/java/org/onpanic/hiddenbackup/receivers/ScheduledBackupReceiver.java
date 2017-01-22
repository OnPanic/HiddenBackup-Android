package org.onpanic.hiddenbackup.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import org.onpanic.hiddenbackup.R;
import org.onpanic.hiddenbackup.constants.HiddenBackupConstants;
import org.onpanic.hiddenbackup.notifications.TriggerNotification;
import org.onpanic.hiddenbackup.services.BackupService;


public class ScheduledBackupReceiver extends BroadcastReceiver {
    private BroadcastReceiver backupFinish;
    private SharedPreferences preferences;
    private LocalBroadcastManager localBroadcastManager;

    public ScheduledBackupReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        localBroadcastManager = LocalBroadcastManager.getInstance(context);

        backupFinish = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                localBroadcastManager.unregisterReceiver(backupFinish);
                if (preferences.getBoolean(context.getString(R.string.pref_runned_notification), false)) {
                    TriggerNotification notification = new TriggerNotification(context);
                    notification.show();
                }
            }
        };

        localBroadcastManager.registerReceiver(
                backupFinish, new IntentFilter(HiddenBackupConstants.BACKUP_FINISH));

        Intent backup = new Intent(context, BackupService.class);
        backup.setAction(HiddenBackupConstants.FULL_BACKUP);
        context.startService(backup);
    }
}
