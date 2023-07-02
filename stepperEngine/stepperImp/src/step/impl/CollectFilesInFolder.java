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

import java.io.File;
import java.io.FilenameFilter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.sun.xml.internal.ws.api.model.wsdl.WSDLBoundOperation.ANONYMOUS.optional;
import static jdk.nashorn.internal.objects.NativeArray.map;

public class CollectFilesInFolder extends AbstractStepDefinition {

    public CollectFilesInFolder() {
        super("Collect Files In Folder", true);
        //step inputs
        addInput(new DataDefinitionDeclarationImpl("FOLDER_NAME", DataNecessity.MANDATORY, "Folder name to scan", DataDefinitionRegistry.STRING, DataDefinitionDeclarationShow.PATH));
        addInput(new DataDefinitionDeclarationImpl("FILTER", DataNecessity.OPTIONAL, "Filter only these files", DataDefinitionRegistry.STRING,DataDefinitionDeclarationShow.TEXT));

        //add outputs
        addOutput(new DataDefinitionDeclarationImpl("FILES_LIST", DataNecessity.NA, "Files list", DataDefinitionRegistry.LIST,null));
        addOutput(new DataDefinitionDeclarationImpl("TOTAL_FOUND", DataNecessity.NA, "Total files found", DataDefinitionRegistry.NUMBER,null));
    }


    @Override
    public StepResult invoke(StepExecutionContext context) {
        StepResult stepResult=StepResult.SUCCESS;
        FlowDefinition flow=context.getCurrentFlow();
        MyLogger logger=new MyLogger();
        StepUsageDeclaration stepAfterUse= flow.getStepUsageDeclarationByOriginalName("Collect Files In Folder");


        String folderName=context.getDataValue(stepAfterUse.getFinalInputByOriginalName("FOLDER_NAME").getName(),String.class);
        String filter = context.getDataValue(stepAfterUse.getFinalInputByOriginalName("FILTER").getName(), String.class);
        String message="Reading folder "+ folderName +"content";
        if(filter!=null){
            message=message+" with filter "+ filter;
        }
        logger.addMessage(message);
        List<File> fileList = new ArrayList<>();
        File folder = new File(folderName);
        if(folder.exists()) {
            if (folder.isDirectory()) {
                File[] files = folder.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (filter == null || file.getName().endsWith(filter)) {
                            fileList.add(file);
                        }
                    }
                }
                else{
                    logger.addMessage("There is no files in the given folder");
                    stepResult=StepResult.WARNING;
                }
            }
            else{
                logger.addMessage("The given path is not a Folder!");
                logger.setStepResult(StepResult.FAILURE);
                logger.setDurationByInstant();
                context.setSummeryLine("Step 'Collect Files In Folder' done with FAILURE, the given path was not a folder");
                context.setStepMessages(logger);
                return StepResult.FAILURE;
            }
        }
        else{
            logger.addMessage("The folder does not exist");
            logger.setStepResult(StepResult.FAILURE);
            context.setSummeryLine("Step 'Collect Files In Folder' done with FAILURE, folder does not exist");
            context.setStepMessages(logger);
            return StepResult.FAILURE;
        }
        context.storeDataValue(stepAfterUse.getFinalOutputByOriginalName("FILES_LIST").getName(),fileList);
        context.storeDataValue(stepAfterUse.getFinalOutputByOriginalName("TOTAL_FOUND").getName(),fileList.size());
        message="Found "+ fileList.size()+ " files in folder";
        if(filter!= null){
            message= message+" matching the filter";
        }
        logger.addMessage(message);
        logger.sortByInstant();
        logger.setDurationByInstant();
        logger.setStepResult(stepResult);
        if(stepResult==StepResult.WARNING)
            context.setSummeryLine("Step 'Collect Files In Folder' done with WARNING there is no files in the given folder");
        else
            context.setSummeryLine("Step 'Collect Files In Folder' done with SUCCESS");
        context.setStepMessages(logger);
        return stepResult;

    }
}
