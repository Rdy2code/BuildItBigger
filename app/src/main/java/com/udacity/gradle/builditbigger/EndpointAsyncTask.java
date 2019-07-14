package com.udacity.gradle.builditbigger;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;
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

    private static final String TAG = EndpointsAsyncTask.class.getSimpleName();

    //Interface callback for use in conjunction with IdlingResource/Espresso Testing
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
                    //BEFORE RUNNING: INSERT LOCAL HOST IP OR DEPLOY PATH INSIDE QUOTES
                    //AFTER https://. Loacal host IP should be in the form
                    //"https://X.X.X.X:8080/_ah/api"
                    .setRootUrl("https://louisudacity.appspot.com/_ah/api")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });

            myApiService = builder.build();
        }

        //If an IOException is thrown, onPostExecute returns without launching the Android Library Activity
        //Instead, the exception message is logged
        //See code block in onPostExecute
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

        //This block checks that the ArrayList returned by the doInBackground method is filled with jokes
        //(i.e., size >1) or is simply a one-string ArrayList with an IOException message
        if (result.size() > 1) {
            Intent intent = new Intent(activity, AndroidActivity.class);
            intent.putStringArrayListExtra(Intent.EXTRA_TEXT, result);
            if (mCallback != null) {
                mCallback.onDone(result);
            }
            activity.startActivity(intent);
        } else {
            Log.e(TAG, result.get(0));
            return;
        }

        progressBar.setVisibility(View.INVISIBLE);

        if (mIdlingResource != null) {
            mIdlingResource.setIdleState(true);
        }
    }
}
