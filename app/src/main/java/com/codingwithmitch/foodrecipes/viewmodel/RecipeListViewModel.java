package com.codingwithmitch.foodrecipes.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.codingwithmitch.foodrecipes.repositories.RecipeRepository;

/**
 * this class is server for RecipeListActivity and client for RecipeRepository
 * Its only job is to be a ViewModel for RecipeListActivity
 */

public class RecipeListViewModel extends AndroidViewModel {

    private static final String TAG = "RecipeListViewModel";
    private RecipeRepository mRepository;

    public enum ViewState {
        CATEGORIES, RECIPES, VIEWING_RECIPE_DETAILS;
    }
    private MutableLiveData<ViewState> viewState;

    RecipeListViewModel(@NonNull Application application) {
        super(application);
        init();
    }

    private void init() {
        Log.d(TAG, "RecipeListViewModel init: called");
        if(viewState == null){
            viewState = new MutableLiveData<>();
            viewState.setValue(ViewState.CATEGORIES);

        }
        mRepository = RecipeRepository.getInstance();
        Log.d(TAG, "init repository: " + mRepository);
    }

    public MutableLiveData<ViewState> getViewState() {
        return viewState;
    }


}
