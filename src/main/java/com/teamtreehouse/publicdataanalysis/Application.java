package com.teamtreehouse.publicdataanalysis;

import com.teamtreehouse.publicdataanalysis.utils.Menu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Application {
    private static final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) {
        Menu menu = new Menu(); // only has static methods (instance not used), but instantiated to kick-off session builder at the beginning.
        int selectedOption = 0;
        while (selectedOption != 6) {
            displayMenu();
            try {
                selectedOption = getSelectedOption();
            } catch (IOException e) {
                e.printStackTrace();
            }
            runSelectedOption(selectedOption);
        }
    }

    private static void displayMenu() {
        // Display menu options
        System.out.println(String.format(
                "%n%n%s%n%s%n%s",
                String.join("", Collections.nCopies(40, "=")),
                "World Bank Database Menu",
                String.join("", Collections.nCopies(40, "="))
        ));
        List<String> menuOptions = Arrays.asList(
                "1. View data table",
                "2. View statistics",
                "3. Add country",
                "4. Edit country",
                "5. Delete country",
                "6. Exit"
        );
        menuOptions.stream().forEach(menuOption -> System.out.printf("%s%n", menuOption));
    }

    private static int getSelectedOption() throws IOException {
        // Get selected option
        int selectedOption = 0;
        System.out.print(String.format(
                "%s%n%s",
                String.join("", Collections.nCopies(40, "-")),
                "Enter option number (1-6): "
        ));
        selectedOption = Integer.parseInt(bufferedReader.readLine());
        return selectedOption;
    }

    private static void runSelectedOption(int selectedOption) {
        // Run selected option
        switch (selectedOption) {
            case 1:
                // View data table
                System.out.printf("%nViewing data table...%n%n");
                Menu.viewDataTable();
                break;
            case 2:
                // View statistics
                System.out.printf("%nViewing statistics...%n%n");
                Menu.viewStatistics();
                break;
            case 3:
                // Add country
                System.out.printf("%nAdding country...%n%n");
                boolean isAdded = false;
                while (!isAdded) {
                    try {
                        isAdded = Menu.addCountry();
                    }
                    catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
                break;
            case 4:
                // Edit country
                System.out.printf("%nEditing country...%n%n");
                boolean isEdited = false;
                while (!isEdited) {
                    try {
                        isEdited = Menu.editCountry();
                    }
                    catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
                break;
            case 5:
                // Delete country
                System.out.printf("%nDeleting country...%n%n");
                boolean isDeleted = false;
                while (!isDeleted) {
                    try {
                        isDeleted = Menu.deleteCountry();
                    }
                    catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
                break;
            case 6:
                // Exit
                System.out.printf("%nExiting...%n%n");
                System.exit(0);
                break;
            default:
                System.out.printf("%nInvalid option. Please enter a number from 1-6.");
        }
    }

}
