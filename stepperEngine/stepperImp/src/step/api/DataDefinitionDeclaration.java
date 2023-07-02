package step.api;

import dd.api.DataDefinition;
import step.DataDefinitionDeclarationShow;

import java.util.Map;

public interface DataDefinitionDeclaration {

    void setCategorizedData(DataDefinitionDeclarationShow categorizedData);

    DataDefinitionDeclarationShow getCategorizedData();

    String getName();
    DataNecessity necessity();
    String userString();
    DataDefinition dataDefinition();
    void setFilled(boolean toFill);
    boolean getFilled();



}
