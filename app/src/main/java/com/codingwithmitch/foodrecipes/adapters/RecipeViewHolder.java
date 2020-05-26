package com.codingwithmitch.foodrecipes.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.codingwithmitch.foodrecipes.R;

/**
 * Creates RecyclerView.ViewHolder object for the RecyclerView' onCreateViewHolder method
 * to return
 */

class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private OnRecipeListener mOnRecipeListener;
    private TextView title, pubisher, socialScore;
    ImageView image;

    RecipeViewHolder(@NonNull View itemView, OnRecipeListener onRecipeListener) {
        super(itemView);
        this.mOnRecipeListener = onRecipeListener;
        this.title = itemView.findViewById(R.id.recipe_title2);
        this.pubisher = itemView.findViewById(R.id.recipe_publisher);
        this.image = itemView.findViewById(R.id.recipe_image2);
        this.socialScore = itemView.findViewById(R.id.recipe_social_score2);

        itemView.setOnClickListener(this);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        mOnRecipeListener.onRecipeClick(getAdapterPosition());
    }

    void setTitle(String title) {
        this.title.setText(title);
    }

    void setPubisher(String pubisher) {
        this.pubisher.setText(pubisher);
    }

    void setSocialScore(String socialScore) {
        this.socialScore.setText(socialScore);
    }
}
