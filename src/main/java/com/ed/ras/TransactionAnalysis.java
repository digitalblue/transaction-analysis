package com.ed.ras;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.Reader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionAnalysis {

    private static final String TRANSACTION_TYPE_PAYMENT = "PAYMENT";
    private static final String TRANSACTION_TYPE_REVERSAL = "REVERSAL";

    public TransactionAnalysis() { }

    public List<Transaction> getAllAccountTransactions(List<Transaction> transactionList, String accountId) {
        return transactionList
                .stream()
                .filter(transaction -> (transaction.getFromAccountId().equals(accountId) || transaction.getToAccountId().equals(accountId)))
                .collect(Collectors.toList());
    }

    public List<String> getReversedTransactionIds(List<Transaction> transactionList) {
        List<String> reversedTransactionIds = new ArrayList<>();
        for (Transaction transaction: transactionList) {
            if (transaction.getTransactionType().equals(TRANSACTION_TYPE_REVERSAL)) {
                reversedTransactionIds.add(transaction.getTransactionId());
                reversedTransactionIds.add(transaction.getRelatedTransaction());
            }
        }
        return reversedTransactionIds;
    }

    public List<Transaction> getTransactionListWithoutReversals(List<Transaction> transactionList, List<String> transactionIds) {
        return transactionList
                .stream()
                .filter(transaction -> !transactionIds.contains(transaction.getTransactionId()))
                .collect(Collectors.toList());
    }

    public List<Transaction> getTransactionsByDateRange(List<Transaction> transactionList, LocalDateTime startDate, LocalDateTime endDate) {
        return transactionList
                .stream()
                .filter(transaction -> (transaction.getCreatedAt().compareTo(startDate) >= 0)
                        && transaction.getCreatedAt().compareTo(endDate) <= 0)
                .collect(Collectors.toList());
    }

    public BigDecimal getRelativeBalance(List<Transaction> transactionList, String accountId) {
        BigDecimal balance = BigDecimal.ZERO;
        for (Transaction transaction: transactionList) {
            if (transaction.getFromAccountId().equals(accountId)) {
                balance = balance.subtract(transaction.getAmount());
            }

            if (transaction.getToAccountId().equals(accountId)) {
                balance = balance.add(transaction.getAmount());
            }
        }
        return balance;
    }

    public List<Transaction> readTransactionsFile(String file, DateTimeFormatter formatter) {
        List<Transaction> transactionList = new ArrayList<>();

        try {
            Reader reader = Files.newBufferedReader(Paths.get(file));
            CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();

            String[] nextRecord;
            while ((nextRecord = csvReader.readNext()) != null) {
                Transaction transaction;
                if (nextRecord.length > 6) {
                    transaction = Transaction.builder()
                            .transactionId(nextRecord[0].trim())
                            .fromAccountId(nextRecord[1].trim())
                            .toAccountId(nextRecord[2].trim())
                            .createdAt(LocalDateTime.parse(nextRecord[3].trim(), formatter))
                            .amount(new BigDecimal(nextRecord[4].trim()))
                            .transactionType(nextRecord[5].trim())
                            .relatedTransaction(nextRecord[6].trim())
                            .build();
                } else {
                    transaction = Transaction.builder()
                            .transactionId(nextRecord[0].trim())
                            .fromAccountId(nextRecord[1].trim())
                            .toAccountId(nextRecord[2].trim())
                            .createdAt(LocalDateTime.parse(nextRecord[3].trim(), formatter))
                            .amount(new BigDecimal(nextRecord[4].trim()))
                            .transactionType(nextRecord[5].trim())
                            .build();
                }
                transactionList.add(transaction);
            }
        } catch (Exception e) {
            System.out.println("Sorry, unable to read file");
            System.exit(0);
        }
        return transactionList;
    }
}
