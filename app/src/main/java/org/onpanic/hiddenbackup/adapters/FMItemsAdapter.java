package org.onpanic.hiddenbackup.adapters;

import android.app.FragmentManager;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.onpanic.hiddenbackup.R;
import org.onpanic.hiddenbackup.fragments.SetFolderBackup;
import org.onpanic.hiddenbackup.providers.DirsProvider;

import java.io.File;
import java.util.ArrayList;

public class FMItemsAdapter extends RecyclerView.Adapter<FMItemsAdapter.ViewHolder> {
    private File[] dirContent;

    private ArrayList<File> prevDir;
    private File currentDir;
    private ContentResolver mResolver;
    private FragmentManager mManager;

    private String[] mProjection = new String[]{
            DirsProvider.Dir._ID,
            DirsProvider.Dir.PATH
    };

    public FMItemsAdapter(File current, ContentResolver contentResolver, FragmentManager fragmentManager) {
        currentDir = current;
        mResolver = contentResolver;
        mManager = fragmentManager;
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

        if (current.isDirectory()) {
            holder.add.setVisibility(View.VISIBLE);

            String where = DirsProvider.Dir.PATH + "='" + current.getAbsolutePath() + "'";
            Cursor c = mResolver.query(
                    DirsProvider.CONTENT_URI, mProjection, where, null, null);

            if (c == null || c.getCount() < 1) {
                ColorMatrix matrix = new ColorMatrix();
                matrix.setSaturation(0);
                ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                holder.add.setColorFilter(filter);

                holder.add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SetFolderBackup folderBackup = new SetFolderBackup();
                        folderBackup.setUp(current.getAbsolutePath(), mManager);
                        mManager.beginTransaction()
                                .addToBackStack(null)
                                .replace(R.id.fragment_container, folderBackup)
                                .commit();
                    }
                });
            }

            if (c != null) c.close();

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

        holder.file = current;
        holder.name.setText(current.getName());

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
