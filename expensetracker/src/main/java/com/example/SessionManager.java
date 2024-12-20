package com.example;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class SessionManager {
    private static UserObj currUser;

    private static List<TransactionObj> expTransactions;
    private static List<TransactionObj> inTransactions;
    private static List<TransactionObj> Transactions;
    private static List<TransactionObj> SavTransactions;

    public static int totalExp=0,totalIn=0;

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
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        TransactionDAO transactionDAO = new TransactionDAO(sessionFactory);
        SavTransactions = transactionDAO.getTransactions(currUser.id,2);
    }

    public static List<TransactionObj> getSavingsTransactionList() {
        return SavTransactions;
    }

    public static void setTotalOverview() {
        for(TransactionObj x : expTransactions)
            totalExp+=x.getAmount();

        for(TransactionObj x : inTransactions)
            totalIn+=x.getAmount();
    }

    public static int[] getTotalOverview() {
        return new int[]{totalExp,totalIn};
    }
}