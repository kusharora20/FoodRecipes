package com.codingwithmitch.foodrecipes.repositories;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.requests.GetRecipeFromDBorAPI;
import com.codingwithmitch.foodrecipes.requests.GetRecipeListFromDBorAPI;
import com.codingwithmitch.foodrecipes.requests.RecipeApiClient;
import com.codingwithmitch.foodrecipes.util.Resource;

import java.util.List;

/**
 * This class is server for RecipeListViewModel and RecipeViewModel and client for RecipeApiClient
 * Its only job is to serve as repository for data fetched by RecipeApiClient and feed
 * that to the ViewModel classes
 */
public class RecipeRepository {

    private RecipeApiClient mRecipeApiClient;
    private MutableLiveData<Boolean> isqueryExhausted;
    private MediatorLiveData<List<Recipe>> recipes;
    private Context context;
    private GetRecipeFromDBorAPI getRecipe;
    private GetRecipeListFromDBorAPI getRecipeList;

    // returns singleton object of the class
    public static RecipeRepository getInstance(Context context) {
        return new RecipeRepository(context.getApplicationContext());
    }

    public LiveData<Resource<List<Recipe>>> getRecipes() {
        return getRecipeList.getResultAsLiveData();
    }

    public LiveData<Resource<Recipe>> getSingleRecipe() {
        return getRecipe.getResultAsLiveData();
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
        getRecipeList = GetRecipeListFromDBorAPI.search(context, query, pageNumber);
    }

    // same method as above, except that it searches for single recipe
    public void searchSingleRecipe(String recipeID) {
        getRecipe = GetRecipeFromDBorAPI.search(recipeID, context);
    }

    public void cancelSearchOperation() {
        mRecipeApiClient.cancelSearchOperation();
    }

    public LiveData<Boolean> isExhausted() {
        return isqueryExhausted;
    }

    public MutableLiveData<Boolean> getRecipeRequestTimedOut() {
        return mRecipeApiClient.getRecipeRequestTimedOut();
    }
    // private constructor prevents direct instantiation

    private RecipeRepository(Context context) {
        this.context = context;
        mRecipeApiClient = RecipeApiClient.getInstance();
        isqueryExhausted = new MutableLiveData<>();
        recipes = new MediatorLiveData<>();
        initMediators();
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
}
