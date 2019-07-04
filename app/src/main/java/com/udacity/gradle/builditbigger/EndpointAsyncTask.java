package com.udacity.gradle.builditbigger;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ProgressBar;

import com.example.android.androidjoke.AndroidActivity;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.udacity.gradle.builditbigger.IdlingResource.SimpleIdlingResource;
import com.udacity.gradle.builditbigger.backend.myApi.MyApi;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

class EndpointsAsyncTask extends AsyncTask<Void, Void, ArrayList<String>>{

    private WeakReference<MainActivity> weakActivity;
    private static MyApi myApiService = null;

    private SimpleIdlingResource mIdlingResource;
    private DelayCallback mCallback;

    interface DelayCallback {
        void onDone (ArrayList<String> jokeList);
    }

    EndpointsAsyncTask (MainActivity activity, DelayCallback callback,
                        @Nullable final SimpleIdlingResource idlingResource) {
        weakActivity = new WeakReference<MainActivity>(activity);
        mIdlingResource = idlingResource;
        mCallback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        MainActivity activity = weakActivity.get();

        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            return;
        }

        ProgressBar progressBar = activity.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        if (mIdlingResource != null) {
            mIdlingResource.setIdleState(false);
        }
    }

    @Override
    protected ArrayList<String> doInBackground(Void... params) {

        if(myApiService == null) {
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    //BEFORE RUNNING: INSERT LOCAL IP OR DEPLOY PATH INSIDE QUOTES
                    .setRootUrl("<insert local ip or deploy path here>")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });

            myApiService = builder.build();
        }

        try {
            return (ArrayList<String>) myApiService.getJokeList().execute().getJavaJokes();

        } catch (final IOException e) {
            return new ArrayList<String>() {{add(e.getMessage());}};
        }
    }

    @Override
    protected void onPostExecute (ArrayList<String> result) {

        MainActivity activity = weakActivity.get();
        ProgressBar progressBar = activity.findViewById(R.id.progress_bar);

        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            return;
        }

        Intent intent = new Intent(activity, AndroidActivity.class);
        intent.putStringArrayListExtra(Intent.EXTRA_TEXT, result);

        progressBar.setVisibility(View.INVISIBLE);

        if (mCallback != null) {
            mCallback.onDone(result);
        }

        if (mIdlingResource != null) {
            mIdlingResource.setIdleState(true);
        }

        activity.startActivity(intent);
    }
}
