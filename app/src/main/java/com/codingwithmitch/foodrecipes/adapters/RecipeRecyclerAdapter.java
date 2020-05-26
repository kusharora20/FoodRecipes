package com.codingwithmitch.foodrecipes.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codingwithmitch.foodrecipes.R;
import com.codingwithmitch.foodrecipes.models.Recipe;
import com.codingwithmitch.foodrecipes.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class RecipeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int RECIPE_TYPE = 1;
    private static final int LOADING_TYPE = 2;
    private static final int CATEGORY_TYPE = 3;
    public static final int EXHAUSTED_TYPE = 4;
    private static final int EXPIRED_TYPE = 5;
    private static final String TAG = "RecipeRecyclerAdapter";
    private List<Recipe> mRecipeList;
    private OnRecipeListener mOnRecipeListener;
    private boolean showQueryExhaustedOnceOnly;

    public RecipeRecyclerAdapter(OnRecipeListener onRecipeListener) {
        this.mOnRecipeListener = onRecipeListener;
        mRecipeList = new ArrayList<>();
    }

    /**
     * Called when RecyclerView needs a new {ViewHolder} of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(RecyclerView.ViewHolder, int, List)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder holder = null;
        View view;
        switch (viewType) {
            case RECIPE_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.layout_recipe_list_item,
                        parent,
                        false);
                return new RecipeViewHolder(view, mOnRecipeListener);

            case LOADING_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.layout_loading_list_item,
                        parent,
                        false);
                Log.d(TAG, "onCreateViewHolder: loading view type");
                return new LoadingViewHolder(view);

            case CATEGORY_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.layout_category_list_item,
                        parent,
                        false);
                return new CategoryViewHolder(view, mOnRecipeListener);

            case EXHAUSTED_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.layout_exhausted, parent, false);
                Log.d(TAG, "onCreateViewHolder: exhausted view type");
                return new ExhaustedViewHolder(view);

            case EXPIRED_TYPE:
                Log.d(TAG, "onCreateViewHolder: EXPIRED_TYPE");
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_exhausted, parent, false);
                Log.d(TAG, "onCreateViewHolder: Expired type");
                return new ExhaustedViewHolder(view);

            default:
                view = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.layout_recipe_list_item,
                        parent,
                        false);
                return new RecipeViewHolder(view, mOnRecipeListener);
        }
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {ViewHolder#itemView} to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {ViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p>
     * Override {onBindViewHolder(ViewHolder, int, List)} instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.ic_launcher_background);
        Context mContext = holder.itemView.getContext();

        // if recipes need to be displayed, then load up the recipes to the viewHolder,
        // else "LOADING" gets displayed and nothing needs to be done
        if (holder.getItemViewType() == RECIPE_TYPE) {
            RecipeViewHolder recipeViewHolder = (RecipeViewHolder) holder;

            recipeViewHolder.setTitle(mRecipeList.get(position).getTitle());
            recipeViewHolder.setPubisher(mRecipeList.get(position).getPublisher());
            recipeViewHolder.setSocialScore(String.valueOf(Math.round(mRecipeList.get(position).getSocial_rank())));

            Glide.with(mContext)
                    .setDefaultRequestOptions(requestOptions)
                    .load(mRecipeList.get(position)
                            .getImage_url())
                    .into(recipeViewHolder.image);
            Log.d(TAG, "onBindViewHolder: " + position + ": " + mRecipeList.get(position).getTitle());

        } else if (holder.getItemViewType() == CATEGORY_TYPE) {

            CategoryViewHolder categoryViewHolder = (CategoryViewHolder) holder;
            categoryViewHolder.setTitle(Constants.categories.get(position));
            Uri path = Uri.parse("android.resource://com.codingwithmitch.foodrecipes/drawable/"
                    + mRecipeList.get(position).getImage_url());
            Glide.with(mContext)
                    .applyDefaultRequestOptions(
                            requestOptions.centerCrop()
                                    .error(R.drawable.ic_launcher_background))
                    .load(path)

                    .into(categoryViewHolder.mImageView);
        } else if (holder.getItemViewType() == EXPIRED_TYPE) {
            ExhaustedViewHolder exhaustedViewHolder = (ExhaustedViewHolder) holder;
            exhaustedViewHolder.mTextView.setText(R.string.request_expired_message);
            Log.d(TAG, "onBindViewHolder: Expired type");
        }
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {

        return (mRecipeList == null) ? 0 : mRecipeList.size();

    }

    /**
     * (client for this method :: RecipeListActivity object in RecipeListActivity)
     * This method should to be called by client JUST BEFORE when search query has been initiated and
     * API call is downloading the query
     * If downloadingInProgress() == true, then "Loading..." gets displayed
     * when downloading completes, next line i.e. the API call is made.
     * When it completes, recipes (LiveData) is updated which notifies all observers i.e.
     * onChange method is called in the client which calls {@link RecipeRecyclerAdapter#setRecipes(List)}
     * which calls notifyDataSetChanged() which updates the RecyclerView with the new data
     */

    public void displayLoading() {
        if (downloadingInProgress()) {
            Recipe recipe = new Recipe();
            recipe.setTitle("LOADING...");
            List<Recipe> loadingList = new ArrayList<>();
            loadingList.add(recipe);
            mRecipeList = loadingList;
            notifyDataSetChanged();
        }
    }

    // if RecyclerView is currently displaying recipes, that means loading is in progress
    // and "LOADING..." needs to be displayed...
    // so we do a negative check and see if currently "LOADING..." is displayed and return false if so
    private boolean downloadingInProgress() {
//        if (mRecipeList != null)
        if (mRecipeList.size() > 0)
            return !mRecipeList.get(mRecipeList.size() - 1).getTitle()
                    .equals("LOADING...");
        return true;
    }

    // entry point when recipes are being searched by client
    // called immediately by client when recipes (LiveData) has finished updating from the API call...
    public void setRecipes(List<Recipe> recipes) {
        mRecipeList = recipes;
        notifyDataSetChanged();
    }

    // entry point when category is displayed
    public void displaySearchCategories() {
        List<Recipe> categories = new ArrayList<>();
        Recipe recipe;
        for (int i = 0; i < Constants.categories.size(); i++) {
            recipe = new Recipe();
            recipe.setTitle(Constants.categories.get(i));

            recipe.setSocial_rank(-1);
            recipe.setImage_url(Constants.categories.get(i));
            categories.add(recipe);
        }
        mRecipeList = categories;
        notifyDataSetChanged();
    }

    /**
     * this methods needs to be overridden when RecyclerView is displaying many ViewHolders.
     * the viewType integer gets written to the "mItemViewType" integer property of the viewHolder.
     * Hence, this is like a "setter" method for this property of viewHolder
     * This "mItemViewType" value gets returned on call to
     * viewHolder.getItemViewType() {@link RecyclerView.ViewHolder#getItemViewType()}
     * Hence this integer helps to uniquely identify a viewHolder in case there are several
     * <p>
     * Returns the view type of the item at <code>position</code> for the purposes
     * of view recycling.
     *
     * <p>The default implementation of this method returns 0, making the assumption of
     * a single view type for the adapter. Unlike ListView adapters, types need not
     * be contiguous. Consider using id resources to uniquely identify item view types.
     *
     * @param position position to query
     * @return integer value identifying the type of the view needed to represent the item at
     * <code>position</code>. Type codes need not be contiguous.
     */
    @Override
    public int getItemViewType(int position) {
        if (mRecipeList.get(position).getSocial_rank() == -1)
            return CATEGORY_TYPE;

        if (mRecipeList.get(position).getTitle().equals("LOADING..."))
            return LOADING_TYPE;

        if (mRecipeList.get(position).getTitle().equals("EXHAUSTED..."))
            return EXHAUSTED_TYPE;

        if (position == (getItemCount() - 1)
                && position != 0
                && !mRecipeList.get(position).getTitle().equals("EXHAUSTED..."))
            return LOADING_TYPE;

        if (mRecipeList.get(position).getTitle().equals("Expired"))
            return EXPIRED_TYPE;

        return RECIPE_TYPE;
    }

    public void displayQueryExhausted() {
        if(showQueryExhaustedOnceOnly) {
            Recipe recipe = new Recipe();
            recipe.setTitle("EXHAUSTED...");
            mRecipeList.add(recipe);
            notifyItemChanged(mRecipeList.size() - 1);
            Log.d(TAG, "displayQueryExhausted: recipe list size: " + mRecipeList.size());
            showQueryExhaustedOnceOnly = false;
        }
    }

    public void setShowQueryExhaustedOnceOnly(boolean showQueryExhaustedOnceOnly) {
        this.showQueryExhaustedOnceOnly = showQueryExhaustedOnceOnly;
    }

    public void requestExpired() {
        Recipe recipe = new Recipe();
        recipe.setTitle("Expired");
        List<Recipe> list = new ArrayList<>();
        list.add(recipe);
        mRecipeList = list;
        Log.d(TAG, "Expired type : requestExpired: called");
        notifyDataSetChanged();
    }

    public Recipe getSelectedRecipe(int position){
        if(mRecipeList != null){
            if(mRecipeList.size() > 0){
                return mRecipeList.get(position);
            }
        }
        return null;
    }

}