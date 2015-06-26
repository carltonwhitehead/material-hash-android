package com.cebesius.materialhash.util.rx;

import rx.Scheduler;

public interface RxSchedulers {

    Scheduler mainThread();

    Scheduler ioThread();

    Scheduler computationThread();

}
