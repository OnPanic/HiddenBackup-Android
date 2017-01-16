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

import java.util.ArrayList;

import info.guardianproject.netcipher.proxy.OrbotHelper;

public class HiddenBackupActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        DirsFragment.OnDirClickListener,
        FileManagerFragment.OnSavePaths {

    private DrawerLayout drawer;
    private FragmentManager mFragmentManager;
    private boolean isOrbotInstalled;
    private boolean hasServerConf;

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

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        hasServerConf = (preferences.getString(getString(R.string.pref_server_onion), null) != null);
        isOrbotInstalled = OrbotHelper.isOrbotInstalled(this);

        // Do not overlapping fragments.
        if (savedInstanceState != null) return;

        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        if (!isOrbotInstalled) {
            AppSetup setup = new AppSetup();
            setup.orbotSetup();
            transaction.replace(R.id.fragment_container, setup);
        } else if (!hasServerConf) {
            AppSetup setup = new AppSetup();
            setup.serverSetup();
            transaction.replace(R.id.fragment_container, setup);
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

        if (!isOrbotInstalled) {
            AppSetup setup = new AppSetup();
            setup.orbotSetup();
            transaction.replace(R.id.fragment_container, setup);
        } else if (!hasServerConf) {
            AppSetup setup = new AppSetup();
            setup.serverSetup();
            transaction.replace(R.id.fragment_container, setup);
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
