package Redmine.redmineapi;

import Redmine.redmineapi.internal.Transport;
import Redmine.redmineapi.internal.URIConfigurator;
import Redmine.redmineapi.internal.comm.betterssl.BetterSSLFactory;
import Redmine.redmineapi.internal.comm.naivessl.NaiveSSLFactory;
import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.Collection;
import java.util.List;

/**
 * <strong>Entry point</strong> for the API. Use this class to communicate with Redmine servers.
 * <p>
 * Collection of creation methods for the redmine. Method number may grow as
 * grows number of requirements. However, having all creation methods in one
 * place allows us to refactor RemineManager internals without changing this
 * external APIs. Moreover, we can create "named constructor" for redmine
 * instances. This will allow us to have many construction methods with the same
 * signature.
 * <p>
 * Sample usage:
 * <pre>
 RedmineManager redmineManager = RedmineManagerFactory.createWithUserAuth(redmineURI, login, password);
 * </pre>
 *
 * @see Redmine.redmineapi.RedmineManager
 */
public final class RedmineManagerFactory {
    /**
     * Prevent construction of this object even with use of dirty tricks.
     */
    private RedmineManagerFactory() {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates a non-authenticating redmine manager.
     *
     * @param uri redmine manager URI.
     */
    public static Redmine.redmineapi.RedmineManager createUnauthenticated(String uri) {
        return createUnauthenticated(uri, createDefaultHttpClient(uri));
    }

    /**
     * Creates a non-authenticating redmine manager.
     *
     * @param uri    redmine manager URI.
     * @param httpClient you can provide your own pre-configured HttpClient if you want
     *                     to control connection pooling, manage connections eviction, closing, etc.
     */
    public static Redmine.redmineapi.RedmineManager createUnauthenticated(String uri,
                                                                                  HttpClient httpClient) {
        return createWithUserAuth(uri, null, null, httpClient);
    }

    /**
     * Creates an instance of RedmineManager class. Host and apiAccessKey are
     * not checked at this moment.
     *  @param uri          complete Redmine server web URI, including protocol and port
     *                     number. Example: http://demo.redmine.org:8080
     * @param apiAccessKey Redmine API access key. It is shown on "My Account" /
     *                     "API access key" webpage (check
     *                     <i>http://redmine_server_url/my/account</i> URL). This
     *                     parameter is <strong>optional</strong> (can be set to NULL) for Redmine
     */
    public static RedmineManager createWithApiKey(String uri,
                                                  String apiAccessKey) {
        return createWithApiKey(uri, apiAccessKey,
                createDefaultHttpClient(uri));
    }

    /**
     * Creates an instance of RedmineManager class. Host and apiAccessKey are
     * not checked at this moment.
     *
     * @param uri          complete Redmine server web URI, including protocol and port
     *                     number. Example: http://demo.redmine.org:8080
     * @param apiAccessKey Redmine API access key. It is shown on "My Account" /
     *                     "API access key" webpage (check
     *                     <i>http://redmine_server_url/my/account</i> URL). This
     *                     parameter is <strong>optional</strong> (can be set to NULL) for Redmine
     *                     projects, which are "public".
     * @param httpClient   Http Client. you can provide your own pre-configured HttpClient if you want
     *                     to control connection pooling, manage connections eviction, closing, etc.
     */
    public static Redmine.redmineapi.RedmineManager createWithApiKey(String uri,
                                                                             String apiAccessKey, HttpClient httpClient) {
        return new Redmine.redmineapi.RedmineManager(new Transport(new URIConfigurator(uri,
                apiAccessKey), httpClient));
    }

    /**
     * Creates a new RedmineManager with user-based authentication.
     *
     * @param uri      redmine manager URI.
     * @param login    user's name.
     * @param password user's password.
     */
    public static Redmine.redmineapi.RedmineManager createWithUserAuth(String uri, String login,
                                                                               String password) {
        return createWithUserAuth(uri, login, password,
                createDefaultHttpClient(uri));
    }

    /**
     * Creates a new redmine managen with user-based authentication.
     *
     * @param uri      redmine manager URI.
     * @param login    user's name.
     * @param password user's password.
     * @param httpClient you can provide your own pre-configured HttpClient if you want
     *                     to control connection pooling, manage connections eviction, closing, etc.
     */
    public static Redmine.redmineapi.RedmineManager createWithUserAuth(String uri, String login,
                                                                               String password, HttpClient httpClient) {
        final Transport transport = new Transport(
                new URIConfigurator(uri, null), httpClient);
        transport.setCredentials(login, password);
        return new RedmineManager(transport);
    }

    /**
     * Creates default insecure connection manager.
     *
     * @return default insecure connection manager.
     * @deprecated Use better key-managed factory with additional keystores.
     */
    @Deprecated
    public static ClientConnectionManager createInsecureConnectionManager() {
        return createConnectionManager(NaiveSSLFactory.createNaiveSSLSocketFactory());
    }
    
    /**
     * Creates a connection manager with extended trust relations. It would 
     * use both default system trusted certificates as well as all certificates
     * defined in the <code>trustStores</code>.
     * @param trustStores list of additional trust stores.
     * @return connection manager with extended trust relationship.
     */
    public static ClientConnectionManager createConnectionManagerWithExtraTrust(Collection<KeyStore> trustStores) throws KeyManagementException, KeyStoreException {
    	return createConnectionManager(BetterSSLFactory.createSocketFactory(trustStores));
    }

    /**
     * Creates default connection manager.
     */
    public static ClientConnectionManager createDefaultConnectionManager() {
        return createConnectionManager(SSLSocketFactory.getSocketFactory());
    }

    /**
     * Creates system default connection manager. Takes in account system
     * properties for SSL configuration.
     *
     * @return default insecure connection manager.
     */
    public static ClientConnectionManager createSystemDefaultConnectionManager() {
        return createConnectionManager(SSLSocketFactory.getSystemSocketFactory());
    }

    public static ClientConnectionManager createConnectionManager(SSLSocketFactory sslSocketFactory) {
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
        registry.register(new Scheme("https", 443, sslSocketFactory));
        return new BasicClientConnectionManager(registry);
    }

    public static HttpClient createDefaultHttpClient(String uri) {
        try {
            return getNewHttpClient(uri, createSystemDefaultConnectionManager());
        } catch (Exception e) {
            e.printStackTrace();
            return new DefaultHttpClient();
        }
    }

    /**
     * Helper method to create an http client from connection manager. This new
     * client is configured to use system proxy (if any).
     */
    public static HttpClient getNewHttpClient(String uri, ClientConnectionManager connectionManager) {
        try {

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            final DefaultHttpClient result = new DefaultHttpClient(
                    connectionManager, params);
            configureProxy(uri, result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new DefaultHttpClient();
        }
    }

    private static void configureProxy(String uri, DefaultHttpClient httpclient) {
        String proxyHost = System.getProperty("http.proxyHost");
        String proxyPort = System.getProperty("http.proxyPort");
        if (proxyHost != null && proxyPort != null) {

            //check standard java nonProxyHost
            List<Proxy> proxyList= null;
            try {
                proxyList = ProxySelector.getDefault().select(new URI(uri));
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }

            if( proxyList != null && proxyList.get(0) == Proxy.NO_PROXY ){
                //use no proxy for this host
                return;
            }

            int port;
            try {
                port = Integer.parseInt(proxyPort);
            } catch (NumberFormatException e) {
                throw new RedmineConfigurationException("Illegal proxy port "
                        + proxyPort, e);
            }
            HttpHost proxy = new HttpHost(proxyHost, port);
            httpclient.getParams().setParameter(
                    org.apache.http.conn.params.ConnRoutePNames.DEFAULT_PROXY,
                    proxy);
            String proxyUser = System.getProperty("http.proxyUser");
            if (proxyUser != null) {
                String proxyPassword = System.getProperty("http.proxyPassword");
                httpclient.getCredentialsProvider().setCredentials(
                        new AuthScope(proxyHost, port),
                        new UsernamePasswordCredentials(proxyUser,
                                proxyPassword));
            }
        }
    }
}
