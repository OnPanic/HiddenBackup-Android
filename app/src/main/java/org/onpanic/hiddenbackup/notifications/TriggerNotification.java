package org.onpanic.hiddenbackup.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import org.onpanic.hiddenbackup.R;


public class TriggerNotification {
    private static final int TRIGGER_NOTIFICATION_ID = 0x1caca1;

    private Context mContext;
    private NotificationManager mNotificationManager;

    public TriggerNotification(Context context) {
        mContext = context;
        mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void show() {
        Notification panic = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(mContext.getString(R.string.runned_notification_title))
                .setContentText(mContext.getString(R.string.runned_notification_content))
                .build();

        mNotificationManager.notify(TRIGGER_NOTIFICATION_ID, panic);
    }
}
