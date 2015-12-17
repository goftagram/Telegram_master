package com.goftagram.telegram.goftagram.taskmanager;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by WORK on 10/30/2015.
 */
public class TaskManager {

    private static TaskManager sTaskManager;
    ExecutorService mExecutor = null;

    private TaskManager(){
        mExecutor = Executors.newFixedThreadPool(5);
    }

    public static synchronized TaskManager getInstance(){
        if(sTaskManager == null){
            sTaskManager = new TaskManager();
        }
        return sTaskManager;
    }

    public void execute(Task task){
        mExecutor.execute(task);
    }

}
