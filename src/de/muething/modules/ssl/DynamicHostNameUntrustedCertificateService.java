package de.muething.modules.ssl;

import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.parosproxy.paros.security.SslCertificateService;
import org.parosproxy.paros.security.SslCertificateServiceImpl;
import org.zaproxy.zap.extension.dynssl.SslCertificateUtils;

/*
 * Creates self signed certificates for the domain requested
 */
@SuppressWarnings("deprecation")
public class DynamicHostNameUntrustedCertificateService extends SslCertificateServiceImpl {
	private static SslCertificateService cached = null;

	private KeyPair keyPair = generateKeyPair();
	 
	static {
		DynamicHostNameUntrustedCertificateService service;
		try {
			service = new DynamicHostNameUntrustedCertificateService();
			service.initializeRootCA(SslCertificateUtils.createRootCA());
			cached = new CachedCertificateServiceWrapper(service);

		} catch (GeneralSecurityException e1) {
			e1.printStackTrace();
		}
	}
	
	
	public DynamicHostNameUntrustedCertificateService() throws GeneralSecurityException{
		
	}

	public static SslCertificateService getCachedService() {
		return cached;
	}
	
	
	@Override
	public KeyStore createCertForHost(String hostname)
			throws NoSuchAlgorithmException, InvalidKeyException, CertificateException, NoSuchProviderException,
			SignatureException, KeyStoreException, IOException, UnrecoverableKeyException {

		
        // Generate public and private keys and use them to make a self-signed certificate.
  		try {
	        X509Certificate certificate = selfSignedCertificate(keyPair, hostname);

	        final KeyStore ks = KeyStore.getInstance("JKS");
	        ks.load(null, null);
	        final Certificate[] chain = new Certificate[1];
	        chain[0] = certificate;
	        ks.setKeyEntry(ZAPROXY_JKS_ALIAS, keyPair.getPrivate(), PASSPHRASE, chain);
	        return ks;
  		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
		
		return null;
	}

    private KeyPair generateKeyPair() throws GeneralSecurityException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
        keyPairGenerator.initialize(1024, new SecureRandom());
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * Generates a certificate for {@code hostName} containing {@code keyPair}'s
     * public key, signed by {@code keyPair}'s private key.
     */
    private X509Certificate selfSignedCertificate(KeyPair keyPair, String hostName) throws GeneralSecurityException {
        X509V3CertificateGenerator generator = new X509V3CertificateGenerator();
        X500Principal issuer = new X500Principal("CN=" + hostName);
        X500Principal subject = new X500Principal("CN=" + hostName);
        generator.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
        generator.setIssuerDN(issuer);
        generator.setNotBefore(  new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30));
        generator.setNotAfter(new Date(System.currentTimeMillis() + 100*(1000L * 60 * 60 * 24 * 30)));
        generator.setSubjectDN(subject);
        generator.setPublicKey(keyPair.getPublic());
        generator.setSignatureAlgorithm("SHA256WithRSAEncryption");
        return generator.generateX509Certificate(keyPair.getPrivate(), "BC");
    }


}
