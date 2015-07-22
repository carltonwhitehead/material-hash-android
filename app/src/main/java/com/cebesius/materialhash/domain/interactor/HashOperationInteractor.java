package com.cebesius.materialhash.domain.interactor;

import android.content.ContentResolver;

import com.cebesius.materialhash.domain.boundary.HashOperationBoundary;
import com.cebesius.materialhash.domain.entity.HashOperation;

import org.spongycastle.util.encoders.Hex;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;

public class HashOperationInteractor {

    private final HashOperationBoundary boundary;

    public HashOperationInteractor(HashOperationBoundary boundary) {
        this.boundary = boundary;
    }

    public Observable<HashOperation> run(final HashOperation hashOperation) {
        return Observable.create(
            subscriber -> {
                subscriber.onStart();

                InputStream inputStream;
                MessageDigest messageDigest;
                try {
                    inputStream = boundary.openInputStream(hashOperation.getFile());
                } catch (HashOperationBoundary.HashOperationBoundaryException e) {
                    subscriber.onError(new HashOperationInteractorException(e));
                    return;
                }
                try {
                    messageDigest = boundary.getMessageDigest(hashOperation.getHashAlgorithm());
                } catch (HashOperationBoundary.HashOperationBoundaryException e) {
                    try {
                        inputStream.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    subscriber.onError(new HashOperationInteractorException(e));
                    return;
                }

                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

                Observable<Long> statusNotifier = Observable.interval(17, TimeUnit.MILLISECONDS);
                Subscription statusNotifierSubscription = statusNotifier.subscribe(
                    sequence -> {
                        subscriber.onNext(hashOperation);
                    }
                );

                int byteAsInt;

                try {
                    while ((byteAsInt = bufferedInputStream.read()) != -1) {
                        messageDigest.update((byte) byteAsInt);
                        hashOperation.incrementBytesDigested();
                    }
                } catch (IOException e) {
                    String message = "Failed to read byte";
                    subscriber.onError(new HashOperationInteractorException(message, e));
                    return;
                } finally {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                byte[] digestAsBytes = messageDigest.digest();

                String digest = Hex.toHexString(digestAsBytes);
                hashOperation.setHash(digest);

                statusNotifierSubscription.unsubscribe();

                subscriber.onNext(hashOperation);
                subscriber.onCompleted();
            }
        );
    }

    public static class HashOperationInteractorException extends Exception {

        public HashOperationInteractorException(Throwable exception) {
            super(exception);
        }

        public HashOperationInteractorException(String detailMessage, Throwable exception) {
            super(detailMessage, exception);
        }
    }
}
