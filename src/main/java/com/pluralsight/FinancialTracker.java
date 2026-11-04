package com.pluralsight;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;


public class FinancialTracker {

    private static final ArrayList<Transaction> transactions = new ArrayList<>();
    private static final String FILE_NAME = "transactions.csv";

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String TIME_PATTERN = "HH:mm:ss";
    private static final String DATETIME_PATTERN = DATE_PATTERN + " " + TIME_PATTERN;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern(DATE_PATTERN);
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern(TIME_PATTERN);
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern(DATETIME_PATTERN);
    public static final String ANSI_RED = "\u001B[0m";
    public static final String ANSI_RESET = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";

    /* ------------------------------------------------------------------
       Main menu
       ------------------------------------------------------------------ */
    public static void main(String[] args) {
        loadTransactions(FILE_NAME);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("Welcome to TransactionApp");
            System.out.println("Choose an option:");
            System.out.println("D) Add Deposit");
            System.out.println("P) Make Payment (Debit)");
            System.out.println("L) Ledger");
            System.out.println("X) Exit");

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "D" -> addDeposit(scanner);
                case "P" -> addPayment(scanner);
                case "L" -> ledgerMenu(scanner);
                case "X" -> running = false;
                default -> System.out.println(ANSI_RED + "Invalid option" + ANSI_RESET);
            }
        }
        scanner.close();
    }

    public static void loadTransactions(String fileName) {
        try {
            transactions.clear();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(FILE_NAME));
            String csvLine;
            while ((csvLine = bufferedReader.readLine()) != null) {
                if (csvLine.trim().isEmpty()) continue;

                String[] tokens = csvLine.split("\\|");
                if (tokens.length == 5) {
                    LocalDate date = LocalDate.parse(tokens[0], DATE_FMT);
                    LocalTime time = LocalTime.parse(tokens[1], TIME_FMT);
                    String description = tokens[2];
                    String vendor = tokens[3];
                    double amount = Double.parseDouble(tokens[4]);

                    Transaction addTransaction = new Transaction(date, time, description, vendor, amount);
                    transactions.add(addTransaction);

                }
                return;
            }
        } catch (FileNotFoundException e) {
            System.out.println("Sorry, no file found. ");
        } catch (IOException e) {
            System.out.println(ANSI_RED + "Sorry, ERROR! " + ANSI_RESET);
        }
    }
    private static void askAndSaveTransactions(Scanner myscanner, boolean isDeposit) {
        System.out.println("Enter date and time (yyyy-MMM-dd HH:mm:ss)  ");
        String dateTimeInput = myscanner.nextLine().trim();

        LocalDate date;
        LocalTime time;

        if (dateTimeInput.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            date = now.toLocalDate();
            time = now.toLocalTime();
        }else {
            try {
                LocalDateTime parsedDateTIme = LocalDateTime.parse(dateTimeInput, DATETIME_FMT);
                date = parsedDateTIme.toLocalDate();
                time = parsedDateTIme.toLocalTime();
            }catch (DateTimeParseException e) {
                System.out.println(ANSI_RED + "Invalid date/time format. Transaction canceled" + ANSI_RESET);
                return;
            }
        }
        System.out.println("Enter description: ");
        String description = myscanner.nextLine().trim();

        System.out.println("Enter vendor name: ");
        String vendor = myscanner.nextLine().trim();

        double amount = 0;
        boolean validAmount = false;
        while (!validAmount) {
            System.out.println("Enter amount (Needs to be a positive value.): ");
            String amountInput = myscanner.nextLine().trim();
            Double parsedAmount = parseDouble(amountInput);
            if (parsedAmount == null || parsedAmount <= 0) {
                System.out.println(ANSI_RED + "Invalid input. (Needs to be a positive value.): " + ANSI_RESET);
            }else {
                amount = parsedAmount;
                validAmount = true;
            }

        }
        if (!isDeposit) {
            amount = -amount;
        }
        Transaction transaction = new Transaction(date, time, description, vendor, amount);
        transactions.add(transaction);
        try {
            BufferedWriter mywriter = new BufferedWriter(new FileWriter(FILE_NAME));
            mywriter.write(transaction.toString());
            mywriter.newLine();
            System.out.println(ANSI_GREEN + "Transaction added successfully! " + ANSI_RESET);
        } catch (Exception e) {
            System.out.println("ERRPR: Could not add transaction to file. ");
        }
    }

    private static void addDeposit(Scanner scanner) {
        System.out.println("\n ===== Add Deposit ===== ");
        askAndSaveTransactions(scanner, true);
    }

    private static void addPayment(Scanner scanner) {
    }

    /* ------------------------------------------------------------------
       Ledger menu
       ------------------------------------------------------------------ */
    private static void ledgerMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("Ledger");
            System.out.println("Choose an option:");
            System.out.println("A) All");
            System.out.println("D) Deposits");
            System.out.println("P) Payments");
            System.out.println("R) Reports");
            System.out.println("H) Home");

            String input = scanner.nextLine().trim();

            switch (input.toUpperCase()) {
                case "A" -> displayLedger();
                case "D" -> displayDeposits();
                case "P" -> displayPayments();
                case "R" -> reportsMenu(scanner);
                case "H" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
    }
    private static void transactionRow() {
        System.out.println();
    }

    /* ------------------------------------------------------------------
       Display helpers: show data in neat columns
       ------------------------------------------------------------------ */
    private static void displayLedger() { /* TODO – print all transactions in column format, newest first */ }

    private static void displayDeposits() { /* TODO – only amount > 0               */ }

    private static void displayPayments() { /* TODO – only amount < 0               */ }

    /* ------------------------------------------------------------------
       Reports menu
       ------------------------------------------------------------------ */
    private static void reportsMenu(Scanner scanner) {
        boolean running = true;
        while (running) {
            System.out.println("\n==== Reports Menu ====");
            System.out.println("Choose an option:");
            System.out.println("1) Month To Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year To Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by Vendor");
            System.out.println("6) Custom Search");
            System.out.println("0) Back");

            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> {/* TODO – month-to-date report */ }
                case "2" -> {/* TODO – previous month report */ }
                case "3" -> {/* TODO – year-to-date report   */ }
                case "4" -> {/* TODO – previous year report  */ }
                case "5" -> {/* TODO – prompt for vendor then report */ }
                case "6" -> customSearch(scanner);
                case "0" -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
    }

    /* ------------------------------------------------------------------
       Reporting helpers
       ------------------------------------------------------------------ */
    private static void filterTransactionsByDate(LocalDate start, LocalDate end) {
        // TODO – iterate transactions, print those within the range
    }

    private static void filterTransactionsByVendor(String vendor) {
        // TODO – iterate transactions, print those with matching vendor
    }

    private static void customSearch(Scanner scanner) {
    }

    /* ------------------------------------------------------------------
       Utility parsers (you can reuse in many places)
       ------------------------------------------------------------------ */
    private static LocalDate parseDate(String s) {
        /* TODO – return LocalDate or null */
        return null;
    }

    private static Double parseDouble(String s) {
        /* TODO – return Double   or null */
        return null;
    }
}
