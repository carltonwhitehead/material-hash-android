package com.cebesius.materialhash.util;

import com.cebesius.materialhash.domain.HashAlgorithm;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import java.security.MessageDigest;
import java.security.Security;
import java.util.List;

import rx.Observable;

public class HashAlgorithmsGateway {

    public Observable<List<HashAlgorithm>> observeBuildAvailableHashAlgorithms() {
        return Observable.create(
                subscriber -> subscriber.onNext(buildAvailableHashAlgorithms())
        );
    }

    public List<HashAlgorithm> buildAvailableHashAlgorithms() {
        List<String> availableHashAlgorithmNames = Lists.newArrayList(
                Security.getAlgorithms(MessageDigest.class.getSimpleName())
        );
        ImmutableList.Builder<HashAlgorithm> availableHashAlgorithmsBuilder = ImmutableList.builder();
        for (String algorithm : availableHashAlgorithmNames) {
            availableHashAlgorithmsBuilder.add(new HashAlgorithm(algorithm));
        }
        return Ordering.natural().immutableSortedCopy(availableHashAlgorithmsBuilder.build());
    }
}
