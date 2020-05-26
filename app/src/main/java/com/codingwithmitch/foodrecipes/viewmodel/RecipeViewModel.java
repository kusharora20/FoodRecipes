package com.codingwithmitch.foodrecipes.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.repositories.RecipeRepository;

public class RecipeViewModel extends ViewModel {

    private RecipeRepository mRecipeRepository;
    private String recipeID;
    private boolean recipeRetrieved;
    private SavedStateHandle state;

    private static final String TAG = "RecipeViewModel";

    public RecipeViewModel() {
        mRecipeRepository = RecipeRepository.getInstance();
    }

    public RecipeViewModel(SavedStateHandle state) {
        this.state = state;
        mRecipeRepository = RecipeRepository.getInstance();
    }

    public void searchSingleRecipe(String recipeID) {
        this.recipeID = recipeID;
        mRecipeRepository.searchSingleRecipe(recipeID);

    }

    public LiveData<Recipe> getSingleRecipe() {
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
