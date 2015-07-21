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
    private static final String KEY_AVAILABLE_HASH_ALGORITHMS = "availableHashAlgorithms";
    private static final String KEY_OPERATION_HASH_ALGORITHM = "operationHashAlgorithm";
    private static final String KEY_OPERATION_FILE = "operationFile";

    private final RxSchedulers rxSchedulers;
    private final HashAlgorithmsGateway hashAlgorithmsGateway;
    private final BehaviorSubject<List<HashAlgorithm>> availableHashAlgorithmsSubject = BehaviorSubject.create();
    private Subscription findAvailableHashAlgorithmsSubscription;
    private boolean availableHashAlgorithmsRevealed;
    private HashAlgorithm operationHashAlgorithm;
    private File operationFile;

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
        return operationHashAlgorithm;
    }

    @Override
    public void setOperationHashAlgorithm(HashAlgorithm hashAlgorithm) {
        this.operationHashAlgorithm = hashAlgorithm;
    }

    @Override
    public boolean hasOperationHashAlgorithm() {
        return operationHashAlgorithm != null;
    }

    @Override
    public void setOperationFile(File file) {
        this.operationFile = file;
    }

    @Override
    public boolean hasOperationFile() {
        return operationFile != null;
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
            bundle.putSerializable(KEY_AVAILABLE_HASH_ALGORITHMS, (Serializable) availableHashAlgorithmsSubject.getValue());
        }
        bundle.putSerializable(KEY_OPERATION_HASH_ALGORITHM, operationHashAlgorithm);
        bundle.putSerializable(KEY_OPERATION_FILE, operationFile);
    }

    @Override
    public void restoreState(Bundle bundle) {
        availableHashAlgorithmsRevealed = bundle.getBoolean(KEY_AVAILABLE_HASH_ALGORITHMS_REVEALED);
        if (bundle.containsKey(KEY_AVAILABLE_HASH_ALGORITHMS)) {
            availableHashAlgorithmsSubject.onNext((List<HashAlgorithm>) bundle.getSerializable(
                KEY_AVAILABLE_HASH_ALGORITHMS
            ));
        }
        operationHashAlgorithm = (HashAlgorithm) bundle.getSerializable(KEY_OPERATION_HASH_ALGORITHM);
        operationFile = (File) bundle.getSerializable(KEY_OPERATION_FILE);
    }
}
