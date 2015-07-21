package com.cebesius.materialhash.mvp;

import com.cebesius.materialhash.domain.entity.File;
import com.cebesius.materialhash.domain.entity.HashAlgorithm;
import com.cebesius.materialhash.util.rx.TestRxSchedulers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import rx.Observable;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HashOperationPresenterTest {

    private HashOperationPresenter presenter;

    @Mock
    private HashOperationModel model;
    @Mock
    private HashOperationView view;
    @Mock
    private List<HashAlgorithm> hashAlgorithms;
    @Mock
    private HashAlgorithm hashAlgorithm;
    @Mock
    private File file;

    @Before
    public void setup() {
        presenter = new HashOperationPresenter(model, view, new TestRxSchedulers());
        when(model.getAvailableHashAlgorithmsObservable()).thenReturn(hashAlgorithmsObservableNever);
    }

    @Test
    public void whenStartedItShouldFindAvailableHashAlgorithmsIfUnknown() {
        when(model.hasAvailableHashAlgorithms()).thenReturn(false);

        presenter.start();

        verify(model).hasAvailableHashAlgorithms();
        verify(model).findAvailableHashAlgorithms();
    }

    @Test
    public void whenStartedItShouldNotFindAvailableHashAlgorithmsIfKnown() {
        when(model.hasAvailableHashAlgorithms()).thenReturn(true);

        presenter.start();

        verify(model).hasAvailableHashAlgorithms();
        verify(model, never()).findAvailableHashAlgorithms();
    }

    @Test
    public void whenStartedWithAvailableRevealedHashAlgorithmsItShouldShow() {
        when(model.hasAvailableHashAlgorithms()).thenReturn(true);
        when(model.isAvailableHashAlgorithmsRevealed()).thenReturn(true);
        when(model.getAvailableHashAlgorithms()).thenReturn(hashAlgorithms);

        presenter.start();

        verify(view).showAvailableHashAlgorithms(hashAlgorithms);
    }

    @Test
    public void whenStartedWithOperationHashAlgorithmItShouldShow() {
        when(model.hasAvailableHashAlgorithms()).thenReturn(true);
        when(model.isAvailableHashAlgorithmsRevealed()).thenReturn(true);
        when(model.hasOperationHashAlgorithm()).thenReturn(true);
        when(model.getOperationHashAlgorithm()).thenReturn(hashAlgorithm);

        presenter.start();

        verify(view).setOperationHashAlgorithm(hashAlgorithm);
    }

    @Test
    public void whenUserSelectedHashAlgorithmItShouldSet() {
        presenter.onUserSelectedHashAlgorithm(hashAlgorithm);

        verify(model).setOperationHashAlgorithm(hashAlgorithm);
    }

    @Test
    public void whenAvailableHashAlgorithmsFoundIfHasFileAndNotYetRevealedItShouldShowAvailable() {
        when(model.isAvailableHashAlgorithmsRevealed()).thenReturn(false);
        when(model.hasOperationFile()).thenReturn(true);

        presenter.onAvailableHashAlgorithmsFound(hashAlgorithms);

        verify(view).revealAvailableHashAlgorithms(hashAlgorithms);
        verify(model).setAvailableHashAlgorithmsRevealed(true);
    }

    @Test
    public void whenUserRequestingShowOperationHashAlgorithmPickerItShouldShow() {
        when(model.getAvailableHashAlgorithms()).thenReturn(hashAlgorithms);
        when(model.getOperationHashAlgorithm()).thenReturn(hashAlgorithm);

        presenter.onUserRequestingShowOperationHashAlgorithmPicker();

        verify(view).showOperationHashAlgorithmPicker(hashAlgorithms, hashAlgorithm);
    }

    @Test
    public void whenUserRequestingShowOperationFilePickerItShouldShow() {
        presenter.onUserRequestingShowOperationFilePicker();

        verify(view).showOperationFilePicker();
    }

    @Test
    public void whenUserSelectedFileItShouldSetFileAndPassBackToView() {
        presenter.onUserSelectedOperationFile(file);

        verify(model).setOperationFile(file);
        verify(view).setOperationFile(file);
    }

    @Test
    public void whenUserSelectedFileIfAvailableHashAlgorithmsNotRevealedAndAvailableItShouldShow() {
        when(model.isAvailableHashAlgorithmsRevealed()).thenReturn(false);
        when(model.hasAvailableHashAlgorithms()).thenReturn(true);
        when(model.getAvailableHashAlgorithms()).thenReturn(hashAlgorithms);

        presenter.onUserSelectedOperationFile(file);

        verify(view).revealAvailableHashAlgorithms(hashAlgorithms);
        verify(model).setAvailableHashAlgorithmsRevealed(true);
    }

    private Observable<List<HashAlgorithm>> hashAlgorithmsObservableNever = Observable.never();
    private Observable<List<HashAlgorithm>> hashAlgorithmsObservablePlausible = Observable.create(
            (subscriber -> {
                List<HashAlgorithm> hashAlgorithms = Arrays.asList(
                        new HashAlgorithm("MD5"),
                        new HashAlgorithm("SHA-1")
                );
                subscriber.onNext(hashAlgorithms);
            })
    );
}
