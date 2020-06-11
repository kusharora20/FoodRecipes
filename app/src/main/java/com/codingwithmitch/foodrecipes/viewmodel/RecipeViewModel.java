package com.codingwithmitch.foodrecipes.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.repositories.RecipeRepository;
import com.codingwithmitch.foodrecipes.util.Resource;

public class RecipeViewModel extends AndroidViewModel {

    private RecipeRepository mRecipeRepository;
    private String recipeID;
    private boolean recipeRetrieved;
    private SavedStateHandle state;

    private static final String TAG = "RecipeViewModel";

    public RecipeViewModel(@NonNull Application application, SavedStateHandle state) {
        super(application);
        this.state = state;
        mRecipeRepository = RecipeRepository.getInstance(application.getApplicationContext());
    }

    public void searchSingleRecipe(String recipeID) {
        this.recipeID = recipeID;
        mRecipeRepository.searchSingleRecipe(recipeID);

    }

    public LiveData<Resource<Recipe>> getSingleRecipe() {
        return mRecipeRepository.getSingleRecipe();
    }

    public String getRecipeID() {
        return recipeID;
    }

    public MutableLiveData<Boolean> getRecipeRequestTimedOut() {
        return mRecipeRepository.getRecipeRequestTimedOut();
    }

    // setter and getter for recipeRetrieved...

//    public void recipeRetrieved(boolean recipeRetrieved) {
//        state.set("recipeRetrieved", recipeRetrieved);
//    }
//
//    public boolean isRecipeRetrieved() {
//        return (boolean) state.get("recipeRetrieved");
//    }
}
