package org.onpanic.hiddenbackup.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.onpanic.hiddenbackup.R;
import org.onpanic.hiddenbackup.constants.HiddenBackupConstants;
import org.onpanic.hiddenbackup.services.FileObserverService;
import org.onpanic.hiddenbackup.services.SchedulerService;


public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            if (prefs.getBoolean(context.getString(R.string.pref_scheduler_enabled), false)) {
                Intent scheduler = new Intent(context, SchedulerService.class);
                scheduler.setAction(HiddenBackupConstants.ACTION_START_SCHEDULER);
                context.startService(scheduler);
            }

            if (prefs.getBoolean(context.getString(R.string.pref_instant_backup_enabled), false)) {
                Intent instant = new Intent(context, FileObserverService.class);
                instant.setAction(HiddenBackupConstants.ACTION_START_INSTANT);
                context.startService(instant);
            }
        }
    }
}
