package org.onpanic.hiddenbackup;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.onpanic.hiddenbackup.adapters.FMItemsAdapter;
import org.onpanic.hiddenbackup.constants.HiddenBackupConstants;
import org.onpanic.hiddenbackup.dialogs.DeleteDirDialog;
import org.onpanic.hiddenbackup.dialogs.SaveDirDialog;
import org.onpanic.hiddenbackup.fragments.AppSetup;
import org.onpanic.hiddenbackup.fragments.BackupNow;
import org.onpanic.hiddenbackup.fragments.DirsFragment;
import org.onpanic.hiddenbackup.fragments.HiddenBackupSettings;
import org.onpanic.hiddenbackup.helpers.BarcodeScannerHelper;
import org.onpanic.hiddenbackup.helpers.CheckDependenciesHelper;
import org.onpanic.hiddenbackup.permissions.PermissionManager;
import org.onpanic.hiddenbackup.providers.DirsProvider;

public class HiddenBackupActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        DirsFragment.OnDirClickListener,
        AppSetup.OnScanQRCallback,
        FMItemsAdapter.OnSetDirBackup {

    private DrawerLayout drawer;
    private FragmentManager mFragmentManager;
    private boolean resumeAfterQRScan = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFragmentManager = getFragmentManager();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Do not overlapping fragments.
        if (savedInstanceState != null) return;

        if (PermissionManager.isLollipopOrHigher() && !PermissionManager.hasExternalWritePermission(this)) {
            PermissionManager.requestExternalWritePermissions(this, HiddenBackupConstants.REQUEST_WRITE_STORAGE);
        }

        initFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_hidden_backup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            mFragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.fragment_container, new HiddenBackupSettings())
                    .commit();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        if (!CheckDependenciesHelper.checkAll(this)) {
            transaction.replace(R.id.fragment_container, new AppSetup());
        } else {
            switch (item.getItemId()) {
                case R.id.run_backup_now:
                    transaction.replace(R.id.fragment_container, new BackupNow());
                    break;
                case R.id.add_backup_dirs:
                    transaction.replace(R.id.fragment_container, new DirsFragment());
                    break;
                case R.id.scheduled:
                    // TODO
                    break;
            }
        }

        transaction.commit();

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (resumeAfterQRScan) {
            initFragment();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case HiddenBackupConstants.SCAN_RESULT:
                resumeAfterQRScan = true;
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        JSONObject qr_data = new JSONObject(data.getStringExtra("SCAN_RESULT"));
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                        SharedPreferences.Editor edit = preferences.edit();
                        edit.putString(getString(R.string.pref_server_onion), qr_data.getString("host"));
                        edit.putString(getString(R.string.pref_server_port), qr_data.getString("port"));
                        edit.apply();

                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("cookie", qr_data.getString("cookie"));
                        clipboard.setPrimaryClip(clip);

                        Toast.makeText(this, R.string.cookie_to_clipboard, Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, R.string.add_server_failure, Toast.LENGTH_LONG).show();
                    }
                }

                return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case HiddenBackupConstants.REQUEST_WRITE_STORAGE: {
                if (grantResults.length < 1 || grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    // Request rationale
                    PermissionManager.requestExternalWritePermissions(this, HiddenBackupConstants.REQUEST_WRITE_STORAGE);
                }

                break;
            }
        }
    }

    public void initFragment() {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        if (!CheckDependenciesHelper.checkAll(this)) {
            transaction.replace(R.id.fragment_container, new AppSetup());
        } else {
            transaction.replace(R.id.fragment_container, new BackupNow());
        }

        transaction.commit();
    }

    @Override
    public void onDirClick(int id) {
        DeleteDirDialog dialog = new DeleteDirDialog();
        Bundle arguments = new Bundle();
        arguments.putInt(DirsProvider.Dir._ID, id);
        dialog.setArguments(arguments);
        dialog.show(getSupportFragmentManager(), "DeleteDirDialog");
    }

    @Override
    public void onScanQR() {
        startActivityForResult(BarcodeScannerHelper.getScanIntent(), HiddenBackupConstants.SCAN_RESULT);
    }

    @Override
    public void setDirBackup(String path) {
        SaveDirDialog dialog = new SaveDirDialog();
        Bundle arguments = new Bundle();
        arguments.putString(DirsProvider.Dir.PATH, path);
        dialog.setArguments(arguments);
        dialog.show(getSupportFragmentManager(), "SaveDirDialog");
    }
}
