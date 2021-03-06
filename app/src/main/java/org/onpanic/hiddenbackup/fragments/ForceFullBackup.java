package org.onpanic.hiddenbackup.fragments;


import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import org.onpanic.hiddenbackup.R;
import org.onpanic.hiddenbackup.constants.HiddenBackupConstants;
import org.onpanic.hiddenbackup.services.OrbotService;

public class ForceFullBackup extends Fragment {
    private Context mContext;
    private ImageView image;
    private LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver backupFinish;

    public ForceFullBackup() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_big_action_button, container, false);

        image = (ImageView) v.findViewById(R.id.big_action_button);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.clockwise);
                view.startAnimation(animation);
                localBroadcastManager = LocalBroadcastManager.getInstance(mContext);

                backupFinish = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        localBroadcastManager.unregisterReceiver(backupFinish);
                        image.clearAnimation();
                        Toast.makeText(mContext, R.string.runned_notification_content, Toast.LENGTH_LONG).show();
                    }
                };

                localBroadcastManager.registerReceiver(
                        backupFinish, new IntentFilter(HiddenBackupConstants.BACKUP_FINISH));

                Intent intent = new Intent(mContext, OrbotService.class);
                intent.setAction(HiddenBackupConstants.FULL_BACKUP);
                mContext.startService(intent);
            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
