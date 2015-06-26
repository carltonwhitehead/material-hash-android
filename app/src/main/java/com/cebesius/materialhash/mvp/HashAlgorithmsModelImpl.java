package com.cebesius.materialhash.mvp;

import android.os.Bundle;

import com.cebesius.materialhash.domain.HashAlgorithm;
import com.cebesius.materialhash.util.HashAlgorithmsGateway;
import com.cebesius.materialhash.util.mvp.BaseModel;
import com.cebesius.materialhash.util.rx.RxSchedulers;

import java.io.Serializable;
import java.util.List;


import rx.Observable;
import rx.Subscription;
import rx.subjects.BehaviorSubject;

public class HashAlgorithmsModelImpl extends BaseModel
        implements HashAlgorithmsModel {

    private static final String KEY_HASH_ALGORITHMS = "hashAlgorithms";
    private static final String KEY_OPERATION_HASH_ALGORITHM = "operationHashAlgorithm";

    private final RxSchedulers rxSchedulers;
    private final HashAlgorithmsGateway hashAlgorithmsGateway;
    private final BehaviorSubject<List<HashAlgorithm>> availableHashAlgorithmsSubject = BehaviorSubject.create();
    private Subscription findAvailableHashAlgorithmsSubscription;
    private HashAlgorithm operationHashAlgorithm;

    public HashAlgorithmsModelImpl(RxSchedulers rxSchedulers, HashAlgorithmsGateway hashAlgorithmsGateway) {
        this.rxSchedulers = rxSchedulers;
        this.hashAlgorithmsGateway = hashAlgorithmsGateway;
    }

    @Override
    public boolean hasAvailableHashAlgorithms() {
        return availableHashAlgorithmsSubject.hasCompleted()
                && !availableHashAlgorithmsSubject.hasThrowable()
                && availableHashAlgorithmsSubject.hasValue();
    }

    @Override
    public Observable<List<HashAlgorithm>> getAvailableHashAlgorithmsObservable() {
        return availableHashAlgorithmsSubject.asObservable();
    }

    @Override
    public void findAvailableHashAlgorithms() {
        findAvailableHashAlgorithmsSubscription = hashAlgorithmsGateway.observeBuildAvailableHashAlgorithms()
                .observeOn(rxSchedulers.computationThread())
                .subscribeOn(rxSchedulers.computationThread())
                .subscribe(availableHashAlgorithmsSubject);
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
        if (hasAvailableHashAlgorithms()) {
            bundle.putSerializable(KEY_HASH_ALGORITHMS, (Serializable) availableHashAlgorithmsSubject.getValue());
        }
        bundle.putSerializable(KEY_OPERATION_HASH_ALGORITHM, operationHashAlgorithm);
    }

    @Override
    public void restoreState(Bundle bundle) {
        if (bundle.containsKey(KEY_HASH_ALGORITHMS)) {
            availableHashAlgorithmsSubject.onNext((List<HashAlgorithm>) bundle.getSerializable(KEY_HASH_ALGORITHMS));
        }
        operationHashAlgorithm = (HashAlgorithm) bundle.getSerializable(KEY_OPERATION_HASH_ALGORITHM);
    }
}
