package com.cebesius.materialhash.mvp;

import android.os.Bundle;

import com.cebesius.materialhash.domain.entity.File;
import com.cebesius.materialhash.domain.entity.HashAlgorithm;
import com.cebesius.materialhash.domain.entity.HashOperation;
import com.cebesius.materialhash.util.HashAlgorithmsGateway;
import com.cebesius.materialhash.util.mvp.BaseModel;
import com.cebesius.materialhash.util.rx.RxSchedulers;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import java.io.Serializable;
import java.util.List;


import rx.Observable;
import rx.Subscription;
import rx.subjects.BehaviorSubject;

public class HashOperationModelImpl extends BaseModel
        implements HashOperationModel {

    private final RxSchedulers rxSchedulers;
    private final HashAlgorithmsGateway hashAlgorithmsGateway;
    private final BehaviorSubject<Optional<List<HashAlgorithm>>> availableHashAlgorithmsSubject = BehaviorSubject
        .create(Optional.absent());
    private final BehaviorSubject<Optional<HashAlgorithm>> operationHashAlgorithmSubject = BehaviorSubject
        .create(Optional.absent());
    private final BehaviorSubject<Optional<File>> operationFileSubject = BehaviorSubject
        .create(Optional.absent());
    private final BehaviorSubject<Optional<HashOperation>> hashOperationSubject = BehaviorSubject
        .create(Optional.absent());
    private Subscription findAvailableHashAlgorithmsSubscription;
    private boolean availableHashAlgorithmsRevealed;
    private boolean hashOperationStartRevealed;

    public HashOperationModelImpl(RxSchedulers rxSchedulers, HashAlgorithmsGateway hashAlgorithmsGateway) {
        this.rxSchedulers = rxSchedulers;
        this.hashAlgorithmsGateway = hashAlgorithmsGateway;
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
    public Observable<Optional<List<HashAlgorithm>>> getAvailableHashAlgorithmsObservable() {
        return availableHashAlgorithmsSubject.asObservable();
    }

    @Override
    public Optional<List<HashAlgorithm>> getAvailableHashAlgorithms() {
        return availableHashAlgorithmsSubject.getValue();
    }

    @Override
    public void findAvailableHashAlgorithms() {
        findAvailableHashAlgorithmsSubscription = hashAlgorithmsGateway.observeBuildAvailableHashAlgorithms()
                .observeOn(rxSchedulers.computationThread())
                .subscribeOn(rxSchedulers.computationThread())
                .subscribe(
                    availableHashAlgorithms ->
                        availableHashAlgorithmsSubject.onNext(Optional.of(availableHashAlgorithms))
                );
    }

    @Override
    public Optional<HashAlgorithm> getOperationHashAlgorithm() {
        return operationHashAlgorithmSubject.getValue();
    }

    @Override
    public Observable<Optional<HashAlgorithm>> getOperationHashAlgorithmObservable() {
        return operationHashAlgorithmSubject.asObservable();
    }

    @Override
    public void setOperationHashAlgorithm(HashAlgorithm hashAlgorithm) {
        operationHashAlgorithmSubject.onNext(
            hashAlgorithm != null
                ? Optional.of(hashAlgorithm)
                : Optional.absent()
        );
    }

    @Override
    public void setOperationFile(File file) {
        operationFileSubject.onNext(Optional.of(file));
    }

    @Override
    public Observable<Optional<File>> getOperationFileObservable() {
        return operationFileSubject.asObservable();
    }

    @Override
    public boolean isHashOperationStartRevealed() {
        return hashOperationStartRevealed;
    }

    @Override
    public void setHashOperationStartRevealed(boolean hashOperationStartRevealed) {
        this.hashOperationStartRevealed = hashOperationStartRevealed;
    }

    @Override
    public Optional<File> getOperationFile() {
        return operationFileSubject.getValue();
    }

    @Override
    public void setHashOperation(HashOperation hashOperation) {
        hashOperationSubject.onNext(Optional.of(hashOperation));
    }

    @Override
    public Optional<HashOperation> getHashOperation() {
        return hashOperationSubject.getValue();
    }

    @Override
    public Observable<Optional<HashOperation>> getHashOperationObservable() {
        return hashOperationSubject.asObservable();
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
        // no-op
    }

    @Override
    public void restoreState(Bundle bundle) {
        // no-op
    }
}
