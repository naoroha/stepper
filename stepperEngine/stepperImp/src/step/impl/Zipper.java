package step.impl;

import dd.impl.DataDefinitionRegistry;
import dd.impl.enumerator.Enumerator;
import dd.impl.enumerator.EnumeratorData;
import dd.impl.enumerator.EnumeratorDataDefinition;
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


public class Zipper extends AbstractStepDefinition {
//    public class EnumeratorData{
//        public final Set<String> enumerator;
//        public EnumeratorData(){
//            enumerator=new HashSet<>();
//            enumerator.add("ZIP");
//            enumerator.add("UNZIP");
//        }
//    }
    public Zipper(){
        super("Zipper",false);
        //step inputs
        addInput(new DataDefinitionDeclarationImpl("SOURCE", DataNecessity.MANDATORY,"Source", DataDefinitionRegistry.STRING, DataDefinitionDeclarationShow.TEXT));
        addInput(new DataDefinitionDeclarationImpl("OPERATION", DataNecessity.MANDATORY,"Operation type", new EnumeratorDataDefinition(),DataDefinitionDeclarationShow.CHOICE_BOX));

        addOutput(new DataDefinitionDeclarationImpl("RESULT",DataNecessity.NA,"Zip operation result",DataDefinitionRegistry.STRING,DataDefinitionDeclarationShow.TEXT));

    }

    @Override
    public StepResult invoke(StepExecutionContext context) throws IOException {

        //the get data enumeration from the context tell us if we need to do zip or unzip
        MyLogger logger = new MyLogger();
        FlowDefinition flow=context.getCurrentFlow();
        StepUsageDeclaration stepAfterUse= flow.getStepUsageDeclarationByOriginalName("Zipper");
        String Source = context.getDataValue(stepAfterUse.getFinalInputByOriginalName("SOURCE").getName(), String.class);
        //check if path round with " or not
        if(Source.charAt(0)=='\"'){
            Source=Source.substring(1,Source.length()-1);
        }
        EnumeratorData zipOrUnzip = context.getDataValue(stepAfterUse.getFinalInputByOriginalName("OPERATION").getName(), EnumeratorData.class);
        if(zipOrUnzip.containsString("ZIP")){

            zipFile(Source,Source);
            logger.addMessage("Step 'ZIPPER' End with Success ,Zip operation was done");
            logger.setStepResult(StepResult.SUCCESS);
            context.setSummeryLine("End with Success ,Zip operation was done");
            logger.sortByInstant();
            logger.setDurationByInstant();
            context.setStepMessages(logger);
            String result = "Success";
            context.storeDataValue(stepAfterUse.getFinalOutputByOriginalName("RESULT").getName(), result);
            return StepResult.SUCCESS;
        }else if(zipOrUnzip.containsString("UNZIP")) {
            boolean check = checkIfFileValid(Source);
            if (!check)
            {

                //mean that the file is not file.zip
                //context.setLogsForStep("Zipper", "Error: File is not unzippable");
                logger.addMessage("Step 'ZIPPER' End with Error: File is not unzippable");
                logger.setStepResult(StepResult.FAILURE);
                context.setSummeryLine("Step 'ZIPPER' End with Failure ,File is not valid");
                String result = "Error: File is not valid";
                context.storeDataValue(stepAfterUse.getFinalOutputByOriginalName("RESULT").getName(), result);
                logger.sortByInstant();
                logger.setDurationByInstant();
                context.setStepMessages(logger);
                return StepResult.FAILURE;
            }
            unzip(Source);
            logger.addMessage("Step 'ZIPPER' End with Success ,Unzip operation was done");
            logger.setStepResult(StepResult.SUCCESS);
            context.setSummeryLine("End with Success ,Unzip operation was done");
            logger.sortByInstant();
            logger.setDurationByInstant();
            context.setStepMessages(logger);
            String result = "Success";
            context.storeDataValue(stepAfterUse.getFinalOutputByOriginalName("RESULT").getName(), result);
            return StepResult.SUCCESS;
        }else {
            logger.addMessage("Step 'ZIPPER' End with Error Operation type is not valid");
            logger.setStepResult(StepResult.FAILURE);
            context.setSummeryLine("Step 'ZIPPER' End with Failure ,File is not valid");
            String result = "Error: File is not valid";
            context.storeDataValue(stepAfterUse.getFinalOutputByOriginalName("RESULT").getName(), result);
            logger.sortByInstant();
            logger.setDurationByInstant();
            context.setStepMessages(logger);
            return StepResult.FAILURE;
        }
    }

    private boolean checkIfFileValid(String path) {
        //check if file path end with .zip\
        String[] split = path.split("\\.");
        String endOfFile = split[split.length - 1];
        if((!endOfFile.equals("zip"))&&(!endOfFile.equals("zip\""))){
            return false;
        }
        return true;
    }

    private void zipFile(String sourceFilePath, String zipFilePath) throws IOException {
        zipFilePath += ".zip";

        File fileToZip = new File(sourceFilePath);

        try (FileOutputStream fos = new FileOutputStream(zipFilePath);
             ZipOutputStream zipOut = new ZipOutputStream(fos);
             FileInputStream fis = new FileInputStream(fileToZip)) {

            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            zipOut.putNextEntry(zipEntry);

            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
        }

    }
    private static void unzip(String source) throws IOException {

        File zipFile = new File(source);
        String destinationFolder = getDestinationFolder(source);

        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFile.toPath()));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            String entryName = zipEntry.getName();
            File newFile = new File(destinationFolder, entryName);

            if (zipEntry.isDirectory()) {
                newFile.mkdirs();
            } else {
                FileOutputStream fos = new FileOutputStream(newFile);
                int length;
                while ((length = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }
                fos.close();
            }

            zis.closeEntry();
            zipEntry = zis.getNextEntry();
        }
        zis.close();
    }
    private static String getDestinationFolder(String source) {
        File zipFile = new File(source);
        String parentFolder = zipFile.getParent();
        return parentFolder != null ? parentFolder : "";
    }
    private static String getBaseName(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex != -1) {
            return fileName.substring(0, lastDotIndex);
        }
        return fileName;
    }
}
