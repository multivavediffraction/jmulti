package org.structureviewer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class StructureViewerFX extends Application {
    @Override
    public void start(Stage stage) {
        try {
            URL fxmlUrl = getClass().getResource("StructureViewer.fxml");
            if(null == fxmlUrl) {
                System.out.println("Can'topen fxml resource");
                stage.close();
                return;
            }
            var fxmlLoader = new FXMLLoader(fxmlUrl);
            fxmlLoader.load();

            StructureViewerController controller = fxmlLoader.getController();
            controller.setStage(stage);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
