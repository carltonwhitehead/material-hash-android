package com.cebesius.materialhash.mvp;

import com.cebesius.materialhash.domain.HashAlgorithm;
import com.cebesius.materialhash.util.mvp.BasePresenter;
import com.cebesius.materialhash.util.rx.RxSchedulers;

import java.util.List;

import rx.Subscription;

public class HashAlgorithmsPresenter extends BasePresenter<
        HashAlgorithmsModel,
        HashAlgorithmsView> {

    private final RxSchedulers rxSchedulers;
    private Subscription availableHashAlgorithmsSubscription;

    public HashAlgorithmsPresenter(
            HashAlgorithmsModel model,
            HashAlgorithmsView view,
            RxSchedulers rxSchedulers
    ) {
        super(model, view);
        this.rxSchedulers = rxSchedulers;
    }

    @Override
    public void start() {
        availableHashAlgorithmsSubscription = model.getAvailableHashAlgorithmsObservable()
                .observeOn(rxSchedulers.mainThread())
                .subscribeOn(rxSchedulers.computationThread())
                .subscribe(this::onAvailableHashAlgorithmsFound);
        if (!model.hasAvailableHashAlgorithms()) {
            model.findAvailableHashAlgorithms();
        }
    }

    void onAvailableHashAlgorithmsFound(List<HashAlgorithm> hashAlgorithms) {
        view.showAvailableHashAlgorithms(hashAlgorithms);
    }

    public void onUserSelectedHashAlgorithm(HashAlgorithm hashAlgorithm) {
        boolean hasOperationHashAlgorithm = model.hasOperationHashAlgorithm();
        model.setOperationHashAlgorithm(hashAlgorithm);
        if (hashAlgorithm != null) {
            // user selected an algorithm
        }
    }

    @Override
    public void stop() {
        availableHashAlgorithmsSubscription.unsubscribe();
        availableHashAlgorithmsSubscription = null;
    }
}
