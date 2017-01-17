package org.onpanic.hiddenbackup.adapters;

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

    public FMItemsAdapter(File current) {
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

        holder.file = current;
        holder.name.setText(current.getName());
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

    class ViewHolder extends RecyclerView.ViewHolder {

        public final ImageView add;
        public final TextView name;
        public File file;

        ViewHolder(final View row) {
            super(row);
            add = (ImageView) row.findViewById(R.id.fm_item_add);
            name = (TextView) row.findViewById(R.id.fm_item_name);
        }
    }
}
