package org.onpanic.hiddenbackup.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.onpanic.hiddenbackup.R;
import org.onpanic.hiddenbackup.ui.ConnectedAppEntry;

import java.util.ArrayList;

import info.guardianproject.panic.Panic;
import info.guardianproject.panic.PanicResponder;

public class TriggerApps extends Fragment {
    private Context mContext;
    private PackageManager pm;
    private ConnectedAppEntry NONE;
    private ArrayList<ConnectedAppEntry> list;
    private ListView apps;
    private int selectedApp = 0;

    public TriggerApps() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NONE = new ConnectedAppEntry(mContext, Panic.PACKAGE_NAME_NONE, R.string.none);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.configure_trigger_app, container, false);
        apps = (ListView) v.findViewById(R.id.trigger_apps);
        String packageName = PanicResponder.getTriggerPackageName(mContext);
        if (packageName == null) {
            packageName = NONE.packageName;
            PanicResponder.setTriggerPackageName(getActivity(),
                    NONE.packageName);
        }

        list = new ArrayList<ConnectedAppEntry>();
        list.add(0, NONE);

        for (ResolveInfo resolveInfo : PanicResponder.resolveTriggerApps(pm)) {
            if (resolveInfo.activityInfo == null)
                continue;
            list.add(new ConnectedAppEntry(pm, resolveInfo.activityInfo));
            if (packageName.equals(resolveInfo.activityInfo.packageName)) {
                selectedApp = list.size() - 1;
            }
        }

        ListAdapter adapter = new ArrayAdapter<ConnectedAppEntry>(mContext,
                android.R.layout.simple_list_item_single_choice, android.R.id.text1, list);

        apps.setAdapter(adapter);
        apps.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        apps.setItemChecked(selectedApp, true);
        apps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ConnectedAppEntry entry = list.get(i);
                PanicResponder.setTriggerPackageName(getActivity(),
                        entry.packageName);
            }
        });
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        pm = mContext.getPackageManager();
    }
}
