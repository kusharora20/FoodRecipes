package com.codingwithmitch.foodrecipes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codingwithmitch.foodrecipes.adapters.OnRecipeListener;
import com.codingwithmitch.foodrecipes.adapters.RecipeRecyclerAdapter;
import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.requests.RecipeApi;
import com.codingwithmitch.foodrecipes.util.OnBackPressed;
import com.codingwithmitch.foodrecipes.util.VerticalSpacingItemDecorator;
import com.codingwithmitch.foodrecipes.viewmodel.RecipeListViewModel;

import java.util.List;

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
        ViewModelProvider viewModelProvider = new ViewModelProvider(this);
        recipeListViewModel = viewModelProvider.get(RecipeListViewModel.class);

        initRecycler();
        subscribeObservers();
        initSearchView();

        if (!recipeListViewModel.isViewingRecipes())
            displaySearchCategories();

        Log.d(TAG, "onCreate: called");
        setSupportActionBar(findViewById(R.id.toolbar));
    }

    // Create an Observer object associated with this Activity to subscribe to the LiveData
    // when LiveData updates, this Observer object's onChanged() method is called
    // which also calls the setRecipes() method in RecipeRecyclerAdapter. The latter has
    // notifyDataSetChanged() within its body and thus notifies RecyclerView (which is an Observer)
    // to refresh itself
    private void subscribeObservers() {
        recipeListViewModel.getRecipes().observe(this,
                (List<Recipe> recipes) -> {
                    if (recipes != null)
                        mRecipeRecyclerAdapter.setRecipes(recipeListViewModel.getRecipes().getValue());
                    else mRecipeRecyclerAdapter.requestExpired();
                });

        recipeListViewModel.getSearchTimedOut().observe(this,
                (Boolean timedOut) -> {
                    if (timedOut) mRecipeRecyclerAdapter.requestExpired();
                }
        );

        recipeListViewModel.isExhausted().observe(this,
                (Boolean isExhausted) -> {
                    mRecipeRecyclerAdapter.displayQueryExhausted();
                    mRecipeRecyclerAdapter.setShowQueryExhaustedOnceOnly(false);
                }
        );
    }

    // searchRecipesApi method calls methods of its namesake across following classes and in order mentioned:
    // RecipeListActvity >>> RecipeListViewModel >>> RecipeRepository >>> RecipeApiClient
    // and passes the search String. PageNumber is a state of Activity, hence stored in ViewModel
    private void searchRecipesApi(String query) {
        mRecipeRecyclerAdapter.setShowQueryExhaustedOnceOnly(true);
        this.query = query;
        mRecipeRecyclerAdapter.displayLoading();
        recipeListViewModel.searchRecipesApi(query);
    }

    // initializer RecyclerView
    private void initRecycler() {
        mRecipeRecyclerAdapter = new RecipeRecyclerAdapter(this);
        mRecyclerView.setAdapter(mRecipeRecyclerAdapter);
        mRecyclerView.addItemDecoration(new VerticalSpacingItemDecorator(30));

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            /**
             * Callback method to be invoked when RecyclerView's scroll state changes.
             *
             * @param recyclerView The RecyclerView whose scroll state has changed.
             * @param newState     The updated scroll state. One of {SCROLL_STATE_IDLE},
             *                     {SCROLL_STATE_DRAGGING} or {SCROLL_STATE_SETTLING}.
             */
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (recipeListViewModel.isViewingRecipes())
                    if (!recyclerView.canScrollVertically(1)) {
                        Log.d(TAG, "onScrollStateChanged: reached");
                        List<Recipe> recipes = recipeListViewModel.getRecipes().getValue();
                        if (recipes != null) recipeListViewModel.loadNextPage(query);
                    }
            }
        });
    }

    // initializes SearchView in Toolbar
    private void initSearchView() {
        searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchRecipesApi(query);
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
        if ((recipeID = recipeListViewModel.getRecipes().getValue().get(position).getRecipe_id()) != null)
            recipeID = recipeListViewModel.getRecipes().getValue().get(position).getRecipe_id();
        Intent intent = new Intent(this, RecipeActivity.class)
                .putExtra("recipe", recipeID);
        startActivity(intent);
//        Toast.makeText(this, "Recipe item clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCategoryClick(String category) {
        recipeListViewModel.setIsViewingRecipes(true);
        searchRecipesApi(category);
    }

    public void displaySearchCategories() {
        recipeListViewModel.setIsViewingRecipes(false);
        mRecipeRecyclerAdapter.displaySearchCategories();
    }

    /**
     * Called when the activity has detected the user's press of the back
     * key. The {@link #getOnBackPressedDispatcher() OnBackPressedDispatcher} will be given a
     * chance to handle the back button before the default behavior of
     * {@link Activity#onBackPressed()} is invoked.
     *
     * @see #getOnBackPressedDispatcher()
     */
    @Override
    public void onBackPressed() {

        OnBackPressed onBackPressed = recipeListViewModel.onBackPressed();
        if (onBackPressed == OnBackPressed.VIEWING_RECIPES) {
            if (recipeListViewModel.isPerformingQuery()) {
                recipeListViewModel.cancelSearchOperation();
                Log.d(TAG, "onBackPressed: isPerformingQuery = true");
            }
            backToCategoryAndClearSearch();
        } else {
            super.onBackPressed();
        }
    }

    // takes screen back to category and clears the searchView in Toolbar
    private void backToCategoryAndClearSearch() {
        searchView.setQuery("", false);
        searchView.setIconified(true);
        displaySearchCategories();
    }

    /**
     * Initialize the contents of the Activity's standard options menu.  You
     * should place your menu items in to <var>menu</var>.
     *
     * <p>This is only called once, the first time the options menu is
     * displayed.  To update the menu every time it is displayed, see
     * {@link #onPrepareOptionsMenu}.
     *
     * <p>The default implementation populates the menu with standard system
     * menu items.  These are placed in the {@link Menu#CATEGORY_SYSTEM} group so that
     * they will be correctly ordered with application-defined menu items.
     * Deriving classes should always call through to the base implementation.
     *
     * <p>You can safely hold on to <var>menu</var> (and any items created
     * from it), making modifications to it as desired, until the next
     * time onCreateOptionsMenu() is called.
     *
     * <p>When you add items to the menu, you can implement the Activity's
     * {@link #onOptionsItemSelected} method to handle them there.
     *
     * @param menu The options menu in which you place your items.
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.recipe_search_menu, menu);

        return true;

    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen (calling the item's Runnable or sending a message to
     * its Handler as appropriate).  You can use this method for any items
     * for which you would like to do processing without those other
     * facilities.
     *
     * <p>Derived classes should call through to the base class for it to
     * perform the default menu handling.</p>
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: reached A");
        if (item.getItemId() == R.id.action_categories) {
            Log.d(TAG, "onOptionsItemSelected: reached B");
            backToCategoryAndClearSearch();
        }
        return super.onOptionsItemSelected(item);
    }
}
