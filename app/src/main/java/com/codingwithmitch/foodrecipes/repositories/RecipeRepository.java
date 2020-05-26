package com.codingwithmitch.foodrecipes.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.requests.RecipeApiClient;

import java.util.List;

/**
 * This class is server for RecipeListViewModel and RecipeViewModel and client for RecipeApiClient
 * Its only job is to serve as repository for data fetched by RecipeApiClient and feed
 * that to the ViewModel classes
 */
public class RecipeRepository {

    private static RecipeRepository instance;
    private RecipeApiClient mRecipeApiClient;
    private MutableLiveData<Boolean> isqueryExhausted;
    private MediatorLiveData<List<Recipe>> recipes;

    // returns singleton object of the class
    public static RecipeRepository getInstance() {
        return (instance == null) ? (instance = new RecipeRepository()) : instance;
    }

    // private constructor prevents direct instantiation
    private RecipeRepository() {
        mRecipeApiClient = RecipeApiClient.getInstance();
        isqueryExhausted = new MutableLiveData<>();
        recipes = new MediatorLiveData<>();
        initMediators();
    }

    public MutableLiveData<Boolean> getRecipeRequestTimedOut() {
        return mRecipeApiClient.getRecipeRequestTimedOut();
    }

    private void initMediators() {
        LiveData<List<Recipe>> recipeListApiSource = mRecipeApiClient.getRecipes();
        recipes.addSource(recipeListApiSource,
                (List<Recipe> recipes) -> {
                    if (recipes != null) this.recipes.setValue(recipes);
                    doneQuery();
                }
        );
    }

    private void doneQuery() {
        if(recipes == null) isqueryExhausted.setValue(true);
        else if(recipes.getValue().size()%30 !=0 ) isqueryExhausted.setValue(true);
    }

    public MutableLiveData<List<Recipe>> getRecipes() {
        return recipes;
    }

    public MutableLiveData<Recipe> getSingleRecipe() {
        return mRecipeApiClient.getRecipe();
    }

    public MutableLiveData<Boolean> getSearchTimedOut() {
        return mRecipeApiClient.getSearchTimedOut();
    }

    // searchRecipesApi method calls methods of its namesake across following classes and in order mentioned:
    // RecipeListActvity >>> RecipeListViewModel >>> RecipeRepository >>> RecipeApiClient
    // and passes the search String and pageNumber from the Activity right uptil the RecipeApiClient
    // returns false if API call failed, true if it was successful
    public void searchRecipesApi(String query, int pageNumber) {
        pageNumber = (pageNumber < 1) ? 1 : pageNumber;
        mRecipeApiClient.searchRecipesApi(query, pageNumber);
    }

    // same method as above, except that it searches for single recipe
    public void searchSingleRecipe(String recipeID) {
        mRecipeApiClient.searchSingleRecipe(recipeID);
    }

    public void cancelSearchOperation() {
        mRecipeApiClient.cancelSearchOperation();
    }

    public LiveData<Boolean> isExhausted() {
        return isqueryExhausted;
    }
}
