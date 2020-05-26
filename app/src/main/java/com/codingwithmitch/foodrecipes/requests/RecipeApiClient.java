package com.codingwithmitch.foodrecipes.requests;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.codingwithmitch.foodrecipes.AppExecutors;
import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.requests.response.RecipeResponse;
import com.codingwithmitch.foodrecipes.requests.response.RecipeSearchResponse;
import com.codingwithmitch.foodrecipes.util.Constants;

import java.io.IOException;
import java.util.ArrayList;
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

    /**
     * This method makes uses {@link RetrieveRecipesRunnable} instance
     * which in turns uses retrofit to make the API call.
     * Times out in case API call takes longer than 3 seconds.
     *
     * @param query      the search query on the API
     * @param pageNumber the pageNumber (one page returns 30 JSON objects)
     * @return true if API call executes through the runnable, false if it times out at 3 seconds
     */
    public void searchRecipesApi(String query, int pageNumber) {

        RetrieveRecipesRunnable mRetrieveRecipesRunnable = new RetrieveRecipesRunnable(query, pageNumber);
        // API call...
        final Future handler = AppExecutors.getInstance().networkIO().submit(mRetrieveRecipesRunnable);
        searchTimedOut.setValue(false);
        // timeout in case API call takes more than 3 seconds...
        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                if (!handler.isDone()) {
                    handler.cancel(true);
                    // give message of timeout to user
                    Log.d(TAG, "run: searchTimedOut!!");
                    searchTimedOut.postValue(true);
                }
            }
        }, Constants.NETWORK_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    // same purpose as above, just that this queries a single Recipe
    public void searchSingleRecipe(String recipeID) {
        RetrieveRecipesRunnable runnable = new RetrieveRecipesRunnable(recipeID);

        Future handler = AppExecutors.getInstance().networkIO().submit(runnable);
        recipeRequestTimedOut.setValue(false);

        // timeout in case API call takes more than 3 seconds...
        AppExecutors.getInstance().networkIO().schedule(new Runnable() {
            @Override
            public void run() {
                if (!handler.isDone()) {
                    handler.cancel(true);

                    recipeRequestTimedOut.postValue(true);
                    Log.d(TAG, "run: interrupted!");
                }
            }
        }, Constants.NETWORK_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    public void cancelSearchOperation() {
        cancelRequest = true;
    }

    /**
     * This is a Runnable which runs asynchronously and makes API calls to retrieve
     * RecipeSearchResponse via Retrofit
     * (The response {@link RecipeSearchResponse} object is a POJO autoconverted from the returned
     * JSON using Gson which is added as a ConverterFactory when building the Retrofit {@link RecipeApiClient})
     */
    private class RetrieveRecipesRunnable implements Runnable {
        private String query;
        private int pageNumber;
        String recipeID;

        RetrieveRecipesRunnable(String query, int pageNumber) {
            this.query = query;
            this.pageNumber = pageNumber;
        }

        RetrieveRecipesRunnable(String recipeID) {
            this.recipeID = recipeID;
        }

        /**
         * When an object implementing interface <code>Runnable</code> is used
         * to create a thread, starting the thread causes the object's
         * <code>run</code> method to be called in that separately executing
         * thread.
         * <p>
         * The general contract of the method <code>run</code> is that it may
         * take any action whatsoever.
         *
         * @see Thread#run()
         */
        @Override
        public void run() {

            try {
                Response response;
                if (recipeID == null)
                    response = recipeSearchResponseCall(query, pageNumber).execute();
                else response = mRecipeResponseCall(recipeID).execute();

                if (cancelRequest) {
                    cancelRequest = false;
                    return;
                }

                if (response.code() == 200) { // response code successful...
                    if (recipeID == null) { // were searching for List of Recipes...
                        List<Recipe> list = new ArrayList<>((
                                (RecipeSearchResponse) response.body()).getRecipes());
                        if (list.size() > 0) {
                            if (pageNumber == 1) recipes.postValue(list); // if loading first page...
                        } else recipes.postValue(null);
                        if (list.size() > 0 && pageNumber > 1) {                                 // if loading Next Page...
                            Log.d(TAG, "run: reached A");
                            List<Recipe> currentRecipes = recipes.getValue();
                            if (list.size() > 0 && currentRecipes != null) {
                                currentRecipes.addAll(list);
                                recipes.postValue(currentRecipes);
                            } else {

                                Log.d(TAG, "run: recipes value: " + recipes.getValue().toString());
                            }
                        }
                        Log.e(TAG, "run: " + response.errorBody().string());
                    } else { // were searching for single recipe...
                        Recipe singleRecipe;
                        if (response.body() != null) {
                            singleRecipe = ((RecipeResponse) response.body()).getRecipe();
                            Log.d(TAG, "API call run: " + singleRecipe.getTitle());
                            mRecipe.postValue(singleRecipe);
                        }
                    }
                } else { // if response code is unsuccessful...
                    // if were searching for list of recipes
                    if (recipeID == null) recipes.postValue(null);
                        // if were searching for singe recipe
                    else mRecipe.postValue(null);
                    Log.d(TAG, "run: response.code() = " + response.code());
                }

            } catch (IOException e) {
                e.printStackTrace();
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

}
