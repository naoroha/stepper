package step.impl;

import dd.impl.DataDefinitionRegistry;
import dd.impl.relation.RelationData;
import flow.definition.api.FlowDefinition;
import flow.definition.api.StepUsageDeclaration;
import flow.execution.MyLogger;
import flow.execution.context.StepExecutionContext;
import javafx.event.EventDispatchChain;
import step.DataDefinitionDeclarationShow;
import step.api.AbstractStepDefinition;
import step.api.DataDefinitionDeclarationImpl;
import step.api.DataNecessity;
import step.api.StepResult;

import java.io.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class FilesContentExtractor extends AbstractStepDefinition {
    public FilesContentExtractor() {
        super("Files Content Extractor", true);
        //step inputs
        addInput(new DataDefinitionDeclarationImpl("FILES_LIST", DataNecessity.MANDATORY, "Files to extract", DataDefinitionRegistry.LIST, DataDefinitionDeclarationShow.FILE));
        addInput(new DataDefinitionDeclarationImpl("LINE", DataNecessity.MANDATORY, "Line number to extract", DataDefinitionRegistry.NUMBER,DataDefinitionDeclarationShow.NUMBER));
        //step output
        addOutput(new DataDefinitionDeclarationImpl("DATA", DataNecessity.NA, "Data extraction", DataDefinitionRegistry.RELATION,DataDefinitionDeclarationShow.TEXT));
    }

    @Override
    public StepResult invoke(StepExecutionContext context) {
        StepResult stepResult = StepResult.SUCCESS;
        FlowDefinition flow = context.getCurrentFlow();
        MyLogger logger = new MyLogger();
        StringBuilder sb = new StringBuilder();
        List<String> columns = new ArrayList<>();
        columns.add("Index");
        columns.add("Original File Name");
        columns.add("Line Content");
        RelationData relationData = new RelationData(columns);
        StepUsageDeclaration stepAfterUse = flow.getStepUsageDeclarationByOriginalName("Files Content Extractor");
        List<File> fileList = context.getDataValue(stepAfterUse.getFinalInputByOriginalName("FILES_LIST").getName(), List.class);
        int line = context.getDataValue(stepAfterUse.getFinalInputByOriginalName("LINE").getName(), Integer.class);
        boolean lineFound = false;
        int index = 1;
        if (fileList.size() == 0) {
            List<String> row = new ArrayList<>();
            logger.addMessage("there are no files in the given path");
            logger.setStepResult(StepResult.SUCCESS);
            context.setSummeryLine("Step 'Files Content Extractor' done with SUCCESS there are no files in the given path");
            row.add("1");
            row.add("File Not Found");
            row.add("File Not Found");
            relationData.addDatas(row);
            context.setStepMessages(logger);
            context.storeDataValue(stepAfterUse.getFinalOutputByOriginalName("DATA").getName(), relationData);
            return StepResult.SUCCESS;
        }

        for (File file : fileList) {

            List<String> row = new ArrayList<>();
            logger.addMessage("About to start work on file " + file.getName());
            row.add(Integer.toString(index));
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String content;
                int currentLine = 1;
                row.add(file.getName());
                while ((content = reader.readLine()) != null) {
                    if (currentLine == line) {
                        row.add(content);
                        break;
                    }

                    currentLine++;
                }
            } catch (IOException e) {
                logger.addMessage("Problem extracting line number " + line + " from file " + file.getName());
                logger.setStepResult(StepResult.FAILURE);
                context.setSummeryLine("Step 'Files Content Extractor' done with FAILURE due some failing in extraction");
                context.setStepMessages(logger);
                return StepResult.FAILURE;
            }
            relationData.addDatas(row);
            index++;
        }
        logger.setStepResult(StepResult.SUCCESS);
        logger.sortByInstant();
        logger.setDurationByInstant();
        context.setStepMessages(logger);
        context.storeDataValue(stepAfterUse.getFinalOutputByOriginalName("DATA").getName(), relationData);
        return stepResult;
    }

}
