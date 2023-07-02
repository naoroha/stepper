package flow.definition.api;

import flow.FlowInformationCategory;
import javafx.util.Pair;
import step.api.DataDefinitionDeclaration;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class FlowDefinitionImpl implements FlowDefinition {
    private final String name;
    private Map<DataDefinitionDeclaration,String> initializeInput;
    private final String description;
    private final List<String> flowOutputs;
    private List<DataDefinitionDeclaration> dataDefinitionDeclarationFlowOutputs;
    private List<FlowDefinition> continuation;
    private Map<DataDefinitionDeclaration, DataDefinitionDeclaration> continuationMapping;
    private final List<StepUsageDeclaration> steps;
    private Map<String, String> stepOriginalToAlias;

    private List<DataDefinitionDeclaration> freeInputs;
    private List<DataDefinitionDeclaration> freeOutputs;
    private Map<Pair<String, DataDefinitionDeclaration>, Pair<String, DataDefinitionDeclaration>> mapping;
    private boolean flowReadOnly;
    private Map<FlowInformationCategory, StringBuilder> flowDescription;


    public FlowDefinitionImpl(String name, String description) {
        this.name = name;
        this.description = description;
        continuation=new ArrayList<>();
        initializeInput=new HashMap<>();
        flowOutputs = new ArrayList<>();
        steps = new ArrayList<>();
        mapping = new LinkedHashMap<>();
        freeInputs = new ArrayList<>();
        freeOutputs = new ArrayList<>();
        flowDescription = new HashMap<>();
        this.stepOriginalToAlias = new LinkedHashMap<>();
        this.dataDefinitionDeclarationFlowOutputs = new ArrayList<>();
        continuationMapping=new HashMap<>();
    }

    @Override
    public Map<DataDefinitionDeclaration, String> getInitializeInput() {
        return initializeInput;
    }

    public Map<DataDefinitionDeclaration, DataDefinitionDeclaration> getContinuationMapping() {
        return continuationMapping;
    }

    public List<FlowDefinition> getContinuation() {
        return continuation;
    }

    public void setContinuation(List<FlowDefinition> continuation) {
        this.continuation = continuation;
    }

    public Map<FlowInformationCategory, StringBuilder> getFlowDescription() {
        return flowDescription;
    }

    public void setFlowDescription(Map<FlowInformationCategory, StringBuilder> flowDescription) {
        this.flowDescription = flowDescription;
    }

    @Override
    public void addFlowOutput(String outputName) {
        flowOutputs.add(outputName);
    }

    @Override
    public void addStepToFlow(StepUsageDeclaration step) {
        steps.add(step);
    }

    @Override
    public void validateFlowStructure() {
        for (DataDefinitionDeclaration input : freeInputs) {
            if (!input.dataDefinition().isUserFriendly())
                throw new IllegalArgumentException("validation failed: there is a free input that is not user friendly.");
        }
    }


    @Override
    public List<DataDefinitionDeclaration> getFlowFreeInputs() {
        return freeInputs;
    }

    @Override
    public Map<String, String> getStepOriginalToAliasName() {
        return stepOriginalToAlias;
    }

    @Override
    public void setStepOriginalToAliasName(Map<String, String> originalToAlias) {
        this.stepOriginalToAlias = originalToAlias;

    }


    @Override
    public List<DataDefinitionDeclaration> getFlowFreeOutputs() {
        return freeOutputs;
    }


    @Override
    public void setMapping(Map<Pair<String, DataDefinitionDeclaration>, Pair<String, DataDefinitionDeclaration>> map) {
        this.mapping = map;
    }

    @Override
    public Map<Pair<String, DataDefinitionDeclaration>, Pair<String, DataDefinitionDeclaration>> getMapping() {
        return mapping;
    }

    @Override
    public void setFreeInputs(List<DataDefinitionDeclaration> toSet) {
        this.freeInputs = toSet;
    }

    @Override
    public void setFreeOutputs(List<DataDefinitionDeclaration> toSet) {
        this.freeOutputs = toSet;
    }

    @Override
    public String getName() {
        return name;
    }


    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public List<StepUsageDeclaration> getFlowSteps() {
        return steps;
    }

    @Override
    public List<String> getFlowFormalOutputs() {
        return flowOutputs;
    }

    public void isReadOnly() {
        flowReadOnly = !(steps.stream().anyMatch(b -> !(b.getStepDefinition().isReadonly())));
    }


    @Override
    public StepUsageDeclaration GetStepByName(String sourceStepName) {
        return steps.stream()
                .filter(key -> key.getFinalStepName().equals(sourceStepName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("there is no such source"));
    }

    @Override
    public boolean isFlowReadOnly() {
        return flowReadOnly;
    }

    @Override
    public List<DataDefinitionDeclaration> getFlowFreeInputs(Map<DataDefinitionDeclaration, DataDefinitionDeclaration> finalSource) {
        List<DataDefinitionDeclaration> result = new ArrayList<>();
        for (DataDefinitionDeclaration output : finalSource.values()) {
            boolean isItFreeInput = false;
            for (Pair<String, DataDefinitionDeclaration> key : mapping.keySet()) {
                if (key.getValue().equals(output)) {
                    isItFreeInput = true;
                    break;
                }
            }
            if (!isItFreeInput) {
                result.add(output);
            }
        }
        return result;
    }
    @Override
    public DataDefinitionDeclaration getFlowFreeInputByName(String name) {
        for (DataDefinitionDeclaration input : this.freeInputs) {
            if (input.getName().equals(name)) {
                return input;
            }
        }
        return null;
    }



        @Override
    public List<DataDefinitionDeclaration> getFlowFreeOutputs(Map<DataDefinitionDeclaration, DataDefinitionDeclaration> finalSource) {
        List<DataDefinitionDeclaration> result = new ArrayList<>();
        for (DataDefinitionDeclaration output : finalSource.values()) {
            boolean isItFreeInput = false;
            for (Pair<String, DataDefinitionDeclaration> value : mapping.values()) {
                if (value.getValue().equals(output)) {
                    isItFreeInput = true;
                    break;
                }
            }
            if (!isItFreeInput) {
                result.add(output);
            }
        }
        return result;
    }

    public StepUsageDeclaration getStepUsageDeclarationByOriginalName(String originalName) {
        for (StepUsageDeclaration step : steps) {
            if (step.getStepDefinition().name().equals(originalName))
                return step;
        }
        return null;
    }

    public String getStepNameByFreeInput(DataDefinitionDeclaration input) {
        for (StepUsageDeclaration step : steps) {
            for (DataDefinitionDeclaration finalInput : step.getFinalInputs().values()) {
                if (finalInput.getName().equals(input.getName()))
                    return step.getFinalStepName();
            }
        }
        return null;

    }

    @Override
    public void setDataDefinitionDeclarationFlowOutputs() {
        for (String output : flowOutputs) {
            for (StepUsageDeclaration step : steps)
                if (step.getDataDefinitionDeclarationByName(step.getFinalOutputs(), output) != null)
                    dataDefinitionDeclarationFlowOutputs.add(step.getDataDefinitionDeclarationByName(step.getFinalOutputs(), output));
        }
    }

    @Override
    public List<DataDefinitionDeclaration> getDataDefinitionDeclarationFlowOutputs() {
        return dataDefinitionDeclarationFlowOutputs;
    }

    @Override
    public DataDefinitionDeclaration getDataDefinitionDeclarationFlowOutputByName(String name) {
        for (DataDefinitionDeclaration output : this.freeOutputs) {
            if (output.getName().equals(name)) {
                return output;
            }
        }
        return null;
    }
}

