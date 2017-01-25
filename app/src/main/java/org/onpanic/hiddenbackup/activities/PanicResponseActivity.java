package org.onpanic.hiddenbackup.activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

import org.onpanic.hiddenbackup.R;
import org.onpanic.hiddenbackup.constants.HiddenBackupConstants;
import org.onpanic.hiddenbackup.helpers.ServerSettingsHelper;
import org.onpanic.hiddenbackup.receivers.RunBackupReceiver;

import info.guardianproject.panic.PanicResponder;

public class PanicResponseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (PanicResponder.receivedTriggerFromConnectedApp(this)) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

            if (preferences.getBoolean(getString(R.string.pref_run_now), false)) {
                Intent run = new Intent(this, RunBackupReceiver.class);
                run.setAction(HiddenBackupConstants.PANIC_BACKUP);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, run, 0);

                try {
                    pendingIntent.send();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
            } else if (preferences.getBoolean(getString(R.string.pref_forgot_server), false)) {
                ServerSettingsHelper.forgot(this);
            }
        }

        if (Build.VERSION.SDK_INT >= 21) {
            finishAndRemoveTask();
        } else {
            finish();
        }
    }
}
