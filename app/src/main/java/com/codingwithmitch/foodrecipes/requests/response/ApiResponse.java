package com.codingwithmitch.foodrecipes.requests.response;

import retrofit2.Response;

public class ApiResponse<T> {

    public ApiResponse<T> create(Response<T> response){

        if(response.isSuccessful()){

            T body = response.body();

            if(body==null || response.code() == 204) return new ApiEmptyResponse<>();
            else if(response.code() == 200) return new ApiSuccessResponse<>(body);


        }

        String errorMessage = "";
        try {
            errorMessage = response.errorBody().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ApiErrorResponse<>(errorMessage);

    }


    public class ApiSuccessResponse<E> extends ApiResponse<E>{

        private E body;

        private ApiSuccessResponse(E body) {
            this.body = body;
        }

        public E getBody(){
            return body;
        }

    }

    public class ApiEmptyResponse<E> extends ApiResponse<E>{}

    public class ApiErrorResponse<E> extends ApiResponse<E>{

        private String errorMessage;

        private ApiErrorResponse(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorMessage(){
            return errorMessage;
        }

    }




}
