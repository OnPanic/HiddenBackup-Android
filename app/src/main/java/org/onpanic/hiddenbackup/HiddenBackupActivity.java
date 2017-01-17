package org.onpanic.hiddenbackup;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
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

import org.onpanic.hiddenbackup.fragments.AppSetup;
import org.onpanic.hiddenbackup.fragments.BackupNow;
import org.onpanic.hiddenbackup.fragments.DirsFragment;
import org.onpanic.hiddenbackup.fragments.FileManagerFragment;
import org.onpanic.hiddenbackup.fragments.HiddenBackupSettings;
import org.onpanic.hiddenbackup.helpers.BarcodeScannerHelper;
import org.onpanic.hiddenbackup.helpers.CheckDependenciesHelper;

import java.util.ArrayList;

import info.guardianproject.netcipher.proxy.OrbotHelper;

public class HiddenBackupActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        DirsFragment.OnDirClickListener,
        FileManagerFragment.OnSavePaths {

    private DrawerLayout drawer;
    private FragmentManager mFragmentManager;

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

        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        if (!CheckDependenciesHelper.checkAll(this)) {
            transaction.replace(R.id.fragment_container, new AppSetup());
        } else {
            transaction.replace(R.id.fragment_container, new BackupNow());
        }

        transaction.commit();
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
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDirClick(int id) {

    }

    @Override
    public void save(ArrayList<String> files) {

    }
}
