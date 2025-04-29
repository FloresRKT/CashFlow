package com.CS22S4.hehe.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteService {
    public static final String DB_URL = "jdbc:sqlite:game.db";

    private Connection connection;

    public SQLiteService() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void storeGameRecord() {
        //Statement stmt = connection.createStatement("SELECT * FROM game_profile");
    }

    public void getAllTimeStats() {

    }
}
