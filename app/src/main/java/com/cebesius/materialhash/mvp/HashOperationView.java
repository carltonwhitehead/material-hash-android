package com.cebesius.materialhash.mvp;

import com.cebesius.materialhash.domain.entity.File;
import com.cebesius.materialhash.domain.entity.HashAlgorithm;
import com.cebesius.materialhash.domain.entity.HashOperation;
import com.cebesius.materialhash.util.mvp.View;
import com.google.common.base.Optional;

import java.util.List;

public interface HashOperationView extends View {

    void showAvailableHashAlgorithms(List<HashAlgorithm> hashAlgorithms);

    void revealAvailableHashAlgorithms(List<HashAlgorithm> hashAlgorithms);

    void showOperationFilePicker();

    void setOperationFile(File file);

    void setOperationHashAlgorithm(HashAlgorithm hashAlgorithm);

    void showOperationHashAlgorithmPicker(
        List<HashAlgorithm> availableHashAlgorithms,
        Optional<HashAlgorithm> operationHashAlgorithm
    );

    void revealHashOperationStart();

    void showHashOperationStart();

    void showHashOperationResult(HashOperation hashOperation);

    void updateHashOperationProgress(HashOperation hashOperation);

    void showHashOperationError();
}
