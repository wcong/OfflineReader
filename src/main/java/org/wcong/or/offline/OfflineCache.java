package org.wcong.or.offline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wcong.or.util.PathUtil;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;

public class OfflineCache {

    private static final Logger logger = LoggerFactory.getLogger(OfflineCache.class.getName());

    private boolean urlStreamHandlerFactoryIsInitialized = false;
    private Path cacheDirectory;
    private String path = "./cache";


    public void init() throws IOException {
        cacheDirectory = FileSystems.getDefault().getPath(path);
        if (!Files.exists(cacheDirectory)) {
            Files.createDirectory(cacheDirectory);
        } else if (!Files.isDirectory(cacheDirectory)) {
            throw new RuntimeException("cache path is exist but not a directory");
        }
        setupURLStreamHandlerFactory();
    }

    static void clearDirectory(final Path path) throws IOException {
        Files.walkFileTree(path, new DeletingFileVisitor(path));
    }

    private void setupURLStreamHandlerFactory() {
        if (urlStreamHandlerFactoryIsInitialized) {
            return;
        }
        URL.setURLStreamHandlerFactory(new URLStreamCacheHandlerFactory(this));
        urlStreamHandlerFactoryIsInitialized = true;
    }


    boolean isCached(URL url) {
        try {
            final Path cacheFile = filenameForURL(url);
            return (Files.exists(cacheFile) && Files.isReadable(cacheFile) && Files.size(cacheFile) > 0);
        } catch (final IOException e) {
            logger.error(e.getMessage());
        }
        return false;
    }


    Path filenameForURL(final URL url) throws UnsupportedEncodingException {
        if (null == cacheDirectory) {
            throw new IllegalStateException("cannot resolve filename for url");
        }
        final String mappedString = Objects.requireNonNull(PathUtil.encodeUrlPath(url.toExternalForm()));
        Path path = cacheDirectory.resolve(mappedString);
        if (!Files.exists(path)) {
            try {
                PathUtil.createParentPath(path);
            } catch (IOException e) {
                logger.info("create path error " + e);
            }
        }
        return path;
    }


    void saveCachedDataInfo(final Path cacheFile, final UrlMetadata urlMetadata) {
        final Path cacheDataFile = Paths.get(cacheFile + ".metadata");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(cacheDataFile.toFile()))) {
            oos.writeObject(urlMetadata);
            oos.flush();
            logger.info("saved metadata " + cacheDataFile);
        } catch (Exception e) {
            logger.error("could not save metadata " + cacheDataFile);
        }
    }

    public UrlMetadata readCachedDataInfo(final Path cacheFile) {
        UrlMetadata cachedDataInfo = null;
        final Path cacheDataFile = Paths.get(cacheFile + ".metadata");
        if (Files.exists(cacheDataFile)) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(cacheDataFile.toFile()))) {
                cachedDataInfo = (UrlMetadata) ois.readObject();
            } catch (Exception e) {
                logger.error("could not read metadata from " + cacheDataFile, e);
            }
        }
        return cachedDataInfo;
    }

    public void clear() throws IOException {
        if (null != cacheDirectory) {
            clearDirectory(cacheDirectory);
        }
    }

    private static class DeletingFileVisitor extends SimpleFileVisitor<Path> {

        private final Path rootDir;

        public DeletingFileVisitor(final Path path) {
            this.rootDir = path;
        }

        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
            if (!attrs.isDirectory()) {
                Files.delete(file);
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
            if (!dir.equals(rootDir)) {
                Files.delete(dir);
            }
            return FileVisitResult.CONTINUE;
        }
    }
}
