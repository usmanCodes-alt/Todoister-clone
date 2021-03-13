package com.bawp.todoister.util;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.bawp.todoister.data.TaskDao;
import com.bawp.todoister.model.Task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Task.class}, version = 1, exportSchema = false)
@TypeConverters({Converter.class})
public abstract class TaskRoomDatabase extends RoomDatabase {
    public static final int NUMBER_OF_THREADS = 4;
    private static TaskRoomDatabase instance;
    private static final String DATABASE_NAME = "todoister_database";
    public static final ExecutorService databaseWriterExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    //DAO
    public abstract TaskDao taskDao();
    public static final RoomDatabase.Callback sRoomDatabaseCallback
            = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriterExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    TaskDao taskDao = instance.taskDao();
                    taskDao.deleteAll();    //clean slate
                }
            });
        }
    };

    public static synchronized TaskRoomDatabase getInstance(Application application) {
        if (instance == null) {
            instance = Room.databaseBuilder(application.getApplicationContext(),
                    TaskRoomDatabase.class, DATABASE_NAME)
                    .addCallback(sRoomDatabaseCallback)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
