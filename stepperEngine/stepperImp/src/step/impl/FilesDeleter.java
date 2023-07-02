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
import sun.misc.UCDecoder;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilesDeleter extends AbstractStepDefinition {
    public FilesDeleter() {
        super("Files Deleter", false);

        //step inputs

        addInput(new DataDefinitionDeclarationImpl("FILES_LIST", DataNecessity.MANDATORY, "Files to delete", DataDefinitionRegistry.LIST, DataDefinitionDeclarationShow.TEXT));
        //step outputs

        addOutput(new DataDefinitionDeclarationImpl("DELETED_LIST", DataNecessity.NA, "Files failed to be deleted", DataDefinitionRegistry.LIST,DataDefinitionDeclarationShow.TEXT));
        addOutput(new DataDefinitionDeclarationImpl("DELETION_STATS", DataNecessity.NA, "Deletion summary results", DataDefinitionRegistry.MAPPING,DataDefinitionDeclarationShow.TEXT));

    }

    public StepResult invoke(StepExecutionContext context) {
        StepResult stepResult = StepResult.SUCCESS;
        Map<Integer, Integer> deletionStates = new HashMap<>();
        FlowDefinition flow=context.getCurrentFlow();
        MyLogger logger=new MyLogger();
        String message="";
        StepUsageDeclaration stepAfterUse= flow.getStepUsageDeclarationByOriginalName("Files Deleter");
        List<File> files = context.getDataValue(stepAfterUse.getFinalInputByOriginalName("FILES_LIST").getName(), List.class);
        message="About to start delete " +files.size()+  " files";
        logger.addMessage(message);
        List<String> undeletedFiles = new ArrayList<>();
        int size= files.size();
        int cdr=0;
        for (File file : files) {
            if (!file.delete()) {
                logger.addMessage("Failed to delete "+ file.getName());
                undeletedFiles.add(file.getAbsolutePath());
                cdr++;
            }
        }
        deletionStates.put(size-cdr, cdr);
        if(cdr==0){
            logger.setStepResult(StepResult.SUCCESS);
            context.setSummeryLine("Step 'Files Deleter' done with SUCCESS all files in folder was deleted");
        } else if (cdr<size) {
            stepResult=StepResult.WARNING;
            logger.addMessage("Failed to delete some of the files");
            context.setSummeryLine("Step 'Files Deleter' done with WARNING due failing to delete some of the files");
        }
        else{
            logger.addMessage("All files were unable to delete");
            logger.setStepResult(StepResult.FAILURE);
            context.setSummeryLine("Step 'Files Deleter' done with FAILURE due failing to delete all files in folder");
            context.setStepMessages(logger);
            return StepResult.FAILURE;

        }
        logger.setStepResult(stepResult);
        logger.sortByInstant();
        logger.setDurationByInstant();
        context.setStepMessages(logger);
        context.storeDataValue(stepAfterUse.getFinalOutputByOriginalName("DELETED_LIST").getName(), undeletedFiles);
        context.storeDataValue(stepAfterUse.getFinalOutputByOriginalName("DELETION_STATS").getName(), deletionStates);
        return stepResult;
    }
}
