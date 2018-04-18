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
    private final int ACTIVE = 1;
    private final int CANCELED = 2;
    private final int FINISHED = 3;


    public App(Connection cxn) throws SQLException {
        db = cxn;
    }


//    View currently-active tasks - list the task IDs, labels, create dates, and due dates (if assigned):
//    active


    @Command
    public void active() throws SQLException {
        String query = "SELECT task_id, task_label, task_create_date, task_due_date " +
                "FROM Tasks" +
                " WHERE task_status  = ?";
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            // Set the first parameter (query key) to the series
            stmt.setInt(1, ACTIVE);
            // once parameters are bound we can run!
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int taskId = rs.getInt("task_id");
                    String taskLabel = rs.getString("task_label");
                    Timestamp taskCreateDate = rs.getTimestamp("task_create_date");
                    Timestamp taskDueDate = rs.getTimestamp("task_due_date");
                    System.out.format("Task_id: %d, label: %s, Create Date: %s, Due Date: %s \n",
                            taskId, taskLabel, taskCreateDate.toString(), taskDueDate.toString());
                }
            }
        }
    }

//    Add new tasks (e.g., add a new task with the label “Finish Assignment”; it should print
//            the task ID once it has added the task)
//    add Finish Final Project

    @Command
    public void add(String... input) throws SQLException {
        String label = "";
        for (int i = 0; i < input.length; i++){
            label += input[i];
        }
        String insertTask = "INSERT INTO Tasks (task_label, task_status) VALUES (?, ?)";
        db.setAutoCommit(false);
        int taskId;
        try {
            try (PreparedStatement stmt = db.prepareStatement(insertTask, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, label);
                stmt.setInt(2, ACTIVE);

                stmt.executeUpdate();
                // fetch the generated task_id!
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (!rs.next()) {
                        throw new RuntimeException("no generated keys???");
                    }
                    taskId = rs.getInt(1);
                    System.out.format("Creating task %d\n", taskId);
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

//    Associate due dates with tasks - to make task 7 due on April 1:
//    due 7 2018-04-01

    @Command
    public void due(int taskId, String dateString) throws SQLException {
        String query = "UPDATE Tasks SET task_due_date = ? WHERE task_id = ?";
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            stmt.setDate(1, Date.valueOf(dateString));
            stmt.setInt(2, taskId);
//            System.out.format("Adding due date %s to %d%\n", dateString, id);
            int nrows = stmt.executeUpdate();
            System.out.format("updated %d tasks\n", nrows);
        }
    }

//    Associate tags with tasks - to tag task 7 with ‘school’ and ‘homework’:
//    tag 7 school homework

    @Command
    public void tag(int taskId, String... tag) throws SQLException {
        for (int i = 0; i < tag.length; i++) {

            int tagId = getTagIdGivenName(tag[i]);

            String insertTask = "INSERT INTO TaskTags (tag_id, task_id) VALUES (?, ?)";
            db.setAutoCommit(false);
            try {
                try (PreparedStatement stmt = db.prepareStatement(insertTask, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setInt(1, taskId);
                    stmt.setInt(2, tagId);

                    stmt.executeUpdate();
                    // fetch the generated task_id!
                }
                db.commit();

            } catch (SQLException | RuntimeException e) {
                db.rollback();
                throw e;
            } finally {
                db.setAutoCommit(true);
            }
        }
    }

    private int getTagIdGivenName(String tagName) throws SQLException {
        String query = "SELECT tag_id FROM Tags" +
                " WHERE tag_name  = ?";
        int tagId = 0;
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            // Set the first parameter (query key) to the series
            stmt.setString(1, tagName);
            // once parameters are bound we can run!
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    tagId = insertTag(tagName);
                } else {
                    while (rs.next()) {
                        tagId = rs.getInt("tag_id");
                    }
                }
            }
        }
        return tagId;
    }

    private int insertTag(String tagName) throws SQLException {
        String insertTag = "INSERT INTO Tags (tag_name) VALUES (?)";
        db.setAutoCommit(false);
        int taskId;
        try {
            try (PreparedStatement stmt = db.prepareStatement(insertTag, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, tagName);

                stmt.executeUpdate();
                // fetch the generated task_id!
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (!rs.next()) {
                        throw new RuntimeException("no generated keys???");
                    }
                    taskId = rs.getInt(1);
                }
            }
            db.commit();
        } catch (SQLException | RuntimeException e) {
            db.rollback();
            throw e;
        } finally {
            db.setAutoCommit(true);
        }
        return taskId;
    }


    @Command
    public void finish(int taskId) throws SQLException {
        String query = "UPDATE Tasks SET task_status = ? WHERE task_id = ?";
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            stmt.setInt(1, FINISHED);
            stmt.setInt(2, taskId);
//            System.out.format("Changing status %s on task %d%\n", "finished", taskId);
            int nrows = stmt.executeUpdate();
            System.out.format("updated %d tasks\n", nrows);
        }
    }

    @Command
    public void cancel(int taskId) throws SQLException {
        String query = "UPDATE Tasks SET task_status = ? WHERE task_id = ?";
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            stmt.setInt(1, CANCELED);
            stmt.setInt(2, taskId);
            System.out.format("Changing status %s on task %d%\n", "canceled", taskId);
            int nrows = stmt.executeUpdate();
            System.out.format("updated %d tasks\n", nrows);
        }
    }

    @Command
    public void show(int taskId) throws SQLException {
        String query = "SELECT * FROM Tasks" +
                " WHERE task_id  = ?";
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            // Set the first parameter (query key) to the series
            stmt.setInt(1, taskId);
            // once parameters are bound we can run!
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
//                    int taskId = rs.getInt("task_id");
                    String taskLabel = rs.getString("task_label");
                    Timestamp taskCreateDate = rs.getTimestamp("task_create_date");
                    Timestamp taskDueDate = rs.getTimestamp("task_due_date");
                    int status = rs.getInt("task_status");
                    String statusString = getStatusString(status);
                    System.out.format("Task_id: %d, label: %s, Create Date: %s, Due Date: %s, Status: %s \n",
                            taskId, taskLabel, taskCreateDate.toString(), taskDueDate.toString(), statusString);
                }
            }
        }
    }

    @Command
    public void active(String tagName) throws SQLException {
        String query = "SELECT Tasks.task_id, Tasks.task_label FROM Tasks" +
                " JOIN TaskTags tt on tt.task_id = Tasks.task_id" +
                " JOIN Tags on Tags.tag_id = tt.tag_id" +
                " WHERE Tags.tag_name  = ? AND Tasks.task_status = ?";
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            // Set the first parameter (query key) to the series
            stmt.setString(1, tagName);
            stmt.setInt(2, ACTIVE);

            // once parameters are bound we can run!
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int taskId = rs.getInt("task_id");
                    String taskLabel = rs.getString("task_label");
                    System.out.format("Task_id: %d, label: %s\n",
                            taskId, taskLabel);
                }
            }
        }
    }

    @Command
    public void completed(String tagName) throws SQLException {
        String query = "SELECT Tasks.task_id, Tasks.task_label FROM Tasks" +
                " JOIN TaskTags tt on tt.task_id = Tasks.task_id" +
                " JOIN Tags on Tags.tag_id = tt.tag_id" +
                " WHERE Tags.tag_name  = ? AND Tasks.task_status = ?";
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            // Set the first parameter (query key) to the series
            stmt.setString(1, tagName);
            stmt.setInt(2, FINISHED);

            // once parameters are bound we can run!
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int taskId = rs.getInt("task_id");
                    String taskLabel = rs.getString("task_label");
                    System.out.format("Task_id: %d, label: %s\n",
                            taskId, taskLabel);
                }
            }
        }
    }

    @Command
    public void overdue() throws SQLException {
        String query = "SELECT task_id, task_label FROM Tasks" +
                " WHERE NOT (task_status = ?) AND task_due_date < current_timestamp";
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            // Set the first parameter (query key) to the series
            // once parameters are bound we can run!
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
//                    int taskId = rs.getInt("task_id");
                    int taskId = rs.getInt("task_id");
                    String taskLabel = rs.getString("task_label");
                    System.out.format("Task_id: %d, label: %s\n",
                            taskId, taskLabel);
                }
            }
        }
    }




    private String getStatusString(int status) {
        if (status == 1) {
            return "active";
        } else if (status == 2) {
            return "canceled";
        } else if (status == 3) {
            return "finished";
        } else {
            return "no status";
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

    public static void main(String[] args) throws IOException, SQLException {
        String dbUrl = args[0];
        try (Connection cxn = DriverManager.getConnection("jdbc:" + dbUrl)) {
            App shell = new App(cxn);
            ShellFactory.createConsoleShell("article", "", shell)
                    .commandLoop();
        }
    }
}
