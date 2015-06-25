package com.votapp.fede.votapp.events;

import retrofit.RetrofitError;

/**
 * Created by fede on 21/5/15.
 */
public class LoadedErrorEvent {

    private RetrofitError error;

    public LoadedErrorEvent(RetrofitError retrofitError) {
        this.error = retrofitError;
    }

    public RetrofitError getError() { return error; }

    public void setError(RetrofitError error) { this.error = error; }
}
