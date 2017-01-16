package org.onpanic.hiddenbackup.fragments;


import android.app.Fragment;
import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.onpanic.hiddenbackup.R;

import info.guardianproject.netcipher.proxy.OrbotHelper;

public class AppSetup extends Fragment {
    private Context mContext;
    private int snackMessage;
    private int textMessage;
    private int snackAction;
    private View.OnClickListener clickListener;

    public AppSetup() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Snackbar.make(getActivity().findViewById(android.R.id.content),
                snackMessage,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(snackAction, clickListener)
                .show();

        View v = inflater.inflate(R.layout.fragment_big_action_button, container, false);

        ImageView image = (ImageView) v.findViewById(R.id.big_action_button);
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        image.setColorFilter(filter);

        TextView text = (TextView) v.findViewById(R.id.big_action_button_summary);
        text.setText(textMessage);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public void orbotSetup() {
        snackMessage = R.string.install_orbot;
        snackAction = R.string.install;
        textMessage = R.string.orbot_is_not_installed;
        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(OrbotHelper.getOrbotInstallIntent(mContext));
            }
        };
    }
}
