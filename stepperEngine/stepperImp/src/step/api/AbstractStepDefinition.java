package step.api;

import step.StepDefinitionRegistry;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractStepDefinition implements StepDefinition{
    private final String stepName;
    private final boolean readonly;
    private final List<DataDefinitionDeclaration> inputs;
    private final List<DataDefinitionDeclaration> outputs;

    public AbstractStepDefinition(String stepName, boolean readonly) {
        this.stepName = stepName;
        this.readonly = readonly;
        inputs = new ArrayList<>();
        outputs = new ArrayList<>();
    }
    public DataDefinitionDeclaration OutputbyName(String name) {
        for (DataDefinitionDeclaration DDD : outputs) {
            if (DDD.getName().equals(name)) {
                return DDD;
            }
        }
        //throw new NullPointerException("there is no such Output Name");
        return null;

    }


    public DataDefinitionDeclaration InputbyName(String name) {

        for (DataDefinitionDeclaration DDD : inputs) {
            {
                if (DDD.getName().equals(name))
                    return DDD;
            }
        }
        //throw new NullPointerException("there is no such Input Name");
        return null;

    }

    protected void addInput(DataDefinitionDeclaration dataDefinitionDeclaration) {
        inputs.add(dataDefinitionDeclaration);
    }

    protected void addOutput(DataDefinitionDeclaration dataDefinitionDeclaration) {
        outputs.add(dataDefinitionDeclaration);
    }

    @Override
    public String name() {
        return stepName;
    }

    @Override
    public boolean isReadonly() {
        return readonly;
    }

    @Override
    public List<DataDefinitionDeclaration> inputs() {
        return inputs;
    }

    @Override
    public List<DataDefinitionDeclaration> outputs() {
        return outputs;
    }
}
