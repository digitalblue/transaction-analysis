package com.ed.ras;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class AppTest
{
    private static final String TRANSACTION_FILE = "./transactions.csv";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final String TEST_ACCOUNT_1 = "ACC334455";
    private static final String TEST_ACCOUNT_2 = "ACC778899";
    private List<Transaction> transactionList;

    private TransactionAnalysis transactionAnalysis = new TransactionAnalysis();

    @Before
    public void setup() {
        this.transactionList = this.transactionAnalysis.readTransactionsFile(TRANSACTION_FILE, FORMATTER);
    }

    @Test
    public void shouldSuccessfullyOpenFileAndPopulateTransactionList() {
        assertTrue( this.transactionList.size() > 0 );
    }

    @Test
    public void shouldReturnAllTransactionsForSpecifiedAccount() {
        assertTrue(this.transactionAnalysis.getAllAccountTransactions(this.transactionList, TEST_ACCOUNT_2).size() == 3);
    }

    @Test
    public void shouldReturnReversedTransactionIds() {
        List<String> expected = Arrays.asList("TX10004","TX10002");

        List<String> transactionIds = this.transactionAnalysis.getReversedTransactionIds(this.transactionList);
        assertEquals(expected, transactionIds);
    }

    @Test
    public void shouldReturnTransactionListWithoutReversedTransactions() {
        List<Transaction> transactions = this.transactionAnalysis.getAllAccountTransactions(this.transactionList, TEST_ACCOUNT_1);

        List<String > reversedTransactionIds = this.transactionAnalysis.getReversedTransactionIds(transactions);

        List<Transaction> transactionsWithoutReversals = this.transactionAnalysis.getTransactionListWithoutReversals(transactions, reversedTransactionIds);

        assertEquals(2, transactionsWithoutReversals.size());
    }

    @Test
    public void shouldReturnTransactionWithinSpecifiedDateRange() {
        LocalDateTime startDate = LocalDateTime.parse("20/10/2018 18:00:00", FORMATTER);
        LocalDateTime endDate = LocalDateTime.parse("20/10/2018 20:00:00", FORMATTER);
        List<Transaction> transactions = this.transactionAnalysis.getTransactionsByDateRange(this.transactionList, startDate, endDate);

        assertEquals(2, transactions.size());
    }

    @Test
    public void shouldReturnNoTransactionWithinSpecifiedDateRange() {
        LocalDateTime startDate = LocalDateTime.parse("20/10/2020 18:00:00", FORMATTER);
        LocalDateTime endDate = LocalDateTime.parse("20/10/2020 20:00:00", FORMATTER);
        List<Transaction> transactions = this.transactionAnalysis.getTransactionsByDateRange(this.transactionList, startDate, endDate);

        assertTrue(transactions.isEmpty());
    }

    @Test
    public void shouldReturnCorrectRelativeBalance() {
        Transaction tx1 = Transaction.builder().transactionId("TX1").fromAccountId(TEST_ACCOUNT_1).toAccountId(TEST_ACCOUNT_2).amount(new BigDecimal("100.00")).build();
        Transaction tx2 = Transaction.builder().transactionId("TX2").fromAccountId(TEST_ACCOUNT_1).toAccountId(TEST_ACCOUNT_2).amount(new BigDecimal("10.00")).build();
        Transaction tx3 = Transaction.builder().transactionId("TX3").fromAccountId(TEST_ACCOUNT_2).toAccountId(TEST_ACCOUNT_1).amount(new BigDecimal("100.00")).build();
        Transaction tx4 = Transaction.builder().transactionId("TX4").fromAccountId(TEST_ACCOUNT_1).toAccountId(TEST_ACCOUNT_2).amount(new BigDecimal("10.00")).build();

        List<Transaction> transactions = Arrays.asList(tx1, tx2, tx3, tx4);

        assertEquals(new BigDecimal("-20.00"), this.transactionAnalysis.getRelativeBalance(transactions, TEST_ACCOUNT_1));
        assertEquals(new BigDecimal("20.00"), this.transactionAnalysis.getRelativeBalance(transactions, TEST_ACCOUNT_2));
    }
}
