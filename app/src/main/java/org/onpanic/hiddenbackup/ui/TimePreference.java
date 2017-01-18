package org.onpanic.hiddenbackup.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import org.onpanic.hiddenbackup.R;

public class TimePreference extends DialogPreference {
    private int lastHour = 0;
    private int lastMinute = 0;
    private TimePicker picker = null;

    public TimePreference(Context ctxt, AttributeSet attrs) {
        super(ctxt, attrs);

        setPositiveButtonText(ctxt.getString(R.string.save));
        setNegativeButtonText(ctxt.getString(R.string.cancel));
    }

    private static int getHour(String time) {
        String[] pieces = time.split(":");

        return (Integer.parseInt(pieces[0]));
    }

    private static int getMinute(String time) {
        String[] pieces = time.split(":");

        return (Integer.parseInt(pieces[1]));
    }

    @Override
    protected View onCreateDialogView() {
        picker = new TimePicker(getContext());
        picker.setIs24HourView(true);
        return (picker);
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);

        picker.setCurrentHour(lastHour);
        picker.setCurrentMinute(lastMinute);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            lastHour = picker.getCurrentHour();
            lastMinute = picker.getCurrentMinute();

            String time = String.format(
                    "%s:%s", String.format("%02d", lastHour), String.format("%02d", lastMinute));

            if (callChangeListener(time)) {
                persistString(time);
                setSummary(getContext().getString(R.string.next_backup_at) + " " + time);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return (a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String time;

        if (restoreValue) {
            if (defaultValue == null) {
                time = getPersistedString("00:00");
            } else {
                time = getPersistedString(defaultValue.toString());
            }
        } else {
            time = defaultValue.toString();
        }

        lastHour = getHour(time);
        lastMinute = getMinute(time);
        setSummary(getContext().getString(R.string.next_backup_at) + " " + time);
    }
}