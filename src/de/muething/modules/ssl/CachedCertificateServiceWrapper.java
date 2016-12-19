package de.muething.modules.ssl;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

import org.parosproxy.paros.security.SslCertificateService;

public class CachedCertificateServiceWrapper implements SslCertificateService {
		private final SslCertificateService delegate;

		public CachedCertificateServiceWrapper(SslCertificateService wrappedService) {
			delegate = wrappedService;
		}

		private Map<String, KeyStore> cache = new HashMap<>();

		@Override
		public synchronized KeyStore createCertForHost(String hostname)
				throws NoSuchAlgorithmException, InvalidKeyException,
				CertificateException, NoSuchProviderException, SignatureException,
				KeyStoreException, IOException, UnrecoverableKeyException {
			if (this.cache.containsKey(hostname)) {
				return this.cache.get(hostname);
			}
			final KeyStore ks = delegate.createCertForHost(hostname);
			this.cache.put(hostname, ks);
			return ks;
		}

		@Override
		public void initializeRootCA(KeyStore keystore) throws KeyStoreException,
				UnrecoverableKeyException, NoSuchAlgorithmException {
			this.delegate.initializeRootCA(keystore);
		}

	}
