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



    /**
     * View currently-active tasks - list the task IDs, labels, create dates, and due dates (if assigned):
     * active
     *
     * @throws SQLException
     */
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


    /**
     * Add new tasks (e.g., add a new task with the label “Finish Assignment”; it should print
     * the task ID once it has added the task)
     * add Finish Final Project
     *
     * @param input
     * @throws SQLException
     */
    @Command
    public void add(String... input) throws SQLException {
        String label = "";
        for (int i = 0; i < input.length; i++) {
            label += input[i] + " ";
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


    /**
     *     Associate due dates with tasks - to make task 7 due on April 1:
     *     due 7 2018-04-01
     *
     * @param taskId
     * @param dateString
     * @throws SQLException
     */
    @Command
    public void due(int taskId, String dateString) throws SQLException {
        String query = "UPDATE Tasks SET task_due_date = ? WHERE task_id = ?";
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            stmt.setDate(1, Date.valueOf(dateString));
            stmt.setInt(2, taskId);
            int nrows = stmt.executeUpdate();
            System.out.format("updated %d tasks\n", nrows);
        }
    }


    /**
     *     Associate tags with tasks - to tag task 7 with ‘school’ and ‘homework’:
     *     tag 7 school homework
     * @param taskId
     * @param tag
     * @throws SQLException
     */
    @Command
    public void tag(int taskId, String... tag) throws SQLException {
        for (int i = 0; i < tag.length; i++) {

            int tagId = getTagIdGivenName(tag[i]);
            boolean alreadyExists = checkIfTaskTagExists(taskId, tagId);

            if(alreadyExists){
                return;
            }
            String insertTask = "INSERT INTO TaskTags (tag_id, task_id) VALUES (?, ?)";
            db.setAutoCommit(false);
            try {
                try (PreparedStatement stmt = db.prepareStatement(insertTask, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setInt(1, tagId);
                    stmt.setInt(2, taskId);

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

    private boolean checkIfTaskTagExists(int taskId, int tagId) throws SQLException {
        boolean exists = false;

        String query = "SELECT tasktag_id FROM TaskTags" +
                " WHERE task_id  = ? AND tag_id = ?";
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            // Set the first parameter (query key) to the series
            stmt.setInt(1, taskId);
            stmt.setInt(2, tagId);

            // once parameters are bound we can run!
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    exists = false;
                } else {
                    while (rs.next()) {
                        exists = true;
                    }
                }
            }
        }
        return exists;
    }


    /**
     *
     * @param tagName
     * @return
     * @throws SQLException
     */
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


    /**
     *
     * @param tagName
     * @return
     * @throws SQLException
     */
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


    /**
     *
     * @param taskId
     * @throws SQLException
     */
    @Command
    public void finish(int taskId) throws SQLException {
        String query = "UPDATE Tasks SET task_status = ? WHERE task_id = ?";
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            stmt.setInt(1, FINISHED);
            stmt.setInt(2, taskId);
            int nrows = stmt.executeUpdate();
            System.out.format("Changing status finished on task %d\n", taskId);
            System.out.format("updated %d tasks\n", nrows);
        }
    }


    /**
     *
     * @param taskId
     * @throws SQLException
     */
    @Command
    public void cancel(int taskId) throws SQLException {
        String query = "UPDATE Tasks SET task_status = ? WHERE task_id = ?";
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            stmt.setInt(1, CANCELED);
            stmt.setInt(2, taskId);
            System.out.format("Changing status canceled on task %d\n", taskId);
            int nrows = stmt.executeUpdate();
            System.out.format("updated %d tasks\n", nrows);
        }
    }


    /**
     *
     * @param taskId
     * @throws SQLException
     */
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


    /**
     *
     * @param tagName
     * @throws SQLException
     */
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


    /**
     *
     * @param tagName
     * @throws SQLException
     */
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


    /**
     *
     * @throws SQLException
     */
    @Command
    public void overdue() throws SQLException {
        String query = "SELECT task_id, task_label FROM Tasks" +
                " WHERE (task_status = ?) AND task_due_date < current_timestamp";
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            stmt.setInt(1, ACTIVE);
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




    /**
     * Show tasks due today, or due in the next 3 days
     * due today
     * due soon
     *
     * @param timeFrame
     * @throws SQLException
     */
    @Command
    public void due(String timeFrame) throws SQLException {
        if (timeFrame.equals("today")) {
            tasksDueToday();
        } else if (timeFrame.equals("soon")) {
            tasksDueSoon();
        } else {
            System.out.println("please enter 'today' or 'soon' after due to view tasks");
        }
    }


    /**
     * called from due for soon query
     *
     * @throws SQLException
     */
    private void tasksDueSoon() throws SQLException {
        String query = "SELECT task_id, task_label FROM Tasks" +
                " WHERE DATE_ADD(CURRENT_TIMESTAMP , INTERVAL 3 DAY) > task_due_date " +
                " AND CURRENT_TIMESTAMP < task_due_date" +
                " AND Tasks.task_status = ?";
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            stmt.setInt(1, ACTIVE);
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


    /**
     * called from due for Today query
     *
     * @throws SQLException
     */
    private void tasksDueToday() throws SQLException {
        String query = "SELECT task_id, task_label FROM Tasks" +
                " WHERE DATE_ADD(CURRENT_TIMESTAMP , INTERVAL 1 DAY) > task_due_date " +
                " AND CURRENT_TIMESTAMP < task_due_date" +
                " AND Tasks.task_status = ?";
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            stmt.setInt(1, ACTIVE);
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



    /**
     * Change the label of a task
     * rename 7 Finish Final Project
     *
     * @param taskId
     * @param input
     * @throws SQLException
     */
    @Command
    public void rename(int taskId, String... input) throws SQLException {
        String newLabel = "";
        for (int i = 0; i < input.length; i++) {
            newLabel += input[i] + " ";
        }
        String query = "UPDATE Tasks SET task_label = ? WHERE task_id = ?";
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            stmt.setString(1, newLabel);
            stmt.setInt(2, taskId);
            int nrows = stmt.executeUpdate();
            System.out.format("updated %d tasks\n", nrows);
        }
    }


    /**
     * Search for tasks by keyword (e.g. search for tasks having the word “project” in their
     * label) search project
     * @param input
     * @throws SQLException
     */
    @Command
    public void search(String... input) throws SQLException {
        String searchParameter = "";
        for (int i = 0; i < input.length; i++) {
            searchParameter += input[i] + " ";
        }

        String query = "SELECT task_id, task_label FROM Tasks " +
                "WHERE MATCH(task_label) AGAINST (?)";
        try (PreparedStatement stmt = db.prepareStatement(query)) {
            stmt.setString(1, searchParameter);
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


    /**
     *     *
     * @param status
     * @return
     */
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


    public static void main(String[] args) throws IOException, SQLException {
        String dbUrl = args[0];
        try (Connection cxn = DriverManager.getConnection("jdbc:" + dbUrl)) {
            App shell = new App(cxn);
            ShellFactory.createConsoleShell("myToDoList", "", shell)
                    .commandLoop();
        }
    }
}
