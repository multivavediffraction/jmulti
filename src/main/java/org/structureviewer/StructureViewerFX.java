package org.structureviewer;

import com.aeonium.javafx.actions.FXActionManager;
import com.aeonium.javafx.utils.LabelService;
import com.aeonium.javafx.validation.ValidatorService;
import com.aeonium.javafx.validation.exceptions.ValidationException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class StructureViewerFX extends Application {
    @Override
    public void start(Stage stage) {
        // Get the pre-configured controller factory:
        FXActionManager myActionControllerFactory = ValidatorService.createActionManager();

        // Load the FXMl with the controller factory
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("StructureViewer.fxml");
        if(null == url) {
            System.out.println("Can't open fxml resource");
            stage.close();
            return;
        }
        fxmlLoader.setLocation(url);
        var localozationBundle = ResourceBundle.getBundle("org.structureviewer.StructureViewer");
        fxmlLoader.setResources(localozationBundle);
        fxmlLoader.setControllerFactory(myActionControllerFactory);

        try {
            Parent root = fxmlLoader.load();
            StructureViewerController controller = fxmlLoader.getController();
            Scene scene = controller.setStage(stage);

            // Add the validation stylesheet - if you have not done so in the FXML
            scene.getStylesheets().add("/com/aeonium/javafx/validation/aeFXValidation.css");

            // Load and set the resource bundle
            ValidatorService.setBundle(localozationBundle);

            // Register all Label instances with the LabelService
            LabelService.initialize(scene);
            myActionControllerFactory.initActions();
            ValidatorService.initialize(fxmlLoader.getController());
            try {
                ValidatorService.validate(fxmlLoader.getController());
            } catch (ValidationException e) {
                e.printStackTrace();
            }
            stage.show();
        } catch (IOException ex) {
            System.out.print("Failed to load fxml resource: ");
            System.out.println(ex.getMessage());
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
