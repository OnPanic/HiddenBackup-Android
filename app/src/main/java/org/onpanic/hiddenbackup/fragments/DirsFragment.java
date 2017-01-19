package org.onpanic.hiddenbackup.fragments;


import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.onpanic.hiddenbackup.R;
import org.onpanic.hiddenbackup.adapters.DirsAdapter;
import org.onpanic.hiddenbackup.providers.DirsProvider;
import org.onpanic.hiddenbackup.ui.SimpleDividerItemDecoration;

public class DirsFragment extends Fragment {
    private ContentResolver mContentResolver;
    private DirsObserver observer;
    private OnDirClickListener mListener;
    private Context mContext;
    private DirsAdapter adapter;
    private FloatingActionButton mFab;

    private String[] mProjection = new String[]{
            DirsProvider.Dir._ID,
            DirsProvider.Dir.PATH,
            DirsProvider.Dir.ENABLED,
            DirsProvider.Dir.OBSERVER,
            DirsProvider.Dir.SCHEDULED
    };

    private String mWhere = null;

    public DirsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.dirs_list_fragment, container, false);
        mFab = (FloatingActionButton) view.findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager()
                        .beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.fragment_container, new FileManagerFragment())
                        .commit();
            }
        });

        adapter = new DirsAdapter(
                mContext,
                mContentResolver.query(
                        DirsProvider.CONTENT_URI, mProjection, mWhere, null, null
                ),
                mListener);

        mContentResolver.registerContentObserver(DirsProvider.CONTENT_URI, true, observer);

        RecyclerView list = (RecyclerView) view.findViewById(R.id.dirs_list);
        list.setLayoutManager(new LinearLayoutManager(view.getContext()));
        list.addItemDecoration(new SimpleDividerItemDecoration(mContext));
        list.setHasFixedSize(true); // does not change, except in onResume()
        list.setAdapter(adapter);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mContentResolver = mContext.getContentResolver();
        observer = new DirsObserver(new Handler());

        if (context instanceof OnDirClickListener) {
            mListener = (OnDirClickListener) mContext;
        } else {
            throw new RuntimeException(mContext.toString()
                    + " must implement OnDirClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mContentResolver.unregisterContentObserver(observer);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.dirs_navigation, menu);
        MenuItem item = menu.findItem(R.id.dir_type);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);

        ArrayAdapter<CharSequence> items = ArrayAdapter.createFromResource(
                mContext, R.array.array_dir_types, android.R.layout.simple_spinner_item);

        items.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(items);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View v, int pos, long id) {
                switch (pos) {
                    case 0:
                        mWhere = null;
                        break;
                    case 1:
                        mWhere = DirsProvider.Dir.SCHEDULED + "=1";
                        break;
                    case 2:
                        mWhere = DirsProvider.Dir.OBSERVER + "=1";
                        break;
                }

                adapter.changeCursor(mContentResolver.query(
                        DirsProvider.CONTENT_URI, mProjection, mWhere, null, null
                ));
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // Do nothing
            }
        });
    }

    public interface OnDirClickListener {
        void onDirClick(int id);
    }

    class DirsObserver extends ContentObserver {
        DirsObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            // New data
            adapter.changeCursor(mContentResolver.query(
                    DirsProvider.CONTENT_URI, mProjection, mWhere, null, null
            ));
        }
    }
}
