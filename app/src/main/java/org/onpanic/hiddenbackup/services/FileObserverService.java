package org.onpanic.hiddenbackup.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.FileObserver;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.onpanic.hiddenbackup.constants.HiddenBackupConstants;
import org.onpanic.hiddenbackup.providers.DirsProvider;

import java.io.File;
import java.util.ArrayList;

import info.guardianproject.netcipher.proxy.OrbotHelper;
import info.guardianproject.netcipher.proxy.StatusCallback;

public class FileObserverService extends Service {
    private final String TAG = "FileObserverService";

    private LocalBroadcastManager localBroadcastManager;
    private int mStartId;
    private ArrayList<FileObserver> fileObservers;
    private OrbotHelper orbotHelper;

    private BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            localBroadcastManager.unregisterReceiver(stopReceiver);
            for (FileObserver o : fileObservers) {
                o.stopWatching();
            }
            stopSelf(mStartId);
        }
    };

    private StatusCallback statusCallback = new StatusCallback() {
        @Override
        public void onEnabled(Intent intent) {

            Log.d(TAG, "onEnabled");

            localBroadcastManager.registerReceiver(
                    stopReceiver, new IntentFilter(HiddenBackupConstants.ACTION_STOP_INSTANT));

            ContentResolver cr = getContentResolver();

            String[] mProjection = new String[]{
                    DirsProvider.Dir._ID,
                    DirsProvider.Dir.PATH,
                    DirsProvider.Dir.OBSERVER,
                    DirsProvider.Dir.ENABLED
            };

            String where = DirsProvider.Dir.ENABLED + "=1 AND " + DirsProvider.Dir.OBSERVER + "=1";
            Cursor files = cr.query(DirsProvider.CONTENT_URI, mProjection, where, null, null);

            if (files != null) {
                while (files.moveToNext()) {
                    File current = new File(files.getString(files.getColumnIndex(DirsProvider.Dir.PATH)));

                    if (current.exists()) {
                        FileObserver observer = new FileObserver(current.getAbsolutePath()) {
                            @Override
                            public void onEvent(int event, String file) {
                                if (event == FileObserver.CREATE) {
                                    Intent backup = new Intent(getApplicationContext(), BackupService.class);
                                    backup.setAction(HiddenBackupConstants.FILE_BACKUP);
                                    backup.putExtra(DirsProvider.Dir.PATH, file);
                                    startService(backup);
                                }
                            }
                        };

                        observer.startWatching();
                        fileObservers.add(observer);

                    } else {
                        cr.delete(DirsProvider.CONTENT_URI,
                                DirsProvider.Dir._ID + "=" + files.getInt(files.getColumnIndex(DirsProvider.Dir._ID)),
                                null);
                    }
                }

                files.close();
            }
        }

        @Override
        public void onStarting() {
            Log.d(TAG, "onStarting");
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
        }

        @Override
        public void onNotYetInstalled() {
            Log.d(TAG, "onNotYetInstalled");
        }
    };

    public FileObserverService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        fileObservers = new ArrayList<>();
        localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        orbotHelper = OrbotHelper.get(getApplicationContext());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {
        mStartId = startId;
        orbotHelper.addStatusCallback(statusCallback);
        orbotHelper.init();
        return Service.START_STICKY;
    }
}
