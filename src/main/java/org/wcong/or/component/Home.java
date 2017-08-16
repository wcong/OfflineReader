package org.wcong.or.component;

import javafx.scene.layout.BorderPane;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 31/07/2017
 */
public class Home extends BorderPane {

    private final Saved saved = new Saved();

    private final SavedFlow savedFlow = new SavedFlow(this, saved);

    private final ToolBox toolBar = new ToolBox(this, savedFlow);

    public Home() {
        setTop(toolBar);
        setCenter(savedFlow);
    }

    public void showSaved() {
        setCenter(saved);
    }

    public void showFlow() {
        setCenter(savedFlow);
    }
}

