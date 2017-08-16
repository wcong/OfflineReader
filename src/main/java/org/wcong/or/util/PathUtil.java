package org.wcong.or.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 16/08/2017
 */
public class PathUtil {

    private static final Logger logger = LoggerFactory.getLogger(PathUtil.class);

    public static String encodeUrlPath(String url) {
        if (null == url || url.isEmpty()) {
            return url;
        }
        String parseUrlString = url.replaceAll(".*://", "");
        String[] split = parseUrlString.split("/");
        if (split.length > 3) {
            StringBuilder encodeUrl = new StringBuilder();
            encodeUrl.append(String.join("/", split[0], split[1], split[2]));
            String leftUrl = String.join(".", Arrays.copyOfRange(split, 3, split.length));
            // length too long to hash code
            if (leftUrl.length() > 200) {
                return encodeUrl.append(leftUrl.hashCode()).toString();
            }
            try {
                encodeUrl.append(URLEncoder.encode(leftUrl, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                logger.info("encode url error " + e);
                encodeUrl.append(leftUrl);
            }
            return encodeUrl.toString();
        }
        return parseUrlString;
    }

    public static void createParentPath(Path path) throws IOException {
        if (Files.exists(path.getParent())) {
            return;
        }
        createDirectory(path.getParent());
    }

    private static void createDirectory(Path path) throws IOException {
        if (!Files.exists(path.getParent())) {
            createDirectory(path.getParent());
        }
        Files.createDirectory(path);
    }

}
