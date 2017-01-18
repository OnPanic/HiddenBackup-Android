package org.onpanic.hiddenbackup.dialogs;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import org.onpanic.hiddenbackup.R;
import org.onpanic.hiddenbackup.providers.DirsProvider;

public class SaveDirDialog extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context context = getContext();
        final View v = getActivity().getLayoutInflater().inflate(R.layout.save_dir_layout, null);
        final Switch observer = (Switch) v.findViewById(R.id.set_file_observer);
        final Switch schedule = (Switch) v.findViewById(R.id.set_for_schedule);

        observer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                schedule.setChecked(!b);
            }
        });

        schedule.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                observer.setChecked(!b);

            }
        });

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:

                        ContentValues values = new ContentValues();
                        values.put(DirsProvider.Dir.PATH, getArguments().getString(DirsProvider.Dir.PATH));
                        values.put(DirsProvider.Dir.OBSERVER, observer.isChecked());
                        values.put(DirsProvider.Dir.SCHEDULED, schedule.isChecked());

                        context.getContentResolver()
                                .insert(DirsProvider.CONTENT_URI, values);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // Do nothing
                        break;
                }
            }
        };

        return new AlertDialog.Builder(context)
                .setMessage(R.string.save_dir_backup)
                .setView(v)
                .setPositiveButton(R.string.save, dialogClickListener)
                .setNegativeButton(R.string.cancel, dialogClickListener)
                .create();
    }
}
