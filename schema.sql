CREATE DATABASE IF NOT EXISTS TodoManager;
USE TodoManager;

CREATE TABLE IF NOT EXISTS Tags(
  tag_id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  tag_name VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS TaskTags(
  tasktag_id       INT PRIMARY KEY  NOT NULL AUTO_INCREMENT,
  tag_id           INT,
  FOREIGN KEY (tag_id) REFERENCES Tags(tag_id),
  task_id INT,
  FOREIGN KEY (task_id) REFERENCES Tags(task_id)
);

CREATE TABLE IF NOT EXISTS Tasks (
  task_id          INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  task_create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  task_due_date    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  task_status      TINYINT         NOT NULL,
  task_label       VARCHAR(100)    NOT NULL
);
CREATE FULLTEXT INDEX task_label_idx ON Tasks(task_label);