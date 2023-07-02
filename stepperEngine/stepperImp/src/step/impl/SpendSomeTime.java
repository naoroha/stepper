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

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class SpendSomeTime extends AbstractStepDefinition {
    public SpendSomeTime(){
        super("Spend some time",true);
        addInput(new DataDefinitionDeclarationImpl("TIME_TO_SPEND", DataNecessity.MANDATORY,"Total sleeping time (sec)", DataDefinitionRegistry.NUMBER, DataDefinitionDeclarationShow.NUMBER));
    }

    @Override
    public StepResult invoke(StepExecutionContext context) {
        StepResult stepResult=StepResult.SUCCESS;
        FlowDefinition flow=context.getCurrentFlow();
        StepUsageDeclaration stepAfterUse= flow.getStepUsageDeclarationByOriginalName("Spend some time");
        MyLogger logger=new MyLogger();

        int timeToSpend=context.getDataValue(stepAfterUse.getFinalInputByOriginalName("TIME_TO_SPEND").getName(),Integer.class);
        if(timeToSpend<=0){
            logger.addMessage("you can't spend negative or zero sec");
            logger.setStepResult(StepResult.FAILURE);
            context.setStepMessages(logger);
            context.setSummeryLine("Step 'Spend some time' done with FAILURE due non natural number input");
            return StepResult.FAILURE;
        }
        logger.addMessage("About to sleep for " +timeToSpend+ " seconds…");

        try {
            Thread.sleep(timeToSpend * 1000);
            logger.addMessage("Done sleeping...");
        }catch (InterruptedException e){
            logger.addMessage("Failed to sleep");
            logger.setStepResult(StepResult.FAILURE);
            context.setStepMessages(logger);
            context.setSummeryLine("Step 'Spend some time' done with FAILURE due there was an interruption with sleeping");
            return StepResult.FAILURE;
        }
        logger.setStepResult(stepResult);
        logger.sortByInstant();
        logger.setDurationByInstant();
        context.setStepMessages(logger);
        context.setSummeryLine("Step 'Spend some time' done with SUCCESS slept for "+timeToSpend+ " sec");
        return stepResult;
    }
}
