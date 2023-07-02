package step.impl;

import dd.impl.DataDefinitionRegistry;
import flow.definition.api.FlowDefinition;
import flow.definition.api.StepUsageDeclaration;
import flow.execution.MyLogger;
import flow.execution.context.StepExecutionContext;
import step.DataDefinitionDeclarationShow;
import step.api.AbstractStepDefinition;
import step.api.DataDefinitionDeclarationImpl;
import step.api.DataNecessity;
import step.api.StepResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandLine extends AbstractStepDefinition {
    public CommandLine() {
        super("Command Line", false);
        addInput(new DataDefinitionDeclarationImpl("COMMAND", DataNecessity.MANDATORY, "Command", DataDefinitionRegistry.STRING, DataDefinitionDeclarationShow.TEXT));
        addInput(new DataDefinitionDeclarationImpl("ARGUMENTS", DataNecessity.OPTIONAL, "Command arguments", DataDefinitionRegistry.STRING,DataDefinitionDeclarationShow.TEXT));
        addOutput(new DataDefinitionDeclarationImpl("RESULT", DataNecessity.NA, "Command output", DataDefinitionRegistry.STRING,DataDefinitionDeclarationShow.TEXT));
    }
    @Override
    public StepResult invoke(StepExecutionContext context) throws IOException {
        FlowDefinition flow=context.getCurrentFlow();
        StepUsageDeclaration stepAfterUse= flow.getStepUsageDeclarationByOriginalName("Command Line");
        String command = context.getDataValue(stepAfterUse.getFinalInputByOriginalName("COMMAND").getName(), String.class);
        String arguments = context.getDataValue(stepAfterUse.getFinalInputByOriginalName("ARGUMENTS").getName(), String.class);
        MyLogger logger = new MyLogger();
        logger.addMessage("About to invoke: " + command + " " + arguments);
        if (arguments != null) {
            command = command + " " + arguments;
        }
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            command="cmd /c "+command;
            processBuilder.command(command.split(" "));

            if (arguments != null && !arguments.isEmpty()) {
                //processBuilder.command().addAll(Arrays.asList(arguments.split(" ")));
            }

            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();

            String line= "";
            do{
                output.append(line).append("\n");
            }  while ((line = reader.readLine()) != null);

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                logger.addMessage("Step 'Command Line' executed command successfully: " + command);
                context.setSummeryLine("Command executed successfully");
                logger.setStepResult(StepResult.SUCCESS);
                logger.sortByInstant();
                logger.setDurationByInstant();
                logger.setStepResult(StepResult.SUCCESS);
                context.setStepMessages(logger);
                context.storeDataValue(stepAfterUse.getFinalOutputByOriginalName("RESULT").getName(), output.toString());
                return StepResult.SUCCESS;
            } else {
                logger.addMessage("Step 'Command Line' Command execution failed: " + command);
                context.setSummeryLine("Command execution failed");
                logger.sortByInstant();
                logger.setDurationByInstant();
                logger.setStepResult(StepResult.FAILURE);
                context.setStepMessages(logger);
                return StepResult.FAILURE;
            }
        } catch (IOException | InterruptedException e) {
            logger.addMessage("An error occurred while executing the command: " + command);
            logger.setStepResult(StepResult.FAILURE);
            context.setSummeryLine("Step 'Files Content Extractor' done with FAILURE due An error occurred while executing the command");
            logger.sortByInstant();
            logger.setDurationByInstant();
            context.setStepMessages(logger);
            return StepResult.FAILURE;
        }
    }
}
