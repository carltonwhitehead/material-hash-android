package com.cebesius.materialhash.mvp;

import com.cebesius.materialhash.domain.entity.File;
import com.cebesius.materialhash.domain.entity.HashAlgorithm;
import com.cebesius.materialhash.util.mvp.BasePresenter;
import com.cebesius.materialhash.util.rx.RxSchedulers;

import java.util.List;

import rx.Subscription;

public class HashOperationPresenter
    extends BasePresenter<HashOperationModel, HashOperationView> {

    private final RxSchedulers rxSchedulers;
    private Subscription availableHashAlgorithmsSubscription;

    public HashOperationPresenter(
        HashOperationModel model,
        HashOperationView view,
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
        } else if (model.isAvailableHashAlgorithmsRevealed()) {
            view.showAvailableHashAlgorithms(model.getAvailableHashAlgorithms());
            if (model.hasOperationHashAlgorithm()) {
                view.setOperationHashAlgorithm(model.getOperationHashAlgorithm());
            }
        }
    }

    void onAvailableHashAlgorithmsFound(List<HashAlgorithm> hashAlgorithms) {
        if (!model.isAvailableHashAlgorithmsRevealed() && model.hasOperationFile()) {
            view.revealAvailableHashAlgorithms(hashAlgorithms);
            model.setAvailableHashAlgorithmsRevealed(true);
        }
    }

    public void onUserSelectedHashAlgorithm(HashAlgorithm hashAlgorithm) {
        model.setOperationHashAlgorithm(hashAlgorithm);
        view.setOperationHashAlgorithm(hashAlgorithm);
    }

    public void onUserRequestingShowOperationFilePicker() {
        view.showOperationFilePicker();
    }

    public void onUserRequestingShowOperationHashAlgorithmPicker() {
        view.showOperationHashAlgorithmPicker(
            model.getAvailableHashAlgorithms(),
            model.getOperationHashAlgorithm()
        );
    }

    public void onUserSelectedOperationFile(File file) {
        model.setOperationFile(file);
        if (!model.isAvailableHashAlgorithmsRevealed() && model.hasAvailableHashAlgorithms()) {
            view.revealAvailableHashAlgorithms(model.getAvailableHashAlgorithms());
            model.setAvailableHashAlgorithmsRevealed(true);
        }
        view.setOperationFile(file);
    }

    @Override
    public void stop() {
        availableHashAlgorithmsSubscription.unsubscribe();
        availableHashAlgorithmsSubscription = null;
    }
}
