package org.wcong.or.offline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Path;
import java.security.Permission;
import java.security.Principal;
import java.security.cert.Certificate;
import java.util.List;
import java.util.Map;

public class HttpsCacheURLConnection extends HttpsURLConnection {

    private static final Logger log = LoggerFactory.getLogger(HttpsCacheURLConnection.class);

    private final HttpsURLConnection delegate;

    private final Path cacheFile;

    private final OfflineCache cache;

    private boolean isReadFromCache = false;

    private InputStream inputStream;

    private UrlMetadata urlMetadata;

    public HttpsCacheURLConnection(OfflineCache cache, HttpsURLConnection delegate)
            throws IOException {
        super(delegate.getURL());
        this.cache = cache;
        this.delegate = delegate;
        this.cacheFile = cache.filenameForURL(delegate.getURL());

        urlMetadata = cache.readCachedDataInfo(cacheFile);
        isReadFromCache = cache.isCached(delegate.getURL()) && null != urlMetadata;
        if (!isReadFromCache) {
            urlMetadata = new UrlMetadata();
        }
    }

    public void connect() throws IOException {
        if (!isReadFromCache) {
            log.info("connect to {}", delegate.getURL().toExternalForm());
            delegate.connect();
        }
    }

    public String getCipherSuite() {
        return delegate.getCipherSuite();
    }

    public Certificate[] getLocalCertificates() {
        return delegate.getLocalCertificates();
    }

    public Certificate[] getServerCertificates() throws SSLPeerUnverifiedException {
        return delegate.getServerCertificates();
    }

    public void addRequestProperty(String key, String value) {
        delegate.addRequestProperty(key, value);
    }

    public Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
        return delegate.getPeerPrincipal();
    }

    public Principal getLocalPrincipal() {
        return delegate.getLocalPrincipal();
    }

    public String getHeaderFieldKey(int n) {
        return delegate.getHeaderFieldKey(n);
    }

    public void setFixedLengthStreamingMode(int contentLength) {
        delegate.setFixedLengthStreamingMode(contentLength);
    }

    public void setFixedLengthStreamingMode(long contentLength) {
        delegate.setFixedLengthStreamingMode(contentLength);
    }

    public void disconnect() {
        if (!isReadFromCache) {
            delegate.disconnect();
        }
    }

    public void setChunkedStreamingMode(int chunklen) {
        delegate.setChunkedStreamingMode(chunklen);
    }

    public String getHeaderField(int n) {
        return delegate.getHeaderField(n);
    }


    public boolean getAllowUserInteraction() {
        return delegate.getAllowUserInteraction();
    }


    public int getConnectTimeout() {
        return isReadFromCache ? 10 : delegate.getConnectTimeout();
    }


    public Object getContent() throws IOException {
        return delegate.getContent();
    }


    public Object getContent(Class[] classes) throws IOException {
        return delegate.getContent(classes);
    }


    public String getContentEncoding() {
        if (!isReadFromCache) {
            urlMetadata.setContentEncoding(delegate.getContentEncoding());
        }
        return urlMetadata.getContentEncoding();
    }


    public int getContentLength() {
        return isReadFromCache ? -1 : delegate.getContentLength();
    }


    public long getContentLengthLong() {
        return isReadFromCache ? -1 : delegate.getContentLengthLong();
    }

    public String getContentType() {
        if (!isReadFromCache) {
            urlMetadata.setContentType(delegate.getContentType());
        }
        return urlMetadata.getContentType();
    }

    public long getDate() {
        return isReadFromCache ? 0 : delegate.getDate();
    }

    public boolean getDefaultUseCaches() {
        return delegate.getDefaultUseCaches();
    }

    public boolean getDoInput() {
        return delegate.getDoInput();
    }

    public boolean getDoOutput() {
        return delegate.getDoOutput();
    }

    public InputStream getErrorStream() {
        return delegate.getErrorStream();
    }

    public long getExpiration() {
        return isReadFromCache ? 0 : delegate.getExpiration();
    }


    public String getHeaderField(String name) {
        return delegate.getHeaderField(name);
    }

    public long getHeaderFieldDate(String name, long Default) {
        return delegate.getHeaderFieldDate(name, Default);
    }

    public int getHeaderFieldInt(String name, int Default) {
        return delegate.getHeaderFieldInt(name, Default);
    }


    public long getHeaderFieldLong(String name, long Default) {
        return delegate.getHeaderFieldLong(name, Default);
    }

    public Map<String, List<String>> getHeaderFields() {
        if (!isReadFromCache) {
            urlMetadata.setHeaderFields(delegate.getHeaderFields());
        }
        return urlMetadata.getHeaderFields();
    }

    public HostnameVerifier getHostnameVerifier() {
        return delegate.getHostnameVerifier();
    }

    public long getIfModifiedSince() {
        return delegate.getIfModifiedSince();
    }


    public InputStream getInputStream() throws IOException {
        if (inputStream != null) {
            return inputStream;
        }
        if (isReadFromCache) {
            inputStream = new FileInputStream(cacheFile.toFile());
        } else {
            CacheFileInputStream wis = new CacheFileInputStream(delegate.getInputStream(), new FileOutputStream(cacheFile.toFile()));
            wis.onInputStreamClose(new CacheJob(delegate, urlMetadata, cacheFile, cache));
            inputStream = wis;
        }
        return inputStream;
    }

    public boolean getInstanceFollowRedirects() {
        return delegate.getInstanceFollowRedirects();
    }

    public long getLastModified() {
        return isReadFromCache ? 0 : delegate.getLastModified();
    }


    public OutputStream getOutputStream() throws IOException {
        return delegate.getOutputStream();
    }


    public Permission getPermission() throws IOException {
        return delegate.getPermission();
    }

    public int getReadTimeout() {
        return delegate.getReadTimeout();
    }

    public String getRequestMethod() {
        return delegate.getRequestMethod();
    }

    public Map<String, List<String>> getRequestProperties() {
        return delegate.getRequestProperties();
    }

    public String getRequestProperty(String key) {
        return delegate.getRequestProperty(key);
    }

    public int getResponseCode() throws IOException {
        return isReadFromCache ? HTTP_OK : delegate.getResponseCode();
    }

    public String getResponseMessage() throws IOException {
        return isReadFromCache ? "OK" : delegate.getResponseMessage();
    }

    public SSLSocketFactory getSSLSocketFactory() {
        return delegate.getSSLSocketFactory();
    }


    public URL getURL() {
        return delegate.getURL();
    }

    public boolean getUseCaches() {
        return delegate.getUseCaches();
    }

    public void setAllowUserInteraction(boolean allowUserInteraction) {
        delegate.setAllowUserInteraction(allowUserInteraction);
    }


    public void setConnectTimeout(int timeout) {
        delegate.setConnectTimeout(timeout);
    }

    public void setDefaultUseCaches(boolean defaultUseCaches) {
        delegate.setDefaultUseCaches(defaultUseCaches);
    }

    public void setDoInput(boolean doInput) {
        delegate.setDoInput(doInput);
    }

    public void setDoOutput(boolean doOutput) {
        delegate.setDoOutput(doOutput);
    }


    public void setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        delegate.setHostnameVerifier(hostnameVerifier);
    }

    public void setIfModifiedSince(long ifModifiedSince) {
        delegate.setIfModifiedSince(ifModifiedSince);
    }

    public void setInstanceFollowRedirects(boolean followRedirects) {
        delegate.setInstanceFollowRedirects(followRedirects);
    }

    public void setReadTimeout(int timeout) {
        delegate.setReadTimeout(timeout);
    }

    public void setRequestMethod(String method) throws ProtocolException {
        delegate.setRequestMethod(method);
    }

    public void setRequestProperty(String key, String value) {
        delegate.setRequestProperty(key, value);
    }

    public void setSSLSocketFactory(SSLSocketFactory sslSocketFactory) {
        delegate.setSSLSocketFactory(sslSocketFactory);
    }

    public void setUseCaches(boolean useCaches) {
        delegate.setUseCaches(useCaches);
    }

    public boolean usingProxy() {
        return delegate.usingProxy();
    }
}
