package com.cebesius.materialhash.mvp;

import com.cebesius.materialhash.domain.HashAlgorithm;
import com.cebesius.materialhash.util.mvp.View;

import java.util.List;

public interface HashAlgorithmsView extends View {

    void showAvailableHashAlgorithms(List<HashAlgorithm> hashAlgorithms);
}
