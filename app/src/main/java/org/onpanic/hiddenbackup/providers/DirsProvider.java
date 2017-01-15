package org.onpanic.hiddenbackup.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.onpanic.hiddenbackup.database.DirsDB;


public class DirsProvider extends ContentProvider {

    private static final String AUTH = "org.onpanic.hiddenbackup.DIRS_PROVIDER";
    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTH + "/dirs");

    //UriMatcher
    private static final int DIRS = 1;
    private static final int DIRS_ID = 2;
    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTH, "dirs", DIRS);
        uriMatcher.addURI(AUTH, "dirs/#", DIRS_ID);
    }

    private DirsDB dirsDB;
    private Context mContext;

    @Override
    public boolean onCreate() {
        mContext = getContext();
        dirsDB = new DirsDB(mContext);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String where = selection;
        if (uriMatcher.match(uri) == DIRS_ID) {
            where = "_id=" + uri.getLastPathSegment();
        }

        SQLiteDatabase db = dirsDB.getReadableDatabase();

        return db.query(DirsDB.DIRS_TABLE_NAME, projection, where,
                selectionArgs, null, null, sortOrder);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = uriMatcher.match(uri);

        switch (match) {
            case DIRS:
                return "vnd.android.cursor.dir/vnd.hiddenbackup.dirs";
            case DIRS_ID:
                return "vnd.android.cursor.item/vnd.hiddenbackup.dir";
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        long regId;

        SQLiteDatabase db = dirsDB.getWritableDatabase();

        regId = db.insert(DirsDB.DIRS_TABLE_NAME, null, values);

        mContext.getContentResolver().notifyChange(CONTENT_URI, null);

        return ContentUris.withAppendedId(CONTENT_URI, regId);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        String where = selection;
        if (uriMatcher.match(uri) == DIRS_ID) {
            where = "_id=" + uri.getLastPathSegment();
        }

        SQLiteDatabase db = dirsDB.getWritableDatabase();

        Integer rows = db.delete(DirsDB.DIRS_TABLE_NAME, where, selectionArgs);

        mContext.getContentResolver().notifyChange(CONTENT_URI, null);

        return rows;

    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dirsDB.getWritableDatabase();
        Integer rows = db.update(DirsDB.DIRS_TABLE_NAME, values, selection, selectionArgs);
        mContext.getContentResolver().notifyChange(CONTENT_URI, null);
        return rows;
    }

    public static final class Dir implements BaseColumns {

        public static final String ENABLED = "enabled";
        public static final String PATH = "path";

        private Dir() {
        }
    }
}
