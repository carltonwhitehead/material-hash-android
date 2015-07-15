package com.cebesius.materialhash.util;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public class BusSingleton {

    private static Bus bus;

    public static synchronized Bus getInstance() {
        if (bus == null) {
            bus = new Bus(ThreadEnforcer.MAIN);
        }
        return bus;
    }
}
