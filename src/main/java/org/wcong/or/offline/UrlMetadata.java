package org.wcong.or.offline;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * describe cache data
 */
@Data
public class UrlMetadata implements Serializable {

    private String contentType;

    private String contentEncoding;

    private Map<String, List<String>> headerFields;
}
