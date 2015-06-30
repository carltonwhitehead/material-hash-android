package com.cebesius.materialhash.util;

import com.cebesius.materialhash.domain.HashAlgorithm;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import org.spongycastle.jce.provider.BouncyCastleProvider;

import java.security.MessageDigest;
import java.security.Provider;
import java.security.Security;
import java.util.List;
import java.util.Set;

import rx.Observable;

public class HashAlgorithmsGateway {

    public Observable<List<HashAlgorithm>> observeBuildAvailableHashAlgorithms() {
        return Observable.create(
                subscriber -> subscriber.onNext(buildAvailableHashAlgorithms())
        );
    }

    public List<HashAlgorithm> buildAvailableHashAlgorithms() {
        Provider spongyBouncyCastleProvider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
        Set<Provider.Service> services = spongyBouncyCastleProvider.getServices();
        ImmutableList.Builder<HashAlgorithm> availableHashAlgorithmsBuilder = ImmutableList.builder();
        for (Provider.Service service : services) {
            if (MessageDigest.class.getSimpleName().equals(service.getType())) {
                availableHashAlgorithmsBuilder.add(new HashAlgorithm(service.getAlgorithm()));
            }
        }
        return Ordering.natural().immutableSortedCopy(availableHashAlgorithmsBuilder.build());
    }
}
