package org.onpanic.hiddenbackup.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v4.content.LocalBroadcastManager;

import org.onpanic.hiddenbackup.R;
import org.onpanic.hiddenbackup.constants.HiddenBackupConstants;
import org.onpanic.hiddenbackup.services.FileObserverService;


public class InstantBackup extends PreferenceFragment {
    private SwitchPreference enable;
    private Context mContext;
    private LocalBroadcastManager broadcaster;

    public InstantBackup() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.instant_backup);

        enable = (SwitchPreference) findPreference(mContext.getString(R.string.pref_instant_backup_enabled));
        enable.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                if ((boolean) o) {
                    Intent intent = new Intent(mContext, FileObserverService.class);
                    intent.setAction(HiddenBackupConstants.ACTION_START_INSTANT);
                    mContext.startService(intent);
                } else {
                    broadcaster.sendBroadcast(new Intent(HiddenBackupConstants.ACTION_STOP_INSTANT));
                }

                return true;
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        broadcaster = LocalBroadcastManager.getInstance(context);
    }
}
