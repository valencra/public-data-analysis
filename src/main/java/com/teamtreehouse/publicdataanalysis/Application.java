package com.teamtreehouse.publicdataanalysis;

import com.teamtreehouse.publicdataanalysis.model.Country;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Application {
    private static final SessionFactory sessionFactory = buildSessionFactory();
    private static final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) {
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

    private static SessionFactory buildSessionFactory() {
        // Build reusable session factory
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        return new MetadataSources(registry).buildMetadata().buildSessionFactory();
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
                viewDataTable();
                break;
            case 2:
                // View statistics
                System.out.printf("%nViewing statistics...%n%n");
                break;
            case 3:
                // Add country
                System.out.printf("%nAdding country...%n%n");
                break;
            case 4:
                // Edit country
                System.out.printf("%nEditing country...%n%n");
                break;
            case 5:
                // Delete country
                System.out.printf("%nDeleting country...%n%n");
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

    private static void viewDataTable() {
        // View data table
        Session session = sessionFactory.openSession();
        Criteria criteria = session.createCriteria(Country.class);
        List<Country> countries = criteria.list();
        session.close();
        System.out.println(String.format(
                "%s%n%s",
                String.format("%-10s%-45s%15s%10s", "Code", "Country", "Internet Users", "Literacy"),
                String.join("", Collections.nCopies(80, "-"))
        ));
        countries.stream().forEach(
                country -> {
                    System.out.println(
                            String.format(
                                    "%-10s%-45s%15s%10s",
                                    country.getCode(),
                                    country.getName(),
                                    country.getInternetUsers() == null ? "--" : roundUpAndFormat(country.getInternetUsers()),
                                    country.getAdultLiteracyRate() == null ? "--" : roundUpAndFormat(country.getAdultLiteracyRate())
                            )
                    );
                }
        );
    }

    private static String roundUpAndFormat(Double value) {
        return String.format(
                "%.2f",
                new BigDecimal(value)
                        .setScale(2, RoundingMode.HALF_UP)
                        .doubleValue()
                );
    }
}
