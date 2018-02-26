package com.example.android.popmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.popmovies.data.MovieContract.MovieEntry;

/**
 * {@link MovieProvider} is a {@link ContentProvider} communicating between the app and the db.
 */
public class MovieProvider extends ContentProvider {

    private static final String LOG_TAG = MovieProvider.class.getSimpleName();

    private static final int MOVIE_LIST = 100;
    private static final int MOVIE_ENTRY = 101;

    private static final UriMatcher uriMatcher = buildMatcher();
    private SQLiteDatabase dbRead, dbWrite;


    // Create a uriMatcher for the full list and single entries respectively.
    private static UriMatcher buildMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTH;
        final String contract = MovieContract.PATH_MOVIE;

        matcher.addURI(authority, contract, MOVIE_LIST);
        matcher.addURI(authority, contract + "/#", MOVIE_ENTRY);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        MovieDbHelper movieDbHelper = new MovieDbHelper(getContext());

        dbRead = movieDbHelper.getReadableDatabase();
        dbWrite = movieDbHelper.getWritableDatabase();
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor csr;
        switch (getMatch(uri)) {
            case MOVIE_LIST: // when querying the full list
                csr = dbRead.query(MovieEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case MOVIE_ENTRY: // when querying a single movie
                selection = MovieEntry.MOVIE_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                csr = dbRead.query(MovieEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown uri, which cannot be queried: "
                        + uri);
        }
        csr.setNotificationUri(getContext().getContentResolver(), uri);
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = getMatch(uri);
        switch (match) {
            case MOVIE_LIST:
                return MovieEntry.CONTENT_LIST_TYPE;
            case MOVIE_ENTRY:
                return MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown uri " + uri + " with match " + match);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        long id;
        switch (getMatch(uri)) {
            case MOVIE_ENTRY:
                id = dbWrite.insert(MovieEntry.TABLE_NAME, null, values);
                if (id == -1) {
                    Log.e(LOG_TAG, "Row could not be inserted for " + uri);
                }
                notifyResolver(uri);
                break;
            default:
                throw new IllegalArgumentException("Unknown uri " + uri + "with match " + getMatch(uri));
        }
        return ContentUris.withAppendedId(uri, id); // return new uri and id of row inserted.
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        switch (getMatch(uri)) {
            case MOVIE_LIST:
                dbWrite.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : values) {
                        long id = dbWrite.insert(MovieEntry.TABLE_NAME, null, value);
                        if (id != -1) {
                            rowsInserted++;
                        }
                    }
                    dbWrite.setTransactionSuccessful();
                } finally {
                    dbWrite.endTransaction();
                }
                if (rowsInserted > 0) {
                    notifyResolver(uri);
                }
                return rowsInserted;

            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int deletedRows = 0;
        switch (getMatch(uri)) {
            case MOVIE_LIST:
                if (selection == null) selection = "1";
                deletedRows = dbWrite.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MOVIE_ENTRY:
                selection = MovieEntry.MOVIE_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                deletedRows = dbWrite.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
        }

        if (deletedRows != 0) {
            notifyResolver(uri);
        }
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.size() == 0) {
            return 0; // return early if nothing has been changed
        }

        int updatedRows = 0;
        switch (getMatch(uri)) {
            case MOVIE_LIST:
                updatedRows = dbWrite.update(MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                if (updatedRows != 0) {
                    notifyResolver(uri);
                }
                break;
            case MOVIE_ENTRY:
                selection = MovieEntry.MOVIE_ID +  "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                updatedRows = dbWrite.update(MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                if (updatedRows != 0) {
                    notifyResolver(uri);
                }
        }
        return updatedRows;
    }

    private void notifyResolver(@NonNull Uri uri) {
        getContext().getContentResolver().notifyChange(uri, null);
    }

    private int getMatch(@NonNull Uri uri) {
        return uriMatcher.match(uri);
    }
}
