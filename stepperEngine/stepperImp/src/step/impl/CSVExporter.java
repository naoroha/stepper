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

import java.util.List;

public class CSVExporter extends AbstractStepDefinition {
    public CSVExporter() {
        super("CSV Exporter", true);

        //step inputs

        addInput(new DataDefinitionDeclarationImpl("SOURCE", DataNecessity.MANDATORY, "Source data", DataDefinitionRegistry.RELATION,DataDefinitionDeclarationShow.TEXT));
        //step outputs

        addOutput(new DataDefinitionDeclarationImpl("RESULT", DataNecessity.NA, "CSV export result", DataDefinitionRegistry.STRING, DataDefinitionDeclarationShow.TEXT));
    }
    public StepResult invoke(StepExecutionContext context) {
        StepResult stepResult=StepResult.SUCCESS;
        StringBuilder sb = new StringBuilder();
        MyLogger logger=new MyLogger();
        FlowDefinition flow=context.getCurrentFlow();
        StepUsageDeclaration stepAfterUse= flow.getStepUsageDeclarationByOriginalName("CSV Exporter");
        RelationData relationData=context.getDataValue(stepAfterUse.getFinalInputByOriginalName("SOURCE").getName(), RelationData.class);

        List<String> columns=relationData.getColumns();
        for (int i = 0; i < relationData.getColumns().size(); i++) {
            sb.append(columns.get(i));
            if (i < relationData.getColumns().size() - 1) {
                sb.append("     ,    ");
            }
        }
        sb.append("\n");

        if(relationData.getRows().size()==0){
            logger.addMessage("the table is empty");
            logger.setStepResult(StepResult.WARNING);
            context.setSummeryLine("Step 'CSV Exporter' done with WARNING due empty table");
            context.storeDataValue(stepAfterUse.getFinalOutputByOriginalName("RESULT").getName(),sb.toString());
            context.setStepMessages(logger);
            return StepResult.WARNING;
        }
        logger.addMessage("About to process"+relationData.getRows().size() +"lines of data");
        // write rows
        for (int i = 0; i < relationData.getRows().size(); i++) {
            for (int j = 0; j < relationData.getColumns().size(); j++) {
                if (relationData.getRowColumnData(i, columns.get(j)) != null)
                    sb.append(relationData.getRowColumnData(i, columns.get(j)));
                else {
                    sb.append("");
                }
                if (i < relationData.getColumns().size() - 1) {
                    sb.append("  ,  ");
                }
            }
                sb.append("\n");

        }
        logger.addMessage("Done with SUCCESS");
        logger.setStepResult(stepResult);
        logger.sortByInstant();
        logger.setDurationByInstant();
        context.setStepMessages(logger);
        context.setSummeryLine("Step 'CSV Exporter' done with SUCCESS");
        context.storeDataValue(stepAfterUse.getFinalOutputByOriginalName("RESULT").getName(),sb.toString());
            return stepResult;


    }
}