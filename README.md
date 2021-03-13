# Todoister-clone

## Functionality
A Todo Android application developed in Java.
The user can perform all CRUD operations on their tasks.

User can.
1. Add a Task
2. Update a Task
3. Delete a Task
4. View all Tasks

Tasks have the following priority levels
1. HIGH
2. MEDIUM
3. LOW

Tasks are color coded according to their priority level automatically by the application.
User can add task deadlines as well as date they are created (date created is saved automatically).

A floating action button is used to open a `BottomSheetFragment` which has the ability to take task title, deadline and priority as an
input, as well as a image button to save the task.

If the user clicks on an existing Task being shown in the `Recycler View`, same `BottomSheetFragment` is popped up and filled with
details of that task such as its `title`, `dueDate`, `priority`.

## Database

Android Room along with SQLite was used to implement all database related functionality of the application.
The project contains following classes related to database

1. Entity
    1. This is the Task class that defines all properties a task would have such as `title`, `dueDate`, `createdAt`.
2. `TaskDao`
    1. This is the data access object
    2. This is an interface.
    3. Defined all the functionalities (methods) that our database will perform.
    4. Functionalities include `CRUD` methods
3. TaskRoomDatabase (actual database class)
4. `TodoisterRepository`
    1. This class talks to the database, triggers all of its functionality and provides a clean `API` for `viewModel` to work with.
5. `TaskViewModel`
    1. This class is used in our `activity` and `fragment`.
    2. Contains `static methods` for `CRUD`, which are used by activity and fragment to perform all
    database operations at their respective level.
