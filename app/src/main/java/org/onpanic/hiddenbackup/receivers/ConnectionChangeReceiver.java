package org.onpanic.hiddenbackup.receivers;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

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
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (preferences.getBoolean(context.getString(R.string.pref_scheduler_delayed), false)) {
                SharedPreferences.Editor edit = preferences.edit();
                edit.putBoolean(context.getString(R.string.pref_scheduler_delayed), false);
                edit.apply();

                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, RunBackupReceiver.class), 0);

                try {
                    pendingIntent.send();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
