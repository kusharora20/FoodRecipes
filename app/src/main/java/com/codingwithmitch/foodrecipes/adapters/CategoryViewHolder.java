package com.codingwithmitch.foodrecipes.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codingwithmitch.foodrecipes.R;
import com.mikhaellopez.circularimageview.CircularImageView;

class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private OnRecipeListener mOnRecipeListener;
    private TextView title;
    CircularImageView mImageView;

    CategoryViewHolder(@NonNull View itemView, OnRecipeListener onRecipeListener) {
        super(itemView);
        this.mOnRecipeListener = onRecipeListener;
        mImageView = itemView.findViewById(R.id.image_recipe_category);
        title = itemView.findViewById(R.id.category_title);

        itemView.setOnClickListener(this);

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

        mOnRecipeListener.onCategoryClick(
                title.getText().toString());
    }

    // setter methods...

    void setTitle(String title) {
        this.title.setText(title);
    }



}
