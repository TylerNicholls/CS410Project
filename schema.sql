DROP DATABASE TodoManager;
CREATE DATABASE IF NOT EXISTS TodoManager;
USE TodoManager;

CREATE TABLE IF NOT EXISTS Tags(
  tag_id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  tag_name VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS Tasks (
  task_id          INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  task_create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  task_due_date    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  task_status      TINYINT         NOT NULL,
  task_label       VARCHAR(100)    NOT NULL
);

CREATE TABLE IF NOT EXISTS TaskTags(
  tasktag_id       INT PRIMARY KEY  NOT NULL AUTO_INCREMENT,
  tag_id           INT,
  FOREIGN KEY (tag_id) REFERENCES Tags(tag_id),
  task_id INT,
  FOREIGN KEY (task_id) REFERENCES Tasks(task_id)
);


CREATE FULLTEXT INDEX task_label_idx ON Tasks(task_label);


INSERT INTO Tasks(task_create_date, task_due_date, task_status, task_label) VALUE ('2018-04-02 14:36:04', '2018-04-29 23:59:59', 1, 'Final Project');
INSERT INTO Tasks(task_create_date, task_due_date, task_status, task_label) VALUE ('2018-04-03 14:36:05', '2018-04-29 23:59:59', 1, 'Paint Stadium');
INSERT INTO Tasks(task_create_date, task_due_date, task_status, task_label) VALUE ('2018-04-04 14:36:06', '2018-04-29 23:59:59', 1, 'Build Death Star');
INSERT INTO Tasks(task_create_date, task_due_date, task_status, task_label) VALUE ('2018-04-05 14:36:07', '2018-04-29 23:59:59', 1, 'Crush Rebellion');
INSERT INTO Tasks(task_create_date, task_due_date, task_status, task_label) VALUE ('2018-04-06 14:36:08', '2018-04-29 23:59:59', 1, 'Give away dog');
INSERT INTO Tasks(task_create_date, task_due_date, task_status, task_label) VALUE ('2018-04-07 14:36:09', '2018-04-29 23:59:59', 1, 'Get Rich');
INSERT INTO Tasks(task_create_date, task_due_date, task_status, task_label) VALUE ('2018-04-08 14:36:10', '2018-04-29 23:59:59', 1, 'Buy AMG GT R');
INSERT INTO Tasks(task_create_date, task_due_date, task_status, task_label) VALUE ('2018-04-08 14:36:11', '2018-04-29 23:59:59', 1, 'Buy Milk');
INSERT INTO Tasks(task_create_date, task_due_date, task_status, task_label) VALUE ('2018-04-09 14:36:12', '2018-04-29 23:59:59', 1, 'Make Coffee');
INSERT INTO Tasks(task_create_date, task_due_date, task_status, task_label) VALUE ('2018-04-10 14:36:13', '2018-04-29 23:59:59', 1, 'Switch human numbering to hex');
INSERT INTO Tasks(task_create_date, task_due_date, task_status, task_label) VALUE ('2018-04-11 14:36:14', '2018-04-29 23:59:59', 1, 'Dump metric to hextric');
INSERT INTO Tasks(task_create_date, task_due_date, task_status, task_label) VALUE ('2018-04-12 14:36:15', '2018-04-29 23:59:59', 1, 'Keep miles though, its cool');
INSERT INTO Tasks(task_create_date, task_due_date, task_status, task_label) VALUE ('2018-04-12 14:36:16', '2018-04-29 23:59:59', 1, 'Complete Trilogy');
INSERT INTO Tasks(task_create_date, task_due_date, task_status, task_label) VALUE ('2018-04-12 14:36:17', '2018-04-29 23:59:59', 1, 'Pass Databases');
INSERT INTO Tasks(task_create_date, task_due_date, task_status, task_label) VALUE ('2018-04-13 14:36:18', '2018-04-29 23:59:59', 1, 'Delete Facebook, from history');
INSERT INTO Tasks(task_create_date, task_due_date, task_status, task_label) VALUE ('2018-04-14 14:36:19', '2018-04-29 23:59:59', 1, 'Release Ice 9 virus');
INSERT INTO Tasks(task_create_date, task_due_date, task_status, task_label) VALUE ('2018-04-14 14:36:20', '2018-04-29 23:59:59', 1, 'Buy Red Hat Stock');
INSERT INTO Tasks(task_create_date, task_due_date, task_status, task_label) VALUE ('2018-04-20 14:36:20', '2018-05-01 00:00:01', 1, 'Create more aggressive todo list');

INSERT INTO Tags(tag_name) VALUE ('school');
INSERT INTO Tags(tag_name) VALUE ('world_domination');
INSERT INTO Tags(tag_name) VALUE ('amusement');
INSERT INTO Tags(tag_name) VALUE ('self_improvement');
INSERT INTO Tags(tag_name) VALUE ('serenity');
INSERT INTO Tags(tag_name) VALUE ('homework');

INSERT INTO TaskTags(tag_id, task_id) VALUE (1,1);
INSERT INTO TaskTags(tag_id, task_id) VALUE (1,2);
INSERT INTO TaskTags(tag_id, task_id) VALUE (6,2);
INSERT INTO TaskTags(tag_id, task_id) VALUE (2,3);
INSERT INTO TaskTags(tag_id, task_id) VALUE (2,4);
INSERT INTO TaskTags(tag_id, task_id) VALUE (2,10);
INSERT INTO TaskTags(tag_id, task_id) VALUE (2,11);
INSERT INTO TaskTags(tag_id, task_id) VALUE (2,12);
INSERT INTO TaskTags(tag_id, task_id) VALUE (2,14);
INSERT INTO TaskTags(tag_id, task_id) VALUE (3,15);
INSERT INTO TaskTags(tag_id, task_id) VALUE (4,15);
INSERT INTO TaskTags(tag_id, task_id) VALUE (1,16);
INSERT INTO TaskTags(tag_id, task_id) VALUE (2,16);
INSERT INTO TaskTags(tag_id, task_id) VALUE (3,16);
INSERT INTO TaskTags(tag_id, task_id) VALUE (4,16);
INSERT INTO TaskTags(tag_id, task_id) VALUE (5,16);
INSERT INTO TaskTags(tag_id, task_id) VALUE (4,5);
INSERT INTO TaskTags(tag_id, task_id) VALUE (4,6);
INSERT INTO TaskTags(tag_id, task_id) VALUE (4,7);
INSERT INTO TaskTags(tag_id, task_id) VALUE (4,8);
INSERT INTO TaskTags(tag_id, task_id) VALUE (4,9);
INSERT INTO TaskTags(tag_id, task_id) VALUE (2,18);



