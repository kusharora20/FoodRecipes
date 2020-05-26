package com.codingwithmitch.foodrecipes.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

/**
 * this class is server for RecipeListActivity and client for RecipeRepository
 * Its only job is to be a ViewModel for RecipeListActivity
 */

public class RecipeListViewModel extends AndroidViewModel {

    private static final String TAG = "RecipeListViewModel";

    public enum ViewState {
        CATEGORIES, RECIPES, VIEWING_RECIPE_DETAILS;
    }
    private MutableLiveData<ViewState> viewState;

    public RecipeListViewModel(@NonNull Application application) {
        super(application);
        init();
    }

    private void init() {
        if(viewState == null){
            viewState = new MutableLiveData<>();
            viewState.setValue(ViewState.CATEGORIES);

        }
    }

    public MutableLiveData<ViewState> getViewState() {
        return viewState;
    }
}
