package de.muething.modules.ssl;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import org.parosproxy.paros.security.SslCertificateService;
import org.zaproxy.zap.extension.dynssl.SslCertificateUtils;

/*
 * Serves a certificate self signed certificate for a static domain (not for the requested).
 */
public class StaticHostNameUntrustedCertificateService extends DynamicHostNameUntrustedCertificateService {
	private String staticHostname = "jannis.co";

	private static final SslCertificateService cached;

	static {
		StaticHostNameUntrustedCertificateService service = null;
		try {
			service = new StaticHostNameUntrustedCertificateService("jannis.co");
		} catch (GeneralSecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		cached = new CachedCertificateServiceWrapper(service);
	}

	public SslCertificateService getCachedService() {
		return cached;
	}

	public StaticHostNameUntrustedCertificateService(String hostname) throws GeneralSecurityException {
		super();
		hostname = staticHostname;
	}

	@Override
	public KeyStore createCertForHost(String hostname)
			throws NoSuchAlgorithmException, InvalidKeyException, CertificateException, NoSuchProviderException,
			SignatureException, KeyStoreException, IOException, UnrecoverableKeyException {
		return super.createCertForHost(staticHostname);
	}
}
