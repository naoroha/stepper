package commands;

import flow.FlowInformationCategory;
import flow.definition.api.FlowDefinition;
import flow.definition.api.StepUsageDeclaration;
import flow.execution.api.FlowExecution;
import javafx.util.Pair;
import step.api.DataDefinitionDeclaration;
import step.api.DataNecessity;
import stepper.StepperDefinition;
import xml.parsing.ParseXml;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class MenuFunctionsImpl implements MenuFunctions {
    private Executed pastFlowsExecuted;
    private Map<String,Statistics> flowStatistics;
    private Map<String,Map<String,Statistics>>stepStatistics;
    public MenuFunctionsImpl(){
        this.pastFlowsExecuted=new Executed();
    }
@Override
    public Executed getPastFlowsExecuted() {
        return pastFlowsExecuted;
    }
@Override
    public Map<String, Statistics> getFlowStatistics() {
        return flowStatistics;
    }
@Override
    public Map<String,Map<String, Statistics>> getStepStatistics() {
        return stepStatistics;
    }

    @Override
    public void executeFlow(FlowExecution flowToExecute) {
//        if(stepper==null||stepper.getFlows().size()==0){
//            System.out.println("there is no flows in the system");
//            return;
//        }
        //FLowExecutor executor = new FLowExecutor();
//        Map<Integer, FlowDefinition> executeMapper = new HashMap<>();
//        Scanner scanner = new Scanner(System.in);
//        int choice=-1;
//        System.out.println("which flow do you want to execute?\n");
//        do {
//            for (int i = 1; i <= stepper.getFlows().size(); i++) {
//                System.out.println(i + ". " + stepper.getFlows().get(i - 1).getName());
//                executeMapper.put(i, stepper.getFlows().get(i - 1));
//            }
//            System.out.println("0. Main menu");
//            choice = scanner.nextInt();
//            if (choice == 0) {
//                return;
//            } else if (choice>stepper.getFlows().size()||choice<0) {
//                System.out.println("not a valid choice");
//
//            }
//            else {
                try {

                   // UUID id = UUID.randomUUID();
                    //String Unique = id.toString();
                   // FlowExecution flowToExecute = new FlowExecutionImpl(Unique, flowDefinition);
                    //flowToExecute.setStartTime();
                    //setFlowFreeInputs(flowToExecute);
                    ///executor.executeFlow(flowToExecute);
                    //flowToExecute.setEndTime();
                    //flowToExecute.setTotalTime();
                    showExecutedFlowDetails(flowToExecute);
                    pastFlowsExecuted.addExecutedFlow(flowToExecute);
                    updateStatistics(flowToExecute);
                }catch(RuntimeException e){
                    return;
                }

//        }while (choice<0||choice>stepper.getFlows().size());

    }
    @Override

    public void updateStatistics(FlowExecution flowToExecute) {
        String flowName=flowToExecute.getFlowDefinition().getName();
        int flowHowManyTime,stepHowManyTime;
        Duration flowTotalDuration,stepTotalDuration;
        flowHowManyTime=flowStatistics.get(flowName).getHowManyTimes();
        flowTotalDuration=flowStatistics.get(flowName).getTotalDuration();
        flowStatistics.get(flowName).setHowManyTimes(flowHowManyTime+1);
        flowStatistics.get(flowName).setTotalDuration(flowTotalDuration.plus(flowToExecute.getTotalTime()));
        Map<String,Statistics> stepInFlowStatistics=stepStatistics.get(flowName);
        for(StepUsageDeclaration step:flowToExecute.getFlowDefinition().getFlowSteps()){
            stepHowManyTime=stepInFlowStatistics.get(step.getStepDefinition().name()).getHowManyTimes();
            stepTotalDuration=stepInFlowStatistics.get((step.getStepDefinition().name())).getTotalDuration();
            stepInFlowStatistics.get((step.getStepDefinition().name())).setHowManyTimes(stepHowManyTime+1);
            stepInFlowStatistics.get((step.getStepDefinition().name())).setTotalDuration(stepTotalDuration
                            .plus(flowToExecute.getStepToLogger().get(flowToExecute.getFlowDefinition().getStepUsageDeclarationByOriginalName((step.getStepDefinition().name())).getFinalStepName()).getDuration()));
        }

    }

    private void showExecutedFlowDetails(FlowExecution flowToExecute) {
        //System.out.println("Flow Unique ID: " + flowToExecute.getUniqueId());
        //System.out.println("Flow name: " + flowToExecute.getFlowDefinition().getName());
        //System.out.println("Flow status: " + flowToExecute.getFlowExecutionResult().name());
        //for (String flowOutput : flowToExecute.getFlowDefinition().getFlowFormalOutputs()) {
            //for (DataDefinitionDeclaration output : flowToExecute.getFlowDefinition().getFlowFreeOutputs())
        for(DataDefinitionDeclaration output:flowToExecute.getFlowDefinition().getDataDefinitionDeclarationFlowOutputs()){
                //if (flowOutput.equals(output.getName())) {
                    System.out.println(output.userString() + ": ");
                    if (flowToExecute.getFlowData().get(output.getName()) != null)
                        System.out.println(flowToExecute.getFlowData().get(output.getName()));
                    else
                        System.out.println("Not created due to failure in flow");
                }
        }
    //}


            private void setFlowFreeInputs (FlowExecution flowToExecute){
                int choice=-1;
                Map<Integer, Pair<String, Object>> choiceToInput = new HashMap<>();
                List<DataDefinitionDeclaration> freeInputs = flowToExecute.getFlowDefinition().getFlowFreeInputs();
                setFilled(freeInputs);
                Scanner scanner = new Scanner(System.in);
                while (choice != freeInputs.size() + 1 || !(allMandatory(freeInputs))) {
                    showFreeInputs(freeInputs);
                    choice = scanner.nextInt();
                    if (scanner.hasNextLine()) {
                        scanner.nextLine();
                    }
                    if(choice==freeInputs.size() + 1) {
                        if (!(allMandatory(freeInputs))) {
                            System.out.println("you need to fill first all the MANDATORY inputs");
                        }
                    } else if (choice>freeInputs.size()) {
                        System.out.println("not a valid choice");

                    } else if (choice==0) {
                        throw new RuntimeException();
                    } else {
                        System.out.println("Enter your input:");
                        String userInput = scanner.nextLine();

                        if (flowToExecute.setFlowData(freeInputs.get(choice - 1).dataDefinition().getType(),
                                freeInputs.get(choice - 1).getName(), userInput)) {
                            System.out.println("SUCCESS");
                            freeInputs.get(choice - 1).setFilled(true);
                        } else {
                            System.out.println("FAILED");
                        }
                    }
                }
            }
@Override
            public void setFilled (List < DataDefinitionDeclaration > freeInputs) {
                for (DataDefinitionDeclaration input : freeInputs) {
                    input.setFilled(false);
                }
            }


            private void showFreeInputs (List < DataDefinitionDeclaration > freeInputs) {
                //Map<DataDefinitionDeclarationCategory,String> toCategory=new HashMap<>();
                Map<Boolean, String> filled = new HashMap<>();
                filled.put(true, "Added");
                filled.put(false, "Not added");
                for (int i = 1; i <= freeInputs.size(); i++) {
                    //toCategory.put(NAME,freeInputs.get(i - 1).getName());
                    //toCategory.put(USER_STRING,freeInputs.get(i - 1).getName());
                   // toCategory.put(NECESSITY,freeInputs.get(i - 1).getName());
                    //freeInputs.get(i - 1).setCategorizedData(toCategory);
                    //System.out.println(i + ". " + freeInputs.get(i - 1).userString() + "( " + freeInputs.get(i - 1).getName()
                    //        + " )" + "[ " + freeInputs.get(i - 1).necessity() + " ]." + " [ " + filled.get(freeInputs.get(i - 1).getFilled()) + " ]");
                }
                System.out.println((freeInputs.size() + 1)+". To execute");
                System.out.println("0. Main Menu");
            }
            @Override
            public boolean allMandatory (List < DataDefinitionDeclaration> flowFreeInputs) {
                for(DataDefinitionDeclaration input:flowFreeInputs){
                    if(input.necessity()==DataNecessity.MANDATORY){
                        if(!input.getFilled()){
                            return false;
                        }

                    }
                }
                return true;
            }

            @Override
            public StepperDefinition applyXMLReaderCommand (String path) throws JAXBException, FileNotFoundException, NoSuchFieldException, ParseXml.NegativeNumberException {
                Map<Integer, FlowDefinition> choiceFlowConverter = new HashMap<>();
                this.flowStatistics=new LinkedHashMap<>();
                this.stepStatistics=new LinkedHashMap<>();
                StepperDefinition stepper = null;

//                    Scanner scanner = new Scanner(System.in);
//                    System.out.print("Enter an XML path: ");
//                    String path = scanner.nextLine();
                    if (isFileExistAndFromTypeXml(path)) {
                        ParseXml parseXml = new ParseXml(path);
                        stepper = parseXml.convertingGeneratedClasses();
                    }
                setFlowAndStepStatistics(stepper);
                return stepper;

            }



    private void setFlowAndStepStatistics(StepperDefinition stepper) {
        for(FlowDefinition flow: stepper.getFlows()){
            flowStatistics.put(flow.getName(),new Statistics());
            Map<String,Statistics> stepInFlowStatistics=new HashMap<>();
            for(StepUsageDeclaration step:flow.getFlowSteps()){
                stepInFlowStatistics.put(step.getStepDefinition().name(),new Statistics());
            }
            stepStatistics.put(flow.getName(),stepInFlowStatistics);
        }
    }

    @Override
    public StringBuilder showFlow (FlowDefinition flowDefinition) {
        Map< FlowInformationCategory, StringBuilder> flowDescription=new HashMap<>();
        StringBuilder stringBuilder=new StringBuilder();
//        if(stepper==null||stepper.getFlows().size()==0){
//            System.out.println("there is no flows in the system");
//            return;
//        }
//        Scanner scanner = new Scanner(System.in);
//        int choice=0;
//        System.out.println("which flow do you want to execute?\n");
//        do {
//            for (int i = 1; i <= stepper.getFlows().size(); i++) {
//                System.out.println(i + ". " + stepper.getFlows().get(i - 1).getName());
//            }
//            System.out.println("0. Main menu");
//            choice = scanner.nextInt();
//            if (choice == 0)
//                return;
//            else if (choice > stepper.getFlows().size() || choice < 0) {
//                System.out.println("not a valid choice");
//            }
//            else {
                //FlowDefinition flowDefinition = stepper.getFlows().get(choice - 1);
        //stringBuilder.append("Flow name: ").append(flowDefinition.getName()).append("\n")
        flowDescription.put(FlowInformationCategory.DESCRIPTION,new StringBuilder(flowDefinition.getDescription() + "\n"));
        flowDescription.put(FlowInformationCategory.FORMAL_OUTPUTS,stringBuilder.append(flowDefinition.getFlowFormalOutputs()).append("\n"));
        flowDescription.put(FlowInformationCategory.READONLY,new StringBuilder("is the flow read-only:\n").append(flowDefinition.isFlowReadOnly()));
        flowDescription.put(FlowInformationCategory.STEPS,new StringBuilder("steps included in flow:"));
        flowDescription.get(FlowInformationCategory.STEPS).append(stepsInfo(flowDefinition));
        flowDescription.put(FlowInformationCategory.FREE_INPUTS,new StringBuilder("Flow free inputs:\n"));
        flowDescription.get(FlowInformationCategory.FREE_INPUTS).append(printFreeInputsInfo(flowDefinition));
        flowDescription.put(FlowInformationCategory.OUTPUTS,new StringBuilder("All the outputs in the flow:\n"));
        flowDescription.get(FlowInformationCategory.OUTPUTS).append(printOutputsInfo(flowDefinition));
        flowDefinition.setFlowDescription(flowDescription);
        return stringBuilder;
            //}
        //}while (choice > stepper.getFlows().size() || choice < 0);


    }

    @Override
    public void showPreviousExecutedFlowsDetails() {
        Scanner scanner=new Scanner(System.in);
        int choice;
        int executedSize=pastFlowsExecuted.getExecutedFlowsList().size();
        if(executedSize==0) {
            System.out.println("you did not executed any flow");
            return;
        }
        System.out.println("which executed flow do you want to see:");
        FlowExecution flowExecution;
        for(int i=1;i<=executedSize;i++){
            flowExecution=pastFlowsExecuted.getExecutedFlowsList().get(executedSize-i);
            System.out.println(i + ". " +flowExecution.getFlowDefinition().getName()+
                    " [ID: "+flowExecution.getUniqueId()+" ]"+
                    " [Started at: " + formattedTime(flowExecution.getStartTime())+" ]");
        }
        System.out.println("0. Main menu");
        choice=scanner.nextInt();
        if(choice==0)
            return;
        flowExecution=pastFlowsExecuted.getExecutedFlowsList().get(executedSize-choice);
        printFlowExecutionDetails(flowExecution);
    }

    @Override
    public void showStatistics(){
//        if(flowStatistics.size()==0||stepStatistics.size()==0){
//            System.out.println("you need to insert an XML first.");
//            return;
//        }
        showStatistic(flowStatistics);
        //showStatistic(stepStatistics);
    }
    public void showStatistic(Map<String,Statistics> flowStatistics) {
        System.out.println("Flows: ");
        int index=1;

        for(String flowName:flowStatistics.keySet()){
            long totalTime=flowStatistics.get(flowName).getTotalDuration().toMillis();
            int howMany=flowStatistics.get(flowName).getHowManyTimes();
            System.out.println((index)+". " +flowName +
                            " ran " + flowStatistics.get(flowName).getHowManyTimes()+" times"+
                   " and in average time of: " +
                    average(totalTime,howMany)+ "(ms)");
        }

    }

    private double average(long totalTime, double howMany) {
        if(howMany==0)
        {
            return 0;
        }
        return totalTime/howMany;
    }

    private void printFlowExecutionDetails(FlowExecution flowExecution) {
        List<DataDefinitionDeclaration> freeFlowExecutionInputs = flowExecution.getFlowDefinition().getFlowFreeInputs();
        List<DataDefinitionDeclaration> freeFlowExecutionOutputs = flowExecution.getFlowDefinition().getFlowFreeOutputs();
        String outputMessage;
        //1
        System.out.println("Unique ID: " + flowExecution.getUniqueId());
        //2
        System.out.println("Flow name: " + flowExecution.getFlowDefinition().getName());
        //3
        System.out.println("Flow result: " + flowExecution.getFlowExecutionResult().name());
        //4
        System.out.println("Flow duration: " + flowExecution.getTotalTime().toMillis());
        //5
        System.out.println("Free inputs info in the flow:");
        freeFlowExecutionInputs.sort(Comparator.comparing(DataDefinitionDeclaration::necessity));
        for (DataDefinitionDeclaration input : freeFlowExecutionInputs) {
            System.out.println("input name: " + input.getName() +
                    "[type: " + input.dataDefinition().getName() + " ]" +
                    "value= " + flowExecution.getFlowData().get(input.getName()) +
                    "[necessity: " + input.necessity() + "]");
        }
        //6
        System.out.println("All the outputs in the flow:\n");
        for (StepUsageDeclaration step : flowExecution.getFlowDefinition().getFlowSteps()) {
            for (DataDefinitionDeclaration Output : step.getFinalOutputs().values()) {
                outputMessage = "final name: " + Output.getName() + "[type: " +
                        Output.dataDefinition().getName() + "] "+"\n"+ "[content:  ";
                if(flowExecution.getFlowData().get(Output.getName())==null){
                    outputMessage=outputMessage +"Not created due to failure in flow";
                }
                else{
                    outputMessage=outputMessage + flowExecution.getFlowData().get(Output.getName())+ "]";
                }
                System.out.println(outputMessage);
            }
        }
        //7
        for(StepUsageDeclaration step:flowExecution.getFlowDefinition().getFlowSteps()) {
            System.out.println("step name: " + step.getStepDefinition().name() +
                    "[Alias: " + step.getFinalStepName() + "]");
            System.out.println("Step result: " + flowExecution.getStepToLogger().get(step.getFinalStepName()).getStepResult().name());
            System.out.println("Summery Line: " + flowExecution.getStepToSummeryLine().get(step.getFinalStepName()));
            if (flowExecution.getStepToLogger().get(step.getFinalStepName()) != null) {
                System.out.println("Duration: " + flowExecution.getStepToLogger().get(step.getFinalStepName()).getDuration().toMillis());
                System.out.println("How many logs: " + flowExecution.getStepToLogger().get(step.getFinalStepName()).getMessages().size());
                System.out.println(flowExecution.getStepToLogger().get(step.getFinalStepName()).printedMessage());
            }
        }


    }
@Override
public String formattedTime(Instant startTime) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(startTime, ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedTime = localDateTime.format(formatter);
        return formattedTime;
    }

    private StringBuilder printOutputsInfo (FlowDefinition flowDefinition){
        StringBuilder stringBuilder=new StringBuilder();
                for (StepUsageDeclaration step : flowDefinition.getFlowSteps()) {
                    for (DataDefinitionDeclaration freeOutput : step.getFinalOutputs().values()) {

                        stringBuilder.append("final name: " + freeOutput.getName() + "[type: " +
                                freeOutput.dataDefinition().getName() + "] " +
                                "step source: " + step.getFinalStepName() + "\n");
                    }
                }
                return stringBuilder;
            }


    private StringBuilder printFreeInputsInfo(FlowDefinition flowDefinition) {
        StringBuilder stringBuilder=new StringBuilder();
            for(DataDefinitionDeclaration freeInput: flowDefinition.getFlowFreeInputs()){
                stringBuilder.append("final name:"+ freeInput.getName()+
                        "[type: "+freeInput.dataDefinition().getName()+"]" + 
                          "step source: " + flowDefinition.getStepNameByFreeInput(freeInput) +"["+
                        freeInput.necessity()+"]\n");

        }
            return stringBuilder;
    }

    private StringBuilder stepsInfo(FlowDefinition flowDefinition) {
        int stepIndex=0;
        StringBuilder stringBuilder=new StringBuilder();

        for(StepUsageDeclaration step:flowDefinition.getFlowSteps()){
            String stepName=step.getStepDefinition().name();
            stringBuilder.append("\nStep name: "+stepName);
            if(!stepName.equals(step.getFinalStepName())){
                stringBuilder.append(" ["+"Alias name: "+step.getFinalStepName()+ "]");
            }
            stringBuilder.append("\nDoes the step read only: "+ step.getStepDefinition().isReadonly());
            stepIndex++;
        }
        return stringBuilder;
    }


    public boolean isFileExistAndFromTypeXml(String path) {
        File file = new File(path);
        if(!file.exists()){
            throw new NullPointerException("file does not exist!");
        }
        else if(!file.getName().endsWith(".xml"))
            throw new IllegalArgumentException("not an xml file!");
        return true;

    }
}



