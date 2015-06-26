package com.cebesius.materialhash.util.mvp;

public abstract class BasePresenter<M extends Model, V extends View> implements Presenter<M, V> {

    protected final M model;
    protected final V view;

    public BasePresenter(M model, V view) {
        this.model = model;
        this.view = view;
    }

    public M getModel() {
        return model;
    }

    public V getView() {
        return view;
    }
}
