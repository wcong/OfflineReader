package org.wcong.or.component;

import com.jfoenix.controls.JFXButton;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * @author wcong<wc19920415@gmail.com>
 * @since 06/08/2017
 */
public class SavedItem extends VBox {

    private ImageView image;
    private JFXButton label;

    public SavedItem(String imageUrl, String labelText) {
        image = new ImageView(imageUrl);
        label = new JFXButton(labelText);
        label.setDisable(true);
        getChildren().addAll(image, label);
        setAlignment(Pos.CENTER);
    }

}
