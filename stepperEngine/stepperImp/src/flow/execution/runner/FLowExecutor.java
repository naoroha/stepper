package flow.execution.runner;

import flow.definition.api.StepUsageDeclaration;
import flow.execution.api.FlowExecution;
import flow.execution.api.FlowExecutionResult;
import flow.execution.context.StepExecutionContext;
import flow.execution.context.StepExecutionContextImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import step.api.StepResult;

public class FLowExecutor {
    private StringProperty currentStepProperty = new SimpleStringProperty("");
    public Task<Void> executeFlow(FlowExecution flowExecution) {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                StepExecutionContext context = new StepExecutionContextImpl(); // actual object goes here...
                context.setFreeInputs(flowExecution.getFlowDefinition().getFlowFreeInputs(), flowExecution.getFlowData());
                context.setFreeOutputs(flowExecution.getFlowDefinition().getFlowFreeOutputs());
                context.setMapping(flowExecution.getFlowDefinition().getMapping());
                context.setCurrentFlow(flowExecution.getFlowDefinition());
                // populate context with all free inputs (mandatory & optional) that were given from the user
                // (typically stored on top of the flow execution object)

                // start actual execution
                int totalIterations = flowExecution.getFlowDefinition().getFlowSteps().size();

                for (int i = 0; i < totalIterations; i++) {
                    Thread.sleep(200);
                    //int progress = (i + 1) * 100 / totalIterations;
                    StepUsageDeclaration stepUsageDeclaration = flowExecution.getFlowDefinition().getFlowSteps().get(i);
                    //System.out.println("Starting to execute step: " + stepUsageDeclaration.getFinalStepName());
                    StepResult stepResult = stepUsageDeclaration.getStepDefinition().invoke(context);
                    flowExecution.setStepSummeryLine(stepUsageDeclaration.getFinalStepName(), context.getSummeryLine());
                    flowExecution.setStepLoggers(stepUsageDeclaration.getFinalStepName(), context.getStepMessages());
                    //System.out.println("Done executing step: " + stepUsageDeclaration.getFinalStepName() + ". Result: " + stepResult);
                    if (stepResult == StepResult.FAILURE) {
                        if (!stepUsageDeclaration.skipIfFail()) {
                            flowExecution.setFlowExecutionResult(FlowExecutionResult.FAILURE);
                            return null;
                        } else
                            flowExecution.setFlowExecutionResult(FlowExecutionResult.WARNING);
                    } else if (stepResult == StepResult.WARNING) {
                        flowExecution.setFlowExecutionResult(FlowExecutionResult.WARNING);
                    }
//        for(String output:flowExecution.getFlowDefinition().getFlowFormalOutputs()){
//            for(String outputValue:flowExecution.getFlowData().keySet()){
//                if(outputValue.equals(output))
//                    System.out.println(output+" result: "+ flowExecution.getFlowData().get(output));
//            }
                    updateProgress(i + 1, totalIterations);
                    //if(!(stepUsageDeclaration.getFinalStepName().equals(stepUsageDeclaration.getStepDefinition().name())))
                      //  currentStepProperty.set(stepUsageDeclaration.getStepDefinition().name()+", alias name: "+stepUsageDeclaration.getFinalStepName());
                    //else
                        currentStepProperty.set(stepUsageDeclaration.getFinalStepName());
                }
                flowExecution.setFlowData(context.getDataValues());
                return null;
            }
        };
    }
    public StringProperty currentStepProperty(){
        return currentStepProperty;
    }
}
