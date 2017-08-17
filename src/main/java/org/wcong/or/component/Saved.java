package org.wcong.or.component;

import com.jfoenix.controls.JFXButton;
import javafx.concurrent.Worker;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 06/08/2017
 */
public class Saved extends HBox {

    private final WebView webView = new WebView();

    private final WebEngine webEngine = webView.getEngine();

    private final WebHistory webHistory = webEngine.getHistory();

    private final JFXButton backButton = new JFXButton("<");

    private final JFXButton forwardButton = new JFXButton(">");

    private final VBox webViewBox = new VBox();

    private final Notes notes = new Notes();
    private final Separator separator = new Separator();

    public Saved() {
        HBox buttonBox = new HBox();
        buttonBox.getChildren().addAll(backButton, forwardButton);
        webViewBox.getChildren().addAll(buttonBox, webView);
        separator.setOrientation(Orientation.VERTICAL);
        getChildren().addAll(webViewBox, separator, notes);
        backButton.setOnMouseClicked(event -> {
            int currentIndex = webHistory.getCurrentIndex();
            if (currentIndex <= 0) {
                return;
            }
            webHistory.go(-1);
            notes.loadNote(webHistory.getEntries().get(currentIndex - 1).getUrl());
        });
        forwardButton.setOnMouseClicked(event -> {
            int currentIndex = webHistory.getCurrentIndex();
            int nextIndex = currentIndex + 1;
            if (nextIndex >= webHistory.getEntries().size()) {
                return;
            }
            webHistory.go(1);
            notes.loadNote(webHistory.getEntries().get(nextIndex).getUrl());
        });

        webEngine.getLoadWorker().stateProperty().addListener((listener, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED
                    || newValue == Worker.State.CANCELLED
                    || newValue == Worker.State.FAILED) {
                notes.loadNote(webEngine.getLocation());
            }
        });
    }

    @Override
    public void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        double webWidth = w / 10 * 7;
        layoutInArea(webViewBox, 0, 0, webWidth, h, 0, HPos.CENTER, VPos.CENTER);
        layoutInArea(notes, webWidth, 0, w - webWidth, h, 0, HPos.CENTER, VPos.CENTER);
    }

    public void loadUrl(String url) {
        webEngine.load(url);
    }
}
