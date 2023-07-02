package flow.execution.api;

import flow.definition.api.FlowDefinition;
import flow.execution.MyLogger;
import javafx.beans.property.SimpleStringProperty;

import java.time.Instant;
import java.util.*;
import java.time.Duration;

public interface FlowExecution {


    // lots more data that needed to be stored while flow is being executed...

    //public FlowExecution(String uniqueId, FlowDefinition flowDefinition);

    SimpleStringProperty flowStatusLabelProperty();

    String getFlowName();

    void setFlowName(String flowName);


    String getUniqueId();
       boolean setFlowData(Class<?> expectedClass, String DataName, String value);

     FlowDefinition getFlowDefinition();

     FlowExecutionResult getFlowExecutionResult();

     Map<String,Object> getFlowData();
    Duration getTotalTime();
    void setTotalTime();

    void setStepSummeryLine(String step,String summeryLine);
    Map<String,String> getStepToSummeryLine();
    Map<String, MyLogger> getStepToLogger();

    void setStepLoggers(String step ,MyLogger logger);

    void setFlowExecutionResult(FlowExecutionResult flowExecutionResult);
    void setEndTime();
    void setStartTime();
    Instant getStartTime();
    Instant getEndTime();

    void setFlowData(Map<String, Object> dataValues);
}
