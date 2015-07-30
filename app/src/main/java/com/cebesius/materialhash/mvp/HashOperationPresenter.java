package com.cebesius.materialhash.mvp;

import com.cebesius.materialhash.domain.entity.File;
import com.cebesius.materialhash.domain.entity.HashAlgorithm;
import com.cebesius.materialhash.domain.entity.HashOperation;
import com.cebesius.materialhash.domain.interactor.HashOperationInteractor;
import com.cebesius.materialhash.util.mvp.BasePresenter;
import com.cebesius.materialhash.util.rx.RxSchedulers;
import com.google.common.base.Optional;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Subscription;

public class HashOperationPresenter
    extends BasePresenter<HashOperationModel, HashOperationView> {

    private final HashOperationInteractor hashOperationInteractor;
    private final RxSchedulers rxSchedulers;
    private Subscription availableHashAlgorithmsSubscription;
    private Subscription operationFileChangedSubscription;
    private Subscription operationHashAlgorithmChangedSubscription;
    private Subscription hashOperationSubscription;

    public HashOperationPresenter(
        HashOperationModel model,
        HashOperationView view,
        HashOperationInteractor hashOperationInteractor,
        RxSchedulers rxSchedulers
    ) {
        super(model, view);
        this.hashOperationInteractor = hashOperationInteractor;
        this.rxSchedulers = rxSchedulers;
    }

    @Override
    public void start() {
        availableHashAlgorithmsSubscription = model.getAvailableHashAlgorithmsObservable()
            .observeOn(rxSchedulers.mainThread())
            .subscribeOn(rxSchedulers.computationThread())
            .subscribe(this::onAvailableHashAlgorithmsFound);
        operationFileChangedSubscription = model.getOperationFileObservable()
            .observeOn(rxSchedulers.mainThread())
            .subscribeOn(rxSchedulers.computationThread())
            .subscribe(this::onOperationFileChanged);
        operationHashAlgorithmChangedSubscription = model.getOperationHashAlgorithmObservable()
            .observeOn(rxSchedulers.mainThread())
            .subscribeOn(rxSchedulers.computationThread())
            .subscribe(this::onOperationHashAlgorithmChanged);
        hashOperationSubscription = model.getHashOperationObservable()
            .observeOn(rxSchedulers.mainThread())
            .subscribeOn(rxSchedulers.computationThread())
            .subscribe(this::onHashOperationInProgress);
    }

    void onAvailableHashAlgorithmsFound(Optional<List<HashAlgorithm>> hashAlgorithmsContainer) {
        if (hashAlgorithmsContainer.isPresent()) {
            if (model.getOperationFile().isPresent()) {
                if (!model.isAvailableHashAlgorithmsRevealed()) {
                    view.revealAvailableHashAlgorithms(hashAlgorithmsContainer.get());
                    model.setAvailableHashAlgorithmsRevealed(true);
                } else {
                    view.showAvailableHashAlgorithms(hashAlgorithmsContainer.get());
                }
            }
        } else {
            model.findAvailableHashAlgorithms();
        }
    }

    public void onUserSelectedOperationHashAlgorithm(HashAlgorithm hashAlgorithm) {
        model.setOperationHashAlgorithm(hashAlgorithm);
    }

    void onOperationHashAlgorithmChanged(Optional<HashAlgorithm> hashAlgorithm) {
        if (hashAlgorithm.isPresent()) {
            view.setOperationHashAlgorithm(hashAlgorithm.get());
            if (!model.isHashOperationStartRevealed()) {
                view.revealHashOperationStart();
                model.setHashOperationStartRevealed(true);
            } else {
                view.showHashOperationStart();
            }
        }
    }

    public void onUserRequestingShowOperationFilePicker() {
        view.showOperationFilePicker();
    }

    public void onUserRequestingShowOperationHashAlgorithmPicker() {
        view.showOperationHashAlgorithmPicker(
            model.getAvailableHashAlgorithms().get(),
            model.getOperationHashAlgorithm()
        );
    }

    public void onUserSelectedOperationFile(File file) {
        model.setOperationFile(file);
    }

    void onOperationFileChanged(Optional<File> file) {
        if (file.isPresent()) {
            if (!model.isAvailableHashAlgorithmsRevealed() && model.getAvailableHashAlgorithms().isPresent()) {
                view.revealAvailableHashAlgorithms(model.getAvailableHashAlgorithms().get());
                model.setAvailableHashAlgorithmsRevealed(true);
            }
            view.setOperationFile(file.get());
        }
    }

    public void onUserRequestingStartHashOperation() {
        HashOperation hashOperation = new HashOperation(
            model.getOperationFile().get(),
            model.getOperationHashAlgorithm().get()
        );
        model.setHashOperation(hashOperation);
        hashOperationInteractor.run(hashOperation)
            .observeOn(rxSchedulers.mainThread())
            .subscribeOn(rxSchedulers.ioThread())
            .subscribe(
                hashOperationInProgress -> onHashOperationInProgress(Optional.of(hashOperationInProgress)),
                this::onHashOperationError,
                this::onHashOperationComplete
            );
    }

    void onHashOperationInProgress(Optional<HashOperation> hashOperationOptional) {
        if (hashOperationOptional.isPresent()) {
            HashOperation hashOperation = hashOperationOptional.get();
            if (!hashOperation.hasHash()) {
                view.updateHashOperationProgress(hashOperation);
            } else {
                onHashOperationComplete();
            }
        }
    }

    void onHashOperationError(Throwable e) {
        model.setHashOperation(null);
        view.showHashOperationError();
    }

    void onHashOperationComplete() {
        view.showHashOperationResult(model.getHashOperation().get());
    }

    @Override
    public void stop() {
        availableHashAlgorithmsSubscription.unsubscribe();
        availableHashAlgorithmsSubscription = null;
        operationFileChangedSubscription.unsubscribe();
        operationFileChangedSubscription = null;
        operationHashAlgorithmChangedSubscription.unsubscribe();
        operationHashAlgorithmChangedSubscription = null;
        hashOperationSubscription.unsubscribe();
        hashOperationSubscription = null;
    }
}
