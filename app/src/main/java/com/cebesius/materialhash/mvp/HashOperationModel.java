package com.cebesius.materialhash.mvp;

import com.cebesius.materialhash.domain.entity.File;
import com.cebesius.materialhash.domain.entity.HashAlgorithm;
import com.cebesius.materialhash.util.mvp.Model;

import java.util.List;

import rx.Observable;

public interface HashOperationModel extends Model {

    boolean hasAvailableHashAlgorithms();

    Observable<List<HashAlgorithm>> getAvailableHashAlgorithmsObservable();

    List<HashAlgorithm> getAvailableHashAlgorithms();

    void findAvailableHashAlgorithms();

    void setOperationHashAlgorithm(HashAlgorithm hashAlgorithm);

    boolean hasOperationHashAlgorithm();

    void setOperationFile(File file);

    boolean hasOperationFile();

    boolean isAvailableHashAlgorithmsRevealed();

    void setAvailableHashAlgorithmsRevealed(boolean revealed);

    HashAlgorithm getOperationHashAlgorithm();
}
