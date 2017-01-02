package de.muething.modules.ssl;

import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import org.parosproxy.paros.security.SslCertificateService;
import org.parosproxy.paros.security.SslCertificateServiceImpl;
import org.zaproxy.zap.extension.dynssl.SslCertificateUtils;

public class DynamicHostNameSignedByUntrustedCertificateService extends SslCertificateServiceImpl {
	private static final SslCertificateService cached;

	static {
		DynamicHostNameSignedByUntrustedCertificateService service = null;
		service = new DynamicHostNameSignedByUntrustedCertificateService();

		try {
			service.initializeRootCA(SslCertificateUtils.createRootCA());
		} catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		cached = new CachedCertificateServiceWrapper(service);
	}

	public static SslCertificateService getCachedService() {
		return cached;
	}

	public DynamicHostNameSignedByUntrustedCertificateService() {
		super();
	}
}
