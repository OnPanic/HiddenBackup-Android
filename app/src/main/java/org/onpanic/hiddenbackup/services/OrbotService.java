package org.onpanic.hiddenbackup.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.onpanic.hiddenbackup.constants.HiddenBackupConstants;
import org.onpanic.hiddenbackup.providers.DirsProvider;

import info.guardianproject.netcipher.proxy.OrbotHelper;
import info.guardianproject.netcipher.proxy.StatusCallback;

public class OrbotService extends Service {
    private final String TAG = "OrbotService";

    private OrbotHelper orbotHelper;
    private int mStartId;
    private Intent mStartIntent;

    private StatusCallback statusCallback = new StatusCallback() {
        private boolean waitPolipo = false; // Ugly fix

        @Override
        public void onEnabled(Intent intent) {
            Log.d(TAG, "onEnabled");

            if (waitPolipo) { // Ugly fix
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                waitPolipo = false;
            }

            switch (mStartIntent.getAction()) {
                case HiddenBackupConstants.ACTION_START_INSTANT:
                    startService(new Intent(getApplicationContext(), FileObserverService.class));
                    break;
                case HiddenBackupConstants.ACTION_START_SCHEDULER:
                    startService(new Intent(getApplicationContext(), SchedulerService.class));
                    break;
                case HiddenBackupConstants.FULL_BACKUP:
                    Intent full = new Intent(getApplicationContext(), BackupService.class);
                    full.setAction(HiddenBackupConstants.FULL_BACKUP);
                    startService(full);
                    break;
                case HiddenBackupConstants.PING_BACKUP_SERVER:
                    startService(new Intent(getApplicationContext(), PingBackupService.class));
                    break;
                case HiddenBackupConstants.FILE_BACKUP:
                    Intent backup = new Intent(getApplicationContext(), BackupService.class);
                    backup.setAction(HiddenBackupConstants.FILE_BACKUP);
                    backup.putExtra(DirsProvider.Dir.PATH, intent.getStringExtra(DirsProvider.Dir.PATH));
                    startService(backup);
                    break;
            }

            orbotHelper.removeStatusCallback(statusCallback);
            OrbotService.this.stopSelf(mStartId);
        }

        @Override
        public void onStarting() {
            Log.d(TAG, "onStarting");
            waitPolipo = true; // Ugly fix
        }

        @Override
        public void onStopping() {
            Log.d(TAG, "onStopping");
        }

        @Override
        public void onDisabled() {
            Log.d(TAG, "onDisabled");
        }

        @Override
        public void onStatusTimeout() {
            Log.d(TAG, "onStatusTimeout");
            orbotHelper.removeStatusCallback(statusCallback);
            OrbotService.this.stopSelf(mStartId);
        }

        @Override
        public void onNotYetInstalled() {
            Log.d(TAG, "onNotYetInstalled");
            orbotHelper.removeStatusCallback(statusCallback);
            OrbotService.this.stopSelf(mStartId);
        }
    };

    public OrbotService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        orbotHelper = OrbotHelper.get(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        mStartId = startId;
        mStartIntent = intent;
        orbotHelper.addStatusCallback(statusCallback);
        orbotHelper.init();

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
