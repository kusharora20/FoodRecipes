package com.codingwithmitch.foodrecipes;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public abstract class BaseActivity extends AppCompatActivity {

    protected ProgressBar mProgressBar;

    // when derived classes call this method, firstly the activity layout gets inflated
    // next, the derived class's Activity Content is going to become child View of FrameLayout
    @Override
    public void setContentView(int layoutResID) {

        // the default activity layout (base_activity) gets inflated
        ConstraintLayout constraintLayout = (ConstraintLayout) getLayoutInflater().inflate(R.layout.activity_base, null);

        // the derived class's Activity Content is going to become child View of FrameLayout
        FrameLayout frameLayout = constraintLayout.findViewById(R.id.activity_content);
        mProgressBar = constraintLayout.findViewById(R.id.progress_bar);
        getLayoutInflater().inflate(layoutResID, frameLayout, true);

        // note: the derived class' activity lives inside the FrameLayout of the Base Activity
        super.setContentView(constraintLayout);

    }

    public void showProgressBar(boolean visibile) {

        mProgressBar.setVisibility(visibile ? View.VISIBLE : View.INVISIBLE);

    }

}
