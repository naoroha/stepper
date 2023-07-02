package commands;

import flow.definition.api.FlowDefinition;
import flow.execution.api.FlowExecution;
import step.api.DataDefinitionDeclaration;
import stepper.StepperDefinition;
import xml.parsing.ParseXml;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public interface MenuFunctions {
    Executed getPastFlowsExecuted();

    Map<String, Statistics> getFlowStatistics();

    Map<String,Map<String, Statistics>> getStepStatistics();

    void executeFlow(FlowExecution flowToExecute);

    boolean allMandatory (List<DataDefinitionDeclaration> flowFreeInputs);

    StepperDefinition applyXMLReaderCommand(String path) throws JAXBException, FileNotFoundException, NoSuchFieldException, ParseXml.NegativeNumberException;
    StringBuilder showFlow(FlowDefinition flowDefinition);

    void showPreviousExecutedFlowsDetails();

    void updateStatistics(FlowExecution flowToExecute);

    void setFilled (List < DataDefinitionDeclaration > freeInputs);

    void showStatistics();

    String formattedTime(Instant startTime);
}
