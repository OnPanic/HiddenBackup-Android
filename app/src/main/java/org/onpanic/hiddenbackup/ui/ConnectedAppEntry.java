package org.onpanic.hiddenbackup.ui;


import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;

public class ConnectedAppEntry {
    public final String packageName;
    public final String simpleName;

    public ConnectedAppEntry(PackageManager pm, ActivityInfo activityInfo) {
        this.packageName = activityInfo.packageName;
        this.simpleName = String.valueOf(activityInfo.loadLabel(pm));

    }

    public ConnectedAppEntry(Context context, String fakePackageName, int simpleNameId) {
        this.packageName = fakePackageName;
        this.simpleName = context.getString(simpleNameId);

    }

    @Override
    public String toString() {
        return simpleName;
    }
}
