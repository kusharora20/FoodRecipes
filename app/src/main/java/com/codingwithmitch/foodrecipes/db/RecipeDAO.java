package com.codingwithmitch.foodrecipes.db;

import android.icu.text.Replaceable;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.codingwithmitch.foodrecipes.models.Recipe;

import java.util.List;

import static androidx.room.OnConflictStrategy.*;

@Dao
public interface RecipeDAO {

    @Insert(onConflict = REPLACE)
    void insertRecipe(RecipeDBmodel recipe);

    @Query("select * from recipes " +
            "where  title LIKE '%' || :query || '%' " +
            "OR ingredients LIKE '%' || :query || '%'" +
            "ORDER BY social_rank DESC " +
            "LIMIT :pageNumber*30" )
    List<RecipeDBmodel> searchRecipes(String query, int pageNumber);

    @Query("select * from recipes " +
            "where  title LIKE '%' || :query || '%' " +
            "OR ingredients LIKE '%' || :query || '%'" +
            "ORDER BY social_rank DESC " +
            "LIMIT 1" )
    List<RecipeDBmodel> searchIfRecipesAvailable(String query, int pageNumber);

    @Delete
    void deleteRecipe(RecipeDBmodel recipe);

    @Query("select * from recipes where recipe_id = :recipeID")
    RecipeDBmodel getRecipe(String recipeID);

}
