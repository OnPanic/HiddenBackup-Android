package org.onpanic.hiddenbackup.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.onpanic.hiddenbackup.R;


public class FullBackup extends Fragment {

    public FullBackup() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.full_backup_layout, container, false);
    }
}
