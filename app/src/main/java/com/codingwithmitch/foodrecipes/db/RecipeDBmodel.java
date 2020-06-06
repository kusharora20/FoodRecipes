package com.codingwithmitch.foodrecipes.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recipes")
public class RecipeDBmodel {

    @NonNull @PrimaryKey
    private String recipe_id;

    private String title;
    private String publisher;
    private byte[] imageAsBytes;
    private float social_rank;
    private String ingredients[];
    private long timeStamp;

    public RecipeDBmodel(){

    }

    @NonNull
    public String getRecipe_id() {
        return recipe_id;
    }

    public void setRecipe_id(@NonNull String recipe_id) {
        this.recipe_id = recipe_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public byte[] getImageAsBytes() {
        return imageAsBytes;
    }

    public void setImageAsBytes(byte[] imageAsBytes) {
        this.imageAsBytes = imageAsBytes;
    }

    public float getSocial_rank() {
        return social_rank;
    }

    public void setSocial_rank(float social_rank) {
        this.social_rank = social_rank;
    }

    public String[] getIngredients() {
        return ingredients;
    }

    public void setIngredients(String[] ingredients) {
        this.ingredients = ingredients;
    }

    public long getTimeStampInSeconds() {
        return timeStamp;
    }

    public void setTimeStampInSeconds(long currentTimeInMillis) {
        this.timeStamp = currentTimeInMillis/1000;
    }
}
