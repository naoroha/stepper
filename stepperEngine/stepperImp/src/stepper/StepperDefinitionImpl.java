package stepper;

import flow.definition.api.FlowDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StepperDefinitionImpl implements StepperDefinition {
    private List<FlowDefinition> flows;
    private int threadPoolSize;
    @Override

    public int getThreadPoolSize() {
        return threadPoolSize;
    }
    @Override

    public void setThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    public StepperDefinitionImpl(){
        flows = new ArrayList<>();
    }
    @Override
    public void addFlow(FlowDefinition flowToAdd) {
        flows.add(flowToAdd);
    }

    @Override
    public void setFlows(List<FlowDefinition> Flows) {
        this.flows=flows;
    }

    @Override
    public FlowDefinition getFlowDefinitionByName(String name) {
        for(FlowDefinition flow:flows){
            if(flow.getName().equals(name))
                return flow;
        }
        return flows.get(0);
    }

    @Override
    public List<FlowDefinition> getFlows() {
        return flows;
    }

}
