package com.hostel;

import com.hostel.db.MongoDBConnection;
import com.hostel.ui.MainMenu;

public class Main {
    public static void main(String[] args) {
        try {
            MongoDBConnection.ensureIndexes();
            new MainMenu().start();
        } catch (Exception ex) {
            System.out.println("Application failed to start: " + ex.getMessage());
        }
    }
}
