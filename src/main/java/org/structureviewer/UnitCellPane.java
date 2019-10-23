package org.structureviewer;

import org.jmulti.UnitCell;

import javax.swing.*;
import java.awt.*;

public class UnitCellPane {
    private JTextField aField = new JTextField();
    private JTextField bField = new JTextField();
    private JTextField cField = new JTextField();
    private JTextField alphaField = new JTextField();
    private JTextField betaField = new JTextField();
    private JTextField gammaField = new JTextField();

    public void setData(UnitCell uc){
        aField.setText(String.valueOf(uc.a()));
        bField.setText(String.valueOf(uc.b()));
        cField.setText(String.valueOf(uc.c()));
        alphaField.setText(String.valueOf(uc.alpha()));
        betaField.setText(String.valueOf(uc.beta()));
        gammaField.setText(String.valueOf(uc.gamma()));
    }

    public UnitCell getData(){
        return new UnitCell(
                Double.parseDouble(aField.getText()),
                Double.parseDouble(bField.getText()),
                Double.parseDouble(cField.getText()),
                Double.parseDouble(alphaField.getText()),
                Double.parseDouble(betaField.getText()),
                Double.parseDouble(gammaField.getText())
        );
    }

    public JPanel layout(){
        aField.setColumns(7);
        bField.setColumns(7);
        cField.setColumns(7);
        alphaField.setColumns(7);
        betaField.setColumns(7);
        gammaField.setColumns(7);

        JPanel pane = new JPanel();
        pane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.BASELINE_TRAILING;
        c.insets = new Insets(3, 3, 3, 3);
        pane.add(new JLabel("a:"), c);

        c.gridx = 2;
        pane.add(new JLabel("b:"), c);

        c.gridx = 4;
        pane.add(new JLabel("c:"), c);

        c.gridx = 0;
        c.gridy = 1;
        pane.add(new JLabel("alpha:"), c);

        c.gridx = 2;
        pane.add(new JLabel("beta:"), c);

        c.gridx = 4;
        pane.add(new JLabel("gamma:"), c);

        c.gridx = 1;
        c.gridy = 0;
        c.weightx = .5;
        c.anchor = GridBagConstraints.BASELINE_LEADING;
        c.fill = GridBagConstraints.HORIZONTAL;
        pane.add(aField, c);

        c.gridx = 3;
        pane.add(bField, c);

        c.gridx = 5;
        pane.add(cField, c);

        c.gridx = 1;
        c.gridy = 1;
        pane.add(alphaField, c);

        c.gridx = 3;
        pane.add(betaField, c);

        c.gridx = 5;
        pane.add(gammaField, c);
        return pane;
    }
}
