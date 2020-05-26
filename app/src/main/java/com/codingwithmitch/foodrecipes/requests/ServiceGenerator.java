package com.codingwithmitch.foodrecipes.requests;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import static com.codingwithmitch.foodrecipes.util.Constants.BASE_URL;

// Retrofit object is built through Retrofit.Builder. calling create(modelClass) on it
// creates the modelClass object which can receive Response object from the API call

class ServiceGenerator {

    private static Retrofit.Builder retrofitBuilder =
            new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory
                            .create());

    private static Retrofit retrofit = retrofitBuilder.build();
    private static RecipeApi recipeApi = retrofit.create(RecipeApi.class);

    static RecipeApi getRecipeApi(){
        return  recipeApi;
    }
}