package org.onpanic.hiddenbackup;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.onpanic.hiddenbackup.fragments.BackupNow;
import org.onpanic.hiddenbackup.fragments.DirsFragment;
import org.onpanic.hiddenbackup.fragments.HiddenBackupSettings;

import info.guardianproject.netcipher.proxy.OrbotHelper;

public class HiddenBackupActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

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

        if (!OrbotHelper.isOrbotInstalled(this)) {
            Snackbar.make(findViewById(android.R.id.content),
                    R.string.install_orbot,
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.install,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(OrbotHelper.getOrbotInstallIntent(HiddenBackupActivity.this));
                        }
                    }).show();
        }

        // Do not overlapping fragments.
        if (savedInstanceState != null) return;

        mFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, new BackupNow())
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_hidden_backup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                mFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, new HiddenBackupSettings())
                        .commit();
                break;
            case R.id.add_backup_dirs:
                mFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, new DirsFragment())
                        .commit();
                break;
            case R.id.scheduled:
                // TODO
                break;
            case R.id.notifications:
                // TODO
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.run_backup_now:
                // TODO
                break;
        }

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
}
