package org.onpanic.hiddenbackup.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.onpanic.hiddenbackup.R;

import java.io.File;
import java.util.ArrayList;

public class FMItemsAdapter extends RecyclerView.Adapter<FMItemsAdapter.ViewHolder> {
    private File[] dirContent;

    private ArrayList<File> prevDir;
    private ArrayList<String> selectedFiles;
    private File currentDir;

    public FMItemsAdapter(File current) {
        currentDir = current;
        dirContent = currentDir.listFiles();
        prevDir = new ArrayList<>();
        selectedFiles = new ArrayList<>();
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
                } else {
                    holder.selected.toggle();
                }
            }
        });

        holder.selected.setChecked(selectedFiles.lastIndexOf(current.getAbsolutePath()) != -1);
        holder.selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    selectedFiles.add(current.getAbsolutePath());
                } else {
                    selectedFiles.remove(selectedFiles.lastIndexOf(current.getAbsolutePath()));
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

    public ArrayList<String> getSelectedFiles() {
        return selectedFiles;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public final CheckBox selected;
        public final ImageView type;
        public final TextView name;
        public File file;

        ViewHolder(final View row) {
            super(row);
            type = (ImageView) row.findViewById(R.id.fm_item_type);
            name = (TextView) row.findViewById(R.id.fm_item_name);
            selected = (CheckBox) row.findViewById(R.id.fm_item_selected);
        }
    }
}
