package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseManagement {

    public Connection con;

    public void establishConnection()
    {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/expense_tracker", "root", "root");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void disconnectConnection() {
        if (con != null) { 
            try {
                con.close();
                System.out.println("Connection Closed Successfully");
            } catch (SQLException e) {
                e.printStackTrace(); 
            }
        } else {
            System.out.println("Connection was not established");
        }
    }
}
