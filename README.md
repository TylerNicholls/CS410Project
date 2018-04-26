# CS410Project

* Team: Tyler Nicholls, John Kehr


##Overview

Creates an executable jar file that runs a basic ToDo list from the command line.



##Manifest

├── CS410-Final-Project.zip
│   ├── App.java
│   ├── example-data.sql
│   ├── final-project-1.0-SNAPSHOT-jar-with-dependencies.jar
│   ├── pom.xml
│   └── schema.sql
├── CS410-Project.pdf
└── README.md

Also available at:
git@github.com:TylerNicholls/CS410Project.git


##Building and Running the project

The project is already built and contained in the:

    final-project-1.0-SNAPSHOT-jar-with-dependencies.jar

To run the program, ensure that the schema.sql has been added to a
database. Then run the following:

$ java -jar final-project-1.0-SNAPSHOT-jar-with-dependencies.jar mysql://username:password@localhost:port/TodoManager

This will start the command line program.


##Features and usage

Commands are as follows:

    active      - command to show active tasks
    add         - used to add a new task
    due         - sets a due date
    tag         - adds a tags to a task number
    finish      - finishes a task
    cancel      - cancels a task
    show        - displays details of a task
    completed   - displays list of completed, based on all or by tag
    overdue     - displays list of overdue, based on all or by tag
    due today   - displays list of tasks due within 1 day
    due soon    - displays list of tasks due within 3 day
    rename      - changes name of a task
    search      - searches for all text



