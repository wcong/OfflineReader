package org.wcong.or.component;

import com.jfoenix.controls.JFXButton;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import java.util.Optional;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 03/08/2017
 */
public class ToolBox extends Region {

    private final JFXButton label = new JFXButton("OfflineReader");
    private final JFXButton homeButton = new JFXButton("Home");
    private final JFXButton setting = new JFXButton("Setting");
    private final JFXButton add = new JFXButton("+");

    private final HBox leftBox = new HBox();

    private final HBox rightBox = new HBox();

    private final SavedFlow savedFlow;

    private final Home home;

    public ToolBox(Home home, SavedFlow savedFlow) {
        this.home = home;
        this.savedFlow = savedFlow;
        label.setDisable(true);
        leftBox.getChildren().addAll(label, homeButton, setting);
        rightBox.getChildren().addAll(add);
        add.setOnMouseClicked((event) -> {
            TextInputDialog textInputDialog = new TextInputDialog();
            textInputDialog.setTitle("add one offline url");
            textInputDialog.getDialogPane().getButtonTypes();
            Optional<String> result = textInputDialog.showAndWait();
            if (result != null && result.isPresent()) {
                savedFlow.addItem(result.get());
                home.showFlow();
            }
        });

        homeButton.setOnMouseClicked(event -> {
            home.showFlow();
        });
        getChildren().addAll(leftBox, rightBox);
    }

    @Override
    public void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        double leftWith = leftBox.prefWidth(h);
        double rightWith = rightBox.prefWidth(h);
        layoutInArea(leftBox, 0, 0, leftWith, h, 0, HPos.CENTER, VPos.CENTER);
        layoutInArea(rightBox, w - rightWith, 0, w, h, 0, HPos.CENTER, VPos.CENTER);
    }
}
