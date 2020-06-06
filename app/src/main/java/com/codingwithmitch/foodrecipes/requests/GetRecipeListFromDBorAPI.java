package com.codingwithmitch.foodrecipes.requests;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.codingwithmitch.foodrecipes.db.AppDatabase;
import com.codingwithmitch.foodrecipes.db.RecipeDAO;
import com.codingwithmitch.foodrecipes.db.RecipeDBmodel;
import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.util.AppExecutors;
import com.codingwithmitch.foodrecipes.util.ImageAsBytes;
import com.codingwithmitch.foodrecipes.util.NetworkBoundResource;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class GetRecipeListFromDBorAPI extends NetworkBoundResource<String, List<Recipe>> {

    private static RecipeDAO recipeDAO;
    private static int page = 1;
    private List<Recipe> resultOfApiCall;
    private static String searchString;
    private MediatorLiveData<List<Recipe>> mListMediatorLiveData;

    private GetRecipeListFromDBorAPI(Context context, String searchQuery) {
        super(context, searchQuery);
    }

    public static GetRecipeListFromDBorAPI search(Context context, String searchQuery, int pageNumber){
        recipeDAO = AppDatabase.get(context.getApplicationContext()).getRecipeDAO();
        page = pageNumber;
        searchString = searchQuery;
        GetRecipeListFromDBorAPI instance = new GetRecipeListFromDBorAPI(context, searchQuery);
        return instance;
    }

    /**
     * @param searchQuery
     * @return true if data must be fetched from API call, false if data must be fetched from DB
     */
    @Override
    protected boolean checkToFetchFromAPIorDB(String searchQuery) {

        List<RecipeDBmodel> list = recipeDAO.searchIfRecipesAvailable(searchQuery, 1);
        return !(list!=null && list.size()>0);
    }

    /**
     * makes API call to get data from API
     *
     * @param searchQuery
     * @return API call result as LiveData
     */
    @Override
    protected LiveData<Response<List<Recipe>>> makeAPIcall(String searchQuery) {

        return RecipeApiClient.getInstance().makeAPIcall(searchQuery, page);

    }

    @Override
    protected MediatorLiveData<List<Recipe>> loadFromDB(String searchQuery) {
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
        return returnedData!=null && returnedData.size() > 0;
    }


    private class LoadFromDB implements Runnable{

        @Override
        public void run() {
            List<RecipeDBmodel> list = recipeDAO.searchRecipes(searchString, page);
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

    private class SaveToDB implements Runnable{
        RecipeDBmodel recipeDBmodel = new RecipeDBmodel();

        @Override
        public void run() {
            resultOfApiCall.forEach((Recipe recipe) -> {
                recipeDBmodel.setImageAsBytes(ImageAsBytes.convertImageToByteArray(recipe.getImage_url()));
                recipeDBmodel.setTitle(recipe.getTitle());
                recipeDBmodel.setTimeStampInSeconds(System.currentTimeMillis());
                recipeDBmodel.setSocial_rank(recipe.getSocial_rank());
                recipeDBmodel.setRecipe_id(recipe.getRecipe_id());
                recipeDBmodel.setIngredients(recipe.getIngredients());
                recipeDBmodel.setPublisher(recipe.getPublisher());
                recipeDAO.insertRecipe(recipeDBmodel);
            });
        }
    }

}
