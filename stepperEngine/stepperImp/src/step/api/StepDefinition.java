package step.api;

import flow.execution.context.StepExecutionContext;
import step.api.DataDefinitionDeclaration;

import java.io.IOException;
import java.util.List;

public interface StepDefinition {
    String name();
    boolean isReadonly();
    List<DataDefinitionDeclaration> inputs();
    List<DataDefinitionDeclaration> outputs();
    StepResult invoke(StepExecutionContext context) throws IOException;
    DataDefinitionDeclaration InputbyName(String name);
    DataDefinitionDeclaration OutputbyName(String name);
}
