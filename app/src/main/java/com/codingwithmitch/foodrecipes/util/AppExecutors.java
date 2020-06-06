package com.codingwithmitch.foodrecipes.util;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Singleton utility class for returning a scheduledExecutorService of 3 threads.
 * use by calling ScheduledExecutorService executorService = AppExecutors.getInstance().networkIO();
 */

public final class AppExecutors {

    private static AppExecutors instance;

    // returns a singleton instance of this class
    public static AppExecutors getInstance(){
        return (instance == null) ? (instance = new AppExecutors()) : instance;
    }

    // private constructor to prevent direct instantiation
    private AppExecutors(){

    }

    private final ScheduledExecutorService diskIO = Executors.newSingleThreadScheduledExecutor();
    private final MainThreadExecutor mainThread = new MainThreadExecutor();

    private class MainThreadExecutor implements Executor {

        private Handler mHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mHandler.post(command);
        }
    }

    public MainThreadExecutor getMainThread() {
        return mainThread;
    }

    public ScheduledExecutorService diskIO() {
        return diskIO;
    }


    
}
