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


    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";

    /* ------------------------------------------------------------------
       Main menu
       ------------------------------------------------------------------ */
    public static void main(String[] args) {
        loadTransactions(FILE_NAME);

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println();
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
                default -> System.out.println("Invalid option" );
            }
        }
        scanner.close();
    }

    public static void loadTransactions(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Sorry ERROR! 0");
                return;
            }
        }
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
            }
        } catch (IOException e) {
            System.out.println("Sorry, no file found. ");
        }
    }
    private static void askAndSaveTransactions(Scanner myscanner, boolean isDeposit) {
        System.out.println("Enter date and time (yyyy-MM-dd HH:mm:ss)  ");
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
                System.out.println("Invalid date/time format. Transaction canceled" );
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
                System.out.println("Invalid input. (Needs to be a positive value.): ");
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
            BufferedWriter mywriter = new BufferedWriter(new FileWriter(FILE_NAME, true));
            mywriter.write(transaction.toString());
            mywriter.newLine();
            System.out.println("Transaction added successfully! ");
        } catch (Exception e) {
            System.out.println("ERRPR: Could not add transaction to file. ");
        }
    }

    private static void addDeposit(Scanner scanner) {
        System.out.println("\n===== Add Deposit ===== ");
        askAndSaveTransactions(scanner, true);
    }

    private static void addPayment(Scanner scanner) {
        System.out.println("\n===== Make Payment ===== ");
        askAndSaveTransactions(scanner, false);
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
    private static void printTransactions(Transaction t) {

        // Print the vertical border brackets along with colored amount text
        System.out.printf("│ %-12s │ %-12s │ %-30s │ %-15s │ $%10.2f │\n",
                t.getDate().format(DATE_FMT),
                t.getTime().format(TIME_FMT),
                t.getDescription(),
                t.getVendor(),// Apply Red or Green
                t.getAmount()); // Reset color back to default at the end of the line
    }
    private static void printHeader() {
        System.out.println("┌──────────────┬──────────────┬────────────────────────────────┬─────────────────┬────────────────┐");
        System.out.println("│         Date │ Time         │ Description                    │ Vendor          │ Amount         │");
        System.out.println("├──────────────┼──────────────┼────────────────────────────────┼─────────────────┼────────────────┤");
    }
    private static void printFootHolder() {
        System.out.println("└──────────────┴──────────────┴────────────────────────────────┴─────────────────┴────────────────┘");
    }
    /* ------------------------------------------------------------------
       Display helpers: show data in neat columns
       ------------------------------------------------------------------ */
    private static void displayLedger() {
        System.out.println("\n================ All Transactions ================ ");
        if (transactions.isEmpty()) {
            System.out.println("None found");
            return;
        }
        printHeader();
        for (int i = transactions.size() - 1; i >= 0 ; i--) {
            Transaction t = transactions.get(i);
            printTransactions(t);
        }
        printFootHolder();
    }

    private static void displayDeposits() {
        System.out.println("\n============ Deposits ============ ");

        boolean matched = false;

        for (int i = transactions.size() - 1; i >= 0 ; i-- ) {
            Transaction t = transactions.get(i);
            if (t.getAmount() > 0) {
                if (!matched) { printHeader(); matched = true; }
                printTransactions(t);
            }
        }
        if (matched) printFootHolder();
        else System.out.println("No deposits found. ");
    }

    private static void displayPayments() {
        System.out.println("\n============ Payments ============ ");

        boolean matched = false;

        for (int i = transactions.size() - 1; i>= 0; i--) {
            Transaction t = transactions.get(i);
            if (t.getAmount() < 0 ) {
                if (!matched) { printHeader(); matched = true; }
                printTransactions(t);
            }
        }
        if (matched) printFootHolder();
        else System.out.println("No payments found. ");
    }

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
            LocalDate now = LocalDate.now();

            switch (input) {
                case "1" -> {filterTransactionsByDate(now.withDayOfMonth(1), now);}

                // Previous Month to date range
                case "2" -> {
                    LocalDate startOfPreviousMonth = now.minusMonths(1).withDayOfMonth(1);
                    LocalDate endOfPreviousMonth = now.minusMonths(1).withDayOfMonth(now.minusMonths(1).lengthOfMonth());
                    System.out.println("\n[ Report: Previous Month (" + startOfPreviousMonth + " to " + endOfPreviousMonth + ")]");
                    filterTransactionsByDate(startOfPreviousMonth, endOfPreviousMonth);
                }
                case "3" -> {filterTransactionsByDate(now.withDayOfYear(1), now);}
                case "4" -> {
                    LocalDate startOfPreviousYear = now.minusYears(1).withDayOfYear(1);
                    LocalDate endOfPreviousYear = now.minusYears(1).withDayOfYear(now.minusYears(1).lengthOfYear());
                    System.out.println("\n[ Report: Previous Year (" + startOfPreviousYear + " to " + endOfPreviousYear + ")]");
                    filterTransactionsByDate(startOfPreviousYear, endOfPreviousYear);
                }
                case "5" -> {
                    System.out.println("Enter vendor name here: ");
                    String vendor = scanner.nextLine().trim();
                    filterTransactionsByVendor(vendor);
                }
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
        printHeader();
        System.out.printf("\n===== Report from %s to %s =====\n", start, end);
        boolean matched = false;
        for (int i = transactions.size() - 1; i >= 0; i--) {
             Transaction transaction = transactions.get(i);
             LocalDate date = transaction.getDate();
             if (date.isAfter(start) || date.isEqual(start) && (date.isBefore(end) || date.isEqual(end))) {
                 printTransactions(transaction);
                 matched = true;
             }
        }
        if (!matched) System.out.println("No matching records found. ");
    }

    private static void filterTransactionsByVendor(String vendor) {
        System.out.printf("\n===== Report for Vendor: %s =====\n", vendor);
        boolean matched = false;

        for (int i = transactions.size() - 1 ; i >= 0; i--) {
            Transaction transaction = transactions.get(i);
            if (transaction.getVendor().equalsIgnoreCase(vendor)) {
                printTransactions(transaction);
                matched = true;
            }
        }
        if (!matched) {
            System.out.println("No matching record found: ");
        }
    }

    private static void customSearch(Scanner scanner) {
        System.out.println("\n==== Custom Search: (Press enter to skip) ==== ");

        System.out.println("Enter Start Date (yyyy-MM-dd): ");
        LocalDate startDate = parseDate(scanner.nextLine().trim());

        System.out.println("Enter End Date (yyyy-MM-dd): ");
        LocalDate endDate = parseDate(scanner.nextLine().trim());

        System.out.println("Enter description : ");
        String description = scanner.nextLine().trim().toLowerCase();

        System.out.println("Enter the vendor name: ");
        String vendorName = scanner.nextLine().trim();

        System.out.println("Enter the exact amount: ");
        Double exactAmount = parseDouble(scanner.nextLine().trim());

        System.out.println("\n================ Custom Search Results ================ ");
        boolean matched = false;

        for (int i = transactions.size() - 1; i >= 0; i--) {
            Transaction transaction = transactions.get(i);

            //Filter for Start Date
            if (startDate != null && transaction.getDate().isBefore(startDate)) continue;
            //Filter for End date
            if (endDate != null && transaction.getDate().isAfter(endDate)) continue;
            //Filter for Description
            if (!description.isEmpty() && !transaction.getDescription().toLowerCase().contains(description)) continue;
            //Filter for Vendors
            if (!vendorName.isEmpty() && !transaction.getVendor().equalsIgnoreCase(vendorName)) continue;
            //Filter for Price
            if (exactAmount != null && Math.abs(transaction.getAmount() - exactAmount) > 0.001) continue;

            printTransactions(transaction);
            matched = true;
        }
        if (!matched) {
            System.out.println("No transactions matched to custom filters: ");
        }
    }



    private static LocalDate parseDate(String s) {
        if (s.isEmpty()) return null;
        try {
            return LocalDate.parse(s, DATE_FMT);
        }catch (DateTimeParseException e) {
            System.out.println("Warning: Invalid date entry skipped");
            return null;
        }
    }

    private static Double parseDouble(String s) {

        return null;
    }

}
