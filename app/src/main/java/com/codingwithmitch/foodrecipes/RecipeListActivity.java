package com.codingwithmitch.foodrecipes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codingwithmitch.foodrecipes.adapters.OnRecipeListener;
import com.codingwithmitch.foodrecipes.adapters.RecipeRecyclerAdapter;
import com.codingwithmitch.foodrecipes.util.VerticalSpacingItemDecorator;
import com.codingwithmitch.foodrecipes.viewmodel.RecipeListViewModel;
import com.codingwithmitch.foodrecipes.viewmodel.RecipeListViewModelFactory;

/**
 * This app implements MVVM in the following way:
 * View: {@link RecipeListActivity} (client for RecipeListViewModel)
 * ViewModel: {@link RecipeListViewModel} (client for RecipeRepository)
 * Model: {@link com.codingwithmitch.foodrecipes.repositories.RecipeRepository}; (Repository; client for RecipeApiClient)
 * {@link com.codingwithmitch.foodrecipes.requests.RecipeApiClient} (API request which retrieves data through Retrofit)
 */
public class RecipeListActivity extends BaseActivity implements OnRecipeListener {

    private static final String TAG = "RecipeListActivity";
    private RecipeListViewModel recipeListViewModel;
    private RecyclerView mRecyclerView;
    private RecipeRecyclerAdapter mRecipeRecyclerAdapter;
    private androidx.appcompat.widget.SearchView searchView;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        mRecyclerView = findViewById(R.id.recipe_list);

//        ViewModelProvider viewModelProvider = new ViewModelProvider(this);
        recipeListViewModel = new ViewModelProvider(this, new RecipeListViewModelFactory(this.getApplication())).get(RecipeListViewModel.class);

        initRecycler();
        subscribeObservers();
        initSearchView();
        setSupportActionBar(findViewById(R.id.toolbar));
    }

    private void subscribeObservers() {
        // observes change of viewState LiveData in RecipeListViewModel class
        recipeListViewModel.getViewState().observe(this,
                (RecipeListViewModel.ViewState viewState) -> {
                    if (viewState != null) switch (viewState) {
                        case CATEGORIES:
                            displaySearchCategories();
                        case RECIPES:
                            // viewing recipes
                    }
                });
    }

    // initializer RecyclerView
    private void initRecycler() {
        mRecipeRecyclerAdapter = new RecipeRecyclerAdapter(this);
        mRecyclerView.setAdapter(mRecipeRecyclerAdapter);
        mRecyclerView.addItemDecoration(new VerticalSpacingItemDecorator(30));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    // initializes SearchView in Toolbar
    private void initSearchView() {
        searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public void onRecipeClick(int position) {

        String recipeID;
        if ((recipeID = mRecipeRecyclerAdapter.getSelectedRecipe(position).getRecipe_id()) != null)
            recipeID = mRecipeRecyclerAdapter.getSelectedRecipe(position).getRecipe_id();
        Intent intent = new Intent(this, RecipeActivity.class)
                .putExtra("recipe", recipeID);
        startActivity(intent);
//        Toast.makeText(this, "Recipe item clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCategoryClick(String category) {

    }

    public void displaySearchCategories() {
        mRecipeRecyclerAdapter.displaySearchCategories();
    }

}
