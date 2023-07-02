package stepper;

import flow.definition.api.FlowDefinition;

import java.util.List;
import java.util.concurrent.ExecutorService;

public interface StepperDefinition {
    List<FlowDefinition> getFlows();

    int getThreadPoolSize();

    void setThreadPoolSize(int threadPoolSize);

    void addFlow(FlowDefinition flowToAdd);
     void setFlows(List<FlowDefinition> Flows);
     FlowDefinition getFlowDefinitionByName(String name);
}
