package com.cebesius.materialhash;

import android.app.Application;

import java.security.Security;

public class MaterialHashApp extends Application {

    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }
}
