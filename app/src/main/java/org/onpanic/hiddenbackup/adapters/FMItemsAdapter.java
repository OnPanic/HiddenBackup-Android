package org.onpanic.hiddenbackup.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.onpanic.hiddenbackup.R;

import java.io.File;
import java.util.ArrayList;

public class FMItemsAdapter extends RecyclerView.Adapter<FMItemsAdapter.ViewHolder> {

    private File[] dirContent;
    private ArrayList<File> prevDir;
    private File currentDir;
    private OnSetDirBackup onSetDirBackup;

    public FMItemsAdapter(File current, Context context) {
        if (context instanceof OnSetDirBackup) {
            onSetDirBackup = (OnSetDirBackup) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSetDirBackup");
        }

        currentDir = current;
        dirContent = currentDir.listFiles();
        prevDir = new ArrayList<>();
    }

    @Override
    public FMItemsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fm_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FMItemsAdapter.ViewHolder holder, int position) {
        final File current = dirContent[position];

        holder.name.setText(current.getName());

        if (current.isDirectory()) {
            holder.add.setVisibility(View.VISIBLE);

            holder.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onSetDirBackup.setDirBackup(current.getAbsolutePath());
                }
            });

            holder.name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (current.isDirectory()) {
                        prevDir.add(currentDir);
                        currentDir = current;
                        dirContent = current.listFiles();
                        notifyDataSetChanged();
                    }
                }
            });
        } else {
            holder.add.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return (dirContent == null) ? 0 : dirContent.length;
    }

    public void goUp() {
        if (prevDir.size() >= 1) {
            File dir = prevDir.remove(prevDir.size() - 1);
            currentDir = dir;
            dirContent = dir.listFiles();
            notifyDataSetChanged();
        }
    }

    public interface OnSetDirBackup {
        void setDirBackup(String path);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView add;
        public final TextView name;

        ViewHolder(final View row) {
            super(row);
            add = (ImageView) row.findViewById(R.id.fm_item_add);
            name = (TextView) row.findViewById(R.id.fm_item_name);
        }
    }
}
