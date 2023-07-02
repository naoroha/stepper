package StepperSceneBuilderFX.Screens.thirdScreen;

import StepperSceneBuilderFX.Screens.BaseScreen.BaseScreenController;
import StepperSceneBuilderFX.Screens.secondScreen.SecondScreen;
import StepperSceneBuilderFX.Screens.secondScreen.SecondScreenController;
import commands.Executed;
import flow.FlowInformationCategory;
import flow.definition.api.StepUsageDeclaration;
import flow.execution.api.FlowExecution;
import flow.execution.impl.FlowExecutionImpl;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import step.api.DataDefinitionDeclaration;

import javax.swing.event.ChangeListener;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.util.logging.Filter;

public class ThirdScreenController extends BaseScreenController {

//    @FXML
//    private AnchorPane baseScreenRoot;
//
//    @FXML
//    private Button LoadFileBtn;
//
//    @FXML
//    private TextField XmlPathTextField;
//
//    @FXML
//    private ToggleButton darkMode;
    private Executed executedFlows;
    private Scene savedScene;
    private FlowExecution selectedFlowExecution;

    @FXML
    private TableView<FlowExecution> oldExecutionTable;
    private VBox collectFlowInputsVB;
    private AnchorPane rerunRoot;
    private VBox freeInputDescription;
    private ListView<String> freeInputListView;

    @FXML
    private TableColumn<FlowExecution, String> flowNameColumn;

    @FXML
    private TableColumn<FlowExecution, String> executedTimeColumn;

    @FXML
    private TableColumn<FlowExecution, String> statusColumn;
    @FXML
    private VBox oldExecutionVBox;
    @FXML
    private Accordion accordionDetails;

    @FXML
    private TitledPane freeInputsTitledPane;
    @FXML
    private ChoiceBox<String> choiceBox;

    @FXML
    private TitledPane flowOutputsTitlePane;

    @FXML
    private TitledPane stepsDetailsTitlePane;
    @FXML
    private HBox detailsAndRunHB;

    @FXML
    public void initialize() {
        accordionDetails.setVisible(false);
        flowDefinitionBtn.setDisable(false);
        statisticsBtn.setDisable(false);
        LoadFileBtn.setDisable(true);
        flowNameColumn.setCellValueFactory(cellData -> {
            FlowExecution flowExecution = cellData.getValue();
            String flowName = flowExecution.getFlowName();
            return new SimpleStringProperty(flowName);
        });
        executedTimeColumn.setCellValueFactory(cellData -> {
            FlowExecution flowExecution = cellData.getValue();
            return new SimpleStringProperty(baseScreen.getHandel().formattedTime(flowExecution.getStartTime()));
        });
        statusColumn.setCellValueFactory(cellData -> {
            FlowExecution flowExecution = cellData.getValue();
            return flowExecution.flowStatusLabelProperty();});
        flowExecutionBtn.setDisable(false);
    }

    public void setOldExecutionTable() {
        executedFlows = baseScreen.getHandel().getPastFlowsExecuted();
        XmlPathTextField.textProperty().bind(xmlPath);
        XmlPathTextField.setStyle("-fx-text-fill: lightgray; -fx-opacity: 1;");
        XmlPathTextField.setDisable(true);
        ObservableList<FlowExecution> flowExecutionObservableList = FXCollections.observableArrayList(executedFlows.getExecutedFlowsList());
        BooleanBinding disableProperty = Bindings.createBooleanBinding((Callable<Boolean>) () -> {
            for (FlowExecution flowExecution : flowExecutionObservableList) {
                if (flowExecution.flowStatusLabelProperty().getValue().equals("Status: running...")) {
                    return false; // Enable the button if any FlowExecution has the desired status
                }
            }
            return true; // Disable the button if no FlowExecution has the desired status
        }, flowExecutionObservableList);

        flowExecutionBtn.disableProperty().bind(disableProperty);

        FilteredList<FlowExecution> filteredList = new FilteredList<>(flowExecutionObservableList);
        oldExecutionTable.setItems(filteredList);
        choiceBox.getItems().addAll("Filter", "Running", "Completed", "Failed");

// Set an initial value for the ChoiceBox
        choiceBox.setValue("Filter");

// Create a predicate to filter the items based on the selected value in the ChoiceBox

// Set a listener to update the filtered list whenever the choiceBox value changes
        choiceBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Update the predicate based on the selected value
            Predicate<FlowExecution> filterPredicate = createFilterPredicate(newValue);
            filteredList.setPredicate(filterPredicate);
        });
    }
    private Predicate<FlowExecution> createFilterPredicate(String selectedValue) {
        // Apply the filter based on the selected value
        if (selectedValue.equals("Running")) {
            return flowExecution -> flowExecution.flowStatusLabelProperty().getValue().equals("Status: running...");
        } else if (selectedValue.equals("Completed")) {
            Platform.runLater(()->flowExecutionBtn.setDisable(true));
            return flowExecution -> flowExecution.flowStatusLabelProperty().getValue().equals("Status: Completed");
        } else if (selectedValue.equals("Failed")) {
            Platform.runLater(()->flowExecutionBtn.setDisable(true));
            return flowExecution -> flowExecution.flowStatusLabelProperty().getValue().equals("Status: Failed");
        } else if (selectedValue.equals("Filter")) {
            return flowExecution -> true; // Show all items if no specific filter selected
        } else {
            return flowExecution -> false;
        }
    }
    @FXML//free inputs,flow outputs,step details(total time, summeryLine).
    void showSelectedFlowInfo(MouseEvent event) {
        TableView.TableViewSelectionModel<FlowExecution> selectionModel = oldExecutionTable.getSelectionModel();
        selectedFlowExecution = selectionModel.getSelectedItem();
        if (selectedFlowExecution.flowStatusLabelProperty().getValue().equals("Status: Completed")) {
            accordionDetails.setVisible(true);
            oldExecutionVBox.getChildren().clear();
            detailsAndRunHB.getChildren().clear();
            oldExecutionVBox.getChildren().add(oldExecutionTable);

            VBox vBox = new VBox(5);
            Label flowName = new Label(selectedFlowExecution.getFlowName());
            Label flowID = new Label(selectedFlowExecution.getUniqueId());
            vBox.getChildren().addAll(flowName, flowID);
            Button rerunFlow = new Button("rerun flow");
            rerunFlow.setOnAction(e -> {
                try {
                    rerunFlowExecution();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            detailsAndRunHB.getChildren().addAll(vBox, rerunFlow);
            oldExecutionVBox.getChildren().add(detailsAndRunHB);
            oldExecutionVBox.getChildren().add(accordionDetails);
        }
    }
    @FXML
    void showFreeInputsDetails(MouseEvent event) {
        freeInputListView=new ListView<>();
        List<String> inputViewList=new ArrayList<>();
        for (DataDefinitionDeclaration input : selectedFlowExecution.getFlowDefinition().getFlowFreeInputs()) {
            inputViewList.add("input name: " + input.getName() +
                    " [type: " + input.dataDefinition().getName() + " ]" +
                    " value= " + selectedFlowExecution.getFlowData().get(input.getName()) +
                    " [necessity: " + input.necessity() + "]");
        }
        freeInputListView.getItems().addAll(FXCollections.observableArrayList(inputViewList));
        freeInputsTitledPane.setContent(freeInputListView);
    }
    @FXML
    void ShowFlowOutputsDetails(MouseEvent event) {
        ListView<String> flowOutputListView = new ListView<>();
        List<String> outputViewList = new ArrayList<>();
        String outputMessage;
        for (StepUsageDeclaration step : selectedFlowExecution.getFlowDefinition().getFlowSteps()) {
            for (DataDefinitionDeclaration Output : step.getFinalOutputs().values()) {
                outputMessage = ("final name: " + Output.getName() + "[type: " +
                        Output.dataDefinition().getName() + "] " + "\n" + "[content:  ");
                if (selectedFlowExecution.getFlowData().get(Output.getName()) == null) {
                    outputMessage = outputMessage + "Not created due to failure in flow";
                } else {
                    outputMessage = outputMessage + selectedFlowExecution.getFlowData().get(Output.getName()) + "]";
                }
                outputViewList.add(outputMessage);
            }
            flowOutputListView.setItems(FXCollections.observableArrayList(outputViewList));
            flowOutputsTitlePane.setContent(flowOutputListView);
        }
    }
    @FXML
    void showStepDetails(MouseEvent event) {
        TreeView<String> stepDetailsTreeView;
        TreeItem<String> root, duration,summeryLine;
        root = new TreeItem<>();
        duration = new TreeItem<>("Duration");
        summeryLine = new TreeItem<>("Summery Line");
        for(StepUsageDeclaration step:selectedFlowExecution.getFlowDefinition().getFlowSteps()) {
            TreeItem<String> stepName=new TreeItem<>(step.getFinalStepName());
            makeBrunch("Duration: " + selectedFlowExecution.getStepToLogger().get(step.getFinalStepName()).getDuration().toMillis() +"[ms]" ,stepName);
            makeBrunch("summeryLine: " + selectedFlowExecution.getStepToSummeryLine().get(step.getFinalStepName()),stepName);
            root.getChildren().add(stepName);
        }
        stepDetailsTreeView=new TreeView<>(root);
        stepDetailsTreeView.setShowRoot(false);
        stepsDetailsTitlePane.setContent(stepDetailsTreeView);

    }
    public void rerunFlowExecution() throws IOException {
        //baseScreen.getHandel().getPastFlowsExecuted().setExecutorService(baseScreen.getStepper().getThreadPoolSize());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("StepperSceneBuilderFX/Screens/secondScreen/secondScreen.fxml"));
        Parent load = loader.load();
        SecondScreenController secondScreenController=loader.getController();
        baseScreen.setFlowDefinition(selectedFlowExecution.getFlowDefinition());
        secondScreenController.setBaseScreen(baseScreen);
        secondScreenController.setStage(stage);
        secondScreenController.setCurrentScene(currentScene);
        secondScreenController.setXmlPath(xmlPath);
        secondScreenController.setRerunFlowExecution(selectedFlowExecution);
        secondScreenController.setSecondScreen();
        Scene scene= new Scene(load,800,600);
        stage.setScene(scene);
        stage.show();
    }
    public void setFirstScreenScene(Scene scene) {
        stage.setScene(scene);
//        this.rerunRoot=rerunRoot;
//        this.collectFlowInputsVB=vBox;
    }


}

