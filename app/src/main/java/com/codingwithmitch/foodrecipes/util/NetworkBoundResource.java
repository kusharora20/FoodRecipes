package com.codingwithmitch.foodrecipes.util;

import android.content.Context;
import android.util.Log;

import androidx.annotation.UiThread;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import retrofit2.Response;

public abstract class NetworkBoundResource<SearchType, ResultType> {

    private static final String TAG = "NetworkBoundResource";
    private MutableLiveData<Resource<ResultType>> resultAsLiveData;
    private MediatorLiveData<ResultType> mediatorResult;
    private Context mContext;

    public NetworkBoundResource(Context context) {
        this.mContext = context;
        resultAsLiveData = new MutableLiveData<>();
        mediatorResult = new MediatorLiveData<>();
//        searchDBorMakeAPIcall(searchQuery);
    }

    public void searchDBorMakeAPIcall(SearchType searchQuery) {

        resultAsLiveData.setValue(Resource.loading());

        if (searchQuery != null) {
            if (checkToFetchFromAPIorDB(searchQuery)) {
                Log.d(TAG, "searchDBorMakeAPIcall: " + searchQuery);
                fetchFromNetwork(searchQuery);
            }
            // search DB and set data to it; notify observer (Activity) to update UI
            else {
                MediatorLiveData<ResultType> mediatorLiveData = loadFromDB();

                mediatorResult.addSource(mediatorLiveData, (ResultType resultFromDB) -> {
                    mediatorResult.removeSource(mediatorLiveData);
                    resultAsLiveData.setValue(Resource.success(resultFromDB));
                });
            }
        }
    }

    // makeAPIcall and subscribe to the livedata result
    // if result !=null save to DB,
    // notify observer (Activity) to update UI
    private void fetchFromNetwork(SearchType searchQuery) {

        //make API call & subscribe to LiveData result
        LiveData<Response<ResultType>> responseLiveData = makeAPIcall(searchQuery);

        mediatorResult.addSource(responseLiveData, (Response<ResultType> apiResponse) -> {

                    Resource<ResultType> resultTypeResource = null;
                    Log.d(TAG, "fetchFromNetwork: apiResponse.code() = " + apiResponse.code());
                    if (apiResponse.isSuccessful()) {
                        if (apiResponse.code() == 200 && apiResponse.body() != null) {
                            Log.d(TAG, "fetchFromNetwork: response.code() = 200");
                            saveDataToDB(apiResponse.body());
                            resultTypeResource = Resource.success(apiResponse.body());
                        } else if (apiResponse.code() == 204 || apiResponse.body() == null) {
                            resultTypeResource = Resource.empty();
                        }
                    } else {
                        resultTypeResource = Resource.error(apiResponse.message());
                    }
                    resultAsLiveData.setValue(resultTypeResource);
                    mediatorResult.removeSource(responseLiveData);
                }
        );

        Log.d(TAG, "fetchFromNetwork: mediatorResult.hasActiveObservers(): " + mediatorResult.hasActiveObservers());
    }

    /**
     * @param searchQuery
     * @return true if data must be fetched from API call, false if data must be fetched from DB
     */
    protected abstract boolean checkToFetchFromAPIorDB(SearchType searchQuery);

    /**
     * makes API call to get data from API
     *
     * @param searchQuery
     * @return API call result as LiveData
     */
    protected abstract LiveData<Response<ResultType>> makeAPIcall(SearchType searchQuery);

    /**
     * @return data from DB as observable LiveData
     */
    @UiThread
    protected abstract MediatorLiveData<ResultType> loadFromDB();

    /**
     * saves result from API call to DB in background thread
     */
    @WorkerThread
    protected abstract void saveDataToDB(ResultType resultOfApiCall);

    /**
     * @param returnedData
     * @return check if returned data is null
     */
    protected abstract boolean dataIsNull(ResultType returnedData);

    // returns result as LiveData
    public LiveData<Resource<ResultType>> getResultAsLiveData() {
        return resultAsLiveData;
    }

    public Context getContext() {
        return mContext;
    }
}