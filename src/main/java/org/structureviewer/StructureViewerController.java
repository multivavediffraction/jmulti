package org.structureviewer;

import com.aeonium.javafx.validation.annotations.FXNumber;
import com.aeonium.javafx.validation.annotations.FXValidationChecked;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;
import javajs.util.P3;
import org.jmol.adapter.smarter.AtomSetCollection;
import org.jmol.adapter.smarter.AtomSetCollectionReader;
import org.jmol.adapter.smarter.SmarterJmolAdapter;
import org.jmol.api.JmolAdapter;
import org.jmulti.CalcParams;
import org.jmulti.Logger;
import org.jmulti.ParametersSweep;
import org.jmulti.UnitCell;
import org.jmulti.calc.AtomDescr;
import org.jmulti.calc.Calc$;
import org.jmulti.calc.Samples;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

import static org.structureviewer.Utils.epsilonEquals;

public class StructureViewerController implements Initializable {
    @FXML private BorderPane rootPane;
    @FXML private ComboBox<String> params;
    @FXML private Button calculateBtn;
    @FXML private ListView<String> logView;
    @FXML private Label hInputLabel;
    @FXNumber @FXML private TextField hInput;
    @FXML private Label kInputLabel;
    @FXNumber @FXML private TextField kInput;
    @FXML private Label lInputLabel;
    @FXNumber @FXML private TextField lInput;
    @FXML private Label sweepParamsLabel;
    @FXML private ComboBox<ParametersSweep> sweepParamsInput;
    @FXML private CheckBox savePeaksInput;
    @FXML private Label psiStartLabel;
    @FXNumber @FXML private TextField psiStartInput;
    @FXML private Label psiEndLabel;
    @FXNumber @FXML private TextField psiEndInput;
    @FXML private Label psiStepsLabel;
    @FXNumber @FXML private TextField psiStepsInput;
    @FXML private Label energyStartLabel;
    @FXNumber @FXML private TextField energyStartInput;
    @FXML private Label energyEndLabel;
    @FXNumber @FXML private TextField energyEndInput;
    @FXML private Label energyStepsLabel;
    @FXNumber @FXML private TextField energyStepsInput;
    @FXML private Label titleLabel;
    @FXML private TextField titleInput;
    @FXML private Label aLabel;
    @FXNumber @FXML private TextField aInput;
    @FXML private Label bLabel;
    @FXNumber @FXML private TextField bInput;
    @FXML private Label cLabel;
    @FXNumber @FXML private TextField cInput;
    @FXML private Label alphaLabel;
    @FXNumber @FXML private TextField alphaInput;
    @FXML private Label betaLabel;
    @FXNumber @FXML private TextField betaInput;
    @FXML private Label gammaLabel;
    @FXNumber @FXML private TextField gammaInput;
    @FXML private TableView<Atom> atomsDataTable;
    @FXML private TableColumn<Atom, Color> atomColorCol;
    @FXML private TableColumn<Atom, String> atomNameCol;
    @FXML private TableColumn<Atom, Integer> atomChargeCol;
    @FXML private TableColumn<Atom, Double> atomXCol;
    @FXML private TableColumn<Atom, Double> atomYCol;
    @FXML private TableColumn<Atom, Double> atomZCol;

    @FXML private StructSceneController structSceneController;

    @FXValidationChecked
    private BooleanProperty isValidated = new SimpleBooleanProperty(true);
    private BooleanProperty isComputing = new SimpleBooleanProperty(false);

    private IntegerProperty h = new SimpleIntegerProperty(0);
    private IntegerProperty k = new SimpleIntegerProperty(0);
    private IntegerProperty l = new SimpleIntegerProperty(1);

    private ObjectProperty<ParametersSweep> sweep = new SimpleObjectProperty<>(ParametersSweep.PSI);
    private BooleanProperty savePeaks = new SimpleBooleanProperty(false);

    private DoubleProperty psiStart = new SimpleDoubleProperty(0.0);
    private DoubleProperty psiEnd = new SimpleDoubleProperty(360.0);
    private IntegerProperty psiSteps = new SimpleIntegerProperty(360);

    private DoubleProperty energyStart = new SimpleDoubleProperty(4.5);
    private DoubleProperty energyEnd = new SimpleDoubleProperty(9);
    private IntegerProperty energySteps = new SimpleIntegerProperty(20);
    private StringProperty title = new SimpleStringProperty("default");

    private DoubleProperty a = new SimpleDoubleProperty(2.0);
    private DoubleProperty b = new SimpleDoubleProperty(2.0);
    private DoubleProperty c = new SimpleDoubleProperty(2.0);

    private DoubleProperty alpha = new SimpleDoubleProperty(90.0);
    private DoubleProperty beta = new SimpleDoubleProperty(90.0);
    private DoubleProperty gamma = new SimpleDoubleProperty(90.0);

    private Stage primaryStage;
    private JmolAdapter adapter;
    private final FileChooser fc = new FileChooser();
    private String file;

    @Override
    public void initialize(URL location, ResourceBundle resources){
        adapter = new SmarterJmolAdapter();
        Logger.setWriter(str -> Platform.runLater(() -> {
            logView.getItems().add(str);
        }));

        hInputLabel.setLabelFor(hInput);
        kInputLabel.setLabelFor(kInput);
        lInputLabel.setLabelFor(lInput);
        psiStartLabel.setLabelFor(psiStartInput);
        psiEndLabel.setLabelFor(psiEndInput);
        psiStepsLabel.setLabelFor(psiStepsInput);
        energyStartLabel.setLabelFor(energyStartInput);
        energyEndLabel.setLabelFor(energyEndInput);
        energyStepsLabel.setLabelFor(energyStepsInput);
        sweepParamsLabel.setLabelFor(sweepParamsInput);
        titleLabel.setLabelFor(titleInput);
        aLabel.setLabelFor(aInput);
        bLabel.setLabelFor(bInput);
        cLabel.setLabelFor(cInput);
        alphaLabel.setLabelFor(alphaInput);
        betaLabel.setLabelFor(betaInput);
        gammaLabel.setLabelFor(gammaInput);

        calculateBtn.disableProperty().bind(isValidated.not().or(isComputing));

        hInput.textProperty().bindBidirectional(h, new NumberStringConverter());
        kInput.textProperty().bindBidirectional(k, new NumberStringConverter());
        lInput.textProperty().bindBidirectional(l, new NumberStringConverter());

        psiStartInput.textProperty().bindBidirectional(psiStart, new NumberStringConverter());
        psiEndInput.textProperty().bindBidirectional(psiEnd, new NumberStringConverter());
        psiStepsInput.textProperty().bindBidirectional(psiSteps, new NumberStringConverter("#####0"));

        energyStartInput.textProperty().bindBidirectional(energyStart, new NumberStringConverter());
        energyEndInput.textProperty().bindBidirectional(energyEnd, new NumberStringConverter());
        energyStepsInput.textProperty().bindBidirectional(energySteps, new NumberStringConverter());

        savePeaksInput.selectedProperty().bindBidirectional(savePeaks);
        sweepParamsInput.getItems().addAll(ParametersSweep.values());
        sweepParamsInput.valueProperty().bindBidirectional(sweep);
        titleInput.textProperty().bindBidirectional(title);

        aInput.textProperty().bindBidirectional(a, new NumberStringConverter());
        bInput.textProperty().bindBidirectional(b, new NumberStringConverter());
        cInput.textProperty().bindBidirectional(c, new NumberStringConverter());

        alphaInput.textProperty().bindBidirectional(alpha, new NumberStringConverter());
        betaInput.textProperty().bindBidirectional(beta, new NumberStringConverter());
        gammaInput.textProperty().bindBidirectional(gamma, new NumberStringConverter());

        atomColorCol.setCellValueFactory(new PropertyValueFactory<>("color"));
        atomColorCol.setCellFactory(new ColorCellFactory<>());
        atomNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        atomChargeCol.setCellValueFactory(new PropertyValueFactory<>("charge"));
        atomXCol.setCellValueFactory(new PropertyValueFactory<>("x"));
        atomYCol.setCellValueFactory(new PropertyValueFactory<>("y"));
        atomZCol.setCellValueFactory(new PropertyValueFactory<>("z"));

        var sweepCellFactory = new SweepCellFactory();
        sweepParamsInput.setButtonCell(sweepCellFactory.call(null));
        sweepParamsInput.setCellFactory(sweepCellFactory);

        savePeaksInput.disableProperty().bind(sweep.isNotEqualTo(ParametersSweep.PSI));
        psiEndInput.disableProperty().bind(sweep.isEqualTo(ParametersSweep.ENERGY));
        psiStepsInput.disableProperty().bind(sweep.isEqualTo(ParametersSweep.ENERGY));
        energyEndInput.disableProperty().bind(sweep.isEqualTo(ParametersSweep.PSI));
        energyStepsInput.disableProperty().bind(sweep.isEqualTo(ParametersSweep.PSI));

        params.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (null != oldValue && oldValue.equals(newValue)) {
                return;
            }

            switch (newValue) {
                case "SiO\u2082":
                    fillCalculationtParams(Samples.sampleSiO2().params());
                    fillUnitCellsParams(Samples.sampleSiO2().unitCell());
                    fillAtomsParameters(Samples.sampleSiO2().atoms());
                    structSceneController.setAtoms(Samples.sampleSiO2().unitCell(), Samples.sampleSiO2().atoms());
                    break;
                case "CuB\u2082O\u2084":
                    fillCalculationtParams(Samples.sampleCuB2O4().params());
                    fillUnitCellsParams(Samples.sampleCuB2O4().unitCell());
                    fillAtomsParameters(Samples.sampleCuB2O4().atoms());
                    structSceneController.setAtoms(Samples.sampleCuB2O4().unitCell(), Samples.sampleCuB2O4().atoms());
                    break;
                default:
                    Logger.log("Unknown parameters set name " + params.getValue() + ". Selecting SiO\u2082");
                    params.setValue("SiO\u2082");
            }
        });

        params.setValue("SiO\u2082");
    }

    private void fillAtomsParameters(AtomDescr[] atoms) {
        var atomCollection = atomsDataTable.getItems();
        atomCollection.clear();
        for(var a:atoms){
            atomCollection.add(new Atom(a));
        }
    }

    private void fillCalculationtParams(CalcParams params) {
        h.set(params.h);
        k.set(params.k);
        l.set(params.l);

        psiStart.set(params.psiStart);
        psiEnd.set(params.psiEnd);
        psiSteps.set(params.psiSteps);

        energyStart.set(params.energyStart);
        energyEnd.set(params.energyEnd);
        energySteps.set(params.energySteps);

        title.set(params.title);
        sweep.set(params.sweep);
        savePeaks.set(params.savePeaks);
    }

    private void fillUnitCellsParams(UnitCell unitCell) {
        a.set(unitCell.a());
        b.set(unitCell.b());
        c.set(unitCell.c());
        alpha.set(unitCell.alpha());
        beta.set(unitCell.beta());
        gamma.set(unitCell.gamma());
    }

    public Scene setStage(Stage stage){
        primaryStage = stage;
        var scene = new Scene(rootPane);
        stage.setScene(scene);
        return scene;
    }

    private void fillForms(AtomSetCollection atomsCollection) {
        int atomCount = atomsCollection.ac;
        AtomDescr[] atoms = new AtomDescr[atomCount];

        float[] unitCellParams = (float[])atomsCollection.getAtomSetAuxiliaryInfo(0).get("unitCellParams");

        UnitCell uc;

        if (null == unitCellParams) {
            Logger.log("Cif file does not contain unit cell information");
            uc = new UnitCell(0,0,0,0,0,0);
        } else {
            uc = new UnitCell(unitCellParams[0], unitCellParams[1], unitCellParams[2],
                    unitCellParams[3], unitCellParams[4], unitCellParams[5]);
        }

        fillUnitCellsParams(uc);

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

        fillAtomsParameters(realAtoms);
        structSceneController.setAtoms(uc, realAtoms);
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


    @FXML
    private void onOpenFile(ActionEvent action){
        if (null == file || file.isBlank()){
            fc.setInitialDirectory(new File("."));
        } else {
            var dir = new File(file).getParentFile();
            if (null != dir) {
                fc.setInitialDirectory(dir);
            }
        }

        File f = fc.showOpenDialog(primaryStage);
        if(null != f){
            file = f.getAbsolutePath();
            title.set(f.getName());
            AtomSetCollection atoms = loadFile(file, 1);

            fillForms(atoms);
        }
    }

    @FXML
    private void onCalculate(ActionEvent action){
        logView.getItems().clear();
        isComputing.set(true);
        Logger.log("Staring calculation");

        var savePeaksValue = sweep.get() == ParametersSweep.PSI && savePeaks.get();

        var psiEndValue = sweep.get() != ParametersSweep.ENERGY ? psiEnd.get() : psiStart.get();
        var psiStepsValue = sweep.get() != ParametersSweep.ENERGY ? psiSteps.get() : 0;

        var energyEndValue = sweep.get() != ParametersSweep.PSI ? energyEnd.get() : energyStart.get();
        var energyStepsValue = sweep.get() != ParametersSweep.PSI ? energySteps.get() : 0;

        var parameters = new CalcParams(psiStart.get(), psiEndValue, psiStepsValue,
                h.get(), k.get(), l.get(),
                energyStart.get(), energyEndValue,  energyStepsValue,
                title.get(), sweep.get(), savePeaksValue);
        var unitCell = new UnitCell(a.get(), b.get(), c.get(), alpha.get(), beta.get(), gamma.get());
        var atomsCollection = atomsDataTable.getItems();

        var atoms = atomsCollection.stream().map(Atom::getDescr).toArray(AtomDescr[]::new);

        var calc = CompletableFuture.runAsync(() -> {
            Logger.log("In executor thread before starting calculation");
            Calc$.MODULE$.apply(unitCell, atoms, parameters);
        });

        calc.thenRun(() -> {
            Logger.log("Calculation completed");
            Platform.runLater(() -> isComputing.set(false));
        });
    }
}
