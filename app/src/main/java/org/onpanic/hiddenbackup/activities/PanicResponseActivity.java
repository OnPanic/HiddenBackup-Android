package org.onpanic.hiddenbackup.activities;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;

import info.guardianproject.panic.PanicResponder;

public class PanicResponseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (PanicResponder.receivedTriggerFromConnectedApp(this)) {
            // TODO
        }

        if (Build.VERSION.SDK_INT >= 21) {
            finishAndRemoveTask();
        } else {
            finish();
        }
    }
}
