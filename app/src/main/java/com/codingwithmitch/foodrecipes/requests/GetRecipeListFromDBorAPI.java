package com.codingwithmitch.foodrecipes.requests;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.codingwithmitch.foodrecipes.db.AppDatabase;
import com.codingwithmitch.foodrecipes.db.RecipeDAO;
import com.codingwithmitch.foodrecipes.db.RecipeDBmodel;
import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.util.AppExecutors;
import com.codingwithmitch.foodrecipes.util.Converters;
import com.codingwithmitch.foodrecipes.util.NetworkBoundResource;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public final class GetRecipeListFromDBorAPI extends NetworkBoundResource<String, List<Recipe>> {

    private static final String TAG = "GetRecipeListFromDBorAP";
    private static RecipeDAO recipeDAO;
    private int page = 1;
    private String searchString;
    private static GetRecipeListFromDBorAPI instance;
    private List<String> list;

    private List<Recipe> resultOfApiCall;
    private MediatorLiveData<List<Recipe>> mListMediatorLiveData;

    public static GetRecipeListFromDBorAPI search(Context context, String searchQuery, int pageNumber) {
        recipeDAO = (recipeDAO == null) ?
                AppDatabase.get(context.getApplicationContext()).getRecipeDAO() : recipeDAO;
        if (instance == null)
            instance = new GetRecipeListFromDBorAPI(context, searchQuery, pageNumber);

        else {
            instance.searchString = searchQuery;
            instance.page = pageNumber;
        }
        instance.searchDBorMakeAPIcall(searchQuery);
        return instance;
    }

    private GetRecipeListFromDBorAPI(Context context, String searchQuery, int pageNumber) {
        super(context);
        this.searchString = searchQuery;
        this.page = pageNumber;
    }

    // TODO: to implement method in background thread
    /**
     * @param searchQuery
     * @return true if data must be fetched from API call, false if data must be fetched from DB
     */
    @Override
    protected boolean checkToFetchFromAPIorDB(String searchQuery) {
        return true;

//        List<String> list = recipeDAO.searchIfRecipesAvailable(instance.searchString, instance.page);
//        return !(list != null && list.size() > ((instance.page - 1) * 30));
    }

    /**
     * makes API call to get data from API
     * @param searchQuery
     * @return API call result as LiveData
     */
    @Override
    protected LiveData<Response<List<Recipe>>> makeAPIcall(String searchQuery) {
        LiveData<Response<List<Recipe>>> liveData =
                RecipeApiClient.getInstance().makeAPIcall(instance.searchString, instance.page);

        new MediatorLiveData<>().addSource(liveData, (Response<List<Recipe>> response) ->{
            Log.d(TAG, "makeAPIcall: called");
        });

        return liveData;
    }

    @Override
    protected MediatorLiveData<List<Recipe>> loadFromDB() {
        mListMediatorLiveData = new MediatorLiveData<>();

        LoadFromDB runnable = new LoadFromDB();
        AppExecutors.getInstance().diskIO().submit(runnable);
        return mListMediatorLiveData;
    }

    /**
     * saves result from API call to DB in background thread
     *
     * @param resultOfApiCall
     */
    @Override
    protected void saveDataToDB(List<Recipe> resultOfApiCall) {
        this.resultOfApiCall = resultOfApiCall;
        SaveToDB runnable = new SaveToDB();
        AppExecutors.getInstance().diskIO().submit(runnable);
    }

    /**
     * @param returnedData
     * @return check if returned data is null
     */
    @Override
    protected boolean dataIsNull(List<Recipe> returnedData) {
        return returnedData != null && returnedData.size() > 0;
    }

    private class LoadFromDB implements Runnable {

        @Override
        public void run() {
            List<RecipeDBmodel> list = recipeDAO.searchRecipes(instance.searchString, instance.page);
            List<Recipe> recipeList = new ArrayList<>();
            Recipe recipe = new Recipe();

            list.forEach((RecipeDBmodel recipeDBmodel) -> {
                recipe.setTitle(recipeDBmodel.getTitle());
                recipe.setRecipe_id(recipeDBmodel.getRecipe_id());
                recipe.setPublisher(recipeDBmodel.getPublisher());
                recipe.setIngredients(recipeDBmodel.getIngredients());
                recipe.setImageAsBytes(recipeDBmodel.getImageAsBytes());
                recipe.setSocial_rank(recipeDBmodel.getSocial_rank());
                recipe.setImage_url(null);

                recipeList.add(recipe);

            });

            mListMediatorLiveData.postValue(recipeList);

        }
    }

    private class SaveToDB implements Runnable {
        RecipeDBmodel recipeDBmodel = new RecipeDBmodel();

        @Override
        public void run() {
            resultOfApiCall.forEach(
                    (Recipe recipe) -> {
                        if (recipeDAO.getRecipeId(recipe.getRecipe_id()) == null) {
                            recipeDBmodel.setImageAsBytes(Converters.convertImageToByteArray(recipe.getImage_url()));
                            recipeDBmodel.setTitle(recipe.getTitle());
                            recipeDBmodel.setTimeStamp(System.currentTimeMillis());
                            recipeDBmodel.setSocial_rank(recipe.getSocial_rank());
                            recipeDBmodel.setRecipe_id(recipe.getRecipe_id());
                            recipeDBmodel.setPublisher(recipe.getPublisher());
                            recipeDBmodel.setIngredients(recipe.getIngredients());

                            recipeDAO.insertRecipe(recipeDBmodel);
                        }
                    }
            );
        }
    }

    private class CheckToFetchFromAPIorDBRunnable implements Runnable{

        @Override
        public void run() {
            list = recipeDAO.searchIfRecipesAvailable(instance.searchString, instance.page);
        }
    }


}
