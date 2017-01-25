package org.onpanic.hiddenbackup.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import org.onpanic.hiddenbackup.R;

public class ConnectionChangeReceiver extends BroadcastReceiver {
    public ConnectionChangeReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();

        boolean wifiOn = activeNetInfo != null
                && activeNetInfo.isConnected()
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI;

        if (wifiOn) {
            Log.d("HIDDEN_BACKUP", "WIFI_ON");
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (preferences.getBoolean(context.getString(R.string.pref_scheduler_delayed), false)) {
                SharedPreferences.Editor edit = preferences.edit();
                edit.putBoolean(context.getString(R.string.pref_scheduler_delayed), false);
                edit.apply();
                context.sendBroadcast(new Intent(context, ScheduledBackupReceiver.class));
            }
        }
    }
}
