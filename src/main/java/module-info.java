module org.structureviewer {
        requires static org.jetbrains.annotations;
        requires scala.library;
        requires java.desktop;
        requires javafx.controls;
        requires javafx.fxml;
        requires org.jmulti;
        requires jmol;
        requires jogl.all;

        exports org.structureviewer;
        opens org.structureviewer to javafx.fxml;
}