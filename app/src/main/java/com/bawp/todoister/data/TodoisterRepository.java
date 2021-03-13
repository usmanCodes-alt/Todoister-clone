package com.bawp.todoister.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.bawp.todoister.model.Task;
import com.bawp.todoister.util.TaskRoomDatabase;

import java.util.List;

public class TodoisterRepository {
    private final TaskDao taskDao;
    private final LiveData<List<Task>> allTasks;

    public TodoisterRepository(Application application) {
        TaskRoomDatabase taskRoomDatabase = TaskRoomDatabase.getInstance(application);
        this.taskDao = taskRoomDatabase.taskDao();
        this.allTasks = taskDao.getAllTasks();
    }

    public LiveData<List<Task>> getAllTasks() {
        return allTasks;
    }

    public void insert(Task task) {
        TaskRoomDatabase.databaseWriterExecutor.execute(() -> taskDao.insert(task));
    }

    public LiveData<Task> getTask(long id) {
        return taskDao.getTaskById(id);
    }

    public void update(Task task) {
        TaskRoomDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                taskDao.update(task);
            }
        });
    }

    public void delete(Task task) {
        TaskRoomDatabase.databaseWriterExecutor.execute(new Runnable() {
            @Override
            public void run() {
                taskDao.delete(task);
            }
        });
    }
}
