package com.cebesius.materialhash.util.rx;

import rx.Scheduler;
import rx.schedulers.Schedulers;

public class TestRxSchedulers implements RxSchedulers {

    @Override
    public Scheduler mainThread() {
        return Schedulers.immediate();
    }

    @Override
    public Scheduler ioThread() {
        return Schedulers.immediate();
    }

    @Override
    public Scheduler computationThread() {
        return Schedulers.immediate();
    }
}
