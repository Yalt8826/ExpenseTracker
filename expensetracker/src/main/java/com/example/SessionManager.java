package com.example;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class SessionManager {
    private static UserObj currUser;

    private static List<TransactionObj> expTransactions;
    private static List<TransactionObj> inTransactions;
    private static List<TransactionObj> Transactions;
    private static List<TransactionObj> SavTransactions;

    public static int totalExp,totalIn;

    public static void setCurrUser(UserObj user) {
        currUser = user;
    }

    public static UserObj getCurrUser() {
        return currUser;
    }

    public static void setExpTransactionList()
    {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        TransactionDAO transactionDAO = new TransactionDAO(sessionFactory);
        expTransactions = transactionDAO.getTransactions(currUser.id,0);
    }

    public static List<TransactionObj> getExpTransactionList()
    {
        return expTransactions;
    }

    public static void setInTransactionList() {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        TransactionDAO transactionDAO = new TransactionDAO(sessionFactory);
        inTransactions = transactionDAO.getTransactions(currUser.id,1);
    }

    public static List<TransactionObj> getInTransactionList() {
        return inTransactions;
    }

    public static void setTransactionList() {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        TransactionDAO transactionDAO = new TransactionDAO(sessionFactory);
        Transactions = transactionDAO.getTransactions(currUser.id,3);
    }

    public static List<TransactionObj> getTransactionList()
    {
        return Transactions;
    }

    public static void updateSavingsTransactionList()
    {

        LocalDate today = LocalDate.now();

        int totalSave = totalIn-totalExp;

        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        TransactionDAO transactionDAO = new TransactionDAO(sessionFactory);
        TransactionObj newTransaction = new TransactionObj();
        newTransaction.setType(2);
        String monthName = today.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        int year = today.getYear();
        String Category = monthName+", "+year;
        newTransaction.setCategory(Category);
        newTransaction.setAmount(totalSave);
        newTransaction.setUid(currUser.id);
        newTransaction.setTransdate(new Date());
        transactionDAO.createTransaction(newTransaction);

        setSavingsTransactionList();
    }

    public static void setSavingsTransactionList() {

        totalExp=0;
        totalIn=0;
        List<TransactionObj> exp1Transactions;
        List<TransactionObj> in1Transactions;
        LocalDate today = LocalDate.now();
        int currentMonth = today.getMonthValue();
        int currentYear = today.getYear();

        exp1Transactions = expTransactions.stream()
            .filter(transaction -> {
                // Extract the date from the transaction and convert to LocalDate
                LocalDate transactionDate = transaction.getTransdate().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate();
                // Check if month and year match the current month and year
                return transactionDate.getMonthValue() == currentMonth && transactionDate.getYear() == currentYear;
            })
            .collect(Collectors.toList());

        in1Transactions = inTransactions.stream()
            .filter(transaction -> {
                // Extract the date from the transaction and convert to LocalDate
                LocalDate transactionDate = transaction.getTransdate().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate();
                // Check if month and year match the current month and year
                return transactionDate.getMonthValue() == currentMonth && transactionDate.getYear() == currentYear;
            })
            .collect(Collectors.toList());

        for(TransactionObj transaction : exp1Transactions )
        {
            totalExp+=transaction.getAmount();
        }
        for(TransactionObj transaction : in1Transactions )
        {
            totalIn+=transaction.getAmount();
        }


        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        TransactionDAO transactionDAO = new TransactionDAO(sessionFactory);
        SavTransactions = transactionDAO.getTransactions(currUser.id,2);
    }

    public static List<TransactionObj> getSavingsTransactionList() {
        return SavTransactions;
    }

    public static int[] getTotalOverview() {
        return new int[]{totalExp,totalIn};
    }
}

