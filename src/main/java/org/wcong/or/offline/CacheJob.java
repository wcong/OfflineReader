package org.wcong.or.offline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/08/2017
 */
public class CacheJob implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(CacheJob.class);

    private final HttpURLConnection delegate;

    private final UrlMetadata urlMetadata;

    private final Path cacheFile;

    private final OfflineCache cache;

    public CacheJob(HttpURLConnection delegate, UrlMetadata urlMetadata, Path cacheFile, OfflineCache cache) {
        this.delegate = delegate;
        this.urlMetadata = urlMetadata;
        this.cacheFile = cacheFile;
        this.cache = cache;
    }

    @Override
    public void run() {
        FileInputStream fileInputStream = null;
        try {
            final int responseCode = delegate.getResponseCode();
            if (responseCode == HTTP_OK) {
                if ("gzip".equals(urlMetadata.getContentEncoding())) {
                    fileInputStream = new FileInputStream(cacheFile.toFile());
                    new GZIPInputStream(fileInputStream);
                }
                cache.saveCachedDataInfo(cacheFile, urlMetadata);
            } else {
                logger.warn("not caching because of response code {} : url {}", responseCode, delegate.getURL());
            }
        } catch (IOException e) {
            logger.error("error happened ", e);
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    logger.warn("close fileInputStream error", e);
                }
            }
        }
    }
}
