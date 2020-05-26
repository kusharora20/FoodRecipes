package com.codingwithmitch.foodrecipes.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.repositories.RecipeRepository;
import com.codingwithmitch.foodrecipes.util.OnBackPressed;

import java.util.List;

/**
 * this class is server for RecipeListActivity and client for RecipeRepository
 * Its only job is to be a ViewModel for RecipeListActivity
 */

public class RecipeListViewModel extends ViewModel {

    private static final String TAG = "RecipeListViewModel";
    private RecipeRepository mRecipeRepository;
    private boolean isViewingRecipes;
    private boolean isPerformingQuery;
    private int page;
    private boolean isViewingRecipeDetails;

    public RecipeListViewModel() {
        mRecipeRepository = RecipeRepository.getInstance();
        page = 1;
    }

    public RecipeListViewModel(MutableLiveData<List<Recipe>> recipes) {
        mRecipeRepository = RecipeRepository.getInstance();
        page = 1;
    }

    // searchRecipesApi method calls methods of its namesake across following classes and in order mentioned:
    // RecipeListActvity >>> RecipeListViewModel >>> RecipeRepository >>> RecipeApiClient
    // and passes the search String and pageNumber from the Activity right uptil the RecipeApiClient
    public void searchRecipesApi(String query) {
        isViewingRecipes = true;
        isPerformingQuery = true;
        mRecipeRepository.searchRecipesApi(query, 1);
    }

    public void loadNextPage(String query) {
        isViewingRecipes = true;
        isPerformingQuery = true;
        page++;
        mRecipeRepository.searchRecipesApi(query, page);
    }

    // records state of RecipeListActivity (i.e. which screen is showing)
    public OnBackPressed onBackPressed() {
        page = 1;
        if (isViewingRecipes) {
            isViewingRecipes = false;
            return OnBackPressed.VIEWING_RECIPES;
        }

        if (isViewingRecipeDetails) {
            isViewingRecipeDetails = false;
            isViewingRecipes = true;
            return OnBackPressed.VIEWING_RECIPE_DETAILS;
        } else {
            isViewingRecipes = false;
            isViewingRecipeDetails = false;
            return OnBackPressed.VIEWING_CATEGORIES;
        }
    }

    public void cancelSearchOperation() {
        mRecipeRepository.cancelSearchOperation();
    }

    // getters and setters which help with recording and retrieving state of Activity

    public LiveData<List<Recipe>> getRecipes() {
        return mRecipeRepository.getRecipes();
    }

    public boolean isPerformingQuery() {
        return isPerformingQuery;
    }

    public void setPerformingQuery(boolean performingQuery) {
        isPerformingQuery = performingQuery;
    }

    public void setIsViewingRecipes(boolean isViewingRecipes) {
        this.isViewingRecipes = isViewingRecipes;
    }

    public boolean isViewingRecipes() {
        return isViewingRecipes;
    }

    public MutableLiveData<Boolean> getSearchTimedOut() {
        return mRecipeRepository.getSearchTimedOut();
    }

    public LiveData<Boolean> isExhausted() {
        return mRecipeRepository.isExhausted();
    }
}
