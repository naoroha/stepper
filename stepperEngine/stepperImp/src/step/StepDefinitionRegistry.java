package step;

import dd.api.DataDefinition;
import dd.impl.relation.RelationDataDefinition;
import dd.impl.string.StringDataDefinition;
import step.api.StepDefinition;
import step.impl.*;

public enum StepDefinitionRegistry {
    SPEND_SOME_TIME(new SpendSomeTime()),
    COLLECT_FILES_IN_FOLDER(new CollectFilesInFolder()),
    FILES_DELETER(new FilesDeleter()),
    FILES_RENAMER(new FilesRenamer()),
    FILES_CONTENT_EXTRACTOR(new FilesContentExtractor()),
    CSV_EXPORTER(new CSVExporter()),
    PROPERTIES_EXPORTER(new PropertiesExporter()),
    FILE_DUMPER(new FileDumper()),
    ZIPPER(new Zipper()),
    COMMAND_LINE(new CommandLine());

    private final StepDefinition stepDefinition;

    StepDefinitionRegistry(StepDefinition stepDefinition) {
        this.stepDefinition = stepDefinition;
    }


    public StepDefinition getStepDefinition() {
        return stepDefinition;
    }
    public StepDefinition getStepDefinitionByName(String name)
    {
        StepDefinitionRegistry step = StepDefinitionRegistry.valueOf(name);
        return step.getStepDefinition();
    }
}
