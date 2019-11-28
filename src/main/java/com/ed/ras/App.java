package com.ed.ras;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;


public class App {

    private static final String TRANSACTION_FILE = "./transactions.csv";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static void main( String[] args )  {

        String accountId;
        String startDate;
        String startTime;
        String endDate;
        String endTime;
        TransactionAnalysis analysis = new TransactionAnalysis();
        List<Transaction> transactionList = analysis.readTransactionsFile(TRANSACTION_FILE, FORMATTER);

        // get user input
        Scanner input = new Scanner(System.in);
        System.out.print("Enter account number:");
        accountId = input.next();
        System.out.print("Enter start date:");
        startDate = input.next();
        System.out.print("Enter start time:");
        startTime = input.next();
        System.out.print("Enter end date:");
        endDate = input.next();
        System.out.print("Enter end time:");
        endTime = input.next();

        LocalDateTime startDateTime = LocalDateTime.parse(startDate + " " + startTime, FORMATTER);
        LocalDateTime endDateTime = LocalDateTime.parse(endDate + " " + endTime, FORMATTER);

        // do analysis
        List<Transaction> allAccountTransactions = analysis.getAllAccountTransactions(transactionList, accountId);
        List<String> reversedTransactionIds = analysis.getReversedTransactionIds(allAccountTransactions);
        List<Transaction> accountTransactionsNoReversals = analysis.getTransactionListWithoutReversals(allAccountTransactions, reversedTransactionIds);
        List<Transaction> accountTransactionsByDateRange = analysis.getTransactionsByDateRange(accountTransactionsNoReversals, startDateTime, endDateTime);
        BigDecimal balance = analysis.getRelativeBalance(accountTransactionsByDateRange, accountId);

        // display result
        System.out.println("accountId: " + accountId);
        System.out.println("from: " + startDateTime.format(FORMATTER));
        System.out.println("to: " + endDateTime.format(FORMATTER));
        System.out.println("Relative balance for the period is: " + NumberFormat.getCurrencyInstance().format(balance));
        System.out.println("Number of transactions included is: " + accountTransactionsByDateRange.size());
    }
}