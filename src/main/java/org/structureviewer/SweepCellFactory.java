package org.structureviewer;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import org.jmulti.ParametersSweep;

public class SweepCellFactory implements Callback<ListView<ParametersSweep>, ListCell<ParametersSweep>> {
    @Override
    public ListCell<ParametersSweep> call(ListView<ParametersSweep> param) {
        return new ListCell<>(){
            @Override
            protected void updateItem(ParametersSweep item, boolean empty) {
                super.updateItem(item, empty);

                if (null == item || empty) {
                    setText("");
                } else {
                    switch (item) {
                        case PSI:
                            setText("Psi");
                            break;
                        case ENERGY:
                            setText("Energy");
                            break;
                        case RR2_FIELD:
                            setText("Rr2 field on Psi X Energy");
                            break;
                        case RL2_FIELD:
                            setText("Rl2 field on Psi X Energy");
                            break;
                    }
                }
            }
        };
    }
}
