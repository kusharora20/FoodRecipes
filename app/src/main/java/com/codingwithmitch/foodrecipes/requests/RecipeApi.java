package com.codingwithmitch.foodrecipes.requests;

import com.codingwithmitch.foodrecipes.requests.response.RecipeResponse;
import com.codingwithmitch.foodrecipes.requests.response.RecipeSearchResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RecipeApi {

    // Get recipe list
    @GET("api/search")
    Call<RecipeSearchResponse> searchRecipe(
            @Query("key") String key,
            @Query("q") String query,
            @Query("page") int page);

    // Get recipe request
    @GET("api/get")
    Call<RecipeResponse> getRecipe(
            @Query("key") String key,
            @Query("rId") String recipe_id);


}
