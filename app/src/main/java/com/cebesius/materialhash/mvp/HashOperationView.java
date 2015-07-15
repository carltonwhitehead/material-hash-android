package com.cebesius.materialhash.mvp;

import com.cebesius.materialhash.domain.entity.File;
import com.cebesius.materialhash.domain.entity.HashAlgorithm;
import com.cebesius.materialhash.util.mvp.View;

import java.util.List;

public interface HashOperationView extends View {

    void showAvailableHashAlgorithms(List<HashAlgorithm> hashAlgorithms);

    void revealAvailableHashAlgorithms(List<HashAlgorithm> hashAlgorithms);

    void showFilePicker();

    void onFileSet(File file);

    void setOperationHashAlgorithm(HashAlgorithm hashAlgorithm);

    void showOperationHashAlgorithmPicker(
        List<HashAlgorithm> availableHashAlgorithms,
        HashAlgorithm operationHashAlgorithm
    );
}
