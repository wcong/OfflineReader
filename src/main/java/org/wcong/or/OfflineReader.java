package org.wcong.or;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.wcong.or.component.Home;
import org.wcong.or.offline.OfflineCache;

import java.io.IOException;

public class OfflineReader extends Application {

    private Scene scene;

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Offline Pocket");
        scene = new Scene(new Home(), 900, 600, Color.web("#666970"));
        stage.setScene(scene);
        scene.getStylesheets().add("toolbar.css");
        stage.show();
    }

    public static void main(String[] args) throws IOException {
        new OfflineCache().init();
        launch(args);
    }
}

