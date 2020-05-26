package com.codingwithmitch.foodrecipes.util;

import android.util.Log;
import com.codingwithmitch.foodrecipes.models.Recipe;
import java.util.List;

public final class Testing {

    public static void printRecipes(List<Recipe> list, String tag){
        int count = 1;
        for (Recipe recipe : list) {
            Log.d(tag, "printRecipes: " + count + ": " + recipe.getTitle());
            count++;
        }

    }

}
