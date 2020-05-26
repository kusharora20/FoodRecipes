package com.codingwithmitch.foodrecipes.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codingwithmitch.foodrecipes.R;

public class ExhaustedViewHolder extends RecyclerView.ViewHolder {

    TextView mTextView;
    public ExhaustedViewHolder(@NonNull View itemView) {
        super(itemView);
        mTextView = itemView.findViewById(R.id.error_message);
    }



}
