package step.impl;

import dd.impl.DataDefinitionRegistry;
import dd.impl.relation.RelationData;
import flow.definition.api.FlowDefinition;
import flow.definition.api.StepUsageDeclaration;
import flow.execution.MyLogger;
import flow.execution.context.StepExecutionContext;
import step.DataDefinitionDeclarationShow;
import step.api.AbstractStepDefinition;
import step.api.DataDefinitionDeclarationImpl;
import step.api.DataNecessity;
import step.api.StepResult;

import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class FilesRenamer extends AbstractStepDefinition {
    public FilesRenamer(){
        super("Files Renamer",false);
        //step inputs
        addInput(new DataDefinitionDeclarationImpl("FILES_TO_RENAME", DataNecessity.MANDATORY,"Files to rename", DataDefinitionRegistry.LIST, DataDefinitionDeclarationShow.FILE));
        addInput(new DataDefinitionDeclarationImpl("PREFIX", DataNecessity.OPTIONAL,"Add this prefix", DataDefinitionRegistry.STRING,DataDefinitionDeclarationShow.TEXT));
        addInput(new DataDefinitionDeclarationImpl("SUFFIX", DataNecessity.OPTIONAL,"Append this suffix", DataDefinitionRegistry.STRING,DataDefinitionDeclarationShow.TEXT));
        //step output
        addOutput(new DataDefinitionDeclarationImpl("RENAME_RESULT",DataNecessity.NA,"Rename operation summary",DataDefinitionRegistry.RELATION,DataDefinitionDeclarationShow.TEXT));
    }

    @Override
    public StepResult invoke(StepExecutionContext context) {
        StepResult stepResult=StepResult.SUCCESS;
        StringBuilder summery=new StringBuilder();
        summery.append("Step 'Files Renamer' done with ");
        FlowDefinition flow=context.getCurrentFlow();
        StepUsageDeclaration stepAfterUse= flow.getStepUsageDeclarationByOriginalName("Files Renamer");
        MyLogger logger=new MyLogger();
        List<String> notRenamedFiles=new ArrayList<>();
        // Add prefix and/or suffix to file names
        List<File>  fileList=context.getDataValue(stepAfterUse.getFinalInputByOriginalName("FILES_TO_RENAME").getName(),List.class);
        String  prefix=context.getDataValue(stepAfterUse.getFinalInputByOriginalName("PREFIX").getName(),String.class);
        String  suffix=context.getDataValue(stepAfterUse.getFinalInputByOriginalName("SUFFIX").getName(),String.class);
        List<String> columns=new ArrayList<>();
        columns.add("Index");
        columns.add("Original name ");
        columns.add("name after change");
        RelationData relationData=new RelationData(columns);
        int Index=1;
        if(fileList.size()==0)
        {
            logger.addMessage("the file given is empty");
            logger.setStepResult(StepResult.SUCCESS);
            context.setSummeryLine("Step 'Files Renamer' done with SUCCESS there are no files in the given path");
            context.setStepMessages(logger);
            context.storeDataValue(stepAfterUse.getFinalOutputByOriginalName("DATA").getName(), relationData);
            return StepResult.SUCCESS;
        }
        logger.addMessage("About to start rename "+fileList.size() +" files. Adding prefix:"+prefix +"; adding suffix:"+suffix);

        for (File file : fileList) {
            List<String> rows=new ArrayList<>();
            rows.add(Integer.toString(Index));
            String filename = file.getName();
            rows.add(filename);
            String newName=filename;
            boolean reNamed=true;

            if (prefix != null) {
                newName = prefix + filename;
                File renamedFile = new File(file.getParent(), newName);
                reNamed = file.renameTo(renamedFile);
            }
            if (suffix != null) {
                int dotIndex = filename.lastIndexOf(".");
                if (dotIndex != -1) {
                    newName = filename.substring(0, dotIndex) + suffix + filename.substring(dotIndex);
                } else {
                    newName = filename + suffix;
                }
                File renamedFile = new File(file.getParent(), newName);
                reNamed = file.renameTo(renamedFile);
            }
            if (!reNamed) {
                stepResult=StepResult.WARNING;
                logger.addMessage("Problem renaming file"+ filename);
                notRenamedFiles.add(filename);
            }
            rows.add(newName);
            relationData.addDatas(rows);
            Index++;
        }
        if(stepResult==StepResult.WARNING){
            summery.append("WARNING due failure to rename those files: ");
            for (int i=0;i< notRenamedFiles.size();i++){
                summery.append(notRenamedFiles.get(i));
                if(i< notRenamedFiles.size()-1)
                    summery.append(",");
            }
        }
        else{
            summery.append("SUCCESS ");
        }
        logger.setStepResult(stepResult);
        logger.sortByInstant();
        logger.setDurationByInstant();
        context.setStepMessages(logger);
        context.storeDataValue(stepAfterUse.getFinalOutputByOriginalName("RENAME_RESULT").getName(),relationData);
        return stepResult;

    }
}
