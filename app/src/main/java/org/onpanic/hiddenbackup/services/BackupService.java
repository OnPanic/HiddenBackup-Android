package org.onpanic.hiddenbackup.services;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.content.LocalBroadcastManager;

import org.onpanic.hiddenbackup.constants.HiddenBackupConstants;
import org.onpanic.hiddenbackup.providers.DirsProvider;

import java.io.File;

import info.guardianproject.netcipher.proxy.OrbotHelper;

public class BackupService extends IntentService {

    public BackupService() {
        super(BackupService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null && OrbotHelper.requestStartTor(this)) {
            final String action = intent.getAction();

            if (action.equals(HiddenBackupConstants.FULL_BACKUP)) {
                fullBackup();
            } else if (action.equals(HiddenBackupConstants.FILE_BACKUP)) {
                String fileName = intent.getStringExtra(DirsProvider.Dir.PATH);
                if (fileName != null) {
                    File file = new File(fileName);
                    if (file.exists() && !file.isDirectory()) {
                        fileBackup(file);
                    }
                }
            }

            LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(this);
            broadcaster.sendBroadcast(new Intent(HiddenBackupConstants.BACKUP_FINISH));
        }
    }

    private void fullBackup() {
        ContentResolver cr = getContentResolver();

        String[] mProjection = new String[]{
                DirsProvider.Dir._ID,
                DirsProvider.Dir.PATH,
                DirsProvider.Dir.SCHEDULED,
                DirsProvider.Dir.ENABLED
        };

        String where = DirsProvider.Dir.ENABLED + "=1 AND " + DirsProvider.Dir.SCHEDULED + "=1";
        Cursor files = cr.query(DirsProvider.CONTENT_URI, mProjection, where, null, null);

        if (files != null) {
            while (files.moveToNext()) {
                File current = new File(files.getString(files.getColumnIndex(DirsProvider.Dir.PATH)));

                if (current.exists()) {
                    for (File f : current.listFiles()) {
                        fileBackup(f);
                    }
                } else {
                    cr.delete(DirsProvider.CONTENT_URI,
                            DirsProvider.Dir._ID + "=" + files.getInt(files.getColumnIndex(DirsProvider.Dir._ID)),
                            null);
                }
            }

            files.close();
        }
    }

    private void fileBackup(File file) {
        // TODO
    }
}
