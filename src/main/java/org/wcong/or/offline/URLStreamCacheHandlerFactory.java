package org.wcong.or.offline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class URLStreamCacheHandlerFactory implements URLStreamHandlerFactory {

    public static final String PROTO_HTTP = "http";
    public static final String PROTO_HTTPS = "https";

    private static final Logger logger = LoggerFactory.getLogger(URLStreamCacheHandlerFactory.class);

    private final OfflineCache cache;

    private Map<String, URLStreamHandler> handlers = new ConcurrentHashMap<>();

    public URLStreamCacheHandlerFactory(OfflineCache cache) {
        this.cache = cache;
        handlers.put(PROTO_HTTP, getURLStreamHandler(PROTO_HTTP));
        handlers.put(PROTO_HTTPS, getURLStreamHandler(PROTO_HTTPS));
    }

    private URLStreamHandler getURLStreamHandler(String protocol) {
        try {
            Method method = URL.class.getDeclaredMethod("getURLStreamHandler", String.class);
            method.setAccessible(true);
            return (URLStreamHandler) method.invoke(null, protocol);
        } catch (Exception e) {
            logger.error("could not access URL.getUrlStreamHandler");
            return null;
        }
    }


    @Override
    public URLStreamHandler createURLStreamHandler(final String protocol) {
        if (null == protocol) {
            throw new IllegalArgumentException("null protocol not allowed");
        }
        logger.info("need to create URLStreamHandler for protocol {}", protocol);

        final String proto = protocol.toLowerCase();
        if (PROTO_HTTP.equals(proto) || PROTO_HTTPS.equals(proto)) {
            return new URLStreamHandler() {
                @Override
                protected URLConnection openConnection(final URL url) throws IOException {
                    logger.info("should open connection to {}", url.toExternalForm());

                    final URLConnection defaultUrlConnection = new URL(protocol, url.getHost(), url.getPort(), url.getFile(), handlers.get(protocol)).openConnection();

                    if (cache.isCached(url)) {
                        // if cached, always use http connection to prevent ssl handshake. As we are reading from the
                        // cache, this is enough
                        return new HttpCacheURLConnection(cache, (HttpURLConnection) defaultUrlConnection);
                    } else {
                        switch (proto) {
                            case PROTO_HTTP:
                                return new HttpCacheURLConnection(cache, (HttpURLConnection) defaultUrlConnection);
                            case PROTO_HTTPS:
                                return new HttpsCacheURLConnection(cache, (HttpsURLConnection) defaultUrlConnection);
                        }
                    }
                    throw new IOException("no matching handler");
                }

                @Override
                protected URLConnection openConnection(final URL u, final Proxy p) throws IOException {
                    logger.info("should open connection to {} via {} ", u.toExternalForm(), p.toString());
                    // URLConnection only has a protected ctor, so we need to go through the URL ctor with the
                    // matching handler to get a default implementation of the needed URLStreamHandler
                    return new URL(protocol, u.getHost(), u.getPort(), u.getFile(), handlers.get(protocol)).openConnection(p);

                }
            };
        }
        // return null to use default ones
        return null;
    }

}
