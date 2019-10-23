package org.structureviewer;

import org.jmulti.CalcParams;

import javax.swing.*;
import java.awt.*;

public class ParametersPane {
    private JTextField hField = new JTextField();
    private JTextField kField = new JTextField();
    private JTextField lField = new JTextField();

    private JTextField psiStartField = new JTextField();
    private JTextField psiEndField = new JTextField();
    private JTextField psiStepsField = new JTextField();

    private JTextField energyField = new JTextField();
    private JTextField titleField = new JTextField();

    public void setParams(CalcParams params) {
        hField.setText(String.valueOf(params.h));
        kField.setText(String.valueOf(params.k));
        lField.setText(String.valueOf(params.l));

        psiStartField.setText(String.valueOf(params.psiStart));
        psiEndField.setText(String.valueOf(params.psiEnd));
        psiStepsField.setText(String.valueOf(params.psiSteps));

        energyField.setText(String.valueOf(params.energy));
        titleField.setText(params.title);
    }

    public CalcParams getParams(){
        return new CalcParams(
                Double.parseDouble(psiStartField.getText()),
                Double.parseDouble(psiEndField.getText()),
                Integer.parseInt(psiStepsField.getText()),
                Integer.parseInt(hField.getText()),
                Integer.parseInt(kField.getText()),
                Integer.parseInt(lField.getText()),
                Double.parseDouble(energyField.getText()),
                titleField.getText()
        );
    }

    public JPanel layout() {
        hField.setColumns(7);
        kField.setColumns(7);
        lField.setColumns(7);
        psiStartField.setColumns(7);
        psiEndField.setColumns(7);
        psiStepsField.setColumns(7);

        JPanel pane = new JPanel();
        pane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.BASELINE_TRAILING;
        c.insets = new Insets(3, 3, 3, 3);
        pane.add(new JLabel("h:"), c);

        c.gridx = 2;
        pane.add(new JLabel("k:"), c);

        c.gridx = 4;
        pane.add(new JLabel("l:"), c);

        c.gridx = 0;
        c.gridy = 1;
        pane.add(new JLabel("Psi start:"), c);

        c.gridx = 2;
        pane.add(new JLabel("end:"), c);

        c.gridx = 4;
        pane.add(new JLabel("steps:"), c);

        c.gridx = 1;
        c.gridy = 0;
        c.weightx = .5;
        c.anchor = GridBagConstraints.BASELINE_LEADING;
        c.fill = GridBagConstraints.HORIZONTAL;
        pane.add(hField, c);

        c.gridx = 3;
        pane.add(kField, c);

        c.gridx = 5;
        pane.add(lField, c);

        c.gridx = 1;
        c.gridy = 1;
        pane.add(psiStartField, c);

        c.gridx = 3;
        pane.add(psiEndField, c);

        c.gridx = 5;
        pane.add(psiStepsField, c);

        c.gridx = 0;
        c.gridy = 2;
        c.anchor = GridBagConstraints.BASELINE_TRAILING;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        pane.add(new JLabel("Beam energy:"), c);

        c.gridx = 2;
        c.anchor = GridBagConstraints.BASELINE_LEADING;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = .5;
        pane.add(energyField, c);

        c.gridx = 4;
        c.anchor = GridBagConstraints.BASELINE_TRAILING;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        pane.add(new JLabel("Title:"), c);

        c.gridx = 5;
        c.anchor = GridBagConstraints.BASELINE_LEADING;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = .5;
        pane.add(titleField, c);

        return pane;
    }

    public void setTitle(String str) {
        titleField.setText(str);
    }
}
