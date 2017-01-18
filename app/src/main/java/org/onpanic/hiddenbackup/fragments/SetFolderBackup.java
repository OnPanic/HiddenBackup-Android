package org.onpanic.hiddenbackup.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.onpanic.hiddenbackup.R;


public class SetFolderBackup extends Fragment {
    private String mPath;

    public SetFolderBackup() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_set_folder_backup, container, false);
    }

    public void setUp(String path) {
        mPath = path;
    }
}
