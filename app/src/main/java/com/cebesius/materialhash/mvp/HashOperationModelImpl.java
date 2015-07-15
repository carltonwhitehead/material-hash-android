package com.cebesius.materialhash.mvp;

import android.os.Bundle;

import com.cebesius.materialhash.domain.entity.File;
import com.cebesius.materialhash.domain.entity.HashAlgorithm;
import com.cebesius.materialhash.util.HashAlgorithmsGateway;
import com.cebesius.materialhash.util.mvp.BaseModel;
import com.cebesius.materialhash.util.rx.RxSchedulers;
import com.google.common.base.Preconditions;

import java.io.Serializable;
import java.util.List;


import rx.Observable;
import rx.Subscription;
import rx.subjects.BehaviorSubject;

public class HashOperationModelImpl extends BaseModel
        implements HashOperationModel {

    private static final String KEY_AVAILABLE_HASH_ALGORITHMS_REVEALED = "availableHashAlgorithmsRevealed";
    private static final String KEY_HASH_ALGORITHMS = "hashAlgorithms";
    private static final String KEY_OPERATION_HASH_ALGORITHM = "hashAlgorithm";
    private static final String KEY_OPERATION_FILE = "file";

    private final RxSchedulers rxSchedulers;
    private final HashAlgorithmsGateway hashAlgorithmsGateway;
    private final BehaviorSubject<List<HashAlgorithm>> availableHashAlgorithmsSubject = BehaviorSubject.create();
    private Subscription findAvailableHashAlgorithmsSubscription;
    private boolean availableHashAlgorithmsRevealed;
    private HashAlgorithm hashAlgorithm;
    private File file;

    public HashOperationModelImpl(RxSchedulers rxSchedulers, HashAlgorithmsGateway hashAlgorithmsGateway) {
        this.rxSchedulers = rxSchedulers;
        this.hashAlgorithmsGateway = hashAlgorithmsGateway;
    }

    @Override
    public boolean hasAvailableHashAlgorithms() {
        return availableHashAlgorithmsSubject.hasValue()
                && !availableHashAlgorithmsSubject.hasThrowable();
    }

    @Override
    public void setAvailableHashAlgorithmsRevealed(boolean revealed) {
        this.availableHashAlgorithmsRevealed = revealed;
    }

    @Override
    public boolean isAvailableHashAlgorithmsRevealed() {
        return availableHashAlgorithmsRevealed;
    }

    @Override
    public Observable<List<HashAlgorithm>> getAvailableHashAlgorithmsObservable() {
        return availableHashAlgorithmsSubject.asObservable();
    }

    @Override
    public List<HashAlgorithm> getAvailableHashAlgorithms() {
        Preconditions.checkState(
            hasAvailableHashAlgorithms(),
            "Programmer error: first check if hasAvailableHashAlgorithms()"
        );
        return availableHashAlgorithmsSubject.getValue();
    }

    @Override
    public void findAvailableHashAlgorithms() {
        findAvailableHashAlgorithmsSubscription = hashAlgorithmsGateway.observeBuildAvailableHashAlgorithms()
                .observeOn(rxSchedulers.computationThread())
                .subscribeOn(rxSchedulers.computationThread())
                .subscribe(availableHashAlgorithmsSubject);
    }

    @Override
    public HashAlgorithm getOperationHashAlgorithm() {
        return hashAlgorithm;
    }

    @Override
    public void setOperationHashAlgorithm(HashAlgorithm hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }

    @Override
    public boolean hasOperationHashAlgorithm() {
        return hashAlgorithm != null;
    }

    @Override
    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public boolean hasFile() {
        return file != null;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {
        if (findAvailableHashAlgorithmsSubscription != null) {
            findAvailableHashAlgorithmsSubscription.unsubscribe();
        }
    }

    @Override
    public void saveState(Bundle bundle) {
        bundle.putBoolean(KEY_AVAILABLE_HASH_ALGORITHMS_REVEALED, availableHashAlgorithmsRevealed);
        if (hasAvailableHashAlgorithms()) {
            bundle.putSerializable(KEY_HASH_ALGORITHMS, (Serializable) availableHashAlgorithmsSubject.getValue());
        }
        bundle.putSerializable(KEY_OPERATION_HASH_ALGORITHM, hashAlgorithm);
        bundle.putSerializable(KEY_OPERATION_FILE, file);
    }

    @Override
    public void restoreState(Bundle bundle) {
        availableHashAlgorithmsRevealed = bundle.getBoolean(KEY_AVAILABLE_HASH_ALGORITHMS_REVEALED);
        if (bundle.containsKey(KEY_HASH_ALGORITHMS)) {
            availableHashAlgorithmsSubject.onNext((List<HashAlgorithm>) bundle.getSerializable(KEY_HASH_ALGORITHMS));
        }
        hashAlgorithm = (HashAlgorithm) bundle.getSerializable(KEY_OPERATION_HASH_ALGORITHM);
        file = (File) bundle.getSerializable(KEY_OPERATION_FILE);
    }
}
