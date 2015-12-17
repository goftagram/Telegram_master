package com.goftagram.telegram.goftagram.application.usecases.absclasses.absget;

import android.content.Context;

import com.goftagram.telegram.goftagram.taskmanager.Task;

/**
 * Created by WORK on 11/1/2015.
 */
public  abstract class AbsGetTask extends Task {

    protected Context mContext;


    public AbsGetTask(Context context){
        mContext = context.getApplicationContext();
    }

    @Override
    abstract public void run();
}
