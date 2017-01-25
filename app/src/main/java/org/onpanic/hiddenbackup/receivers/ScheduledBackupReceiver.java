package org.onpanic.hiddenbackup.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.onpanic.hiddenbackup.R;
import org.onpanic.hiddenbackup.constants.HiddenBackupConstants;
import org.onpanic.hiddenbackup.notifications.TriggerNotification;
import org.onpanic.hiddenbackup.services.OrbotService;


public class ScheduledBackupReceiver extends BroadcastReceiver {
    private BroadcastReceiver backupFinish;
    private SharedPreferences preferences;
    private LocalBroadcastManager localBroadcastManager;

    public ScheduledBackupReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        preferences = PreferenceManager.getDefaultSharedPreferences(context);

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();

        boolean delayed = (activeNetInfo == null)
                || !activeNetInfo.isConnected()
                || (preferences.getBoolean(context.getString(R.string.pref_wait_for_wifi), true) && activeNetInfo.getType() != ConnectivityManager.TYPE_WIFI);

        if (delayed) {
            Log.d("HIDDEN_BACKUP", "DELAYED");
            SharedPreferences.Editor edit = preferences.edit();
            edit.putBoolean(context.getString(R.string.pref_scheduler_delayed), true);
            edit.apply();
        } else {
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

            Intent backup = new Intent(context, OrbotService.class);
            backup.setAction(HiddenBackupConstants.FULL_BACKUP);
            context.startService(backup);
        }
    }
}
