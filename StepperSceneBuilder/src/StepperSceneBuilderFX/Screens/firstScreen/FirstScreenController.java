package StepperSceneBuilderFX.Screens.firstScreen;


import StepperSceneBuilderFX.Screens.BaseScreen.BaseScreenController;
import StepperSceneBuilderFX.Screens.secondScreen.SecondScreen;
import StepperSceneBuilderFX.Screens.secondScreen.SecondScreenController;
import flow.definition.api.FlowDefinition;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

import static flow.FlowInformationCategory.*;

public class FirstScreenController extends BaseScreenController {
    //private BaseScreen baseScreen;
    @FXML
    private HBox ExecuteButtonHBox;
    @FXML
    private ScrollPane scrollPaneRoot;

    @FXML
    private Button executeButton;


    @FXML
    private VBox AvailableFlowsVB;

    @FXML
    private VBox descriptionVBox;
    public FirstScreenController(){
        xmlPath=new SimpleStringProperty();
    }

    @FXML
    void execution(ActionEvent event) throws IOException {
        baseScreen.getHandel().getPastFlowsExecuted().setExecutorService(baseScreen.getStepper().getThreadPoolSize());
        flowDefinitionBtn.setDisable(false);
        executionHistoryBtn.setDisable(false);
        statisticsBtn.setDisable(false);
        baseScreen.setFirstScene(flowExecutionBtn.getScene());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/StepperSceneBuilderFX/Screens/secondScreen/secondScreen.fxml"));
        Parent load = loader.load();
        SecondScreenController secondScreenController=loader.getController();
        secondScreenController.setBaseScreen(baseScreen);
        secondScreenController.setStage(stage);
        secondScreenController.setXmlPath(xmlPath);
        secondScreenController.setSecondScreen();
        Scene scene= new Scene(load,800,600);
        scene.getStylesheets().add(getClass().getResource("screenStyle.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
        }

    @FXML
    public void initialize() {
        XmlPathTextField.textProperty().bind(xmlPath);
        scrollPaneRoot.setFitToHeight(true);
        scrollPaneRoot.setFitToWidth(true);
    }

    @FXML
    void LoadFIleAction(ActionEvent event) {
//        //ExeptionLabel.setText("");
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.setTitle("Select File");
//        Stage stage = (Stage) LoadFileBtn.getScene().getWindow();
//        File selectedFile = fileChooser.showOpenDialog(stage);
//        if (selectedFile != null) {
//            String filePath = selectedFile.getAbsolutePath();
//            XmlPathTextField.setText(filePath);
//            try {
//                baseScreen.setStepper(baseScreen.getHandel().applyXMLReaderCommand(filePath));
//            } catch (JAXBException | FileNotFoundException | IllegalArgumentException e) {
//                //ExeptionLabel.setText("not matched file");
//            } catch (NoSuchFieldException ignore) {
//            } catch (ParseXml.NegativeNumberException e) {
//                //ExeptionLabel.setText("negative or zero Threadpool");
//            }
    super.LoadFileAction(event);
            initializeAvailableFlowVB();

    }

    public void initializeAvailableFlowVB() {
        Label availableFlowLabel = new Label("Available Flows: ");
        availableFlowLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: blue;");
        Button button;
        EventHandler<ActionEvent> buttonHandler = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                descriptionVBox.getChildren().clear();
                Button clickedButton = (Button) event.getSource();
                FlowDefinition flowDefinition = baseScreen.getStepper().getFlowDefinitionByName(clickedButton.getText());
                baseScreen.setFlowDefinition(flowDefinition);
                initializeDescription(flowDefinition);
                executeButton.setVisible(true);
                //initializeExecuteButtonHBox(flowDefinition);
                //SelectedFlowDetailsTA.setText(flowDefinition.getFlowDescription().get(CategoryChoiceBox.getValue()).toString());
            }
        };
        for (FlowDefinition flow : baseScreen.getStepper().getFlows()) {
            button = new Button(flow.getName());
            button.setOnAction(buttonHandler);
            AvailableFlowsVB.getChildren().add(button);
        }

    }

    private void initializeExecuteButtonHBox(FlowDefinition flowDefinition) {

    }

    private void initializeDescription(FlowDefinition flowDefinition) {
        baseScreen.getHandel().showFlow(flowDefinition);
        Label flowNameLabel = new Label(flowDefinition.getName());
        VBox.setVgrow(descriptionVBox, Priority.ALWAYS);
        flowNameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        descriptionVBox.getChildren().add(flowNameLabel);
        Text flowDescriptionText = new Text(baseScreen.getFlowDefinition().getDescription());
        flowDescriptionText.setWrappingWidth(500);
        descriptionVBox.getChildren().add(flowDescriptionText);
        TreeView<String> treeView;
        TreeItem<String> root, formalOutputs, flowSteps, freeInputs;
        root = new TreeItem<>();
        flowSteps = new TreeItem<>("Flow Steps");
        freeInputs = new TreeItem<>("Free Inputs");
        formalOutputs = new TreeItem<>("Formal Outputs");
        makeBrunch(flowDefinition.getFlowDescription().get(FORMAL_OUTPUTS).toString(),formalOutputs);
        makeBrunch(flowDefinition.getFlowDescription().get(FREE_INPUTS).toString(),freeInputs);
        makeBrunch(flowDefinition.getFlowDescription().get(STEPS).toString(),flowSteps);
        root.getChildren().add(formalOutputs);
        root.getChildren().add(flowSteps);
        root.getChildren().add(freeInputs);
        treeView=new TreeView<>(root);
        treeView.setShowRoot(false);
        descriptionVBox.getChildren().add(treeView);
    }

//    public void makeBrunch(String title, TreeItem<String> parent) {
//        TreeItem<String> item=new TreeItem<>(title);
//        item.setExpanded(true);
//        parent.getChildren().add(item);
//    }
}


