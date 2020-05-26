package com.codingwithmitch.foodrecipes.adapters;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codingwithmitch.foodrecipes.R;
import com.codingwithmitch.foodrecipes.util.HorizontalDottedProgress;

 class LoadingViewHolder extends RecyclerView.ViewHolder {

    private HorizontalDottedProgress mHorizontalDottedProgress;

    LoadingViewHolder(@NonNull View itemView) {
        super(itemView);
        mHorizontalDottedProgress = itemView.findViewById(R.id.progress_dotted_horizontal);

    }

 }
