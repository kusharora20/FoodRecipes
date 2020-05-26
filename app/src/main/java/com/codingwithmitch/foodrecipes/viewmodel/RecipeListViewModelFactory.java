package com.codingwithmitch.foodrecipes.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class RecipeListViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    private Application application;
    /**
     * Creates a {@code AndroidViewModelFactory}
     *
     * @param application an application to pass in {AndroidViewModel}
     */
    public RecipeListViewModelFactory(@NonNull Application application) {
        super(application);
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new RecipeListViewModel(application);
    }
}
