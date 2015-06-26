package com.cebesius.materialhash.util.mvp;

public interface Presenter<M extends Model, V extends View> {

    void start();

    void stop();
}
