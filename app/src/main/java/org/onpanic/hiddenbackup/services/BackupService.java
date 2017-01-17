package org.onpanic.hiddenbackup.services;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import org.onpanic.hiddenbackup.constants.HiddenBackupConstants;
import org.onpanic.hiddenbackup.providers.DirsProvider;

import java.io.File;

import info.guardianproject.netcipher.proxy.OrbotHelper;

public class BackupService extends Service {
    private String[] mProjection = new String[]{
            DirsProvider.Dir._ID,
            DirsProvider.Dir.PATH,
            DirsProvider.Dir.ENABLED
    };

    private LocalBroadcastManager broadcaster;

    public BackupService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        ContentResolver cr = getContentResolver();

        if (OrbotHelper.requestStartTor(this)) {
            Cursor files = cr.query(DirsProvider.CONTENT_URI, mProjection, DirsProvider.Dir.ENABLED + "=1", null, null);

            if (files != null) {
                while (files.moveToNext()) {
                    File current = new File(files.getString(files.getColumnIndex(DirsProvider.Dir.PATH)));

                    if (current.exists()) {
                        // TODO
                    } else {
                        cr.delete(DirsProvider.CONTENT_URI,
                                DirsProvider.Dir._ID + "=" + files.getInt(files.getColumnIndex(DirsProvider.Dir._ID)),
                                null);
                    }
                }

                files.close();
            }
        }

        broadcaster.sendBroadcast(new Intent(HiddenBackupConstants.BACKUP_FINISH));

        stopSelf(startId);

        return Service.START_STICKY;
    }
}
