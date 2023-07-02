package step.api;


import dd.api.DataDefinition;

import step.DataDefinitionDeclarationShow;

public class DataDefinitionDeclarationImpl implements DataDefinitionDeclaration {
    private final String name;
    private final DataNecessity necessity;
    private final String userString;
    private final DataDefinition dataDefinition;
    private boolean filled;
    private DataDefinitionDeclarationShow categorizedData;

    public DataDefinitionDeclarationImpl(String name, DataNecessity necessity, String userString, DataDefinition dataDefinition,DataDefinitionDeclarationShow uiShow) {
        this.name = name;
        this.necessity = necessity;
        this.userString = userString;
        this.dataDefinition = dataDefinition;
        this.filled=false;
        this.categorizedData=uiShow;
    }
    public DataDefinitionDeclarationImpl(DataDefinitionDeclaration Original ,String name) {
        this.name=name;
        this.necessity=Original.necessity();
        this.userString= Original.userString();
        this.dataDefinition= Original.dataDefinition();
        this.categorizedData=Original.getCategorizedData();
    }
@Override
    public void setCategorizedData(DataDefinitionDeclarationShow categorizedData) {
        this.categorizedData = categorizedData;
    }
@Override
    public DataDefinitionDeclarationShow getCategorizedData() {
        return categorizedData;
    }

    @Override
    public String getName() {
        return name;
    }


    @Override
    public DataNecessity necessity() {
        return necessity;
    }

    @Override
    public String userString() {
        return userString;
    }

    @Override
    public DataDefinition dataDefinition() {
        return dataDefinition;
    }
    @Override
    public void setFilled(boolean toFill){
        this.filled=toFill;
    }
    public boolean getFilled(){
        return filled;
    }

}
