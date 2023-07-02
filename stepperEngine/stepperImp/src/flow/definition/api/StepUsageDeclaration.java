package flow.definition.api;

import step.api.DataDefinitionDeclaration;
import step.api.StepDefinition;

import java.util.List;
import java.util.Map;

public interface StepUsageDeclaration {
    String getFinalStepName();
    StepDefinition getStepDefinition();
    boolean skipIfFail();

        Map<DataDefinitionDeclaration,DataDefinitionDeclaration> getFinalInputs();
        DataDefinitionDeclaration getFinalInputByOriginalName(String originalName);
    Map<DataDefinitionDeclaration,DataDefinitionDeclaration> getFinalOutputs();
    DataDefinitionDeclaration getFinalOutputByOriginalName(String originalName);
        public DataDefinitionDeclaration getDataDefinitionDeclarationByName(Map<DataDefinitionDeclaration, DataDefinitionDeclaration> finalSource, String sourceOutputName);
}
