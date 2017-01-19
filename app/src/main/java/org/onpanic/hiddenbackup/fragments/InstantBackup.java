package org.onpanic.hiddenbackup.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import org.onpanic.hiddenbackup.R;


public class InstantBackup extends PreferenceFragment {

    public InstantBackup() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.instant_backup);
    }
}
