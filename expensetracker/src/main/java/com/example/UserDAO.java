package com.example;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class UserDAO {
    
    private final SessionFactory sessionFactory;

    public UserDAO(SessionFactory sessionFactory)
    {
        this.sessionFactory=sessionFactory;
    }

    public void createUser(UserObj user)
    {
        Transaction tx=null;
        try(Session session=sessionFactory.openSession())
        {
            tx=session.beginTransaction();
            session.persist(user);
            tx.commit();
        }
        catch(Exception e)
        {
            if(tx!=null)
            {
                tx.rollback();
            }
            e.printStackTrace();
        }
    }

    public UserObj getUser(String name) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "FROM UserObj WHERE name = :name";
            return session.createQuery(hql, UserObj.class)
                        .setParameter("name", name)
                        .uniqueResult();
        } 
    }
    

    public void updateUser(UserObj user)
    {
        Transaction tx = null;
        try(Session session=sessionFactory.openSession())
        {
            tx=session.beginTransaction();
            session.merge(user);
            tx.commit();
        }
        catch (Exception e) 
        {
            if (tx != null) 
            {
                tx.rollback();
            }
            e.printStackTrace();
        }
    }

    public void deleteUser(String name) 
    {
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            UserObj user = session.get(UserObj.class, name);
            if (user != null) {
                session.remove(user); // Use remove instead of delete
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        }
    }
}
