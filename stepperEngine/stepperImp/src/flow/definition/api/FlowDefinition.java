package flow.definition.api;

import flow.FlowInformationCategory;
import javafx.util.Pair;
import step.api.DataDefinitionDeclaration;

import java.util.List;
import java.util.Map;

public interface FlowDefinition {
    List<FlowDefinition> getContinuation();
    String getName();
    String getDescription();
    List<StepUsageDeclaration> getFlowSteps();
    List<String> getFlowFormalOutputs();
    void validateFlowStructure();
    void addFlowOutput(String outputName);
    void addStepToFlow(StepUsageDeclaration step);
   //void setStepsUsageNameFromSteps(List<StepUsageDeclaration> steps);
     StepUsageDeclaration GetStepByName(String sourceStepName);
     StepUsageDeclaration getStepUsageDeclarationByOriginalName(String originalName);

    List<DataDefinitionDeclaration> getFlowFreeInputs();
    Map<String,String> getStepOriginalToAliasName();
    void setStepOriginalToAliasName(Map<String,String> originalToAlias);

    List<DataDefinitionDeclaration> getFlowFreeOutputs();
    String getStepNameByFreeInput(DataDefinitionDeclaration input);


    void setMapping(Map<Pair<String,DataDefinitionDeclaration>, Pair<String,DataDefinitionDeclaration>> map);
    Map<Pair<String,DataDefinitionDeclaration>, Pair<String,DataDefinitionDeclaration>> getMapping();
    void setFreeInputs(List<DataDefinitionDeclaration> toSet);
    void setFreeOutputs(List<DataDefinitionDeclaration> toSet);
    Map<FlowInformationCategory, StringBuilder> getFlowDescription();
    void setFlowDescription(Map<FlowInformationCategory, StringBuilder> flowDescription);


    boolean isFlowReadOnly();

    Map<DataDefinitionDeclaration, String> getInitializeInput();

    Map<DataDefinitionDeclaration, DataDefinitionDeclaration> getContinuationMapping();

    DataDefinitionDeclaration getFlowFreeInputByName(String name);

    List<DataDefinitionDeclaration> getFlowFreeOutputs(Map<DataDefinitionDeclaration, DataDefinitionDeclaration> finalSource);


        List<DataDefinitionDeclaration> getFlowFreeInputs( Map<DataDefinitionDeclaration, DataDefinitionDeclaration> finalInputs);

    void setDataDefinitionDeclarationFlowOutputs();

    List<DataDefinitionDeclaration> getDataDefinitionDeclarationFlowOutputs();

    DataDefinitionDeclaration getDataDefinitionDeclarationFlowOutputByName(String name);
}