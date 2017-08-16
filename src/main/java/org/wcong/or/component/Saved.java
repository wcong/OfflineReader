package org.wcong.or.component;

import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 06/08/2017
 */
public class Saved extends HBox {

    // TODO add history

    private final WebView webView = new WebView();

    private final WebEngine webEngine = webView.getEngine();

    private final Notes notes = new Notes();
    private final Separator separator = new Separator();

    public Saved() {
        separator.setOrientation(Orientation.VERTICAL);
        getChildren().addAll(webView, separator, notes);
    }

    @Override
    public void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        double webWidth = w / 10 * 7;
        layoutInArea(webView, 0, 0, webWidth, h, 0, HPos.CENTER, VPos.CENTER);
        layoutInArea(notes, webWidth, 0, w - webWidth, h, 0, HPos.CENTER, VPos.CENTER);
    }

    public void loadUrl(String url) {
        webEngine.load(url);
        notes.loadNote(url);
    }
}
