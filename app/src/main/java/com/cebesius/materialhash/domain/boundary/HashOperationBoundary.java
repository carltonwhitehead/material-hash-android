package com.cebesius.materialhash.domain.boundary;

import android.content.ContentResolver;

import com.cebesius.materialhash.domain.entity.File;
import com.cebesius.materialhash.domain.entity.HashAlgorithm;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashOperationBoundary {

    private final ContentResolver contentResolver;

    public HashOperationBoundary(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public InputStream openInputStream(File file) throws HashOperationBoundaryException {
        try {
            return contentResolver.openInputStream(file.getUri());
        } catch (FileNotFoundException e) {
            String message = "Failed to open InputStream for uri: " + file.getUri().toString();
            throw new HashOperationBoundaryException(message, e);
        }
    }

    public MessageDigest getMessageDigest(HashAlgorithm hashAlgorithm) throws HashOperationBoundaryException {
        try {
            return MessageDigest.getInstance(hashAlgorithm.getName());
        } catch (NoSuchAlgorithmException e) {
            String message = "Failed to get MessageDigest instance for algorithm name: " + hashAlgorithm.getName();
            throw new HashOperationBoundaryException(message, e);
        }
    }

    public static class HashOperationBoundaryException extends Exception {

        public HashOperationBoundaryException(String detailMessage, Throwable e) {

        }
    }
}
