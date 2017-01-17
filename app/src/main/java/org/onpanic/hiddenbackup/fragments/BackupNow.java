package org.onpanic.hiddenbackup.fragments;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import org.onpanic.hiddenbackup.R;
import org.onpanic.hiddenbackup.services.BackupService;

public class BackupNow extends Fragment {
    private ImageView image;
    private Context mContext;
    private Animation animation;

    public BackupNow() {
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
                animation = AnimationUtils.loadAnimation(mContext, R.anim.clockwise);
                image.startAnimation(animation);
                Intent intent = new Intent(mContext, BackupService.class);
                mContext.startService(intent);
            }
        });

        TextView text = (TextView) v.findViewById(R.id.big_action_button_summary);
        text.setText(mContext.getString(R.string.run_backup));
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
