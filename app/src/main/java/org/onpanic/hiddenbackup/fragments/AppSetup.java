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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.onpanic.hiddenbackup.R;
import org.onpanic.hiddenbackup.helpers.BarcodeScannerHelper;
import org.onpanic.hiddenbackup.helpers.CheckDependenciesHelper;

import info.guardianproject.netcipher.proxy.OrbotHelper;

public class AppSetup extends Fragment {
    private Context mContext;

    private View.OnClickListener clickListener;

    public AppSetup() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.dependencies_layout, container, false);

        ImageView image = (ImageView) v.findViewById(R.id.dependencies_image);
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        image.setColorFilter(filter);

        Button orbot = (Button) v.findViewById(R.id.install_orbot_dependency);
        orbot.setEnabled(!CheckDependenciesHelper.checkOrbot(mContext));
        orbot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(OrbotHelper.getOrbotInstallIntent(mContext));
            }
        });

        Button barcode = (Button) v.findViewById(R.id.install_barcode_dependency);
        barcode.setEnabled(!CheckDependenciesHelper.checkBarcode(mContext));
        barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(OrbotHelper.getOrbotInstallIntent(mContext));
            }
        });

        Button scan = (Button) v.findViewById(R.id.read_qr_dependency);
        scan.setEnabled(!CheckDependenciesHelper.checkServer(mContext));
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO
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
