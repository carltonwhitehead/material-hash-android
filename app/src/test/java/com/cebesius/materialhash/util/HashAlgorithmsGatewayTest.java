package com.cebesius.materialhash.util;

import com.cebesius.materialhash.domain.entity.HashAlgorithm;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.spongycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class HashAlgorithmsGatewayTest {

    private HashAlgorithmsGateway subject;

    @BeforeClass
    public static void beforeClass() {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    @AfterClass
    public static void afterClass() {
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
    }

    @Before
    public void setup() {
        subject = new HashAlgorithmsGateway();
    }

    @Test
    public void whenBuildAvailableHashAlgorithmsItShouldBuild() {
        List<HashAlgorithm> actual = subject.buildAvailableHashAlgorithms();

        assertThat(actual)
                .isNotEmpty()
                .isSorted();
    }

}
