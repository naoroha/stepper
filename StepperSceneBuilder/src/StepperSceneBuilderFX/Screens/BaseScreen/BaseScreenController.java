package StepperSceneBuilderFX.Screens.BaseScreen;

import StepperSceneBuilderFX.Screens.AlertBox;
import StepperSceneBuilderFX.Screens.firstScreen.FirstScreen;
import StepperSceneBuilderFX.Screens.firstScreen.FirstScreenController;
import StepperSceneBuilderFX.Screens.secondScreen.SecondScreen;
import StepperSceneBuilderFX.Screens.secondScreen.SecondScreenController;
import StepperSceneBuilderFX.Screens.statisticScreen.StatisticScreenController;
import StepperSceneBuilderFX.Screens.thirdScreen.ThirdScreenController;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import xml.parsing.ParseXml;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public abstract class BaseScreenController {

    protected BaseScreen baseScreen;
    protected Stage stage;
    protected Stage firstStage;
    protected Scene currentScene;
    protected Scene firstScene;


    @FXML
    protected Button LoadFileBtn;

    @FXML
    protected TextField XmlPathTextField;

    @FXML
    protected Button flowDefinitionBtn;

    @FXML
    protected Button flowExecutionBtn;

    @FXML
    protected Button executionHistoryBtn;

    @FXML
    protected Button statisticsBtn;

    @FXML
    protected ToggleButton darkMode;
    protected SimpleStringProperty xmlPath;

    public BaseScreenController(){
        xmlPath=new SimpleStringProperty();
    }
    @FXML
    public void initialize(){
        XmlPathTextField.textProperty().bind(xmlPath);

    }


    @FXML
    protected void LoadFileAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");
        Stage stage = (Stage) LoadFileBtn.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            xmlPath.set(selectedFile.getAbsolutePath());
            try {
                baseScreen.setStepper(baseScreen.getHandel().applyXMLReaderCommand(xmlPath.getValue()));
            } catch (JAXBException | FileNotFoundException | IllegalArgumentException e) {
                AlertBox.displayError("error", "not a matched file");
            } catch (NoSuchFieldException ignore) {
            } catch (ParseXml.NegativeNumberException e) {
                AlertBox.displayError("error", "thread pool cannot be negative or zero");
            }
        }
    }
    public void showExecutionHistory(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/StepperSceneBuilderFX/Screens/thirdScreen/ThirdScreen.fxml"));
        Parent load = loader.load();
        ThirdScreenController thirdScreenController=loader.getController();
        thirdScreenController.setBaseScreen(baseScreen);
        thirdScreenController.setStage(stage);
        thirdScreenController.setCurrentScene(currentScene);
        thirdScreenController.setXmlPath(xmlPath);
        thirdScreenController.setOldExecutionTable();
        loader.setController(thirdScreenController);
        Scene scene= new Scene(load,800,600);
        //scene.getStylesheets().add(getClass().getResource("screenStyle.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }


    @FXML
    void showStatistics(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/StepperSceneBuilderFX/Screens/statisticScreen/statisticScreen.fxml"));
        Parent load = loader.load();
        StatisticScreenController statisticScreenController=loader.getController();
        statisticScreenController.setBaseScreen(baseScreen);
        statisticScreenController.setStage(stage);
        statisticScreenController.setXmlPath(xmlPath);
        statisticScreenController.setChartData(baseScreen.getHandel().getFlowStatistics());
        Scene scene= new Scene(load,800,600);
        stage.setScene(scene);
        stage.show();
    }

    public BaseScreen getBaseScreen() {
        return baseScreen;
    }

    public void setBaseScreen(BaseScreen baseScreen) {
        this.baseScreen = baseScreen;
    }

    public void setXmlPath(SimpleStringProperty xmlPath) {
        this.xmlPath=xmlPath;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
        public void makeBrunch(String title, TreeItem<String> parent) {
        TreeItem<String> item=new TreeItem<>(title);
        item.setExpanded(true);
        parent.getChildren().add(item);
    }
    @FXML
    void showFlowExecution(ActionEvent event) {
        stage.setScene(currentScene);
        stage.show();
    }
    @FXML
   public void showFlowDefinition(ActionEvent event) throws IOException {
        baseScreen.getPrimaryStage().setScene(baseScreen.getFirstScene());
        baseScreen.getPrimaryStage().show();
    }

    public void setCurrentScene(Scene currentScene) {
        this.currentScene = currentScene;
    }

}
