package org.onpanic.hiddenbackup.fragments;

import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import org.onpanic.hiddenbackup.R;
import org.onpanic.hiddenbackup.helpers.ServerSettingsHelper;


public class HiddenBackupSettings extends PreferenceFragment {
    private Context mContext;
    private AppSetup.OnScanQRCallback onScanQRCallback;

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
    }
}
