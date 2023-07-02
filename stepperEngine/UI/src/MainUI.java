import commands.MenuFunctions;
import commands.MenuFunctionsImpl;
import stepper.StepperDefinition;
import stepper.StepperDefinitionImpl;

import javax.xml.bind.JAXBException;
import java.util.Scanner;

public class MainUI {
    public static void main(String[] args) throws JAXBException, NoSuchFieldException {
        MenuFunctions handeler = new MenuFunctionsImpl();
        StepperDefinition stepper = new StepperDefinitionImpl();
        Scanner scanner = new Scanner(System.in);
        int choice;
        do {
            System.out.println("Menu:");
            System.out.println("1. Read XML path");
            System.out.println("2. show flow info");
            System.out.println("3. execute flow");
            System.out.println("4. full details of previous executed flows");
            System.out.println("5. show flow statistics");
            System.out.println("6. Exit");

            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            switch (choice) {
//                case 1:
//                    stepper = handeler.applyXMLReaderCommand("h");
//                    break;
//                case 2:
//                    handeler.showFlow(stepper);
//                    break;
                case 3:
                    handeler.executeFlow(null);
                    break;
                case 4:
                    handeler.showPreviousExecutedFlowsDetails();
                    break;
                case 5:
                    handeler.showStatistics();
                    break;
                case 6:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        } while (choice != 6);
    }
}