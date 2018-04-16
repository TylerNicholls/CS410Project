package com.finalproject.cs410;

/**
 * Hello world!
 */

import com.budhash.cliche.Command;
import com.budhash.cliche.ShellFactory;

import java.io.IOException;
import java.sql.*;
import java.util.Calendar;

public class App {
    private final Connection db;
    private final int ACTIVE = 1;
    private final int CANCELED = 2;
    private final int FINISHED = 3;


    public App(Connection cxn) {
        db = cxn;
    }


    @Command
    public void active() throws SQLException {
        String query = "SELECT * FROM TASKS" +
                "WHERE task_status  = ?";
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            // Set the first parameter (query key) to the series
            stmt.setInt(1, ACTIVE);
            // once parameters are bound we can run!
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int taskId = rs.getInt("task_id");
                    String taskLabel = rs.getString("task_label");
                    Date taskCreateDate = rs.getDate("task_create_date");
                    Date taskDueDate = rs.getDate("task_due_date");
                    System.out.format("Task_id: %d, label: %s, Create Date: %s, Due Date: %s \n",
                            taskId, taskLabel, taskCreateDate.toString(), taskDueDate.toString());
                }
            }
        }
    }

    @Command
    public void add(String label) throws SQLException {
        Date date = new java.sql.Date(Calendar.getInstance().getTime().getTime());
        String insertTask = "INSERT INTO Tasks (task_label, task_create_date, task_status) VALUES (?, ?, ?)";
        db.setAutoCommit(false);
        int taskId;
        try {
            try (PreparedStatement stmt = db.prepareStatement(insertTask, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, label);
                stmt.setDate(2, date);
                stmt.setInt(3, ACTIVE);

                stmt.executeUpdate();
                // fetch the generated task_id!
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (!rs.next()) {
                        throw new RuntimeException("no generated keys???");
                    }
                    taskId = rs.getInt(1);
                    System.out.format("Creating task %d%n", taskId);
                }
            }
            db.commit();
        } catch (SQLException | RuntimeException e) {
            db.rollback();
            throw e;
        } finally {
            db.setAutoCommit(true);
        }
    }

    @Command
    public void due(int id, String dateString) throws SQLException {
        String query = "UPDATE Tasks SET task_due_date = ? WHERE task_id = ?";
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            stmt.setDate(1, Date.valueOf(dateString));
            stmt.setInt(2, id);
            System.out.format("Adding due date %s to %d%\n", dateString, id);
            int nrows = stmt.executeUpdate();
            System.out.format("updated %d tasks%n", nrows);
        }
    }


    @Command
    public void topAuthors() throws SQLException {
        String query = "SELECT author_id, author_name, COUNT(article_id) AS article_count" +
                " FROM author JOIN article_author USING (author_id)" +
                " GROUP BY author_id" +
                " ORDER BY article_count DESC LIMIT 10";
        System.out.println("Top Authors by Publication Count:");
        try (Statement stmt = db.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String name = rs.getString("author_name");
                int count = rs.getInt("article_count");
                System.out.format("  %s (with %d pubs)\n", name, count);
            }
        }
    }

    /**
     * We can run
     * <pre>
     *     conference-top-authors CHI
     * </pre>
     * @param series
     * @throws SQLException
     */
    @Command
    public void conferenceTopAuthors(String series) throws SQLException {
        String query = "SELECT author_id, author_name, COUNT(article_id) AS article_count" +
                " FROM author JOIN article_author USING (author_id)" +
                " JOIN article USING (article_id)" +
                " JOIN proceedings USING (proc_id)" +
                " JOIN conf_series USING (cs_id)" +
                " WHERE cs_hb_key = ?" +
                " GROUP BY author_id" +
                " ORDER BY article_count DESC LIMIT 10";
        System.out.format("Top Authors in %s by Publication Count:%n", series);
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            // Set the first parameter (query key) to the series
            stmt.setString(1, series);
            // once parameters are bound we can run!
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("author_name");
                    int count = rs.getInt("article_count");
                    System.out.format("  %s (with %d pubs)\n", name, count);
                }
            }
        }
    }

    /**
     * SQL injection!
     * @throws SQLException
     */
    @Command
    public void badConferenceTopAuthors(String series) throws SQLException {
        String query = "SELECT author_id, author_name, COUNT(article_id) AS article_count" +
                " FROM author JOIN article_author USING (author_id)" +
                " JOIN article USING (article_id)" +
                " JOIN proceedings USING (proc_id)" +
                " JOIN conf_series USING (cs_id)" +
                " WHERE cs_hb_key = '" + series + "'" +
                " GROUP BY author_id" +
                " ORDER BY article_count DESC LIMIT 10";
        System.out.format("Top Authors in %s by Publication Count:%n", series);
        try (Statement stmt = db.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String name = rs.getString("author_name");
                int count = rs.getInt("article_count");
                System.out.format("  %s (with %d pubs)\n", name, count);
            }
        }

    }

    public static void main(String[] args) throws IOException, SQLException {
        String dbUrl = args[0];
        try (Connection cxn = DriverManager.getConnection("jdbc:" + dbUrl)) {
            App shell = new App(cxn);
            ShellFactory.createConsoleShell("article", "", shell)
                    .commandLoop();
        }
    }
}
