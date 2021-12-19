module org.structureviewer {
        requires static org.jetbrains.annotations;
        requires scala.library;
        requires java.desktop;
        requires javafx.controls;
        requires javafx.fxml;
        requires org.jmulti;
        requires javafx.validations;
        requires aeFXActions;
        requires jmol;

        requires org.jmulti.plugins;
        
        exports org.structureviewer;
        opens org.structureviewer;
}