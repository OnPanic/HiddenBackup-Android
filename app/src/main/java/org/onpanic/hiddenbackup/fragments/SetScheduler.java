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
import org.onpanic.hiddenbackup.services.OrbotService;

public class SetScheduler extends PreferenceFragment {
    private Context mContext;
    private SwitchPreference enable;
    private LocalBroadcastManager broadcaster;

    public SetScheduler() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.set_scheduler);

        enable = (SwitchPreference) findPreference(mContext.getString(R.string.pref_scheduler_enabled));
        enable.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                if ((boolean) o) {
                    Intent intent = new Intent(mContext, OrbotService.class);
                    intent.setAction(HiddenBackupConstants.ACTION_START_SCHEDULER);
                    mContext.startService(intent);
                } else {
                    broadcaster.sendBroadcast(new Intent(HiddenBackupConstants.ACTION_STOP_SCHEDULER));
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
