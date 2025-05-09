package com.CS22S4.hehe.services;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLiteService {
    public static final String DB_URL = "jdbc:sqlite:game.db";

    private Connection connection;

    public SQLiteService() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Connection to SQLite has been established.");
            initTables();
            insertGameProfileIfNotExists("Player");
            insertGameStatsIfNotExists();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void initTables() {
        String createGameProfileTable = "CREATE TABLE IF NOT EXISTS game_profile (" +
            "profile_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "name TEXT NOT NULL" +
            ");";

        String createGameStatsTable = "CREATE TABLE IF NOT EXISTS game_stats (" +
            "stat_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "profile_id INTEGER NOT NULL, " +
            "easy_high_score INTEGER NOT NULL, " +
            "normal_high_score INTEGER NOT NULL," +
            "hard_high_score INTEGER NOT NULL," +
            "FOREIGN KEY(profile_id) REFERENCES game_profile(profile_id) ON DELETE CASCADE" +
            ");";

        String createGameRecordTable = "CREATE TABLE IF NOT EXISTS game_records (" +
            "game_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "profile_id INTEGER NOT NULL," +
            "score INTEGER NOT NULL," +
            "total_customers_served INTEGER NOT NULL," +
            "total_amount_dispensed INTEGER NOT NULL," +
            "FOREIGN KEY(profile_id) REFERENCES game_profile(profile_id) ON DELETE CASCADE" +
            ");";

        try {
            connection.createStatement().execute(createGameProfileTable);
            connection.createStatement().execute(createGameStatsTable);
            connection.createStatement().execute(createGameRecordTable);
            System.out.println("Tables have been initialized.");
        } catch (SQLException e) {
            System.out.println("Error initializing tables: " + e.getMessage());
        }
    }

    public void insertGameProfileIfNotExists(String name) {
        String sql = "INSERT OR IGNORE INTO game_profile (profile_id, name) VALUES (1, ?);";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.executeUpdate();
            System.out.println("Profile inserted if it did not exist.");
        } catch (SQLException e) {
            System.out.println("Error inserting profile: " + e.getMessage());
        }
    }

    public void insertGameStatsIfNotExists() {
        String sql = "INSERT OR IGNORE INTO game_stats (stat_id, profile_id, easy_high_score, normal_high_score, hard_high_score) VALUES (1, 1, 0, 0, 0);";

        try {
            connection.createStatement().execute(sql);
            System.out.println("Stats inserted if it did not exist.");
        } catch (SQLException e) {
            System.out.println("Error inserting game stats: " + e.getMessage());
        }
    }

    public void storeGameRecord(int score, int total_customers_served, int total_amount_dispensed) {
        String sql = "INSERT INTO game_records (profile_id, score, total_customers_served, total_amount_dispensed) VALUES (1, ?, ?, ?);";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, score);
            pstmt.setInt(2, total_customers_served);
            pstmt.setInt(3, total_amount_dispensed);
            pstmt.executeUpdate();
            System.out.println("Game recorded.");
        } catch (SQLException e) {
            System.out.println("Error recording game: " + e.getMessage());
        }
    }

    public List<Integer> getHighScores() {
        String sql = "SELECT easy_high_score, normal_high_score, hard_high_score FROM game_stats gs " +
            "JOIN game_profile gp ON gs.profile_id = gp.profile_id WHERE gp.profile_id = 1;";
        List<Integer> highScores = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int easyHighScore = rs.getInt("easy_high_score");
                int normalHighScore = rs.getInt("normal_high_score");
                int hardHighScore = rs.getInt("hard_high_score");
                highScores.add(easyHighScore);
                highScores.add(normalHighScore);
                highScores.add(hardHighScore);

            }
        } catch (SQLException e) {
            System.out.println("Error retrieving high scores: " + e.getMessage());
        }

        return highScores;
    }

    public void setHighScores(int easyHighScore, int normalHighScore, int hardHighScore) {
        String sql = "UPDATE game_stats SET easy_high_score = ?, normal_high_score = ?, hard_high_score = ? WHERE profile_id = 1;";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, easyHighScore);
            pstmt.setInt(2, normalHighScore);
            pstmt.setInt(3, hardHighScore);
            pstmt.executeUpdate();
            System.out.println("High scores updated successfully.");
        } catch (SQLException e) {
            System.out.println("Error updating high scores: " + e.getMessage());
        }
    }

    public List<Integer> getAllTimeStats() {
        String sql = "SELECT SUM(total_customers_served) AS total_customers_served, SUM(total_amount_dispensed) " +
            "AS total_amount_dispensed FROM game_records gr JOIN game_profile gp ON gr.profile_id = gp.profile_id " +
            "WHERE gp.profile_id = 1;";

        List<Integer> results = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int totalCustomersServed = rs.getInt("total_customers_served");
                int totalAmountDispensed = rs.getInt("total_amount_dispensed");
                results.add(totalCustomersServed);
                results.add(totalAmountDispensed);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving game stats: " + e.getMessage());
        }

        return results;
    }
}
