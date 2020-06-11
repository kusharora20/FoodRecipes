package com.codingwithmitch.foodrecipes.requests;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.codingwithmitch.foodrecipes.db.AppDatabase;
import com.codingwithmitch.foodrecipes.db.RecipeDAO;
import com.codingwithmitch.foodrecipes.db.RecipeDBmodel;
import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.util.AppExecutors;
import com.codingwithmitch.foodrecipes.util.Converters;
import com.codingwithmitch.foodrecipes.util.NetworkBoundResource;

import retrofit2.Response;

/**
 * To use this class, write :
 * GetRecipeFromDBorAPI getRecipeFromDBorAPI = new GetRecipeFromDBorAPI();
 * GetRecipeFromDBorAPI.setContext(context).search(recipeID).getResultAsLiveData();
 * LiveData result =
 */

public final class GetRecipeFromDBorAPI extends NetworkBoundResource<String, Recipe> {

    private static final String TAG = "GetRecipeFromDBorAPI";
    private static RecipeDAO recipeDAO;
    private String searchQuery;
    private static GetRecipeFromDBorAPI instance;

    private RecipeDBmodel recipeDBmodel;
    private Recipe resultOfApiCall;
    private MediatorLiveData<Recipe> recipeLiveData;

    public static GetRecipeFromDBorAPI search(String recipeID, Context context) {
        recipeDAO = (recipeDAO == null) ?
                AppDatabase.get(context.getApplicationContext()).getRecipeDAO() : recipeDAO;

        if (instance == null) {
            instance = new GetRecipeFromDBorAPI(context, recipeID);
        } else instance.searchQuery = recipeID;

        instance.searchDBorMakeAPIcall(recipeID);
        return instance;
    }

    private GetRecipeFromDBorAPI(Context context, String recipeID) {
        super(context);
        this.searchQuery = recipeID;
    }

    /**
     * @param recipeID
     * @return true if data must be fetched from API call, false if data must be fetched from DB
     */
    @Override
    protected boolean checkToFetchFromAPIorDB(String recipeID) {

        return !(recipeDAO.getRecipeId(recipeID) == null);
    }

    /**
     * makes API call to get data from API
     *
     * @param searchQuery
     * @return API call result as LiveData
     */
    @Override
    protected LiveData<Response<Recipe>> makeAPIcall(String searchQuery) {

        return RecipeApiClient.getInstance().makeAPIcall(instance.searchQuery);

    }

    /**
     * @return data from DB as observable LiveData
     */
    @Override
    protected MediatorLiveData<Recipe> loadFromDB() {

        recipeLiveData = new MediatorLiveData<>();
        LoadFromDB runnable = new LoadFromDB();
        AppExecutors.getInstance().diskIO().submit(runnable);
        return recipeLiveData;
    }

    /**
     * saves result from API call to DB in background thread
     *
     * @param resultOfApiCall
     */
    @Override
    protected void saveDataToDB(Recipe resultOfApiCall) {

        this.resultOfApiCall = resultOfApiCall;

        // save data to DB in background thread
        SaveDataToDB saveDataToDB = new SaveDataToDB();
        AppExecutors.getInstance().diskIO().submit(saveDataToDB);

    }

    /**
     * @param returnedData
     * @return check if returned data is null
     */
    @Override
    protected boolean dataIsNull(Recipe returnedData) {
        return returnedData == null;
    }

    private class SaveDataToDB implements Runnable {
        @Override
        public void run() {

            recipeDBmodel = new RecipeDBmodel();
//            recipeDBmodel.setIngredients(resultOfApiCall.getIngredients());
            recipeDBmodel.setPublisher(resultOfApiCall.getPublisher());
            recipeDBmodel.setRecipe_id(resultOfApiCall.getRecipe_id());
            recipeDBmodel.setSocial_rank(resultOfApiCall.getSocial_rank());
            recipeDBmodel.setTimeStamp(System.currentTimeMillis());
            recipeDBmodel.setTitle(resultOfApiCall.getTitle());

            byte[] array = Converters.convertImageToByteArray(resultOfApiCall.getImage_url());

            recipeDBmodel.setImageAsBytes(array);
        }
    }

    private class LoadFromDB implements Runnable {

        @Override
        public void run() {
            recipeDBmodel = recipeDAO.getRecipe(instance.searchQuery);
            Recipe recipe = new Recipe();

            recipe.setTitle(recipeDBmodel.getTitle());
//            recipe.setIngredients(recipeDBmodel.getIngredients());
            recipe.setSocial_rank(recipeDBmodel.getSocial_rank());
            recipe.setPublisher(recipeDBmodel.getPublisher());
            recipe.setRecipe_id(recipeDBmodel.getRecipe_id());
            recipe.setImageAsBytes(recipeDBmodel.getImageAsBytes());
            recipeLiveData.setValue(recipe);
        }
    }
}