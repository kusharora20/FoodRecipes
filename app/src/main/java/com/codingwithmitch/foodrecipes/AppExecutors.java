package com.codingwithmitch.foodrecipes;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class AppExecutors {

    private static AppExecutors instance;

    // returns a singleton instance of this class
    public static AppExecutors getInstance(){
        return (instance == null) ? (instance = new AppExecutors()) : instance;
    }

    // private constructor to prevent direct instantiation
    private AppExecutors(){

    }

    private final ScheduledExecutorService networkIO = Executors.newScheduledThreadPool(3);

    public ScheduledExecutorService networkIO() {
        return networkIO;
    }




}
