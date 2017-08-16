package org.wcong.or.offline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Path;
import java.security.Permission;
import java.util.List;
import java.util.Map;

public class HttpCacheURLConnection extends HttpURLConnection {

    private static final Logger logger = LoggerFactory.getLogger(HttpCacheURLConnection.class);


    private final HttpURLConnection delegate;

    private final Path cacheFile;

    private final OfflineCache cache;

    private boolean readFromCache = false;

    private InputStream inputStream;

    private UrlMetadata urlMetadata;

    private HttpCacheURLConnection(URL u) {
        super(u);
        this.cache = null;
        this.delegate = null;
        this.cacheFile = null;
    }

    public HttpCacheURLConnection(OfflineCache cache, HttpURLConnection delegate) throws IOException {
        super(delegate.getURL());
        this.cache = cache;
        this.delegate = delegate;
        this.cacheFile = cache.filenameForURL(delegate.getURL());

        urlMetadata = cache.readCachedDataInfo(cacheFile);
        readFromCache = cache.isCached(delegate.getURL()) && null != urlMetadata;
        if (!readFromCache) {
            urlMetadata = new UrlMetadata();
        }
    }

    public void connect() throws IOException {
        if (!readFromCache) {
            delegate.connect();
        }
    }

    public void addRequestProperty(String key, String value) {
        delegate.addRequestProperty(key, value);
    }

    public String getHeaderFieldKey(int n) {
        return delegate.getHeaderFieldKey(n);
    }

    public void setFixedLengthStreamingMode(int contentLength) {
        delegate.setFixedLengthStreamingMode(contentLength);
    }

    public void disconnect() {
        if (!readFromCache) {
            delegate.disconnect();
        }
    }

    public void setFixedLengthStreamingMode(long contentLength) {
        delegate.setFixedLengthStreamingMode(contentLength);
    }

    public boolean getAllowUserInteraction() {
        return delegate.getAllowUserInteraction();
    }

    public void setChunkedStreamingMode(int chunklen) {
        delegate.setChunkedStreamingMode(chunklen);
    }

    public int getConnectTimeout() {
        return readFromCache ? 10 : delegate.getConnectTimeout();
    }

    public String getHeaderField(int n) {
        return delegate.getHeaderField(n);
    }

    public Object getContent() throws IOException {
        return delegate.getContent();
    }

    public Object getContent(Class[] classes) throws IOException {
        return delegate.getContent(classes);
    }

    public String getContentEncoding() {
        if (!readFromCache) {
            urlMetadata.setContentEncoding(delegate.getContentEncoding());
        }
        return urlMetadata.getContentEncoding();
    }

    public int getContentLength() {
        return readFromCache ? -1 : delegate.getContentLength();
    }

    public long getContentLengthLong() {
        return readFromCache ? -1 : delegate.getContentLengthLong();
    }

    public String getContentType() {
        if (!readFromCache) {
            urlMetadata.setContentType(delegate.getContentType());
        }
        return urlMetadata.getContentType();
    }

    public long getDate() {
        return readFromCache ? 0 : delegate.getDate();
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
        return readFromCache ? 0 : delegate.getExpiration();
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
        if (!readFromCache) {
            urlMetadata.setHeaderFields(delegate.getHeaderFields());
        }
        return urlMetadata.getHeaderFields();
    }

    public long getIfModifiedSince() {
        return delegate.getIfModifiedSince();
    }

    public InputStream getInputStream() throws IOException {
        if (inputStream != null) {
            return inputStream;
        }
        if (readFromCache) {
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
        return readFromCache ? 0 : delegate.getLastModified();
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
        return readFromCache ? HTTP_OK : delegate.getResponseCode();
    }

    public String getResponseMessage() throws IOException {
        return readFromCache ? "OK" : delegate.getResponseMessage();
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

    public void setUseCaches(boolean useCaches) {
        delegate.setUseCaches(useCaches);
    }

    public boolean usingProxy() {
        return delegate.usingProxy();
    }
}
