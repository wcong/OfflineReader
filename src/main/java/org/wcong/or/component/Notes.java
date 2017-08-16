package org.wcong.or.component;

import com.jfoenix.controls.JFXTabPane;
import com.sun.tools.javac.util.List;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * what a notes will do
 * 1 write markdown text
 * 2 save with url
 * 3 display markdown
 *
 * @author wcong<wc19920415@gmail.com>
 * @since 07/08/2017
 */
public class Notes extends Pane {

    private static final Logger logger = LoggerFactory.getLogger(Notes.class);

    private final WebView webView = new WebView();

    private final WebEngine webEngine = webView.getEngine();

    private final TextArea textArea = new TextArea();

    private final Path noteDirectionPath = FileSystems.getDefault().getPath("./notes");

    private final JFXTabPane notePane = new JFXTabPane();
    private final Parser parser = Parser.builder().build();
    private final HtmlRenderer renderer = HtmlRenderer.builder().build();

    private Path notePath;

    private String lastText;

    public Notes() {
        Tab previewTab = new Tab();
        previewTab.setText("preview");
        previewTab.setContent(webView);
        notePane.getTabs().addAll(previewTab);

        Tab editTab = new Tab();
        editTab.setText("edit");
        editTab.setContent(textArea);
        notePane.getTabs().add(editTab);

        getChildren().add(notePane);

        previewTab.setOnSelectionChanged(event -> {
            if (!Files.exists(notePath)) {
                return;
            }
            if (!previewTab.isSelected()) {
                return;
            }
            String text = textArea.getText();
            if (text.isEmpty()) {
                return;
            }
            if (text.equals(lastText)) {
                return;
            }
            try {
                Files.write(notePath, text.getBytes());
            } catch (IOException e) {
                logger.error("write note error", e);
            }
            Node document = parser.parse(text);
            String htmlDocument = renderer.render(document);
            webEngine.loadContent(htmlDocument);
        });

    }

    public void loadNote(String url) {
        notePath = getNotePath(url);
        if (!Files.exists(notePath)) {
            try {
                createPath(notePath);
            } catch (IOException e) {
                logger.error("create path error", e);
            }
        }
        if (Files.exists(notePath)) {
            try {
                String notes = new String(Files.readAllBytes(notePath));
                lastText = notes;
                textArea.setText(notes);
                webEngine.loadContent(renderer.render(parser.parse(notes)));
            } catch (IOException e) {
                logger.error("read file error", e);
            }
        }
    }

    private void createPath(Path path) throws IOException {
        if (!Files.exists(path.getParent())) {
            createDirectory(path.getParent());
        }
    }

    private void createDirectory(Path path) throws IOException {
        if (!Files.exists(path.getParent())) {
            createDirectory(path.getParent());
        }
        Files.createDirectory(path);
    }

    private Path getNotePath(String url) {
        String parseUrlString = url.replaceAll(".*://", "");
        String[] splitUrl = parseUrlString.split("/");
        if (splitUrl.length <= 3) {
            return noteDirectionPath.resolve(parseUrlString);
        } else {
            String prefix = String.join("/", List.of(splitUrl[0], splitUrl[1], splitUrl[2]));
            String encodeUrl = prefix + "/" + String.join("/", Arrays.copyOfRange(splitUrl, 3, splitUrl.length)).hashCode();
            return noteDirectionPath.resolve(encodeUrl);
        }
    }


}
