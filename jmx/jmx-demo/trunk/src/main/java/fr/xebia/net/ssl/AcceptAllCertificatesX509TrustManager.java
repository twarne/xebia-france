package fr.xebia.net.ssl;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.cxf.common.logging.LogUtils;

/**
 * Deactivate certificate validation. If the certificate is not trusted by the underlying list of trust
 * managers, a SEVERE log message is emitted.
 */
public class AcceptAllCertificatesX509TrustManager implements X509TrustManager {

    private static final Logger LOG = LogUtils.getL7dLogger(AcceptAllCertificatesX509TrustManager.class);

    private TrustManager[] trustManagers;

    public AcceptAllCertificatesX509TrustManager(TrustManager[] trustManagers)
        throws NoSuchAlgorithmException, KeyStoreException {
        super();
        if (trustManagers == null) {
            // load default trust managers
            String defaultAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(defaultAlgorithm);
            trustManagerFactory.init((KeyStore)null);
            this.trustManagers = trustManagerFactory.getTrustManagers();
        } else {
            this.trustManagers = trustManagers;
        }
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        for (TrustManager trustManager : trustManagers) {
            if (trustManager instanceof X509TrustManager) {
                X509TrustManager x509TrustManager = (X509TrustManager)trustManager;
                try {
                    x509TrustManager.checkClientTrusted(chain, authType);
                } catch (CertificateException e) {
                    logCertificationException(chain, e);
                }
            }
        }
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        for (TrustManager trustManager : trustManagers) {
            if (trustManager instanceof X509TrustManager) {
                X509TrustManager x509TrustManager = (X509TrustManager)trustManager;
                try {
                    x509TrustManager.checkServerTrusted(chain, authType);
                } catch (CertificateException e) {
                    logCertificationException(chain, e);
                }
            }
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        List<X509Certificate> acceptedIssuers = new ArrayList<X509Certificate>();
        for (TrustManager trustManager : trustManagers) {
            if (trustManager instanceof X509TrustManager) {
                X509TrustManager x509TrustManager = (X509TrustManager)trustManager;
                for (X509Certificate acceptedIssuer : x509TrustManager.getAcceptedIssuers()) {
                    acceptedIssuers.add(acceptedIssuer);
                }
            }
        }
        return acceptedIssuers.toArray(new X509Certificate[acceptedIssuers.size()]);
    }

    private void logCertificationException(X509Certificate[] chain, CertificateException e) {
        String msg = "DEACTIVATED X509 CERTIFICATE VALIDATION ERROR ! SECURITY IS COMPROMISED ! "
                     + "CERTIFICATE VALIDATION DEACTIVATION SHOULD NOT BE USED IN PRODUCTION !";
        if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.SEVERE, msg, e);
        } else {
            LOG.log(Level.SEVERE, msg + " " + e);
        }

        StringBuilder sb = new StringBuilder();

        if (chain.length == 1) {
            X509Certificate certificate = chain[0];

            String validityError;
            String validityInterval;
            try {
                certificate.checkValidity();
                validityError = "";
                validityInterval = "";
            } catch (CertificateNotYetValidException cnyve) {
                validityError = "not-yet-valid ";
                validityInterval = " (valid from " + certificate.getNotBefore() + " until "
                                   + certificate.getNotAfter() + ")";
            } catch (CertificateExpiredException cee) {
                validityError = "expired ";
                validityInterval = " (valid from " + certificate.getNotBefore() + " until "
                                   + certificate.getNotAfter() + ")";
            }

            if (certificate.getSubjectDN().equals(certificate.getIssuerDN())) {
                sb.append("Untrusted self-signed " + validityError + "certificate: '"
                          + certificate.getSubjectDN() + "'" + validityInterval);
            } else {
                sb.append("Untrusted " + validityError + "certificate: '" + certificate.getSubjectDN()
                          + "' issued by '" + certificate.getIssuerDN() + "'" + validityInterval);
            }
        } else {
            sb.append("Untrusted certificates chain: ");
            for (int i = 0; i < chain.length; i++) {
                X509Certificate certificate = chain[i];
                if (i > 0) {
                    sb.append(", ");
                }

                String validityError;
                String validityInterval;
                try {
                    certificate.checkValidity();
                    validityError = "";
                    validityInterval = "";
                } catch (CertificateNotYetValidException cnyve) {
                    validityError = "not-yet-valid ";
                    validityInterval = " (valid from " + certificate.getNotBefore() + " until "
                                       + certificate.getNotAfter() + ")";
                } catch (CertificateExpiredException cee) {
                    validityError = "expired ";
                    validityInterval = " (valid from " + certificate.getNotBefore() + " until "
                                       + certificate.getNotAfter() + ")";
                }
                if (certificate.getSubjectDN().equals(certificate.getIssuerDN())) {
                    sb.append("self-signed " + validityError + "certificate '" + certificate.getSubjectDN()
                              + "'" + validityInterval);
                } else {
                    sb.append("" + validityError + "certificate '" + certificate.getSubjectDN()
                              + "' issued by '" + certificate.getIssuerDN() + "'" + validityInterval);
                }
            }
        }
        LOG.log(Level.SEVERE, sb.toString());
    }
}
