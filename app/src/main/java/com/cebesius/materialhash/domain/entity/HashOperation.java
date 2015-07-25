package com.cebesius.materialhash.domain.entity;

import com.google.common.base.Strings;

public class HashOperation {

    private final File file;
    private final HashAlgorithm hashAlgorithm;
    private long bytesDigested = 0;
    private String hash;

    public HashOperation(File file, HashAlgorithm hashAlgorithm) {
        this.file = file;
        this.hashAlgorithm = hashAlgorithm;
    }

    public File getFile() {
        return file;
    }

    public HashAlgorithm getHashAlgorithm() {
        return hashAlgorithm;
    }

    public double getProgress() {
        return (double) bytesDigested / (double) file.getSize();
    }

    public void incrementBytesDigested() {
        bytesDigested++;
    }

    public String getHash() {
        return hash;
    }

    public boolean hasHash() {
        return !Strings.isNullOrEmpty(hash);
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
