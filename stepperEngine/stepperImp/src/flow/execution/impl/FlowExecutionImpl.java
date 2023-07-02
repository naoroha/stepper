package flow.execution.impl;

import dd.impl.enumerator.Enumerator;
import dd.impl.enumerator.EnumeratorData;
import flow.definition.api.FlowDefinition;
import flow.execution.MyLogger;
import flow.execution.api.FlowExecution;
import flow.execution.api.FlowExecutionResult;
import javafx.beans.property.SimpleStringProperty;
import xml.parsing.ParseXml;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class FlowExecutionImpl implements FlowExecution {
    private static int progress;
    private final String uniqueId;
    private final FlowDefinition flowDefinition;
    private String flowName;
    private Duration totalTime;
    private FlowExecutionResult flowExecutionResult;
    private Map<String, CompletableFuture<Void>> flowContinuationMap;
    private SimpleStringProperty flowStatusLabel;
    private Map<String,Object> flowData;
    private Map<String,String> stepToSummeryLine;
    private Map<String, MyLogger> stepToLog;
    private Instant startTime;
    private Instant endTime;


    public FlowExecutionImpl(String uniqueId, FlowDefinition flowDefinition) {
        this.uniqueId = uniqueId;
        this.flowDefinition = flowDefinition;
        flowData=new LinkedHashMap<>();
        this.stepToSummeryLine=new LinkedHashMap<>();
        this.stepToLog=new LinkedHashMap<>();
        this.flowExecutionResult=FlowExecutionResult.SUCCESS;
        this.flowContinuationMap=new HashMap<>();
        flowStatusLabel=new SimpleStringProperty();
        flowName=flowDefinition.getName();
        progress=0;
    }
@Override
    public SimpleStringProperty flowStatusLabelProperty() {
        return flowStatusLabel;
    }
    @Override

    public String getFlowName() {
        return flowName;
    }
@Override
    public void setFlowName(String flowName) {
        this.flowName=flowName;
    }

    public Map<String, CompletableFuture<Void>> getFlowContinuationMap() {
        return flowContinuationMap;
    }

    public void setFlowContinuationMap(Map<String, CompletableFuture<Void>> flowContinuationMap) {
        this.flowContinuationMap = flowContinuationMap;
    }



    public String getUniqueId() {
        return uniqueId;
    }

        public FlowDefinition getFlowDefinition() {
            return flowDefinition;
        }

        public FlowExecutionResult getFlowExecutionResult() {
            return flowExecutionResult;
        }
@Override
public boolean setFlowData(Class<?> expectedClass, String dataName, String value)throws  IllegalArgumentException ,NumberFormatException {
        if(expectedClass.isAssignableFrom(Integer.class))
        {
                int intValue = Integer.parseInt(value);
                    if(intValue<=0)
                        throw new IllegalArgumentException();
                flowData.put(dataName, intValue);

        } else if (expectedClass.isAssignableFrom(Double.class)) {
            Double doubleValue=Double.parseDouble(value);
            flowData.put(dataName,doubleValue);

        }
        else if((expectedClass.isAssignableFrom(String.class))) {
            flowData.put(dataName,value);

        } else if ((expectedClass.isAssignableFrom(EnumeratorData.class))) {
            if(isValidEnumValue(dataName, Enumerator.class)){
                if(Enumerator.valueOf(dataName).getEnumeratorData().containsString(value))
                    flowData.put(dataName,Enumerator.valueOf(dataName).getEnumeratorData());
            }
        } else{
            return false;}
        return true;
}
    public static <E extends Enum<E>> boolean isValidEnumValue(String value, Class<E> enumClass) {
        try {
            Enum.valueOf(enumClass, value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public Map<String, Object> getFlowData() {
        return flowData;
    }
    public Duration getTotalTime(){
        return totalTime;
    }

    public void setTotalTime() {
        this.totalTime=Duration.between(startTime, endTime);
    }

    @Override
    public void setStepSummeryLine(String step,String summeryLine) {
        this.stepToSummeryLine.put(step,summeryLine);

    }

    @Override
    public Map<String, String> getStepToSummeryLine() {
        return stepToSummeryLine;
    }

    @Override
    public Map<String, MyLogger> getStepToLogger() {
        return stepToLog;
    }

    @Override
    public void setStepLoggers(String step,MyLogger logger) {
        this.stepToLog.put(step,logger);

    }
    public void setFlowData(Map<String,Object>toSet){
        this.flowData=toSet;
    }
    @Override
    public void setFlowExecutionResult(FlowExecutionResult flowExecutionResult){
        this.flowExecutionResult=flowExecutionResult;
    }
    @Override
    public void setStartTime(){
        this.startTime=Instant.now();
    }
    @Override
    public Instant getEndTime() {
        return endTime;
    }
    @Override
    public Instant getStartTime() {
        return startTime;
    }
    @Override
    public void setEndTime(){
        this.endTime=Instant.now();
    }

}

