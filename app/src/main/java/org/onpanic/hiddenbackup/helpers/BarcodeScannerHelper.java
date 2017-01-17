package org.onpanic.hiddenbackup.helpers;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;

import java.util.List;

public class BarcodeScannerHelper {
    private static boolean isAppInstalled(Context context, String uri) {
        try {
            PackageManager p = context.getPackageManager();
            p.getPackageInfo("com.google.zxing.client.android", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static Intent getInstallIntent(Context context) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setData(Uri.parse("market://details?id=com.google.zxing.client.android"));
        PackageManager pm = context.getPackageManager();
        List resInfos = pm.queryIntentActivities(intent, 0);
        String foundPackageName = null;

        for (Object resInfo : resInfos) {
            ResolveInfo r = (ResolveInfo) resInfo;
            if (TextUtils.equals(r.activityInfo.packageName, "org.fdroid.fdroid") || TextUtils.equals(r.activityInfo.packageName, "com.android.vending")) {
                foundPackageName = r.activityInfo.packageName;
                break;
            }
        }

        if (foundPackageName == null) {
            intent.setData(Uri.parse("https://f-droid.org/repository/browse/?fdid=com.google.zxing.client.android"));
        } else {
            intent.setPackage(foundPackageName);
        }

        return intent;
    }

    public static Intent getScanIntent(Context context) {
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes
        return intent;
    }
}
