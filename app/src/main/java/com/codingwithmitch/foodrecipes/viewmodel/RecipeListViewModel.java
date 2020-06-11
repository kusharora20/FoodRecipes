package com.codingwithmitch.foodrecipes.viewmodel;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.repositories.RecipeRepository;
import com.codingwithmitch.foodrecipes.util.Resource;

import java.util.List;

/**
 * this class is server for RecipeListActivity and client for RecipeRepository
 * Its only job is to be a ViewModel for RecipeListActivity
 */

public class RecipeListViewModel extends AndroidViewModel {

    public enum ViewState {
        CATEGORIES, RECIPES, VIEWING_RECIPE_DETAILS;
    }

    private static final String TAG = "RecipeListViewModel";
    private RecipeRepository mRepository;
    private MutableLiveData<ViewState> viewState;
    private int pageNumber = 1;
    private String categorySelected;

    RecipeListViewModel(@NonNull Application application) {
        super(application);
        mRepository = RecipeRepository.getInstance(application.getApplicationContext());
        init();
    }

    private void init() {
        Log.d(TAG, "RecipeListViewModel init: called");
        if(viewState == null){
            viewState = new MutableLiveData<>();
            viewState.setValue(ViewState.CATEGORIES);

        }
        Log.d(TAG, "init repository: " + mRepository);
    }
    
    public void searchRecipes(String category){
        Log.d(TAG, "searchRecipes: " + category);
        this.categorySelected = category;
        mRepository.searchRecipesApi(categorySelected, pageNumber);
    }
    
    public void loadMoreRecipes(){
        pageNumber++;
        searchRecipes(categorySelected);
    }

    public MutableLiveData<ViewState> getViewState() {
        return viewState;
    }

    public LiveData<Resource<List<Recipe>>> getRecipes() {
        return mRepository.getRecipes();
    }

    public LiveData<Resource<Recipe>> getSingleRecipe() {
        return mRepository.getSingleRecipe();
    }

    public void setViewState(ViewState viewState) {
        this.viewState.setValue(viewState);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "onCleared: mRepository == null: " + (mRepository == null));
    }
}
