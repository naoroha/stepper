package StepperSceneBuilderFX.Screens.secondScreen;

import StepperSceneBuilderFX.Screens.AlertBox;
import StepperSceneBuilderFX.Screens.BaseScreen.BaseScreen;
import StepperSceneBuilderFX.Screens.BaseScreen.BaseScreenController;
import StepperSceneBuilderFX.Screens.thirdScreen.ThirdScreenController;
import flow.FlowInformationCategory;
import flow.definition.api.FlowDefinition;
import flow.execution.api.FlowExecution;
import flow.execution.impl.FlowExecutionImpl;
import flow.execution.runner.FLowExecutor;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import step.api.DataDefinitionDeclaration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static flow.FlowInformationCategory.FREE_INPUTS;
import static flow.FlowInformationCategory.STEPS;

public class SecondScreenController extends BaseScreenController {
    private Future<?> currentTask;

    @FXML
    private AnchorPane secondScreenRoot;
    private Timer statusTimer;
    private FXMLLoader loader;

    @FXML
    private BorderPane borderPaneRoot;

    @FXML
    private AnchorPane basePaneRoot;
//    private AnchorPane rerunBasePaneRoot;
    @FXML
    private AnchorPane executeHBox;
//    private AnchorPane rerunExecuteHBox;
    private FlowExecution rerunFlow;
    private FlowExecution continueExecution;
    @FXML
    private ScrollPane scrollPaneRoot;

    private TreeView<String> outputTreeView;
    private ListView<String> logListView;
    private Label statusLabel;
    private ProgressBar progressBar;

    private TreeItem <String> outputRoot;

    @FXML
    private VBox collectFlowInputsVB;
    @FXML
    private ScrollPane scrollPaneRoot1;


    @FXML
    private Button startExecutionBtn;

    @FXML
    private VBox freeInputDescription;
    @FXML
    private VBox executionDetailsVbox;



    @FXML
        public void initialize(){
        flowDefinitionBtn.setDisable(false);
        VBox.setVgrow(freeInputDescription, Priority.ALWAYS);
        logListView=new ListView<>();
        XmlPathTextField.setStyle("-fx-text-fill: lightgray; -fx-opacity: 1;");
        XmlPathTextField.setDisable(true);
        scrollPaneRoot.setFitToHeight(true);
        scrollPaneRoot.setFitToWidth(true);
        //borderPaneRoot=(BorderPane) secondScreenRoot.getChildren().get(0);
    }


    public void setSecondScreen() {
        XmlPathTextField.textProperty().bind(xmlPath);
        baseScreen.getHandel().showFlow(baseScreen.getFlowDefinition());
        Label flowNameLabel = new Label(baseScreen.getFlowDefinition().getName());
        flowNameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        freeInputDescription.getChildren().add(flowNameLabel);
        Text flowDescriptionText = new Text(baseScreen.getFlowDefinition().getDescription());
        flowDescriptionText.setWrappingWidth(250);
        freeInputDescription.getChildren().add(flowDescriptionText);
        TreeView<String> treeView;
        TreeItem<String> root, formalOutputs, flowSteps, freeInputs;
        root = new TreeItem<>();
        flowSteps = new TreeItem<>("Flow Steps");
        freeInputs = new TreeItem<>("Free Inputs");
        formalOutputs = new TreeItem<>("Formal Outputs");
        makeBrunch(baseScreen.getFlowDefinition().getFlowDescription().get(FlowInformationCategory.FORMAL_OUTPUTS).toString(), formalOutputs);
        makeBrunch(baseScreen.getFlowDefinition().getFlowDescription().get(FREE_INPUTS).toString(), freeInputs);
        makeBrunch(baseScreen.getFlowDefinition().getFlowDescription().get(STEPS).toString(), flowSteps);
        root.getChildren().add(formalOutputs);
        root.getChildren().add(flowSteps);
        root.getChildren().add(freeInputs);
        treeView = new TreeView<>(root);
        treeView.setShowRoot(false);
        freeInputDescription.getChildren().add(treeView);
        if (rerunFlow != null) {
            setFlowInputs(rerunFlow, true);

        } else if (continueExecution != null) {
            setFlowInputs(continueExecution, false);

        } else {
            UUID id = UUID.randomUUID();
            String Unique = id.toString();
            baseScreen.setFlowExecution(new FlowExecutionImpl(Unique, baseScreen.getFlowDefinition()));
            baseScreen.getHandel().setFilled(baseScreen.getFlowDefinition().getFlowFreeInputs());
            setFlowInputs(baseScreen.getFlowExecution(), false);
        }
    }
    public void setFlowInputs(FlowExecution flowExecution,boolean rerun) {
        Tooltip tooltip = new Tooltip("Execution available just after all the mandatory inputs are define!");
        Tooltip.install(startExecutionBtn, tooltip);

            for (DataDefinitionDeclaration input : flowExecution.getFlowDefinition().getFlowFreeInputs()) {
                if (flowExecution.getFlowDefinition().getInitializeInput().containsKey(input)) {
                    setFlowData(input, baseScreen.getFlowDefinition().getInitializeInput().get(input), flowExecution);
                } else{
                    HBox hBox = new HBox(10);
                    HBox.setHgrow(hBox, Priority.ALWAYS);
                    hBox.setFillHeight(true);
                    Label label = new Label(input.getName());
                    label.setStyle("-fx-font-weight: bold; -fx-text-fill:#0fdca5;");
                    TextField inputField = new TextField();
                    inputField.setStyle("-fx-text-fill: lightgray; -fx-opacity: 1;");
                    Label necessity = new Label(input.necessity().name());
                    necessity.setStyle("-fx-font-weight: bold; -fx-text-fill:#114ce1;");
                    inputField.setOnMouseClicked(event -> {
                        input.setFilled(false);
                        setExecution();

                        Stage currentStage = (Stage) inputField.getScene().getWindow();
                        File selectedFolder = null;
                        if (input.getCategorizedData().name().equals("PATH")) {
                            DirectoryChooser directoryChooser = new DirectoryChooser();
                            selectedFolder = directoryChooser.showDialog(currentStage);
                        } else if (input.getCategorizedData().name().equals("FILE")) {
                            FileChooser fileChooser = new FileChooser();
                            selectedFolder = fileChooser.showOpenDialog(currentStage);
                        }
                        if (selectedFolder != null) {
                            // Set the selected folder path in the TextField
                            inputField.setStyle("");
                            inputField.setText(selectedFolder.getAbsolutePath());
                        } else {

                            inputField.setStyle("");
                            inputField.setText("");
                        }
                    });
                    Button addButton = new Button();
                    addButton.setOnAction(event -> {
                        if (addButton.getText().equals("Add")) {
                            if(setFlowData(input, inputField.getText(), flowExecution)) {
                                setExecution();
                                inputField.setDisable(true);
                                addButton.setText("Edit");
                            }
                        } else {
                            inputField.setDisable(false);
                            addButton.setText("Add");
                        }
                    });
                    if ((input.getFilled())) {
                        if (rerun) {
                            if(flowExecution.getFlowData().get(input.getName())!=null) {
                                inputField.setText(flowExecution.getFlowData().get(input.getName()).toString());
                                inputField.setDisable(true);
                                inputField.setStyle("");
                                addButton.setText("Edit");
                                setExecution();
                            }
                            else {
                                inputField.setText(input.userString());
                                addButton.setText("Add");
                            }
                        }
                        else {
                            continue;
                        }
                    } else {
                        inputField.setText(input.userString());
                        addButton.setText("Add");
                    }

                    hBox.getChildren().addAll(inputField, addButton, necessity);
                    this.collectFlowInputsVB.getChildren().addAll(label, hBox);
                    VBox.setVgrow(this.collectFlowInputsVB, Priority.ALWAYS);
                    VBox.setVgrow(this.executionDetailsVbox, Priority.ALWAYS);
                }
            }
    }
    @FXML
    void startExecution(ActionEvent event) {
        executionDetailsVbox.getChildren().clear();
        Accordion accordion=new Accordion();
        collectFlowInputsVB.setDisable(true);
        TitledPane titledPane=new TitledPane("executed flow",collectFlowInputsVB);
        titledPane.setExpanded(true);
        accordion.getPanes().add(titledPane);
        executionDetailsVbox.getChildren().add(accordion);
        VBox vBox=new VBox(10);
        HBox hBox=new HBox(10);
        statusLabel=new Label();
        progressBar=new ProgressBar();
        progressBar.maxWidth(Double.MAX_VALUE);
        hBox.getChildren().addAll(statusLabel,progressBar);
        vBox.getChildren().add(hBox);
        executionDetailsVbox.getChildren().add(vBox);
        if(rerunFlow!=null)
            executeTask(rerunFlow);
        else if (continueExecution!=null) {
            executeTask(continueExecution);
        } else
            executeTask(baseScreen.getFlowExecution());
    }

    private void setExecution() {
        if(baseScreen.getHandel().allMandatory(baseScreen.getFlowDefinition().getFlowFreeInputs())){
            startExecutionBtn.setDisable(false);
        }
        else{
            startExecutionBtn.setDisable(true);
        }
    }

    @FXML
    private boolean setFlowData(DataDefinitionDeclaration input, String inputField,FlowExecution flowExecution) {
        try {
            if (flowExecution.setFlowData(input.dataDefinition().getType(), input.getName(), inputField) && !(inputField.equals("")) && !(inputField.equals(input.userString()))) {
                input.setFilled(true);
                return true;
            } else {
                input.setFilled(false);
                AlertBox.displayError("error", "please try again");
                return false;
            }
        }catch (IllegalArgumentException e){
            input.setFilled(false);
            AlertBox.displayError("error", "you have to enter a positive number");
            return false;
        }

    }


    public void setBaseScreen(BaseScreen baseScreen) {
        this.baseScreen = baseScreen;
    }

    public void executeTask(FlowExecution flowExecution) {
        statusLabel.textProperty().bind(flowExecution.flowStatusLabelProperty());
        flowExecution.setStartTime();
        FLowExecutor fLowExecutor=new FLowExecutor();
        outputRoot=new TreeItem<>();
        outputRoot.setExpanded(false);
        Task<Void> task =fLowExecutor.executeFlow(flowExecution);
        CompletableFuture<Void> future = CompletableFuture.runAsync(task,baseScreen.getHandel().getPastFlowsExecuted().getExecutorService());
            fLowExecutor.currentStepProperty().addListener((observable, oldValue, newValue) -> {
            createTreeItem(newValue,flowExecution);
            createListView(newValue,flowExecution);
        });

        task.setOnRunning(event -> {
            flowExecution.flowStatusLabelProperty().set("Status: running...");
            //updateStatusLabel(statusLabel,"Status: Running");
            updateProgressBar(progressBar,task.getProgress());
            currentScene=flowExecutionBtn.getScene();
            stage=(Stage) currentScene.getWindow();
            flowExecutionBtn.setDisable(false);
        });

        task.setOnSucceeded(event -> {
            flowExecution.flowStatusLabelProperty().set("Status: Completed");
            updateProgressBar(progressBar, 100.0);
            flowExecution.setEndTime();
            flowExecution.setTotalTime();
            baseScreen.getHandel().updateStatistics(flowExecution);
            if(flowExecution.getFlowDefinition().getContinuation().size()>0)
               continuation(flowExecution,AlertBox.displayContinuation("continue",flowExecution.getFlowDefinition().getContinuation()));
        });

        task.setOnFailed(event -> {
            flowExecution.flowStatusLabelProperty().set("Status: Failed");
            updateProgressBar(progressBar,0.0);
            flowExecution.setEndTime();
            baseScreen.getHandel().updateStatistics(flowExecution);
            flowExecutionBtn.setDisable(true);
        });

        baseScreen.getHandel().getPastFlowsExecuted().getExecutorService().execute(task);
        baseScreen.getHandel().getPastFlowsExecuted().setFlowIdToTask(flowExecution.getUniqueId(),future);
        baseScreen.getHandel().getPastFlowsExecuted().addExecutedFlow(flowExecution);
        outputTreeView=new TreeView<>(outputRoot);
        outputTreeView.setShowRoot(false);
        executionDetailsVbox.getChildren().add(outputTreeView);
        executionDetailsVbox.getChildren().add(logListView);

        executionHistoryBtn.setDisable(false);
        statisticsBtn.setDisable(false);
        startExecutionBtn.setDisable(true);
        startExecutionBtn.setVisible(true);

    }

    private void continuation(FlowExecution flowExecution, String aContinue) {
        if(aContinue!=null) {
            stage.close();
            UUID id = UUID.randomUUID();
            String Unique = id.toString();
            FlowDefinition flowToContinue = baseScreen.getStepper().getFlowDefinitionByName(aContinue);
            FlowExecution continueExecution = new FlowExecutionImpl(Unique, flowToContinue);
            Map<DataDefinitionDeclaration, DataDefinitionDeclaration> continueMapper = flowExecution.getFlowDefinition().getContinuationMapping();
            baseScreen.getHandel().setFilled(flowToContinue.getFlowFreeInputs());
            for (DataDefinitionDeclaration continueInput : continueMapper.keySet()) {
                if (!(continueMapper.get(continueInput).getFilled())) {
                    setFlowData(continueMapper.get(continueInput), flowExecution.getFlowData().get(continueInput.getName()).toString(), continueExecution);
                }
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/StepperSceneBuilderFX/Screens/secondScreen/secondScreen.fxml"));
            try {
                loader.load(); // Load the FXML again
                Parent load = loader.getRoot();
                SecondScreenController secondScreenController = loader.getController();
                baseScreen.setFlowDefinition(flowToContinue);
                secondScreenController.setBaseScreen(baseScreen);
                secondScreenController.setStage(stage);
                secondScreenController.setXmlPath(xmlPath);
                secondScreenController.setContinueExecution(continueExecution);
                secondScreenController.setSecondScreen();
                loader.setController(secondScreenController);
                Scene scene = new Scene(load, 800, 600);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String getContinuationMessage(FlowDefinition flowDefinition) {
        //StringBuilder message=new StringBuilder();
        //message.append(flowDefinition.getName()).append(" has a continuation to \n");
        //message.append(flowDefinition.)
        return null;
    }

    @SuppressWarnings("unchecked")
    private void createListView(String newValue, FlowExecution flowExecution) {
        List <String> messages= Arrays.asList(flowExecution.getStepToLogger().get(newValue).printedMessage().split("\\n"));
        List<String> stepLogs=new ArrayList<>();
        for(String message:messages){
            String stepLog=newValue +": " +message;
            stepLogs.add(stepLog);
        }
        Platform.runLater(() -> {
            logListView.getItems().addAll(FXCollections.observableArrayList(stepLogs));
        });
    }
    @SuppressWarnings("unchecked")
    private void createTreeItem(String stepName, FlowExecution flowExecution) {
        TreeItem<String> treeItem = new TreeItem<>(stepName);
        TreeItem<String>  stepResult, summeryLine, duration,howManyLogs;

        stepResult = new TreeItem<>("Step Result");
        makeBrunch(flowExecution.getStepToLogger().get(stepName).getStepResult().name(),stepResult);

        summeryLine = new TreeItem<>("Summery Line");
        makeBrunch(flowExecution.getStepToSummeryLine().get(stepName),summeryLine);

        duration = new TreeItem<>("Duration");
        makeBrunch(String.valueOf(flowExecution.getStepToLogger().get(stepName).getDuration().toMillis()),duration);

        howManyLogs = new TreeItem<>("How Many Logs");
        makeBrunch(String.valueOf(flowExecution.getStepToLogger().get(stepName).getMessages().size()),howManyLogs);

        treeItem.getChildren().addAll(stepResult, summeryLine, duration,howManyLogs);

        outputRoot.getChildren().add(treeItem);
    }

    private void updateProgressBar(ProgressBar progressBar, double progress) {
        // Update the progress bar on the JavaFX Application Thread
        Platform.runLater(() -> {
                progressBar.setProgress(progress / 100.0);
        });
    }
   // @FXML @Override
//    public void showExecutionHistory(ActionEvent event) throws IOException {
//        FXMLLoader loader = new FXMLLoader(ThirdScreenController.class.getResource("ThirdScreen.fxml"));
//        Parent load = loader.load();
//        this.setCurrentScene(startExecutionBtn.getScene());
//        this.setStage((Stage) currentScene.getWindow());
//        ThirdScreenController thirdScreenController=loader.getController();
//        thirdScreenController.setBaseScreen(baseScreen);
//        thirdScreenController.setStage(stage);
//        thirdScreenController.setCurrentScene(currentScene);
//        thirdScreenController.setXmlPath(xmlPath);
//        thirdScreenController.setOldExecutionTable();
//        loader.setController(thirdScreenController);
//        Scene scene= new Scene(load,800,600);
//        //scene.getStylesheets().add(getClass().getResource("screenStyle.css").toExternalForm());
//        stage.setScene(scene);
//        stage.show();
//    }

    public void setRerunFlowExecution(FlowExecution selectedFlowExecution) {
        UUID id = UUID.randomUUID();
        String Unique = id.toString();
        this.rerunFlow=new FlowExecutionImpl(Unique,selectedFlowExecution.getFlowDefinition());
        this.rerunFlow.setFlowData(selectedFlowExecution.getFlowData());
    }
    public void setContinueExecution(FlowExecution continueExecution) {
        this.continueExecution = continueExecution;
    }
    public FXMLLoader getLoader() {
        return loader;
    }
}
