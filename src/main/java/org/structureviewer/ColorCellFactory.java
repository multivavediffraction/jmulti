package org.structureviewer;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.paint.Color;
import javafx.util.Callback;

public class ColorCellFactory<S> implements Callback<TableColumn<S, Color>, TableCell<S,Color>> {
    @Override
    public TableCell<S, Color> call(TableColumn<S, Color> param) {
        TableCell<S, Color> cell = new TableCell<>() {
            @Override
            protected void updateItem(Color item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText("");
                    setStyle(""); // set cell style
                } else {
                    setText("");
                    var r = (int) (255*item.getRed());
                    var g = (int) (255*item.getGreen());
                    var b = (int) (255*item.getBlue());
                    setStyle(String.format("-fx-background-color: #%02X%02X%02X;", r, g, b));
                }
            }
        };
        return cell;
    }
}
