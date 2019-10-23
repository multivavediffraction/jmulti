package org.structureviewer;

import com.aeonium.javafx.validation.annotations.FXNumber;
import com.aeonium.javafx.validation.annotations.FXValidationChecked;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;
import org.jmulti.CalcParams;
import org.jmulti.Logger;
import org.jmulti.UnitCell;
import org.jmulti.calc.Calc$;
import org.jmulti.calc.SampleData$;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

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
    @FXML private Label beamEnergyLabel;
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
    @FXML private TableView<?> atomsDataTable;

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

    @Override
    public void initialize(URL location, ResourceBundle resources){
        hInputLabel.setLabelFor(hInput);
        kInputLabel.setLabelFor(kInput);
        lInputLabel.setLabelFor(lInput);
        psiStartLabel.setLabelFor(psiStartInput);
        psiEndLabel.setLabelFor(psiEndInput);
        psiStepsLabel.setLabelFor(psiStepsInput);
        beamEnergyLabel.setLabelFor(beamEnergyInput);
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

        var params = SampleData$.MODULE$.params();
        var unitCell = SampleData$.MODULE$.unitCell();
        var atoms = SampleData$.MODULE$.atoms();

        h.set(params.h); k.set(params.k); l.set(params.l);

        psiStart.set(params.psiStart); psiEnd.set(params.psiEnd); psiSteps.set(params.psiSteps);

        energy.set(params.energy);
        title.set(params.title);

        a.set(unitCell.a()); b.set(unitCell.b()); c.set(unitCell.c());
        alpha.set(unitCell.alpha()); beta.set(unitCell.beta()); gamma.set(unitCell.gamma());

        Logger.setWriter(logView::appendText);
    }

    public void setStage(Stage stage){
        var scene = new Scene(rootPane);
        stage.setScene(scene);
    }

    @FXML
    private void onOpenFile(ActionEvent action){
    }

    @FXML
    private void onCalculate(ActionEvent action){
        //calculateBtn.setDisable(true);
        isComputing.set(true);
        System.out.println("Calculation started");

        var parameters = new CalcParams(psiStart.get(), psiEnd.get(), psiSteps.get(),
                h.get(), k.get(), l.get(),
                energy.get(), title.get());
        var unitCell = new UnitCell(a.get(), b.get(), c.get(), alpha.get(), beta.get(), gamma.get());
        var atoms = SampleData$.MODULE$.atoms();

        var calc = CompletableFuture.runAsync(() -> {
                Calc$.MODULE$.apply(unitCell, atoms, parameters);
        });

        calc.thenRun(() -> isComputing.set(false));
    }
}
