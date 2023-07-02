package flow.execution.context;

import flow.definition.api.FlowDefinition;
import flow.execution.MyLogger;
import javafx.util.Pair;
import step.api.DataDefinitionDeclaration;
import step.api.StepDefinition;

import java.util.List;
import java.util.Map;

public interface StepExecutionContext {
    <T> T getDataValue(String dataName, Class<T> expectedDataType);
    boolean storeDataValue(String dataName, Object value);
    List<DataDefinitionDeclaration> getFreeInputs();
    void setFreeOutputs(List<DataDefinitionDeclaration> freeOutputs);

    void setFreeInputs(List<DataDefinitionDeclaration> FreeInputs,Map<String, Object> UserInputs);
    void setMapping(Map<Pair<String, DataDefinitionDeclaration>, Pair<String, DataDefinitionDeclaration>> mapping);
void setCurrentFlow(FlowDefinition flow);
FlowDefinition getCurrentFlow();
    void setStepMessages(MyLogger stepMessages);
     MyLogger getStepMessages();
    public void setSummeryLine(String summeryLine);
    public String getSummeryLine();
    Map<String,Object> getDataValues();
    // some more utility methods:
    // allow step to store log lines
    // allow steps to declare their summary line
}
