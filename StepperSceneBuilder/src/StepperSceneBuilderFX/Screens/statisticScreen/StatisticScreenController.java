package StepperSceneBuilderFX.Screens.statisticScreen;
import flow.definition.api.FlowDefinition;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import StepperSceneBuilderFX.Screens.BaseScreen.BaseScreenController;
import commands.Statistics;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.List;
import java.util.Map;

public class StatisticScreenController extends BaseScreenController {


    @FXML
    private BarChart<String, Number> flowStatistics;

    @FXML
    private PieChart stepStatistics;
    private ObservableList<XYChart.Series<String, Number>> data;
    @FXML
    private HBox pieTableHbox;
    @FXML
    private CategoryAxis categoryAxis;

    @FXML
    private NumberAxis numberAxis;
    @FXML
    private VBox stepsName;
    @FXML
    private Label stepNameLabel;

    @FXML
    private VBox howManyTimesVBox;

    @FXML
    private Label howManyTimesLabel;

    @FXML
    private VBox average;

    @FXML
    private Label averageDurationLabel;


    public void initialize() {
//        XmlPathTextField.textProperty().bind(xmlPath);
//        XmlPathTextField.setStyle("-fx-text-fill: lightgray; -fx-opacity: 1;");
//        XmlPathTextField.setDisable(true);
        data = FXCollections.observableArrayList();
        executionHistoryBtn.setDisable(false);
        flowDefinitionBtn.setDisable(false);
    }

    public void setChartData(Map<String, Statistics> chartData) {
        XmlPathTextField.textProperty().bind(xmlPath);
        XmlPathTextField.setStyle("-fx-text-fill: lightgray; -fx-opacity: 1;");
        XmlPathTextField.setDisable(true);
        data.clear(); // Clear existing data

        for (String name : chartData.keySet()) {
            long totalTime = chartData.get(name).getTotalDuration().toMillis();
            double timesPressed = chartData.get(name).getHowManyTimes();

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(name);

            XYChart.Data<String, Number> dataPoint = new XYChart.Data<>(name, timesPressed);
            series.getData().add(dataPoint);

            // Create a custom data point node with a stack pane
            StackPane dataNode = new StackPane();
            StackPane.setAlignment(dataNode, Pos.TOP_CENTER);
            dataPoint.setNode(dataNode);
            // Set the tooltip on the data point node
            Tooltip tooltip = new Tooltip("Average run time: " + average(totalTime, timesPressed));
            tooltip.setShowDelay(Duration.ZERO);
            Tooltip.install(dataNode, tooltip);
            dataNode.setOnMouseClicked(event -> showStepStatistic(baseScreen.getHandel().getStepStatistics().get(name)));


            data.add(series);
        }

        // Set the custom labels as tick labels for each category
        flowStatistics.setBarGap(-100);
        flowStatistics.setData(data);
    }


    private double average(long totalTime, double howMany) {
        if (howMany == 0) {
            return 0;
        }
        return totalTime / howMany;

    }

    void showStepStatistic(Map<String, Statistics> chartData) {
        pieTableHbox.getChildren().clear();
        pieTableHbox.setVisible(true);
        stepStatistics.getData().clear();
        stepsName.getChildren().clear();
        stepsName.getChildren().add(stepNameLabel);
        howManyTimesVBox.getChildren().clear();
        howManyTimesVBox.getChildren().add(howManyTimesLabel);
        average.getChildren().clear();
        average.getChildren().add(averageDurationLabel);
        pieTableHbox.getChildren().addAll(stepsName,howManyTimesVBox,average);
        for (String name : chartData.keySet()) {
            long totalTime=chartData.get(name).getTotalDuration().toMillis();
            double howManyTimes = chartData.get(name).getHowManyTimes();
            stepsName.getChildren().add(new Label(name));
            howManyTimesVBox.getChildren().add(new Label(Double.toString(howManyTimes)));
            average.getChildren().add(new Label(Double.toString(average(totalTime,howManyTimes))));
            PieChart.Data data = new PieChart.Data(name,howManyTimes);
            stepStatistics.getData().add(data);
        }
    }
}
