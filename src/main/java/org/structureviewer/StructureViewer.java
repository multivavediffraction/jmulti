package org.structureviewer;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import org.jmol.adapter.smarter.AtomSetCollection;
import org.jmol.adapter.smarter.AtomSetCollectionReader;
import org.jmol.adapter.smarter.SmarterJmolAdapter;
import org.jmol.api.JmolAdapter;

import org.jmulti.CalcParams;
import org.jmulti.Logger;
import org.jmulti.UnitCell;
import org.jmulti.calc.AtomDescr;
import org.jmulti.calc.Calc$;
import org.jmulti.calc.SampleData;
import org.jmulti.calc.SampleData$;
import org.structureviewer.StructureGLListener.ShowAtoms;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

import javajs.util.P3;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.*;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import static org.structureviewer.Utils.epsilonEquals;

public class StructureViewer {
    private float[] atomsTransMat = {
            1f, 0f, 0f,
            0f, 1f, 0f,
            0f, 0f, 1f
    };

    private int oldMouseX = 0;
    private int oldMouseY = 0;

    private JFrame frame;

    private JmolAdapter adapter;

    private JFileChooser fc;
    private String file;

    private GLCanvas structureView;

    private AtomDataModel atomsData = new AtomDataModel();
    private UnitCellPane unitCellPane = new UnitCellPane();
    private ParametersPane paramsPane = new ParametersPane();
    private JTextArea logView;

    private StructureViewer(){
        SwingUtilities.invokeLater(this::initUI);
    }

    private void initUI() {
        adapter = new SmarterJmolAdapter();

        GLCapabilities config = new GLCapabilities(GLProfile.get(GLProfile.GL4));

        frame = new JFrame("Structure viewer");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        frame.setLayout(layout);

        config.setSampleBuffers(true);
        config.setNumSamples(4);
        structureView = new GLCanvas(config);
        StructureGLListener structureRenderer = new StructureGLListener();
        structureView.addGLEventListener(structureRenderer);
        structureView.setPreferredSize(new Dimension(512,512));

        JPanel cellsNumberPanel = new JPanel();

        cellsNumberPanel.add(new JLabel("Number of unit cells: "));

        JLabel cellsNumberLbl = new JLabel("1");
        cellsNumberPanel.add(cellsNumberLbl);

        JSlider cellsNumberSlider = new JSlider(0, 10);
        cellsNumberSlider.setValue(1);
        cellsNumberSlider.setMajorTickSpacing(5);
        cellsNumberSlider.setMinorTickSpacing(1);

        Hashtable<Integer, JLabel> cellsSliderLabels = new Hashtable<>();
        cellsSliderLabels.put(1, new JLabel("1"));
        cellsSliderLabels.put(5, new JLabel("5"));
        cellsSliderLabels.put(10, new JLabel("10"));
        cellsNumberSlider.setLabelTable(cellsSliderLabels);
        cellsNumberSlider.setPaintLabels(true);

        JButton readFileBtn = new JButton("Open file...");
        JButton calcBtn = new JButton("Calculate");

        JTable atomDataTable = new JTable();
        atomsData.setAtoms(SampleData$.MODULE$.atoms());
        atomDataTable.setModel(atomsData);

        atomDataTable.setDefaultRenderer(Color.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column)
            {
                JLabel renderer = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                renderer.setBackground((Color)value);
                renderer.setText("");
                return renderer;
            }
            });


        unitCellPane.setData(SampleData.unitCell());
        paramsPane.setParams(SampleData.params());

        logView = new JTextArea();
        //logView.setEditable(false);
        logView.setRows(5);

        c.insets.set(6,6,6,6);

        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 7;
        frame.add(structureView, c);

        c.insets.set(6,6,3,6);

        c.gridx = 1;
        c.gridy = 0;
        c.gridheight = 1;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;

        c.insets.set(3,6,3,6);

        c.gridy = 1;
        frame.add(cellsNumberPanel, c);

        c.gridy = 2;
        frame.add(cellsNumberSlider, c);

        c.insets.set(3,6,6,6);

        c.gridy = 3;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        frame.add(readFileBtn, c);

        c.gridx = 2;
        frame.add(calcBtn, c);

        c.gridx = 1;
        c.gridy = 4;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.HORIZONTAL;
        frame.add(paramsPane.layout(), c);

        c.gridy = 5;
        frame.add(unitCellPane.layout(), c);

        c.gridy = 6;
        c.fill = GridBagConstraints.BOTH;
        frame.add(new JScrollPane(atomDataTable), c);

        c.gridx = 0;
        c.gridy = 7;
        c.gridwidth = 3;
        c.fill = GridBagConstraints.HORIZONTAL;
        frame.add(new JScrollPane(logView), c);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        Logger.setWriter(msg -> {logView.append(msg); logView.append(System.lineSeparator());});

        cellsNumberSlider.addChangeListener(e ->{
            int cellsNumber = cellsNumberSlider.getValue();

            cellsNumberLbl.setText(String.valueOf(cellsNumber));

            if(null != file && !file.isEmpty()){
                AtomSetCollection atoms = loadFile(file, cellsNumber);

                structureRenderer.setAtoms(atoms, cellsNumberSlider.getValue() == 0 ? ShowAtoms.UNEQUIVALENT : ShowAtoms.ALL);

                structureView.display();
            }
        });

        readFileBtn.addActionListener(e -> {
            if(null == fc) {
                fc = new JFileChooser(new File("."));
                fc.setMultiSelectionEnabled(false);
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            }

            int retval = fc.showOpenDialog(frame);
            if(JFileChooser.APPROVE_OPTION == retval){
                file = fc.getSelectedFile().getAbsolutePath();
                File f = new File(file);
                paramsPane.setTitle(f.getName());
                AtomSetCollection atoms = loadFile(file, cellsNumberSlider.getValue());

                fillForms(atoms);

                structureRenderer.setAtoms(atoms, cellsNumberSlider.getValue() == 0 ? ShowAtoms.UNEQUIVALENT : ShowAtoms.ALL);

                structureView.display();
            }
        });

        calcBtn.addActionListener(e -> {
            try {
                logView.setText("");
                Logger.log("Starting calcuaton...");
                UnitCell uc = unitCellPane.getData();
                AtomDescr[] atoms = atomsData.getAtoms();
                CalcParams params = paramsPane.getParams();
                Calc$.MODULE$.apply(uc, atoms, params);
            } catch (Exception ex) {
                Logger.log("Calculation failed: " + ex.getMessage());
            }
        });

        structureView.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                updateTransMatrix(e);

                structureRenderer.setTransformMatrix(atomsTransMat);

                structureView.display();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                updateMousePos(e);
            }
        });

        structureRenderer.setAtoms(SampleData.atoms(), SampleData.unitCell(), 0);
    }

    private void fillForms(AtomSetCollection atomsCollection) {
        int atomCount = atomsCollection.ac;
        AtomDescr[] atoms = new AtomDescr[atomCount];

        float[] unitCellParams = (float[])atomsCollection.getAtomSetAuxiliaryInfo(0).get("unitCellParams");

        UnitCell uc;
        
        if (null == unitCellParams) {
           JOptionPane.showMessageDialog(frame, "Cif file does not contain unit cell information", "File load info", JOptionPane.WARNING_MESSAGE);
           uc = new UnitCell(0,0,0,0,0,0);
        } else {
            uc = new UnitCell(unitCellParams[0], unitCellParams[1], unitCellParams[2],
                    unitCellParams[3], unitCellParams[4], unitCellParams[5]);
        }

        unitCellPane.setData(uc);

        int realCount = 0;

        for(int i = 0; i < atomCount; ++i){
            org.jmulti.calc.P3 coords = new org.jmulti.calc.P3(
                    atomsCollection.atoms[i].x,
                    atomsCollection.atoms[i].y,
                    atomsCollection.atoms[i].z);
            String element = atomsCollection.atoms[i].getElementSymbol();
            int charge = atomsCollection.atoms[i].formalCharge;

            atoms[realCount] = new AtomDescr(element, charge, coords);

            P3[] ucVectors = new P3[]{
                        P3.new3(1,0,0),
                        P3.new3(0,1,0),
                        P3.new3(0,0,1)};

            boolean equivalent_atom = false;
            P3 a = P3.new3((float)coords.x(), (float)coords.y(), (float)coords.z());
            for(int j = 0; j < realCount; ++j){
                P3 atom = P3.new3(atomsCollection.atoms[j].x, atomsCollection.atoms[j].y, atomsCollection.atoms[j].z);

                P3 translatedAtom = P3.newP(atom);
                translatedAtom.add(ucVectors[0]);
                if(epsilonEquals(a, translatedAtom, 1e-5f)){
                    equivalent_atom = true;
                    break;
                }

                translatedAtom = P3.newP(atom);
                translatedAtom.add(ucVectors[1]);
                if(epsilonEquals(a, translatedAtom, 1e-5f)){
                    equivalent_atom = true;
                    break;
                }

                translatedAtom = P3.newP(atom);
                translatedAtom.add(ucVectors[2]);
                if(epsilonEquals(a, translatedAtom, 1e-5f)){
                    equivalent_atom = true;
                    break;
                }

                translatedAtom = P3.newP(atom);
                translatedAtom.add(ucVectors[0]);
                translatedAtom.add(ucVectors[2]);
                if(epsilonEquals(a, translatedAtom, 1e-5f)){
                    equivalent_atom = true;
                    break;
                }

                translatedAtom = P3.newP(atom);
                translatedAtom.add(ucVectors[1]);
                translatedAtom.add(ucVectors[2]);
                if(epsilonEquals(a, translatedAtom, 1e-5f)){
                    equivalent_atom = true;
                    break;
                }

                translatedAtom = P3.newP(atom);
                translatedAtom.add(ucVectors[0]);
                translatedAtom.add(ucVectors[1]);
                if(epsilonEquals(a, translatedAtom, 1e-5f)){
                    equivalent_atom = true;
                    break;
                }

                translatedAtom = P3.newP(atom);
                translatedAtom.add(ucVectors[0]);
                translatedAtom.add(ucVectors[1]);
                translatedAtom.add(ucVectors[2]);
                if(epsilonEquals(a, translatedAtom, 1e-5f)){
                    equivalent_atom = true;
                    break;
                }
            }

            if(!equivalent_atom){
                ++realCount;
            }
        }

        AtomDescr[] realAtoms = new AtomDescr[realCount];
        System.arraycopy(atoms, 0, realAtoms, 0, realCount);
        
        atomsData.setAtoms(realAtoms);
    }

    private void updateMousePos(MouseEvent e) {
        oldMouseX = e.getX();
        oldMouseY = e.getY();
    }

    private void updateTransMatrix(MouseEvent e) {
        double w = 0.01;
        float cosX = (float)Math.cos(w*(e.getX() - oldMouseX));
        float sinX = (float)Math.sin(w*(e.getX() - oldMouseX));

        float cosY = (float)Math.cos(w*(e.getY() - oldMouseY));
        float sinY = (float)Math.sin(w*(e.getY() - oldMouseY));

        updateMousePos(e);

        float[] My = {
                cosX, 0, sinX,
                0, 1,    0,
                -sinX, 0, cosX
        };

        float[] Mx = {
                1,    0,     0,
                0, cosY, -sinY,
                0, sinY,  cosY
        };

        float[] diffMatrix = Utils.matMul( My, Mx, 3);

        atomsTransMat = Utils.matMul(atomsTransMat, diffMatrix, 3);
    }

    private AtomSetCollection loadFile(String name, int cellsNumber){
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(name)));

            Map<String, Object> htParams = new HashMap<>();
            //htParams.put("spaceGroupIndex", -1);
            if(0 < cellsNumber) {
                htParams.put("lattice", P3.new3(cellsNumber, cellsNumber, cellsNumber));
            } else {
                htParams.put("lattice", P3.new3(1, 1, 1));
            }
            htParams.put("packed", true);

            AtomSetCollectionReader fileReader = (AtomSetCollectionReader) adapter.getAtomSetCollectionReader(name, null, reader, htParams);

            Object result =  adapter.getAtomSetCollection(fileReader);
            if(result instanceof AtomSetCollection){
                return (AtomSetCollection) result;
            } else if (result instanceof String) {
                throw new IOError(new Error((String)result));
            } else {
                throw new AssertionError("Unhandled read result type");
            }
        } catch (FileNotFoundException e){
            throw new IOError(e);
        }
    }


    public static void main(String[] args){
        GLProfile.initSingleton();
        new StructureViewer();
    }
}
