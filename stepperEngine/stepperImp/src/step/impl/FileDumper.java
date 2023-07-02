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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileDumper extends AbstractStepDefinition {
    public FileDumper() {
        super("File Dumper", true);

        //step inputs

        addInput(new DataDefinitionDeclarationImpl("CONTENT", DataNecessity.MANDATORY, "Content", DataDefinitionRegistry.STRING, DataDefinitionDeclarationShow.TEXT));
        addInput(new DataDefinitionDeclarationImpl("FILE_NAME", DataNecessity.MANDATORY, "Target file path", DataDefinitionRegistry.STRING,DataDefinitionDeclarationShow.TEXT));
        //step outputs

        addOutput(new DataDefinitionDeclarationImpl("RESULT", DataNecessity.NA, "File Creation Result", DataDefinitionRegistry.STRING,DataDefinitionDeclarationShow.TEXT));


    }

    @Override
    public StepResult invoke(StepExecutionContext context) {

        StepResult stepResult=StepResult.SUCCESS;
        FlowDefinition flow=context.getCurrentFlow();
        MyLogger logger=new MyLogger();
        StepUsageDeclaration stepAfterUse= flow.getStepUsageDeclarationByOriginalName("File Dumper");
        try {
            // Create a new BufferedWriter to write to the file
            String content=context.getDataValue(stepAfterUse.getFinalInputByOriginalName("CONTENT").getName(),String.class);
            String fileName=context.getDataValue(stepAfterUse.getFinalInputByOriginalName("FILE_NAME").getName(),String.class);

            logger.addMessage("About to create file named "+fileName);
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));


            // Write the content to the file
            writer.write(content);

            // Close the writer
            writer.close();

            // Return success message
            if(content.equals("")){
                stepResult=StepResult.WARNING;
                logger.addMessage("empty content");
                context.setSummeryLine("Step 'File Dumper' done with WARNING due empty content");
            }
            else{
                context.setSummeryLine("Step 'File Dumper' done with SUCCESS ");
            }
            logger.sortByInstant();
            logger.setDurationByInstant();
            logger.setStepResult(stepResult);
            context.setStepMessages(logger);
            context.storeDataValue(stepAfterUse.getFinalOutputByOriginalName("RESULT").getName(),"SUCCESS");
            return stepResult;
        } catch (IOException e) {
            logger.addMessage("Failed to create the file");
            logger.setStepResult(StepResult.FAILURE);
            context.setSummeryLine("Step 'File Dumper' done with Failure due failing to create the file");
            context.setStepMessages(logger);
            context.storeDataValue(stepAfterUse.getFinalOutputByOriginalName("RESULT").getName(),"could not load file");
            return StepResult.FAILURE;
        }

    }
}
