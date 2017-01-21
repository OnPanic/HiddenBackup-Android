package org.onpanic.hiddenbackup.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import org.onpanic.hiddenbackup.R;
import org.onpanic.hiddenbackup.constants.HiddenBackupConstants;
import org.onpanic.hiddenbackup.helpers.ServerSettingsHelper;
import org.onpanic.hiddenbackup.services.OrbotService;


public class HiddenBackupSettings extends PreferenceFragment {
    private Context mContext;
    private AppSetup.OnScanQRCallback onScanQRCallback;
    private LocalBroadcastManager broadcaster;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int msg = R.string.server_offline;

            broadcaster.unregisterReceiver(mReceiver);

            String status = intent.getStringExtra(HiddenBackupConstants.BACKUP_SERVER_STATUS);
            if (status.equals(HiddenBackupConstants.BACKUP_SERVER_ONLINE)) {
                msg = R.string.server_online;
            }

            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
        }
    };

    public HiddenBackupSettings() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.hiddenbackup_settings);

        Preference scanQR = (Preference) findPreference(getString(R.string.pref_scan_server_qr));
        scanQR.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                onScanQRCallback.onScanQR();
                return true;
            }
        });

        Preference clipboard = (Preference) findPreference(getString(R.string.pref_auth_to_clipboard));
        clipboard.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ServerSettingsHelper.authToClipboard(mContext);
                return true;
            }
        });

        Preference ping = (Preference) findPreference(getString(R.string.pref_check_backup_server_status));
        ping.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                broadcaster.registerReceiver(
                        mReceiver, new IntentFilter(HiddenBackupConstants.PING_SERVER_RESPONSE));
                Intent intent = new Intent(mContext, OrbotService.class);
                intent.setAction(HiddenBackupConstants.PING_BACKUP_SERVER);
                mContext.startService(intent);
                return true;
            }
        });

        Preference triggerApp = (Preference) findPreference(getString(R.string.pref_trigger_app));
        triggerApp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                getFragmentManager()
                        .beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.fragment_container, new TriggerApps())
                        .commit();

                return false;
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;

        if (mContext instanceof AppSetup.OnScanQRCallback) {
            onScanQRCallback = (AppSetup.OnScanQRCallback) mContext;
        } else {
            throw new RuntimeException(mContext.toString()
                    + " must implement OnScanQRCallback");
        }

        broadcaster = LocalBroadcastManager.getInstance(mContext);
    }
}
