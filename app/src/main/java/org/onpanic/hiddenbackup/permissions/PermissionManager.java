package org.onpanic.hiddenbackup.permissions;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import org.onpanic.hiddenbackup.R;


public class PermissionManager {

    public static boolean isLollipopOrHigher() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }

    @SuppressLint("NewApi")
    public static boolean hasExternalWritePermission(Context context) {
        return (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    public static void requestExternalWritePermissions(FragmentActivity activity, int action) {
        final int mAction = action;
        final FragmentActivity mActivity = activity;

        if (ActivityCompat.shouldShowRequestPermissionRationale
                (mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(mActivity.findViewById(android.R.id.content),
                    R.string.please_grant_permissions_for_external_storage,
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.grant,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ActivityCompat.requestPermissions(mActivity,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    mAction);
                        }
                    }).show();
        } else {
            ActivityCompat.requestPermissions(mActivity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    mAction);
        }
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}

