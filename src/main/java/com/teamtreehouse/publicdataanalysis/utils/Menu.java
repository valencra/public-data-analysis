package com.teamtreehouse.publicdataanalysis.utils;

import com.teamtreehouse.publicdataanalysis.model.Country;
import com.teamtreehouse.publicdataanalysis.model.CountryBuilder;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Menu {
    private static final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        // Build reusable session factory
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        return new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    public static void viewDataTable() {
        // View data table
        List<Country> countries = getCountries();
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

    public static void viewStatistics() {
        // View statistics
        List<Country> countries = getCountries();

        // Calculate individual statistics
        List<Double> allInternetUsers = countries.stream()
                .filter(country -> country.getInternetUsers() != null)
                .map(country -> country.getInternetUsers())
                .collect(Collectors.toList());
        Map<String, Double> allInternetUsersStats = Statistics.calculateStatistics(allInternetUsers);
        List<Double> allAdultLiteracyRates = countries.stream()
                .filter(country -> country.getAdultLiteracyRate() != null)
                .map(country -> country.getAdultLiteracyRate())
                .collect(Collectors.toList());
        Map<String, Double> allAdultLiteracyRatesStats = Statistics.calculateStatistics(allAdultLiteracyRates);

        // Calculate correlation
        List<Double> pairedInternetUsers = countries.stream()
                .filter(country -> (country.getInternetUsers() != null) && (country.getAdultLiteracyRate() != null))
                .map(country -> country.getInternetUsers())
                .collect(Collectors.toList());
        Map<String, Double> pairedInternetUsersStats = Statistics.calculateStatistics(pairedInternetUsers);
        List<Double> pairedAdultLiteracyRates = countries.stream()
                .filter(country -> (country.getInternetUsers() != null) && (country.getAdultLiteracyRate() != null))
                .map(country -> country.getAdultLiteracyRate())
                .collect(Collectors.toList());
        Map<String, Double> pairedAdultLiteracyRatesStats = Statistics.calculateStatistics(pairedAdultLiteracyRates);
        Double indicatorCorr = Statistics.calculateCorrelation(
                pairedInternetUsers, pairedInternetUsersStats, pairedAdultLiteracyRates, pairedAdultLiteracyRatesStats
        );

        System.out.println(String.format(
                "%s%n%s",
                String.format("%-20s%15s%15s%15s%15s", "Indicator", "Min", "Max", "Count", "Average"),
                String.join("", Collections.nCopies(80, "-"))
        ));
        System.out.println(String.format(
                "%s",
                String.format(
                        "%-20s%15s%15s%15s%15s",
                        "Internet Users",
                        roundUpAndFormat(allInternetUsersStats.get("min")),
                        roundUpAndFormat(allInternetUsersStats.get("max")),
                        roundUpAndFormat(allInternetUsersStats.get("count")),
                        roundUpAndFormat(allInternetUsersStats.get("avg"))
                )
        ));
        System.out.println(String.format(
                "%s",
                String.format(
                        "%-20s%15s%15s%15s%15s",
                        "Adult Literacy Rate",
                        roundUpAndFormat(allAdultLiteracyRatesStats.get("min")),
                        roundUpAndFormat(allAdultLiteracyRatesStats.get("max")),
                        roundUpAndFormat(allAdultLiteracyRatesStats.get("count")),
                        roundUpAndFormat(allAdultLiteracyRatesStats.get("avg"))
                )
        ));
        System.out.printf("%n* Correlation: %s%n", roundUpAndFormat(indicatorCorr));
    }

    public static boolean addCountry() throws IOException, IllegalArgumentException {
        // Add a country
        String code = null;
        String name = null;
        Double internetUsers = null;
        Double literacy = null;
        List<Country> countries = getCountries();
        List<String> countryCodes = countries.stream()
                .map(country -> { return  country.getCode(); })
                .collect(Collectors.toList());

        // get code
        System.out.print("Enter code: ");
        code = bufferedReader.readLine();
        if (code.length() != 3 || countryCodes.contains(code)) {
            throw new IllegalArgumentException("Country code must be a new, unique, 3-character string");
        }

        // get name
        System.out.print("Enter name: ");
        name = bufferedReader.readLine();
        if (name.length() > 32) {
            throw new IllegalArgumentException("Country name can have a maximum of 32 characters");
        }

        // get internet users
        System.out.print("Enter internet users: ");
        internetUsers = Double.parseDouble(bufferedReader.readLine());
        String internetUsersText = internetUsers.toString();
        int digits = internetUsersText.indexOf(".");
        int decimals = internetUsersText.length() - 1 - digits;
        if (digits > 11 || decimals > 8) {
            throw new IllegalArgumentException("Country internet users can have a maximum of 11 digits and 8 decimals");
        }

        // get literacy
        System.out.print("Enter literacy: ");
        literacy = Double.parseDouble(bufferedReader.readLine());
        String literacyText = literacy.toString();
        digits = literacyText.indexOf(".");
        decimals = literacyText.length() - 1 - digits;
        if (digits > 11 || decimals > 8) {
            throw new IllegalArgumentException("Country literacy can have a maximum of 11 digits and 8 decimals");
        }

        // create and save new country
        Country newCountry = new CountryBuilder(code, name)
                .withInternetUsers(internetUsers)
                .withAdultLiteracyRate(literacy)
                .build();
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.save(newCountry);
        session.getTransaction().commit();
        session.close();
        System.out.println("Country added successfully!");
        return true;
    }

    public static boolean editCountry() throws IOException, IllegalArgumentException {
        // Edit a country
        String code = null;
        String name = null;
        Double internetUsers = null;
        Double literacy = null;
        List<Country> countries = getCountries();
        List<String> countryCodes = countries.stream()
                .map(country -> { return  country.getCode(); })
                .collect(Collectors.toList());

        // get country to edit
        System.out.print("Enter code: ");
        code = bufferedReader.readLine();
        if (!countryCodes.contains(code)) {
            throw new IllegalArgumentException("Country code must be an existing, unique, 3-character string");
        }
        Country country = getCountryByCode(code);

        System.out.print("Enter new code: ");
        code = bufferedReader.readLine();
        if (code.equals(country.getCode())) {
        }
        else if (code.length() != 3 || countryCodes.contains(code)) {
            throw new IllegalArgumentException("Country code must be a new, unique, 3-character string");
        }
        else {
            country.setCode(code);
        }

        // get name
        System.out.print("Enter new name: ");
        name = bufferedReader.readLine();
        if (name.length() > 32) {
            throw new IllegalArgumentException("Country name can have a maximum of 32 characters");
        }
        else {
            country.setName(name);
        }

        // get internet users
        System.out.print("Enter new internet users: ");
        internetUsers = Double.parseDouble(bufferedReader.readLine());
        String internetUsersText = internetUsers.toString();
        int digits = internetUsersText.indexOf(".");
        int decimals = internetUsersText.length() - 1 - digits;
        if (digits > 11 || decimals > 8) {
            throw new IllegalArgumentException("Country internet users can have a maximum of 11 digits and 8 decimals");
        }
        else {
            country.setInternetUsers(internetUsers);
        }

        // get literacy
        System.out.print("Enter new literacy: ");
        literacy = Double.parseDouble(bufferedReader.readLine());
        String literacyText = literacy.toString();
        digits = literacyText.indexOf(".");
        decimals = literacyText.length() - 1 - digits;
        if (digits > 11 || decimals > 8) {
            throw new IllegalArgumentException("Country literacy can have a maximum of 11 digits and 8 decimals");
        }
        else {
            country.setAdultLiteracyRate(literacy);
        }

        // edit and update country
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.update(country);
        session.getTransaction().commit();
        session.close();
        System.out.println("Country updated successfully!");
        return true;
    }

    public static boolean deleteCountry() throws IOException, IllegalArgumentException {
        // Delete a country
        String code = null;
        List<Country> countries = getCountries();
        List<String> countryCodes = countries.stream()
                .map(country -> {
                    return country.getCode();
                })
                .collect(Collectors.toList());

        // get country to delete
        System.out.print("Enter code: ");
        code = bufferedReader.readLine();
        if (!countryCodes.contains(code)) {
            throw new IllegalArgumentException("Country code must be an existing, unique, 3-character string");
        }
        Country country = getCountryByCode(code);

        // delete country
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.delete(country);
        session.getTransaction().commit();
        session.close();
        System.out.println("Country deleted successfully!");
        return true;
    }

    private static List<Country> getCountries() {
        // Get a list of all the countries in the data table
        Session session = sessionFactory.openSession();
        Criteria criteria = session.createCriteria(Country.class);
        List<Country> countries = criteria.list();
        session.close();
        return countries;
    }

    private static Country getCountryByCode(String code) {
        // Get a country by country code
        Session session = sessionFactory.openSession();
        Country country = session.get(Country.class, code);
        session.close();
        return country;
    }

    private static String roundUpAndFormat(Double value) {
        // Round up and format doubles to 2 decimals
        return String.format(
                "%.2f",
                new BigDecimal(value)
                        .setScale(2, RoundingMode.HALF_UP)
                        .doubleValue()
                );
    }
}
