package org.structureviewer;

import com.aeonium.javafx.validation.annotations.FXNumber;
import com.aeonium.javafx.validation.annotations.FXValidationChecked;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
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
import org.jmulti.UnitCell;
import org.jmulti.calc.AtomDescr;
import org.jmulti.calc.Calc$;
import org.jmulti.calc.SampleData$;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

import static org.structureviewer.Utils.epsilonEquals;

public class StructureViewerController implements Initializable {
    @FXML private BorderPane rootPane;
    @FXML private Label unitCellsDisplay;
    @FXML private Slider unitCellsSlider;
    @FXML private Button calculateBtn;
    @FXML private TextArea logView;
    @FXML private Label hInputLabel;
    @FXNumber @FXML private TextField hInput;
    @FXML private Label kInputLabel;
    @FXNumber @FXML private TextField kInput;
    @FXML private Label lInputLabel;
    @FXNumber @FXML private TextField lInput;
    @FXML private Label psiStartLabel;
    @FXNumber @FXML private TextField psiStartInput;
    @FXML private Label psiEndLabel;
    @FXNumber @FXML private TextField psiEndInput;
    @FXML private Label psiStepsLabel;
    @FXNumber @FXML private TextField psiStepsInput;
    @FXML private Label startEnergyLabel;
    @FXNumber @FXML private TextField beamEnergyInput;
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

    @FXValidationChecked
    private BooleanProperty isValidated = new SimpleBooleanProperty(true);
    private BooleanProperty isComputing = new SimpleBooleanProperty(false);

    private IntegerProperty unitCellsCount = new SimpleIntegerProperty(1);

    private IntegerProperty h = new SimpleIntegerProperty(0);
    private IntegerProperty k = new SimpleIntegerProperty(0);
    private IntegerProperty l = new SimpleIntegerProperty(1);

    private DoubleProperty psiStart = new SimpleDoubleProperty(0.0);
    private DoubleProperty psiEnd = new SimpleDoubleProperty(360.0);
    private IntegerProperty psiSteps = new SimpleIntegerProperty(360);

    private DoubleProperty energy = new SimpleDoubleProperty(4.5);
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
            logView.appendText(str);
            logView.appendText(System.lineSeparator());
        }));

        hInputLabel.setLabelFor(hInput);
        kInputLabel.setLabelFor(kInput);
        lInputLabel.setLabelFor(lInput);
        psiStartLabel.setLabelFor(psiStartInput);
        psiEndLabel.setLabelFor(psiEndInput);
        psiStepsLabel.setLabelFor(psiStepsInput);
        startEnergyLabel.setLabelFor(beamEnergyInput);
        titleLabel.setLabelFor(titleInput);
        aLabel.setLabelFor(aInput);
        bLabel.setLabelFor(bInput);
        cLabel.setLabelFor(cInput);
        alphaLabel.setLabelFor(alphaInput);
        betaLabel.setLabelFor(betaInput);
        gammaLabel.setLabelFor(gammaInput);

        calculateBtn.disableProperty().bind(isValidated.not().or(isComputing));

        unitCellsSlider.valueProperty().bindBidirectional(unitCellsCount);
        unitCellsDisplay.textProperty().bind(Bindings.format("Number of unit cells: %d", unitCellsCount));

        hInput.textProperty().bindBidirectional(h, new NumberStringConverter());
        kInput.textProperty().bindBidirectional(k, new NumberStringConverter());
        lInput.textProperty().bindBidirectional(l, new NumberStringConverter());

        psiStartInput.textProperty().bindBidirectional(psiStart, new NumberStringConverter());
        psiEndInput.textProperty().bindBidirectional(psiEnd, new NumberStringConverter());
        psiStepsInput.textProperty().bindBidirectional(psiSteps, new NumberStringConverter());

        beamEnergyInput.textProperty().bindBidirectional(energy, new NumberStringConverter());
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

        fillCalculationtParams(SampleData$.MODULE$.params());
        fillUnitCellsParams(SampleData$.MODULE$.unitCell());
        fillAtomsParameters(SampleData$.MODULE$.atoms());

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

        energy.set(params.energy);
        title.set(params.title);
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
            AtomSetCollection atoms = loadFile(file, unitCellsCount.get());

            fillForms(atoms);

            //structureRenderer.setAtoms(atoms, cellsNumberSlider.getValue() == 0 ? StructureGLListener.ShowAtoms.UNEQUIVALENT : StructureGLListener.ShowAtoms.ALL);

            //structureView.display();
        }
    }

    @FXML
    private void onCalculate(ActionEvent action){
        logView.clear();
        isComputing.set(true);
        Logger.log("Staring calculation");

        var parameters = new CalcParams(psiStart.get(), psiEnd.get(), psiSteps.get(),
                h.get(), k.get(), l.get(),
                energy.get(), title.get());
        var unitCell = new UnitCell(a.get(), b.get(), c.get(), alpha.get(), beta.get(), gamma.get());
        var atomsCollection = atomsDataTable.getItems();

        var atoms = atomsCollection.stream().map(Atom::getDescr).toArray(AtomDescr[]::new);

        var calc = CompletableFuture.runAsync(() -> {
            Logger.log("In executor thread before starting calculation");
            Calc$.MODULE$.apply(unitCell, atoms, parameters);
        });

        calc.thenRun(() -> isComputing.set(false));
    }
}
