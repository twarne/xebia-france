package fr.xebia.net.ssl;

import java.security.KeyStore;
import java.security.Principal;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.junit.Test;

import sun.security.x509.X500Name;

public class DefaultTrustManagerDemo {

    public static void main(String[] args) throws Exception {

TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory
    .getDefaultAlgorithm());

trustManagerFactory.init((KeyStore)null);

System.out.println("JVM Default Trust Managers");
for (TrustManager trustManager : trustManagerFactory.getTrustManagers()) {
    System.out.println(trustManager);

    if (trustManager instanceof X509TrustManager) {
        X509TrustManager x509TrustManager = (X509TrustManager)trustManager;
        System.out.println("\tAccepted issuers count : " + x509TrustManager.getAcceptedIssuers().length);
    }
}

    }
}
