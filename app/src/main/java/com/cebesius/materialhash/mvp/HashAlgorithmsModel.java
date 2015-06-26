package com.cebesius.materialhash.mvp;

import com.cebesius.materialhash.domain.HashAlgorithm;
import com.cebesius.materialhash.util.mvp.Model;

import java.util.List;

import rx.Observable;

public interface HashAlgorithmsModel extends Model {

    boolean hasAvailableHashAlgorithms();

    Observable<List<HashAlgorithm>> getAvailableHashAlgorithmsObservable();

    void findAvailableHashAlgorithms();

    void setOperationHashAlgorithm(HashAlgorithm hashAlgorithm);

    boolean hasOperationHashAlgorithm();
}
