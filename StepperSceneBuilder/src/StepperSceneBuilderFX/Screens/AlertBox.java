package StepperSceneBuilderFX.Screens;

import flow.definition.api.FlowDefinition;
import flow.execution.api.FlowExecution;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.ArrayList;
import java.util.List;

public class AlertBox {
    static String continueToFlow;

    static boolean answer;
    public static void displayError(String title,String message){
        Stage window=new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setMinWidth(400);
        window.setTitle(title);
        Label label=new Label(message);
        Button okButton=new Button("close");
        okButton.setOnAction(e-> window.close());

        VBox layout= new VBox(10);
        layout.getChildren().addAll(label,okButton);
        layout.setAlignment(Pos.CENTER);

        Scene scene=new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }
    public static String displayContinuation(String title, List<FlowDefinition> flowDefinitionList){
        Stage window=new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setMinWidth(400);
        window.setMaxHeight(400);
        window.setTitle(title);
        Label label=new Label("This flow has a continuation:\n would you like to continue to another flow?");
        label.setStyle("-fx-font-weight: bold; -fx-text-fill:#0fdca5; -fx-font-size: 20px;");
        ListView<String> listView=new ListView<>();
        List<String> flowNames=new ArrayList<>();
        for(FlowDefinition flow:flowDefinitionList){
            flowNames.add(flow.getName());
        }
        listView.getItems().addAll(FXCollections.observableArrayList(flowNames));
        Button yesButton=new Button("continue");
        yesButton.setDisable(true);
        Button noButton=new Button("stop here");
        HBox hBox=new HBox(10);
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (listView.getSelectionModel().getSelectedItem() != null){
                yesButton.setDisable(false);
            }
                    yesButton.setOnAction(e -> {
                        continueToFlow = newValue;
                        window.close();
                    });

                    noButton.setOnAction(e -> {
                        continueToFlow = null;
                        window.close();
                    });
                });
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(yesButton,noButton);
        HBox.setHgrow(hBox,Priority.ALWAYS);
        HBox.setMargin(hBox,new Insets(5,0,5,0));
        listView.setPrefHeight((flowNames.size()+2) * 24);
        VBox layout=new VBox(20);
        VBox.setVgrow(layout, Priority.ALWAYS);
        layout.getChildren().addAll(label,listView,hBox);
        layout.setAlignment(Pos.CENTER);
        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        Scene scene=new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
        return continueToFlow;
    }
}
