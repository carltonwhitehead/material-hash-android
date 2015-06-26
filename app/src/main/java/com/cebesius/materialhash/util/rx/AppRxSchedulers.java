package com.cebesius.materialhash.util.rx;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AppRxSchedulers implements RxSchedulers {

    @Override
    public Scheduler mainThread() {
        return AndroidSchedulers.mainThread();
    }

    @Override
    public Scheduler ioThread() {
        return Schedulers.io();
    }

    @Override
    public Scheduler computationThread() {
        return Schedulers.computation();
    }
}
