package com.example;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class TransactionDAO {
    private final SessionFactory sessionFactory;

    public TransactionDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    // Create
    public void createTransaction(TransactionObj transaction) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.persist(transaction);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
    }

    // Read
    public TransactionObj getTransaction(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(TransactionObj.class, id);
        }
    }

    public List<TransactionObj> getAllTransactions() {
        try (Session session = sessionFactory.openSession()) {
            Query<TransactionObj> query = session.createQuery("FROM TransactionObj", TransactionObj.class);
            return query.getResultList();
        }
    }

    // Update
    public void updateTransaction(TransactionObj transaction) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.merge(transaction);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
    }

    // Delete
    public void deleteTransaction(int id) {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            TransactionObj transaction = session.get(TransactionObj.class, id);
            if (transaction != null) {
                session.remove(transaction); // Use remove instead of delete
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
    }


    public List<TransactionObj> getTransactions(int uid,int type) {
        try (Session session = sessionFactory.openSession()) {
            if(type==3)
            {
                Query<TransactionObj> query = session.createQuery(
                "FROM TransactionObj WHERE uid = :uid", 
                TransactionObj.class
            );
            query.setParameter("uid", uid);

            return query.getResultList();
            }
            else
            {
                Query<TransactionObj> query = session.createQuery(
                    "FROM TransactionObj WHERE uid = :uid AND type = :type", 
                    TransactionObj.class
                );
                query.setParameter("type", type);
                query.setParameter("uid", uid);

                return query.getResultList();
            }
        }
    }

}