package flow.definition.api;

import step.api.DataDefinitionDeclaration;
import step.api.StepDefinition;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


public class StepUsageDeclarationImpl implements StepUsageDeclaration {
    private final StepDefinition stepDefinition;
    private final boolean skipIfFail;
    private final String stepName;
    private Map<DataDefinitionDeclaration, DataDefinitionDeclaration> finalInputs;
    private Map<DataDefinitionDeclaration, DataDefinitionDeclaration> finalOutputs;
    // the first map represents finalOutputs of step x the other represents the finalInputs of step y
    //private Map<Map<DataDefinitionDeclaration, DataDefinitionDeclaration>
           // , Map<DataDefinitionDeclaration, DataDefinitionDeclaration>> costumeMapping;


    public StepUsageDeclarationImpl(StepDefinition stepDefinition) {
        this(stepDefinition, false, stepDefinition.name());
    }

    public StepUsageDeclarationImpl(StepDefinition stepDefinition, String name) {
        this(stepDefinition, false, name);
    }

    public StepUsageDeclarationImpl(StepDefinition stepDefinition, boolean skipIfFail, String stepName) {
        this.stepDefinition = stepDefinition;
        this.skipIfFail = skipIfFail;
        this.stepName = stepName;
        this.finalInputs = new LinkedHashMap<>();
        this.finalOutputs = new LinkedHashMap<>();
        //initialize the maps to identify functions for now
        finalInputs = this.getStepDefinition().inputs().stream().collect(Collectors.toMap(s -> s, s -> s));
        finalOutputs = this.getStepDefinition().outputs().stream().collect(Collectors.toMap(s -> s, s -> s));
       // this.costumeMapping = new HashMap<>();

    }

    @Override
    public String getFinalStepName() {
        return stepName;
    }

    @Override
    public StepDefinition getStepDefinition() {
        return stepDefinition;
    }


    @Override
    public boolean skipIfFail() {
        return skipIfFail;
    }


    @Override
    public Map<DataDefinitionDeclaration, DataDefinitionDeclaration> getFinalInputs() {
        return finalInputs;
    }

    @Override
    public DataDefinitionDeclaration getFinalInputByOriginalName(String originalName) {
        for(DataDefinitionDeclaration input: finalInputs.keySet()){
            if(input.getName().equals(originalName))
                return finalInputs.get(input);
        }
        return null;
    }

    @Override
    public Map<DataDefinitionDeclaration, DataDefinitionDeclaration> getFinalOutputs() {
        return finalOutputs;
    }

    @Override
    public DataDefinitionDeclaration getFinalOutputByOriginalName(String originalName) {
        for(DataDefinitionDeclaration output: finalOutputs.keySet()){
            if(output.getName().equals(originalName))
                return finalOutputs.get(output);
        }
        return null;

    }

    @Override
    public DataDefinitionDeclaration getDataDefinitionDeclarationByName(Map<DataDefinitionDeclaration, DataDefinitionDeclaration> finalSource, String sourceOutputName) {
        return finalSource.values().stream()
                .filter(key -> key.getName().equals(sourceOutputName))
                .findFirst()
                .orElse(null);
    }


}



