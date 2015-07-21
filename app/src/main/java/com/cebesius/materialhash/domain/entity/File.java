package com.cebesius.materialhash.domain.entity;

import android.net.Uri;

import java.io.Serializable;

public class File implements Serializable {

    private transient Uri uri;
    private final String uriAsString;
    private final String displayName;
    private final long size;

    public File(Uri uri, String displayName, long size) {
        this.uri = uri;
        this.uriAsString = uri.toString();
        this.displayName = displayName;
        this.size = size;
    }

    public Uri getUri() {
        if (uri == null) {
            uri = Uri.parse(uriAsString);
        }
        return uri;
    }

    public String getDisplayName() {
        return displayName;
    }

    public long getSize() {
        return size;
    }
}
