package flow.execution.context;

import dd.api.DataDefinition;
import dd.impl.DataDefinitionRegistry;
import flow.definition.api.FlowDefinition;
import flow.execution.MyLogger;
import javafx.util.Pair;
import step.api.DataDefinitionDeclaration;
import step.api.StepDefinition;

import java.time.Duration;
import java.util.*;
import java.util.logging.Logger;

public class StepExecutionContextImpl implements StepExecutionContext {

    private  Map<String, Object> dataValues;
    private List<DataDefinitionDeclaration> freeInputs;
    private List<DataDefinitionDeclaration> freeOutputs;
    private Map<Pair<String, DataDefinitionDeclaration>, Pair<String, DataDefinitionDeclaration>> mapping;
    private FlowDefinition currentFlow;
    private  MyLogger stepMessages;
    private String summeryLine;

    public StepExecutionContextImpl() {

        dataValues = new HashMap<>();

    }

    @Override
    public <T> T getDataValue(String dataName, Class<T> expectedDataType) {
        DataDefinition dataDefinition;
        for (Pair<String, DataDefinitionDeclaration> inputBeforeAliasing : mapping.keySet()) {

            if (inputBeforeAliasing.getValue().getName().equals(dataName)) {
                dataName = mapping.get(inputBeforeAliasing).getValue().getName();
                break;
            }
        }
            final String nameToSearch=dataName;
        try {
            // assuming that from the data name we can get to its data definition
            Object value = dataValues.entrySet().stream()
                    .filter(entry -> nameToSearch.equals(entry.getKey()))
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElse(null);


            //DataDefinition theExpectedDataDefinition = theExpectedDataDefinitionDeclaration.dataDefinition();


            if (expectedDataType.isAssignableFrom(value.getClass())) {
                Object aValue = dataValues.get(dataName);
                // what happens if it does not exist ?

                return expectedDataType.cast(aValue);
            } else {
                // error handling of some sort...
            }
        } catch (NullPointerException e) {
            //Log expression
            return null;
        }
        return null;
    }

    @Override
    public boolean storeDataValue(String dataName, Object value) {
        //dataName must be either in freeOutpus or in mapping because the only time it's been called is in invoke of a step.
        // if the same output is once at freeOutput and once in mapping they will be differ by aliasName
        DataDefinitionDeclaration theDataDefinitionDeclaration;
        DataDefinition theData = null;
        try {
            for (Pair<String, DataDefinitionDeclaration> values : mapping.values()) {
                //Pair<String, DataDefinitionDeclaration> key = entry.getKey();
                //Pair<String, DataDefinitionDeclaration> mapValue = entry.getValue();
                if (values.getValue().getName().equals(dataName)) {
                    theDataDefinitionDeclaration = values.getValue();
                    dataName= theDataDefinitionDeclaration.getName();
                    theData=theDataDefinitionDeclaration.dataDefinition();
                }
            }
            final String finalName=dataName;
            if(theData==null) {
                 theDataDefinitionDeclaration = freeOutputs.stream().
                        filter(data -> data.getName().equals(finalName))
                        .findFirst().
                        orElse(null);
                if (theDataDefinitionDeclaration != null)
                    theData = theDataDefinitionDeclaration.dataDefinition();
            }
             if(theData!=null){
                // we have the DD type so we can make sure that its from the same type
                if (theData.getType().isAssignableFrom(value.getClass())) {
                    dataValues.put(dataName, value);
                    return true;
                } else {
                    // error handling of some sort...
                }
            }
        } catch (NullPointerException e) {
            //Log expression
            return false;

        }
        return false;
    }


    @Override
    public List<DataDefinitionDeclaration> getFreeInputs() {
        return freeInputs;
    }

    @Override
    public void setFreeInputs(List<DataDefinitionDeclaration> FreeInputs,Map<String, Object> UserInputs) {
        this.freeInputs=FreeInputs;
        this.dataValues=UserInputs;

    }
    @Override
    public void setFreeOutputs(List<DataDefinitionDeclaration> freeOutputs){
        this.freeOutputs=freeOutputs;
    }
    @Override
    public void setMapping(Map<Pair<String, DataDefinitionDeclaration>, Pair<String, DataDefinitionDeclaration>> mapping) {
        this.mapping = mapping;
    }
    @Override
    public void setCurrentFlow(FlowDefinition flow){
        this.currentFlow=flow;
    }

    @Override
    public FlowDefinition getCurrentFlow() {
        return currentFlow;
    }
    @Override
    public void setStepMessages( MyLogger stepMessages) {
        this.stepMessages=stepMessages;
    }

@Override
    public  MyLogger getStepMessages() {
        return stepMessages;
    }

    @Override
    public void setSummeryLine(String summeryLine) {
        this.summeryLine = summeryLine;
    }
    @Override
    public String getSummeryLine(){
        return summeryLine;
    }
    @Override
    public Map<String,Object>getDataValues(){
        return dataValues;
    }
}
