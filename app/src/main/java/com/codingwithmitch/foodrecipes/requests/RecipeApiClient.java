package com.codingwithmitch.foodrecipes.requests;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.codingwithmitch.foodrecipes.util.AppExecutors;
import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.requests.response.RecipeResponse;
import com.codingwithmitch.foodrecipes.requests.response.RecipeSearchResponse;
import com.codingwithmitch.foodrecipes.util.Constants;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

/**
 * This class is server for RecipeRepository.
 * Its only job is to retrieve data from API using Retrofit
 * Data retrieved is stored as MutableLiveData
 */

public class RecipeApiClient {

    private static final String TAG = "RecipeApiClient";
    private static RecipeApiClient instance;
    private MutableLiveData<List<Recipe>> recipes;
    private MutableLiveData<Recipe> mRecipe;
    private MutableLiveData<Boolean> recipeRequestTimedOut;
    private MutableLiveData<Boolean> searchTimedOut;
    private boolean cancelRequest;
    private String searchQuery;
    private MutableLiveData<Response<Recipe>> mRecipeResponseLiveData;
    private MutableLiveData<Response<List<Recipe>>> mRecipeListResponseLiveData;
    private int pageNumber = 1;

    // returns a singleton instance of this class
    public static RecipeApiClient getInstance() {
        return (instance == null) ? (instance = new RecipeApiClient()) : instance;
    }

    // private constructor prevents direct instantiation
    private RecipeApiClient() {
        recipes = new MutableLiveData<>();
        mRecipe = new MutableLiveData<>();
        recipeRequestTimedOut = new MutableLiveData<>();
        searchTimedOut = new MutableLiveData<>();
        mRecipeResponseLiveData = new MutableLiveData<>();
    }

    // simple getter for the LiveData (List of Recipes)
    public MutableLiveData<List<Recipe>> getRecipes() {
        return recipes;
    }

    // simple getter for the LiveData (single Recipe)
    public MutableLiveData<Recipe> getRecipe() {
        return mRecipe;
    }

    public MutableLiveData<Boolean> getRecipeRequestTimedOut() {
        return recipeRequestTimedOut;
    }

    public MutableLiveData<Boolean> getSearchTimedOut() {
        return searchTimedOut;
    }


    public void cancelSearchOperation() {
        cancelRequest = true;
    }

    // API call for single recipe
    public LiveData<Response<Recipe>> makeAPIcall(String searchQuery) {
        this.searchQuery = searchQuery;
        RecipeApiRunnable runnable = new RecipeApiRunnable();

        Future handler = AppExecutors.getInstance().diskIO().submit(runnable);

        // interrupt if API call takes more than 3 seconds
        AppExecutors.getInstance().diskIO().schedule(new Runnable() {
            @Override
            public void run() {
                handler.cancel(true);
                mRecipeResponseLiveData.postValue(Response.error(204, null));
            }
        }, 3000, TimeUnit.MILLISECONDS);

        return mRecipeResponseLiveData;
    }

    private class RecipeApiRunnable implements Runnable {

        @Override
        public void run() {
            Response<RecipeResponse> response = null;
            try {
                response = mRecipeResponseCall(searchQuery).execute();

            } catch (IOException e) {
                e.printStackTrace();
            }

            Response<Recipe> recipeResponse = Response.success(response.body().getRecipe());
            mRecipeResponseLiveData.postValue(recipeResponse);

        }
    }

    // API call for list of recipes
    public LiveData<Response<List<Recipe>>> makeAPIcall(String searchQuery, int pagenNumber) {
        this.searchQuery = searchQuery;
        RecipeListApiRunnable runnable = new RecipeListApiRunnable();

        Future handler = AppExecutors.getInstance().diskIO().submit(runnable);

        // interrupt if API call takes more than 3 seconds
        AppExecutors.getInstance().diskIO().schedule(new Runnable() {
            @Override
            public void run() {
                handler.cancel(true);
                mRecipeListResponseLiveData.postValue(Response.error(204, null));
            }
        }, 3000, TimeUnit.MILLISECONDS);

        return mRecipeListResponseLiveData;

    }

    private class RecipeListApiRunnable implements Runnable {

        @Override
        public void run() {
            Response<RecipeSearchResponse> response = null;
            try {
                response = recipeSearchResponseCall(searchQuery, pageNumber).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Response<List<Recipe>> recipeResponse = Response.success(response.body().getRecipes());
            mRecipeListResponseLiveData.postValue(recipeResponse);

        }
    }

    // API call which returns Call object of List<Recipe>
    private Call<RecipeSearchResponse> recipeSearchResponseCall(String query, int pageNumber) {
        return ServiceGenerator.getRecipeApi().searchRecipe(
                Constants.API_KEY,
                query,
                pageNumber
        );
    }

    // API call which returns Call object of single recipe
    private Call<RecipeResponse> mRecipeResponseCall(String recipeID) {
        return ServiceGenerator.getRecipeApi()
                .getRecipe(Constants.API_KEY, recipeID);
    }

}

