package de.muething.modules.ssl;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import org.parosproxy.paros.security.SslCertificateService;
import org.parosproxy.paros.security.SslCertificateServiceImpl;
import org.zaproxy.zap.extension.dynssl.SslCertificateUtils;

import de.muething.Main;


/*
 * Creates certificates derived from the trusted root certificate but for a static domain, not the requested domain.
 */
public class StaticHostNameCertficateService extends SslCertificateServiceImpl {
	private String staticHostname = "jannis.co";
	
	private static final SslCertificateService cached;
	
	static {
		StaticHostNameCertficateService service = new StaticHostNameCertficateService("jannis.co");
		
		URL url = Main.class.getResource("root_ca.pem");
		try {
			service.initializeRootCA(SslCertificateUtils.pem2Keystore(new File(url.toURI())));
		} catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException | CertificateException
				| InvalidKeySpecException | IOException | URISyntaxException e) {
			e.printStackTrace();
		}
		
		
		cached = new CachedCertificateServiceWrapper(service);
	}
	
	
	public SslCertificateService getCachedService() {
		return cached;
	}
	
	public StaticHostNameCertficateService(String hostname) {
		super();
		hostname = staticHostname;
	}
	
	@Override
	public KeyStore createCertForHost(String hostname) throws NoSuchAlgorithmException, InvalidKeyException, CertificateException, NoSuchProviderException, SignatureException, KeyStoreException, IOException, UnrecoverableKeyException {
		return super.createCertForHost(staticHostname);
	}
}
