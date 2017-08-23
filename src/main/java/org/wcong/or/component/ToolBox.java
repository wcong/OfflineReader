package org.wcong.or.component;

import com.jfoenix.controls.JFXButton;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.text.TextAlignment;

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

        TextInputDialog textInputDialog = new TextInputDialog();
        textInputDialog.setTitle("add url");
        JFXButton desc = new JFXButton("add one offline url");
        desc.setDisable(true);
        desc.setAlignment(Pos.CENTER);
        desc.setTextAlignment(TextAlignment.CENTER);
        textInputDialog.getDialogPane().setHeader(desc);

        add.setOnMouseClicked((event) -> {
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
