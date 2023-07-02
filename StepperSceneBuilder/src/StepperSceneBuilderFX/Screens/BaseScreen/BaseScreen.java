package StepperSceneBuilderFX.Screens.BaseScreen;

import commands.MenuFunctions;
import commands.MenuFunctionsImpl;
import flow.definition.api.FlowDefinition;
import flow.execution.api.FlowExecution;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import stepper.StepperDefinition;
import stepper.StepperDefinitionImpl;

public class BaseScreen extends Application {
    private StepperDefinition stepper;
    private MenuFunctions handel;
    private FlowExecution flowExecution;
    private FlowDefinition flowDefinition;
    private  Stage primaryStage;
    private Scene firstScene;
    public BaseScreen(){
        stepper=new StepperDefinitionImpl();
        handel=new MenuFunctionsImpl();
    }
    @Override
    public void start(Stage primaryStage) throws Exception {

    }


    public void setFlowDefinition(FlowDefinition flowDefinition) {
        this.flowDefinition = flowDefinition;
    }

    public FlowDefinition getFlowDefinition() {
        return flowDefinition;
    }

    public void setFlowExecution(FlowExecution flowExecution) {
        this.flowExecution = flowExecution;
    }

    public FlowExecution getFlowExecution() {
        return flowExecution;
    }

    public void setStepper(StepperDefinition stepper) {
        this.stepper = stepper;
    }

    public StepperDefinition getStepper() {
        return stepper;
    }

    public MenuFunctions getHandel() {
        return handel;
    }

    public void setHandel(MenuFunctions handle) {
        this.handel = handle;
    }

    public void setPrimaryStage(Stage stage) {
        this.primaryStage=stage;
    }

    public void setFirstScene(Scene firstScene) {
        this.firstScene = firstScene;
    }

    public Scene getFirstScene() {
        return firstScene;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
