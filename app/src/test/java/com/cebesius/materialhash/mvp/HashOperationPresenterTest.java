package com.cebesius.materialhash.mvp;

import com.cebesius.materialhash.domain.entity.File;
import com.cebesius.materialhash.domain.entity.HashAlgorithm;
import com.cebesius.materialhash.domain.entity.HashOperation;
import com.cebesius.materialhash.domain.interactor.HashOperationInteractor;
import com.cebesius.materialhash.util.rx.TestRxSchedulers;
import com.google.common.base.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import rx.Observable;

import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HashOperationPresenterTest {

    private Observable<Optional<List<HashAlgorithm>>> hashAlgorithmsObservable = Observable.never();
    private Observable<Optional<File>> operationFileObservable = Observable.never();
    private Observable<Optional<HashAlgorithm>> operationHashAlgorithmObservable = Observable.never();
    private Observable<Optional<HashOperation>> hashOperationObservable = Observable.never();

    private HashOperationPresenter presenter;
    @Mock
    private HashOperationModel model;
    @Mock
    private HashOperationView view;
    @Mock
    private HashOperationInteractor hashOperationInteractor;

    @Mock
    private Optional<List<HashAlgorithm>> hashAlgorithmsOptional;
    @Mock
    private List<HashAlgorithm> hashAlgorithms;
    @Mock
    private Optional<HashAlgorithm> hashAlgorithmOptional;
    @Mock
    private HashAlgorithm hashAlgorithm;
    @Mock
    private Optional<File> fileOptional;
    @Mock
    private File file;
    @Mock
    private Optional<HashOperation> hashOperationOptional;
    @Mock
    private  HashOperation hashOperation;

    @Before
    public void setup() {
        presenter = new HashOperationPresenter(model, view, hashOperationInteractor, new TestRxSchedulers());
        when(model.getAvailableHashAlgorithmsObservable()).thenReturn(hashAlgorithmsObservable);
        when(model.getOperationFileObservable()).thenReturn(operationFileObservable);
        when(model.getOperationHashAlgorithmObservable()).thenReturn(operationHashAlgorithmObservable);
        when(model.getHashOperationObservable()).thenReturn(hashOperationObservable);
        when(model.getAvailableHashAlgorithms()).thenReturn(hashAlgorithmsOptional);
        when(model.getOperationFile()).thenReturn(fileOptional);
        when(model.getOperationHashAlgorithm()).thenReturn(hashAlgorithmOptional);
        when(model.getHashOperation()).thenReturn(hashOperationOptional);
    }

    @Test
    public void whenUserSelectedOperationHashAlgorithmItShouldSet() {
        presenter.onUserSelectedOperationHashAlgorithm(hashAlgorithm);

        verify(model).setOperationHashAlgorithm(hashAlgorithm);
    }

    @Test
    public void whenOperationHashAlgorithmChangedItShouldPassToView() {
        when(hashAlgorithmOptional.isPresent()).thenReturn(true);
        when(hashAlgorithmOptional.get()).thenReturn(hashAlgorithm);

        presenter.onOperationHashAlgorithmChanged(hashAlgorithmOptional);

        verify(view).setOperationHashAlgorithm(hashAlgorithm);
    }

    @Test
    public void whenOperationHashAlgorithmChangedItShouldShowHashOperationStartItNotYetRevealed() {
        when(hashAlgorithmOptional.isPresent()).thenReturn(true);
        when(model.isHashOperationStartRevealed()).thenReturn(true);

        presenter.onOperationHashAlgorithmChanged(hashAlgorithmOptional);

        verify(view).showHashOperationStart();
    }

    @Test
    public void whenOperationHashAlgorithmChangedOperationStartNotYetRevealedItShouldReveal() {
        when(hashAlgorithmOptional.isPresent()).thenReturn(true);
        when(model.isHashOperationStartRevealed()).thenReturn(false);

        presenter.onOperationHashAlgorithmChanged(hashAlgorithmOptional);

        verify(view).revealHashOperationStart();
        verify(model).setHashOperationStartRevealed(true);
    }

    @Test
    public void whenAvailableHashAlgorithmsFoundIfHasFileAndNotYetRevealedItShouldShowAvailable() {
        when(hashAlgorithmsOptional.isPresent()).thenReturn(true);
        when(fileOptional.isPresent()).thenReturn(true);
        when(hashAlgorithmsOptional.get()).thenReturn(hashAlgorithms);
        when(model.isAvailableHashAlgorithmsRevealed()).thenReturn(false);

        presenter.onAvailableHashAlgorithmsFound(hashAlgorithmsOptional);

        verify(view).revealAvailableHashAlgorithms(hashAlgorithms);
        verify(model).setAvailableHashAlgorithmsRevealed(true);
    }

    @Test
    public void whenAvailableHashAlgorithmsFoundAbsentItShouldFind() {
        when(hashAlgorithmsOptional.isPresent()).thenReturn(false);

        presenter.onAvailableHashAlgorithmsFound(hashAlgorithmsOptional);

        verify(model).findAvailableHashAlgorithms();
    }

    @Test
    public void whenAvailableHashAlgorithmsFoundPresentRevealedItShouldShow() {
        when(hashAlgorithmsOptional.isPresent()).thenReturn(true);
        when(fileOptional.isPresent()).thenReturn(true);
        when(model.isAvailableHashAlgorithmsRevealed()).thenReturn(true);
        when(hashAlgorithmsOptional.get()).thenReturn(hashAlgorithms);

        presenter.onAvailableHashAlgorithmsFound(hashAlgorithmsOptional);

        verify(view).showAvailableHashAlgorithms(hashAlgorithms);
    }

    @Test
    public void whenUserRequestingShowOperationHashAlgorithmPickerItShouldShow() {
        when(hashAlgorithmsOptional.get()).thenReturn(hashAlgorithms);

        presenter.onUserRequestingShowOperationHashAlgorithmPicker();

        verify(view).showOperationHashAlgorithmPicker(hashAlgorithms, hashAlgorithmOptional);
    }

    @Test
    public void whenUserRequestingShowOperationFilePickerItShouldShow() {
        presenter.onUserRequestingShowOperationFilePicker();

        verify(view).showOperationFilePicker();
    }

    @Test
    public void whenUserSelectedOperationFileItShouldSetFile() {
        presenter.onUserSelectedOperationFile(file);

        verify(model).setOperationFile(file);
    }

    @Test
    public void whenOperationFileChangedIfAvailableHashAlgorithmsNotRevealedAndAvailableItShouldShow() {
        when(fileOptional.isPresent()).thenReturn(true);
        when(model.isAvailableHashAlgorithmsRevealed()).thenReturn(false);
        when(hashAlgorithmsOptional.isPresent()).thenReturn(true);
        when(hashAlgorithmsOptional.get()).thenReturn(hashAlgorithms);

        presenter.onOperationFileChanged(fileOptional);

        verify(view).revealAvailableHashAlgorithms(hashAlgorithms);
        verify(model).setAvailableHashAlgorithmsRevealed(true);
    }

    @Test
    public void whenHashOperationInProgressPresentWithoutHashItShouldNotifyView() {
        when(hashOperationOptional.isPresent()).thenReturn(true);
        when(hashOperationOptional.get()).thenReturn(hashOperation);
        when(hashOperation.hasHash()).thenReturn(false);

        presenter.onHashOperationInProgress(hashOperationOptional);

        verify(view).updateHashOperationProgress(hashOperation);
    }

    @Test
    public void whenHashOperationErrorItShouldNotifyView() {
        Throwable throwable = mock(Throwable.class);

        presenter.onHashOperationError(throwable);

        verify(model).setHashOperation(null);
        verify(view).showHashOperationError();
    }

    @Test
    public void whenHashOperationCompleteItShouldShowHashOperationResult() {
        when(hashOperationOptional.get()).thenReturn(hashOperation);

        presenter.onHashOperationComplete();

        verify(view).showHashOperationResult(hashOperation);
    }
}
