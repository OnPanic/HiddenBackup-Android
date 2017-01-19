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
import android.widget.TextView;

import org.onpanic.hiddenbackup.R;
import org.onpanic.hiddenbackup.constants.HiddenBackupConstants;
import org.onpanic.hiddenbackup.services.BackupService;

public class BackupNow extends Fragment {
    private Context mContext;
    private BroadcastReceiver receiver;
    private LocalBroadcastManager localBroadcastManager;

    public BackupNow() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_big_action_button, container, false);

        final TextView text = (TextView) v.findViewById(R.id.big_action_button_summary);
        text.setText(mContext.getString(R.string.run_backup));

        ImageView image = (ImageView) v.findViewById(R.id.big_action_button);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.clockwise);
                view.startAnimation(animation);
                Intent intent = new Intent(mContext, BackupService.class);
                intent.setAction(HiddenBackupConstants.FULL_BACKUP);
                mContext.startService(intent);
                text.setText(mContext.getString(R.string.backup_is_running));
            }
        });

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                text.setText(mContext.getString(R.string.run_backup));
            }
        };

        localBroadcastManager.registerReceiver(
                receiver, new IntentFilter(HiddenBackupConstants.BACKUP_FINISH));

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        localBroadcastManager.unregisterReceiver(receiver);
    }
}
