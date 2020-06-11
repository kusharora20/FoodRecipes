package com.codingwithmitch.foodrecipes.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * get the DAO object to access the recipes table in db by writing AppDatabase.get(context).getRecipeDAO()
 */

@Database(entities = {RecipeDBmodel.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public static AppDatabase get(final Context context) {
        return instance = (instance == null) ?
                Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class,
                        "recipesDB")
                        .build()
                : instance;
    }

    public abstract RecipeDAO getRecipeDAO();
}
