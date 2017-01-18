package org.onpanic.hiddenbackup.adapters;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import org.onpanic.hiddenbackup.R;
import org.onpanic.hiddenbackup.fragments.DirsFragment;
import org.onpanic.hiddenbackup.providers.DirsProvider;

import java.io.File;


public class DirsAdapter extends CursorRecyclerViewAdapter<DirsAdapter.ViewHolder> {

    private final DirsFragment.OnDirClickListener mListener;
    private Context mContext;

    public DirsAdapter(Context context, Cursor cursor, DirsFragment.OnDirClickListener listener) {
        super(cursor);
        mContext = context;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dir_item_fragment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, Cursor cursor) {
        final int id = cursor.getInt(cursor.getColumnIndex(DirsProvider.Dir._ID));
        final String file_name = cursor.getString(cursor.getColumnIndex(DirsProvider.Dir.PATH));
        final Boolean active = (cursor.getInt(cursor.getColumnIndex(DirsProvider.Dir.ENABLED)) == 1);

        if ((cursor.getInt(cursor.getColumnIndex(DirsProvider.Dir.SCHEDULED)) == 1)) {
            viewHolder.mImage.setImageResource(R.drawable.ic_scheduled_dir);
        } else {
            viewHolder.mImage.setImageResource(R.drawable.ic_new_file_created);
        }

        viewHolder.mActive.setChecked(active);
        viewHolder.mActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ContentResolver resolver = mContext.getContentResolver();
                ContentValues fields = new ContentValues();
                fields.put(DirsProvider.Dir.ENABLED, isChecked);
                resolver.update(
                        DirsProvider.CONTENT_URI, fields, "_ID=" + id, null
                );
            }
        });

        File file = new File(file_name);

        viewHolder.mName.setText(file.getName());
        viewHolder.mName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onDirClick(id);
                }
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView mImage;
        final Switch mActive;
        final TextView mName;

        ViewHolder(View view) {
            super(view);
            mImage = (ImageView) view.findViewById(R.id.contact_image);
            mActive = (Switch) view.findViewById(R.id.contact_active);
            mName = (TextView) view.findViewById(R.id.contact_name);
        }
    }
}
