CREATE DATABASE IF NOT EXISTS TodoManager;
USE TodoManager;

CREATE TABLE IF NOT EXISTS Tags(
  tag_id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  tag_name VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS Tasks (
  task_id          INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  task_create_date DATE            NOT NULL,
  task_due_date    DATE            NOT NULL,
  task_status      TINYINT         NOT NULL,
  task_label       VARCHAR(100)    NOT NULL,
  tag_id           INT,
  FOREIGN KEY (tag_id) REFERENCES Tags(tag_id)
)