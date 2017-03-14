package de.muething;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.log4j.Appender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.moxy.json.MoxyJsonConfig;
import org.glassfish.jersey.server.ResourceConfig;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.control.Proxy;
import org.parosproxy.paros.network.SSLConnector;
import org.xml.sax.SAXException;
import org.zaproxy.zap.extension.dynssl.ExtensionDynSSL;
import org.zaproxy.zap.extension.dynssl.SslCertificateUtils;
import org.zaproxy.zap.utils.ClassLoaderUtil;

/**
 * Main class.
 *
 */
public class Main extends ResourceConfig {
	public static final String LOCALPATH_BPROXYUI = "file:///Users/jannis/Developer/BSc/bproxyui/";	
	public static final String BASE_IP = "192.168.2.112";
	public static final String BASE_URI = "http://" + BASE_IP + ":8181/api/";
	
	/**
	 * Starts Grizzly HTTP server exposing JAX-RS resources defined in this
	 * application.
	 * 
	 * @return Grizzly HTTP server.
	 */
	public static HttpServer startServer() {
		// create a resource config that scans for JAX-RS resources and
		// providers
		// in de.muething package
		final ResourceConfig rc = new ResourceConfig().packages("de.muething").register(createMoxyJsonResolver());

		// create and start a new instance of grizzly http server
		// exposing the Jersey application at BASE_URI
		HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc, true);
		server.getListener("grizzly").getFileCache().setEnabled(false);

		return server;
	}

	static {
	//	Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionLogger());

		// set SSLConnector as socketfactory in HttpClient.
		ProtocolSocketFactory sslFactory = null;
		try {
			final Protocol protocol = Protocol.getProtocol("https");
			sslFactory = protocol.getSocketFactory();

		} catch (final IllegalStateException e) {
			// Print the exception - log not yet initialised
			e.printStackTrace();
		}

		if (sslFactory == null || !(sslFactory instanceof SSLConnector)) {
			Protocol.registerProtocol("https", new Protocol("https", (ProtocolSocketFactory) new SSLConnector(), 443));
		}
	}
	
	Main() {
		packages("de.muething.models").register
        (new JsonMoxyConfigurationContextResolver());
	}
	
	
	@Provider
    final static class JsonMoxyConfigurationContextResolver 
    implements ContextResolver<MoxyJsonConfig> {

        @Override
        public MoxyJsonConfig getContext(Class<?> objectType) {
            final MoxyJsonConfig configuration = new MoxyJsonConfig();

            Map<String, String> namespacePrefixMapper = new HashMap<String, String>(1);
            namespacePrefixMapper.put("http://www.w3.org/2001/XMLSchema-instance", "xsi");

            configuration.setNamespacePrefixMapper(namespacePrefixMapper);
            configuration.setNamespaceSeparator(':');

            return configuration;
        }
    }

	private static void initClassLoader() {
		try {
			// lang directory includes all of the language files
			final File langDir = new File(Constant.getZapInstall(), "lang");
			if (langDir.exists() && langDir.isDirectory()) {
				ClassLoaderUtil.addFile(langDir.getAbsolutePath());

			} else {
				System.out.println("Warning: failed to load language files from " + langDir.getAbsolutePath());
			}

			// Load all of the jars in the lib directory
			final File libDir = new File(Constant.getZapInstall(), "lib");
			if (libDir.exists() && libDir.isDirectory()) {
				final File[] files = libDir.listFiles();
				for (final File file : files) {
					if (file.getName().toLowerCase(Locale.ENGLISH).endsWith("jar")) {
						ClassLoaderUtil.addFile(file);
					}
				}

			} else {
				System.out.println("Warning: failed to load jar files from " + libDir.getAbsolutePath());
			}

		} catch (final IOException e) {
			System.out.println("Failed loading jars: " + e);
		}
	}

	static Proxy proxy;
	
	 public static ContextResolver<MoxyJsonConfig> createMoxyJsonResolver() {
	        final MoxyJsonConfig moxyJsonConfig = new MoxyJsonConfig();
	        Map<String, String> namespacePrefixMapper = new HashMap<String, String>(1);
	        namespacePrefixMapper.put("http://www.w3.org/2001/XMLSchema-instance", "xsi");
	        moxyJsonConfig.setNamespacePrefixMapper(namespacePrefixMapper).setNamespaceSeparator(':');
	        return moxyJsonConfig.resolver();
	    }
	
	/**
	 * Main method.
	 * 
	 * @param args
	 * @throws Exception 
	 * @throws SAXException 
	 */
	public static void main(String[] args) throws SAXException, Exception {
		

		
		
		
		final HttpServer server = startServer();
		System.out.println(String.format(
				"Jersey app started with WADL available at " + "%sapplication.wadl\nHit enter to stop it...",
				BASE_URI));

		CLStaticHttpHandler staticHttpHandler = new CLStaticHttpHandler(new URLClassLoader(new URL[] {new URL(LOCALPATH_BPROXYUI)}));
		server.getServerConfiguration().addHttpHandler(staticHttpHandler, "/");
		
		initClassLoader();

//		Model model = new Model();
//		ControlOverrides override = new ControlOverrides();
//		override.setProxyPort(30325);
//		override.setProxyHost("localhost");
//		
//		model.init(override);

		ExtensionDynSSL ssl =  new ExtensionDynSSL();
		
		URL url = Main.class.getResource("root_ca.pem");
		ssl.setRootCa(SslCertificateUtils.pem2Keystore(new File(url.toURI())));
		
	
//		proxy = new Proxy(model, override);
//		listener = new BProxyListener("test", 0);
//		proxy.addProxyListener(listener);
//		proxy.addPersistentConnectionListener(listener);
//		
//		proxy.startServer();
		System.in.read();
		server.stop();
	}

	private static final class UncaughtExceptionLogger implements Thread.UncaughtExceptionHandler {

		private static final Logger logger = Logger.getLogger(UncaughtExceptionLogger.class);

		private static boolean loggerConfigured = false;

		@Override
		public void uncaughtException(Thread t, Throwable e) {
			if (!(e instanceof ThreadDeath)) {
				if (loggerConfigured || isLoggerConfigured()) {
					logger.error("Exception in thread \"" + t.getName() + "\"", e);

				} else {
					System.err.println("Exception in thread \"" + t.getName() + "\"");
					e.printStackTrace();
				}
			}
		}

		private static boolean isLoggerConfigured() {
			if (loggerConfigured) {
				return true;
			}

			@SuppressWarnings("unchecked")
			Enumeration<Appender> appenders = LogManager.getRootLogger().getAllAppenders();
			if (appenders.hasMoreElements()) {
				loggerConfigured = true;
			} else {

				@SuppressWarnings("unchecked")
				Enumeration<Logger> loggers = LogManager.getCurrentLoggers();
				while (loggers.hasMoreElements()) {
					Logger c = loggers.nextElement();
					if (c.getAllAppenders().hasMoreElements()) {
						loggerConfigured = true;
						break;
					}
				}
			}

			return loggerConfigured;
		}
	}
}