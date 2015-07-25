package com.cebesius.materialhash.mvp;

import com.cebesius.materialhash.domain.entity.File;
import com.cebesius.materialhash.domain.entity.HashAlgorithm;
import com.cebesius.materialhash.domain.entity.HashOperation;
import com.cebesius.materialhash.util.mvp.Model;
import com.google.common.base.Optional;

import java.util.List;

import rx.Observable;
import rx.Subscription;

public interface HashOperationModel extends Model {

    Observable<Optional<List<HashAlgorithm>>> getAvailableHashAlgorithmsObservable();

    Optional<List<HashAlgorithm>> getAvailableHashAlgorithms();

    void findAvailableHashAlgorithms();

    void setOperationHashAlgorithm(HashAlgorithm hashAlgorithm);

    void setOperationFile(File file);

    Observable<Optional<File>> getOperationFileObservable();

    Optional<File> getOperationFile();

    boolean isAvailableHashAlgorithmsRevealed();

    void setAvailableHashAlgorithmsRevealed(boolean revealed);

    Optional<HashAlgorithm> getOperationHashAlgorithm();

    Observable<Optional<HashAlgorithm>> getOperationHashAlgorithmObservable();

    boolean isHashOperationStartRevealed();

    void setHashOperationStartRevealed(boolean hashOperationStartRevealed);

    void setHashOperation(HashOperation hashOperation);

    Optional<HashOperation> getHashOperation();

    Observable<Optional<HashOperation>> getHashOperationObservable();
}
