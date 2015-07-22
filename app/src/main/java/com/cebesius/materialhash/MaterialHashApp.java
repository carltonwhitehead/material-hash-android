package com.cebesius.materialhash;

import android.app.Application;

import com.cebesius.materialhash.util.rx.AppRxSchedulers;
import com.cebesius.materialhash.util.rx.RxSchedulers;

import java.security.Security;

public class MaterialHashApp extends Application {

    private static RxSchedulers rxSchedulers;

    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    public static synchronized RxSchedulers getRxSchedulers() {
        if (rxSchedulers == null) {
            rxSchedulers = new AppRxSchedulers();
        }
        return rxSchedulers;
    }
}
