package org.structureviewer;

import org.jmulti.calc.AtomDescr;

import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import scala.Product;

class AtomDataModel extends AbstractTableModel {
    private List<AtomDescr> atoms = new ArrayList<>();

    public void setAtoms(AtomDescr[] atoms){
        this.atoms = Arrays.asList(atoms);
        fireTableDataChanged();
    }

    public Product test() { return null;}

    public AtomDescr[] getAtoms(){
        return atoms.toArray(new AtomDescr[0]);
    }

    @Override
    public int getRowCount() {
        return atoms.size();
    }

    @Override
    public int getColumnCount() {
        return 6;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (atoms.size() <= rowIndex) {
            return null;
        }

        AtomDescr atom = atoms.get(rowIndex);

        switch (columnIndex) {
            case 0:
                float[] c = GlUtils.colorFromElementName(atom.name());
                return new Color(c[0], c[1], c[2]);
            case 1:
                return atom.name();
            case 2:
                return atom.charge();
            case 3:
                return atom.p().x();
            case 4:
                return atom.p().y();
            case 5:
                return atom.p().z();
            default:
                return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex){
        switch (columnIndex){
            case 0:
                return Color.class;
            case 1:
                return String.class;
            case 2:
                return Integer.class;
            case 3:
            case 4:
            case 5:
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex){
            case 0:
                return "Color";
            case 1:
                return "Atom";
            case 2:
                return "Oxidation";
            case 3:
                return "x";
            case 4:
                return "y";
            case 5:
                return "z";
            default:
                return "";
        }
    }
}
