package org.wcong.or.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.layout.FlowPane;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * saved meta data
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 03/08/2017
 */
public class SavedFlow extends FlowPane {

    private Logger logger = LoggerFactory.getLogger(SavedFlow.class);

    private Path metadataPath = FileSystems.getDefault().getPath("./metadata");

    private Path defaultImage = FileSystems.getDefault().getPath("./saved.png");

    private final Home home;

    private final Saved saved;

    private final Map<String, Metadata> metadataMap = new HashMap<>();

    private ObjectMapper objectMapper = new ObjectMapper();

    public SavedFlow(Home home, Saved saved) {
        this.home = home;
        this.saved = saved;
        saved.setSavedFlow(this);
        init();
    }

    private void init() {
        if (!Files.exists(metadataPath)) {
            try {
                Files.createFile(metadataPath);
            } catch (IOException e) {
                throw new RuntimeException("create metadata path error", e);
            }
        }
        Metadatas metadatas = null;
        if (Files.exists(metadataPath)) {
            try {
                metadatas = objectMapper.readValue(metadataPath.toFile(), Metadatas.class);
            } catch (IOException e) {
                logger.info("get metadata error " + e);
            }
        }
        if (metadatas != null) {
            for (Metadata metadata : metadatas.metadatas) {
                String imageUrl = metadata.image != null ? metadata.image : defaultImage.toString();
                String labelText = metadata.title != null ? metadata.title : "offline";
                SavedItem savedItem = new SavedItem(imageUrl, labelText);
                savedItem.setOnMouseClicked((event) -> {
                    home.showSaved();
                    saved.loadUrl(metadata);
                });
                getChildren().add(savedItem);
            }
        }
    }

    public void addItem(String url) {
        if (!Files.exists(metadataPath)) {
            return;
        }
        Metadata metadata = metadataMap.get(url);
        if (metadata == null) {
            metadata = new Metadata(url, null, null);
            metadataMap.put(url, metadata);
            saveMetadata();
        }
        final Metadata savedMetadata = metadata;
        SavedItem savedItem = new SavedItem(defaultImage.toString(), "offline");
        savedItem.setOnMouseClicked((event) -> {
            home.showSaved();
            saved.loadUrl(savedMetadata);
        });
        getChildren().add(savedItem);

    }

    public void saveMetadata() {
        try {
            objectMapper.writeValue(metadataPath.toFile(), new Metadatas(new ArrayList<>(metadataMap.values())));
        } catch (IOException e) {
            logger.error("save metadata error", e);
        }
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class Metadatas implements Serializable {
        private List<Metadata> metadatas;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class Metadata implements Serializable {
        private String url;
        private String title;
        private String image;
    }

}
