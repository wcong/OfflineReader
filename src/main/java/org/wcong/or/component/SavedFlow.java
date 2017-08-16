package org.wcong.or.component;

import javafx.scene.layout.FlowPane;
import lombok.Data;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.logging.Logger;

/**
 * saved meta data
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 03/08/2017
 */
public class SavedFlow extends FlowPane {

    private Logger logger = Logger.getLogger(SavedFlow.class.getName());

    private Path metadataPath = FileSystems.getDefault().getPath("./metadata");

    private Path defaultImage = FileSystems.getDefault().getPath("./saved.png");

    private final Home home;

    private final Saved saved;

    public SavedFlow(Home home, Saved saved) {
        this.home = home;
        this.saved = saved;
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
        List<String> metadataList = null;
        if (Files.exists(metadataPath)) {
            try {
                metadataList = Files.readAllLines(metadataPath);
            } catch (IOException e) {
                logger.info("get metadata error " + e);
            }
        }
        if (metadataList != null) {
            for (String metadata : metadataList) {
                String[] split = metadata.split(";");
                String url = split[0];
                String imageUrl = split.length > 1 ? split[1] : defaultImage.toString();
                String labelText = split.length > 2 ? split[2] : "offline";
                SavedItem savedItem = new SavedItem(imageUrl, labelText);
                savedItem.setOnMouseClicked((event) -> {
                    home.showSaved();
                    saved.loadUrl(url);
                });
                getChildren().add(savedItem);
            }
        }
    }

    public void addItem(String url) {
        if (!Files.exists(metadataPath)) {
            return;
        }
        try {
            Files.write(metadataPath, (url + "\n").getBytes(), StandardOpenOption.APPEND);
            SavedItem savedItem = new SavedItem(defaultImage.toString(), "offline");
            savedItem.setOnMouseClicked((event) -> {
                home.showSaved();
                saved.loadUrl(url);
            });
            getChildren().add(savedItem);
        } catch (IOException e) {
            logger.info("add url error");
        }
    }

    @Data
    static class Metadata implements Serializable {
        private String url;
        private String title;
        private String image;
    }

}
