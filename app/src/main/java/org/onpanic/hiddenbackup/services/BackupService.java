package org.onpanic.hiddenbackup.services;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import org.onpanic.hiddenbackup.R;
import org.onpanic.hiddenbackup.constants.HiddenBackupConstants;
import org.onpanic.hiddenbackup.providers.DirsProvider;

import java.io.File;
import java.io.IOException;

import info.guardianproject.netcipher.client.StrongBuilder;
import info.guardianproject.netcipher.client.StrongOkHttpClientBuilder;
import info.guardianproject.netcipher.proxy.OrbotHelper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BackupService extends IntentService implements StrongBuilder.Callback<OkHttpClient> {
    private Intent mIntent;
    private OkHttpClient httpClient;
    private String mUrl;

    public BackupService() {
        super(BackupService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null && OrbotHelper.requestStartTor(this)) {

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            String onion = preferences.getString(getString(R.string.pref_server_onion), null);
            String port = preferences.getString(getString(R.string.pref_server_port), null);

            if (port != null && onion != null) {
                mUrl = "http://" + onion + ":" + port;
                mIntent = intent;

                try {
                    StrongOkHttpClientBuilder
                            .forMaxSecurity(this)
                            .build(this);
                } catch (Exception e) {
                    // TODO
                }
            }
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
        RequestBody body = RequestBody.create(
                MediaType.parse("multipart/form-data;"), file);

        Request request = new Request.Builder()
                .url(mUrl)
                .post(body)
                .build();
        try {
            Response response = httpClient.newCall(request).execute();
            response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(OkHttpClient okHttpClient) {
        httpClient = okHttpClient;

        final String action = mIntent.getAction();

        if (action.equals(HiddenBackupConstants.FULL_BACKUP)) {
            fullBackup();
        } else if (action.equals(HiddenBackupConstants.FILE_BACKUP)) {
            String fileName = mIntent.getStringExtra(DirsProvider.Dir.PATH);
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

    @Override
    public void onConnectionException(Exception e) {
        // TODO
    }

    @Override
    public void onTimeout() {
        // TODO
    }

    @Override
    public void onInvalid() {
        // TODO
    }
}
