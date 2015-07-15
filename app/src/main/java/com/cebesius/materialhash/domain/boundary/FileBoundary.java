package com.cebesius.materialhash.domain.boundary;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import com.cebesius.materialhash.domain.entity.File;
import com.google.common.base.Preconditions;

public class FileBoundary {

    private final ContentResolver contentResolver;

    public FileBoundary(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public File toEntity(Intent dataResultOfGetOpenableContent) {
        Preconditions.checkNotNull(dataResultOfGetOpenableContent, "argument must not be null");

        Uri uri = dataResultOfGetOpenableContent.getData();

        String[] projection = new String[] {
            OpenableColumns.DISPLAY_NAME,
            OpenableColumns.SIZE
        };
        Cursor cursor = contentResolver.query(uri, projection, null, null, null);
        if (!cursor.moveToFirst()) {
            throw new IllegalArgumentException("Unable to query metadata for file");
        }

        String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
        long size = cursor.getLong(cursor.getColumnIndex(OpenableColumns.SIZE));


        return new File(uri, displayName, size);
    }

}
