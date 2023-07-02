package xml.parsing;

import flow.definition.api.FlowDefinition;
import flow.definition.api.FlowDefinitionImpl;
import flow.definition.api.StepUsageDeclaration;
import flow.definition.api.StepUsageDeclarationImpl;
import javafx.util.Pair;
import step.StepDefinitionRegistry;
import step.api.DataDefinitionDeclaration;
import step.api.DataDefinitionDeclarationImpl;
import step.api.StepDefinition;
import stepper.StepperDefinitionImpl;
import xml.generated.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ParseXml {
    private STStepper stStepper;
    private Map<FlowDefinition,List<STContinuation>> mapper;

    public ParseXml(String xmlPath) throws JAXBException,FileNotFoundException {
        STStepper stStepper = getXmlStepperObjFromJaxBClasses(xmlPath);
        mapper=new HashMap<>();
    }

    public STStepper getXmlStepperObjFromJaxBClasses(String xmlPath)throws JAXBException,FileNotFoundException {
            InputStream inputStream = new FileInputStream(new File(xmlPath));
            JAXBContext jaxbContext = JAXBContext.newInstance(STStepper.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            stStepper = (STStepper) jaxbUnmarshaller.unmarshal(inputStream);
            return stStepper;

    }

    public StepperDefinitionImpl convertingGeneratedClasses() throws NullPointerException, NegativeNumberException {
        StepperDefinitionImpl stepper = new StepperDefinitionImpl();
        List<STFlow> stFlows = stStepper.getSTFlows().getSTFlow();
        List<StepUsageDeclaration> stepsInFlow;
        stepper.setThreadPoolSize(processNumber(stStepper.getSTThreadPool()));



        for (STFlow flow : stFlows) {


                //set flow name and description and formal output of one flow
                FlowDefinition flowToAdd = new FlowDefinitionImpl(flow.getName(), flow.getSTFlowDescription());
                List<String> CurrentFlowOutputs = Arrays.asList(flow.getSTFlowOutput().split(","));
                flowToAdd.getFlowFormalOutputs().addAll(CurrentFlowOutputs);

                //set steps to one flow
                stepsInFlow = getStStepsInFlow(flow,flowToAdd);
                flowToAdd.getFlowSteps().addAll(stepsInFlow);

                if(flow.getSTFlowLevelAliasing()!=null){
                setFlowLevelAliasing(flowToAdd, flow.getSTFlowLevelAliasing().getSTFlowLevelAlias());}

                setAutoMapping(flowToAdd,stepsInFlow);



                //flowToAdd.setStepsUsageNameFromSteps(stepsInFlow);
                //StepsNameUsage = flowToAdd.getStepsUsageName();


                if(flow.getSTCustomMappings()!=null) {
                    //set custom mapping to one flow
                    setCustomMapping(stepsInFlow,flow.getSTCustomMappings().getSTCustomMapping(),flowToAdd);
                }

                //set custom mapping to flow
            for(StepUsageDeclaration step:stepsInFlow){
                flowToAdd.getFlowFreeInputs().addAll(flowToAdd.getFlowFreeInputs(step.getFinalInputs()));
                flowToAdd.getFlowFreeOutputs().addAll(flowToAdd.getFlowFreeOutputs(step.getFinalOutputs()));
            }
            if(flow.getSTContinuations()!=null)
            {
                mapper.put(flowToAdd,flow.getSTContinuations().getSTContinuation());
            }
                unionFreeInputsByFinalName(flowToAdd);
                flowToAdd.setDataDefinitionDeclarationFlowOutputs();
                stepper.addFlow(flowToAdd);
                if(flow.getSTInitialInputValues()!=null) {
                    setInitialInput(flowToAdd, flow);
                }
            sortFlowFreeInputsByNecessity(flowToAdd);
            }
        setContinuationMapping(stepper);
        return stepper;
    }

    private void unionFreeInputsByFinalName(FlowDefinition flow) {
        Set<String> uniqueNames = new LinkedHashSet<>();
        List<DataDefinitionDeclaration> result = new ArrayList<>();

        for (DataDefinitionDeclaration input : flow.getFlowFreeInputs()) {
            if (!uniqueNames.contains(input.getName())) {
                uniqueNames.add(input.getName());
                result.add(input);
            }
        }
        flow.setFreeInputs(result);
    }

    private void sortFlowFreeInputsByNecessity(FlowDefinition flow) {
        flow.setFreeInputs(flow.getFlowFreeInputs().stream()
                .sorted(Comparator.comparingInt(inputNecessity -> inputNecessity.necessity().name().equals("MANDATORY") ? 0 : 1))
                .collect(Collectors.toList()));
    }

    public void setInitialInput(FlowDefinition flowToAdd, STFlow flow) {
        for(STInitialInputValue initialInputValue:flow.getSTInitialInputValues().getSTInitialInputValue()){
            flowToAdd.getInitializeInput().put(flowToAdd.getFlowFreeInputByName(initialInputValue.getInputName()),initialInputValue.getInitialValue());
        }
    }

    public void setContinuationMapping(StepperDefinitionImpl stepper) {

        for(FlowDefinition flow: mapper.keySet()){
            for(STContinuation continuation: mapper.get(flow)){
                FlowDefinition targetFlow=stepper.getFlowDefinitionByName(continuation.getTargetFlow());
                flow.getContinuation().add(targetFlow);
                for(STContinuationMapping continuationMapping: continuation.getSTContinuationMapping()){
                    DataDefinitionDeclaration sourceData=flow.getDataDefinitionDeclarationFlowOutputByName(continuationMapping.getSourceData());
                    DataDefinitionDeclaration targetData=targetFlow.getFlowFreeInputByName(continuationMapping.getTargetData());
                    flow.getContinuationMapping().put(sourceData,targetData);
                }
            }
        }

    }

    private void setAutoMapping(FlowDefinition flowToAdd, List<StepUsageDeclaration> stepsInFlow) {
        Map<Pair<String,DataDefinitionDeclaration>, Pair<String,DataDefinitionDeclaration>> result = new LinkedHashMap<>();

        for (int i = 0; i < stepsInFlow.size(); i++) {
            StepUsageDeclaration stepX = stepsInFlow.get(i);

            for (int j = i + 1; j < stepsInFlow.size(); j++) {
                StepUsageDeclaration stepY = stepsInFlow.get(j);

                for(DataDefinitionDeclaration output:stepX.getFinalOutputs().values())
                {
                    for(DataDefinitionDeclaration input:stepY.getFinalInputs().values()){
                        if(output.getName().equals(input.getName())&&
                                output.dataDefinition().getType().equals(input.dataDefinition().getType())){
                            result.put(new Pair<>(stepY.getFinalStepName(),input),new Pair<>(stepX.getFinalStepName(),output));
                        }
                    }


                }
            }
        }
        flowToAdd.setMapping(result);
    }







    private void setCustomMapping(List<StepUsageDeclaration> stepsInFlow, List<STCustomMapping> stCustomMapping, FlowDefinition flowToAdd) {

        boolean hasSourceNameInInput, hasSourceNameInOutput;
        Map<DataDefinitionDeclaration,DataDefinitionDeclaration> finalInputs,finalOutputs;
    for(STCustomMapping customMapping:stCustomMapping){
        final String sourceStepName= customMapping.getSourceStep();
        final String sourceOutputName= customMapping.getSourceData();;
        final String targetStepName=customMapping.getTargetStep();
        final String targetInputName=customMapping.getTargetData();

        //Predicate<StepUsageDeclaration> hasSourceName = StepUsageDeclaration -> StepUsageDeclaration.getFinalStepName().equals(sourceStepName);
        try {
            StepUsageDeclaration SourceStep = flowToAdd.GetStepByName(sourceStepName);
            StepUsageDeclaration TargetStep = flowToAdd.GetStepByName(targetStepName);
            if(checkValidation(stepsInFlow,SourceStep,TargetStep)){ // means target appear after sourcex`
                //System.out.println("ok");
                finalInputs=TargetStep.getFinalInputs();
                finalOutputs=SourceStep.getFinalOutputs();
                DataDefinitionDeclaration dataDefinitionDeclarationOutput= SourceStep.getDataDefinitionDeclarationByName(finalOutputs,sourceOutputName);
                DataDefinitionDeclaration dataDefinitionDeclarationInput= TargetStep.getDataDefinitionDeclarationByName(finalInputs,targetInputName);

                if(dataDefinitionDeclarationInput.dataDefinition().getName().equals(dataDefinitionDeclarationOutput.dataDefinition().getName())){
                    flowToAdd.getMapping().put(new Pair<>(targetStepName,dataDefinitionDeclarationInput),new Pair<>(sourceStepName,dataDefinitionDeclarationOutput));

                }
                else {
                    //log
                    return;
                }


            }



        }catch(IllegalArgumentException e){
            //log

            return;

        }



    }

    }

    private void setFlowLevelAliasing(FlowDefinition currentFlow,  List<STFlowLevelAlias> stFlowLevelAlias) throws SecurityException,NullPointerException {

        Map<DataDefinitionDeclaration, DataDefinitionDeclaration> hasInInput, hasInOutput;
        boolean hasSourceNameInInput, hasSourceNameInOutput;
        for (STFlowLevelAlias flowLevelAlias : stFlowLevelAlias) {

                String AliasName = flowLevelAlias.getAlias();
                String sourceDataName = flowLevelAlias.getSourceDataName();
                StepUsageDeclaration stepInFlow = currentFlow.GetStepByName(flowLevelAlias.getStep());

                Predicate<DataDefinitionDeclaration> hasSourceName = DataDefinitionDeclaration -> DataDefinitionDeclaration.getName().equals(sourceDataName);


                hasInInput = stepInFlow.getFinalInputs();
                hasInOutput = stepInFlow.getFinalOutputs();

                hasSourceNameInInput = hasInInput.keySet().stream().anyMatch(hasSourceName);
                hasSourceNameInOutput = hasInOutput.keySet().stream().anyMatch(hasSourceName);


                if (hasSourceNameInInput) {

                    UpdateFlowLevelAliasing(hasInInput, sourceDataName, AliasName);

                } else if (hasSourceNameInOutput) {

                    UpdateFlowLevelAliasing(hasInOutput, sourceDataName, AliasName);

                } else{
                     throw new NullPointerException("the source data name does not exist");
                }
        }
    }

    private void UpdateFlowLevelAliasing(Map<DataDefinitionDeclaration, DataDefinitionDeclaration> SourceMap, String sourceDataName, String aliasName) {
        SourceMap.keySet().stream()
                .filter(key -> key.getName().equals(sourceDataName))
                .findFirst()
                .ifPresent(key -> SourceMap.put(key, new DataDefinitionDeclarationImpl(key,aliasName)));}




    public List<StepUsageDeclaration> getStStepsInFlow(STFlow flow, FlowDefinition flowToAdd)throws NullPointerException {
        List<StepUsageDeclaration> result = new ArrayList<StepUsageDeclaration>();
        List<STStepInFlow> stepsInFlow = flow.getSTStepsInFlow().getSTStepInFlow();
        String nameToSearch;
        StepUsageDeclaration CurrentStepToAdd=null;
        Map<String,String> originalToAlias=new LinkedHashMap<>();

        StepDefinition CurrentStepDefinition;
        for (STStepInFlow step : stepsInFlow) {
            nameToSearch = step.getName().toUpperCase().replace(" ", "_");
            if(StepDefinitionRegistry.valueOf(nameToSearch)!=null){
                CurrentStepDefinition = StepDefinitionRegistry.valueOf(nameToSearch).getStepDefinition();}
            else{
                throw new NullPointerException("there is no such step");
            }
            if(step.getAlias()!=null && step.isContinueIfFailing()!=null){
                CurrentStepToAdd = new StepUsageDeclarationImpl(CurrentStepDefinition,step.isContinueIfFailing() ,step.getAlias());}

            else if(step.isContinueIfFailing()!=null){
                CurrentStepToAdd = new StepUsageDeclarationImpl(CurrentStepDefinition,step.isContinueIfFailing() ,step.getName());}

            else if(step.getAlias()!=null){
                CurrentStepToAdd = new StepUsageDeclarationImpl(CurrentStepDefinition,step.getAlias());
                originalToAlias.put(step.getName(),step.getAlias());}

            else{
                CurrentStepToAdd = new StepUsageDeclarationImpl(CurrentStepDefinition,step.getName());
                originalToAlias.put(step.getName(),step.getName());}
            result.add(CurrentStepToAdd);


        }
        flowToAdd.setStepOriginalToAliasName(originalToAlias);
        return result;
    }

    private boolean checkValidation(List<StepUsageDeclaration> stepsInFlow, StepUsageDeclaration source,StepUsageDeclaration target) {
        int targetInd , sourceInd;
        sourceInd = stepsInFlow.indexOf(source);
        targetInd = stepsInFlow.indexOf(target);
        return (targetInd > sourceInd);
    }
    public class NegativeNumberException extends Exception {
        public NegativeNumberException(String message) {
            super(message);
        }
    }
    public int processNumber(int number) throws NegativeNumberException {
        if (number <= 0) {
            throw new NegativeNumberException("ThreadPool can not be negative or zero");
        }
        return number;
        // Process the number if it is valid
    }
}





