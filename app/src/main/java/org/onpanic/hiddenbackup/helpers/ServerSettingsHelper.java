package org.onpanic.hiddenbackup.helpers;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.onpanic.hiddenbackup.R;

public class ServerSettingsHelper {
    public static void save(Context context, JSONObject data) throws JSONException {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = preferences.edit();

        edit.putString(context.getString(R.string.pref_server_onion), data.getString("host"));
        edit.putString(context.getString(R.string.pref_server_port), data.getString("port"));
        edit.putString(context.getString(R.string.pref_server_auth), data.getString("cookie"));

        edit.apply();

    }

    public static void authToClipboard(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("cookie", preferences.getString(context.getString(R.string.pref_server_auth), null));
        clipboard.setPrimaryClip(clip);

        Toast.makeText(context, R.string.cookie_to_clipboard, Toast.LENGTH_LONG).show();
    }

    public static void forgot(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = preferences.edit();

        edit.putString(context.getString(R.string.pref_server_onion), null);
        edit.putString(context.getString(R.string.pref_server_port), null);
        edit.putString(context.getString(R.string.pref_server_auth), null);

        edit.apply();
    }
}
