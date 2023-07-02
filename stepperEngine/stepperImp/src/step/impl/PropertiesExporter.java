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

public class PropertiesExporter extends AbstractStepDefinition {
    public PropertiesExporter(){
        super("Properties Exporter",true);
        //step input
        addInput(new DataDefinitionDeclarationImpl("SOURCE", DataNecessity.MANDATORY,"Source data", DataDefinitionRegistry.RELATION, DataDefinitionDeclarationShow.TEXT));
        //step output
        addOutput(new DataDefinitionDeclarationImpl("RESULT",DataNecessity.NA,"Properties export result",DataDefinitionRegistry.STRING,DataDefinitionDeclarationShow.TEXT));
    }

    @Override
    public StepResult invoke(StepExecutionContext context) {
        StepResult stepResult=StepResult.SUCCESS;
        MyLogger logger=new MyLogger();
        StringBuilder result = new StringBuilder();
        FlowDefinition flow = context.getCurrentFlow();
        StepUsageDeclaration stepAfterUse = flow.getStepUsageDeclarationByOriginalName("Properties Exporter");
        RelationData relationData = context.getDataValue(stepAfterUse.getFinalInputByOriginalName("SOURCE").getName(), RelationData.class);
        List<String> columns = relationData.getColumns();
        if(relationData.getRows().size()==0){
            logger.addMessage("the table is empty");
            logger.setStepResult(StepResult.WARNING);
            context.setSummeryLine("Step 'Properties Exporter' done with WARNING due empty table");
            context.storeDataValue(stepAfterUse.getFinalOutputByOriginalName("RESULT").getName(),"");
            context.setStepMessages(logger);
            return StepResult.WARNING;
        }
        logger.addMessage("About to process "+relationData.getRows().size()+ " lines of data");

        for (int i = 0; i < relationData.getRows().size(); i++) {
            for (int j = 0; j < relationData.getColumns().size(); j++) {
                result.append("row-" + (i + 1) + ". " + columns.get(j) + "= " + relationData.getRowColumnData(i,columns.get(j)));
                if (j < relationData.getColumns().size() - 1) {
                    result.append(",");
                }
            }
            result.append("\n");
        }
        logger.addMessage("Extracted total of "+relationData.getRows().size());
        logger.setStepResult(stepResult);
        logger.sortByInstant();
        logger.setDurationByInstant();
        context.setStepMessages(logger);
        context.setSummeryLine("Step 'Properties Exporter' done with SUCCESS");
        context.storeDataValue(stepAfterUse.getFinalOutputByOriginalName("RESULT").getName(),result.toString());
        return stepResult;
    }
}
