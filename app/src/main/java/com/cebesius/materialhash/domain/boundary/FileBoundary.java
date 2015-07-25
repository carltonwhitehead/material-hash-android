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

        File file;
        switch (uri.getScheme()) {
            case "file":
                file = fromUriWithFileScheme(uri);
                break;
            case "content":
            default:
                file = fromUriWithContentScheme(uri);
                break;
        }

        return file;
    }

    private File fromUriWithContentScheme(Uri uri) {
        Preconditions.checkArgument("content".equals(uri.getScheme()));

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

    private File fromUriWithFileScheme(Uri uri) {
        Preconditions.checkArgument("file".equals(uri.getScheme()));

        java.io.File javaIoFile = new java.io.File(uri.getPath());

        String displayName = uri.getLastPathSegment();
        long size = javaIoFile.length();

        return new File(uri, displayName, size);
    }

}
