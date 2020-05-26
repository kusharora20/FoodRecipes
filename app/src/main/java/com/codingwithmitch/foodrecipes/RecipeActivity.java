package com.codingwithmitch.foodrecipes;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateViewModelFactory;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.viewmodel.RecipeViewModel;

import static android.view.View.GONE;

public class RecipeActivity extends BaseActivity {

    private static final String TAG = "RecipeActivity";
    private Recipe mRecipe;

    private ImageView mImageView;
    private TextView title, socialRank;
    private LinearLayout ingredientsContainer;
    private ScrollView mScrollView;
    private RecipeViewModel mRecipeViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        // bind views...

        mImageView = findViewById(R.id.recipe_image2);
        title = findViewById(R.id.recipe_title2);
        socialRank = findViewById(R.id.recipe_social_score2);
        ingredientsContainer = findViewById(R.id.ingredients_container);
        mScrollView = findViewById(R.id.parent);

        // ViewModel...

        ViewModelProvider viewModelProvider = new ViewModelProvider(this);
        mRecipeViewModel = viewModelProvider.get(RecipeViewModel.class);

        super.showProgressBar(true);
        observeLiveData();
        mRecipeViewModel.searchSingleRecipe(getRecipeIDFromIntent());

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void observeLiveData() {
        /**
         * Observer which observes change in {@link com.codingwithmitch.foodrecipes.requests.RecipeApiClient#mRecipe}
         */
        mRecipeViewModel.getSingleRecipe().observe(this, new Observer<Recipe>() {
            @Override
            public void onChanged(Recipe recipe) {
                if (getRecipeIDFromIntent().equals(recipe.getRecipe_id())) {
                    mRecipe = recipe;
                    populateRecipeData();
                }
            }
        });

        /**
         * Observer which observes change in {@link com.codingwithmitch.foodrecipes.requests.RecipeApiClient#recipeRequestTimedOut}
         */

        mRecipeViewModel.getRecipeRequestTimedOut().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean requestTimedOut) {
//                Log.d(TAG, "onChanged: observe requestTimedOut: " + requestTimedOut);
                if (requestTimedOut) displayRequestTimeOutScreen();
            }
        });
    }

    public void displayRequestTimeOutScreen() {
        mImageView.setImageDrawable(getApplicationContext().getResources()
                .getDrawable(R.drawable.ic_launcher_background));
        title.setText(R.string.no_recipe_error_message);
        socialRank.setVisibility(GONE);
        ingredientsContainer.setVisibility(GONE);
        showProgressBar(false);
        mScrollView.setVisibility(View.VISIBLE);
    }

    private void populateRecipeData() {

        title.setText(mRecipe.getTitle());
        socialRank.setText(String.valueOf(Math.round(mRecipe.getSocial_rank())));

        RequestOptions requestOptions = RequestOptions
                .placeholderOf(R.drawable.ic_launcher_background);
        Glide.with(getApplicationContext())
                .setDefaultRequestOptions(requestOptions)
                .load(mRecipe.getImage_url())
                .into(mImageView);

        // add ingredients to TextView(s)
        for (String ingredient : mRecipe.getIngredients()) {
            TextView textView = new TextView(this);
            textView.setText(ingredient);
            textView.setTextSize(15);
            textView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ingredientsContainer.addView(textView);

        }

        showProgressBar(false);
        mScrollView.setVisibility(View.VISIBLE);

    }

    public String getRecipeIDFromIntent() {

        return getIntent().getStringExtra("recipe");
    }
}
