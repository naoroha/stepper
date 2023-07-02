package StepperSceneBuilderFX.Screens.firstScreen;

import StepperSceneBuilderFX.Screens.BaseScreen.BaseScreen;
import StepperSceneBuilderFX.Screens.secondScreen.SecondScreen;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FirstScreen extends BaseScreen {
    private FirstScreenController controller;

//    public FirstScreen(){
//        controller=new FirstScreenController();
//    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Stepper");
        this.setPrimaryStage(primaryStage);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("StepperSceneBuilder.fxml"));
        Parent load = loader.load();
        controller = loader.getController();
        controller.setBaseScreen(this);
        controller.setStage(primaryStage);
        Scene scene= new Scene(load,800,600);
        scene.getStylesheets().add(getClass().getResource("screenStyle.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }
   public static void main(String[] args){launch(args);}
}
