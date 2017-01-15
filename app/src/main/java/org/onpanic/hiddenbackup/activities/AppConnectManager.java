package org.onpanic.hiddenbackup.activities;

import android.app.Activity;
import android.os.Bundle;

import info.guardianproject.panic.PanicTrigger;

public class AppConnectManager extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (PanicTrigger.checkForDisconnectIntent(this)) {
            finish();
        }
    }
}
