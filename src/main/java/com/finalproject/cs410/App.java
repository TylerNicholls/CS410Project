package com.finalproject.cs410;

/**
 * Hello world!
 */

import com.budhash.cliche.Command;
import com.budhash.cliche.ShellFactory;

import java.io.IOException;
import java.sql.*;

public class App {
    private final Connection db;

    public App(Connection cxn) {
        db = cxn;
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
