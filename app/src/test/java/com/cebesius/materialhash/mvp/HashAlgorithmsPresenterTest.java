package com.cebesius.materialhash.mvp;

import com.cebesius.materialhash.domain.HashAlgorithm;
import com.cebesius.materialhash.util.rx.TestRxSchedulers;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
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
public class HashAlgorithmsPresenterTest {

    private HashAlgorithmsPresenter presenter;

    @Mock
    private HashAlgorithmsModel model;
    @Mock
    private HashAlgorithmsView view;

    @Before
    public void setup() {
        presenter = new HashAlgorithmsPresenter(model, view, new TestRxSchedulers());
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
    public void whenStartedItShouldShowHashAlgorithms() {
        when(model.getAvailableHashAlgorithmsObservable()).thenReturn(hashAlgorithmsObservablePlausible);

        presenter.start();

        ArgumentCaptor<List<HashAlgorithm>> hashAlgorithmsCaptor = ArgumentCaptor.forClass(List.class);
        verify(view).showAvailableHashAlgorithms(hashAlgorithmsCaptor.capture());
        Assertions.assertThat(hashAlgorithmsCaptor.getValue())
                .hasSize(2);
    }

    @Test
    public void whenUserSelectedHashAlgorithmItShouldSet() {
        HashAlgorithm hashAlgorithm = mock(HashAlgorithm.class);

        presenter.onUserSelectedHashAlgorithm(hashAlgorithm);

        verify(model).setOperationHashAlgorithm(hashAlgorithm);
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
