package com.udacity.gradle.builditbigger;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.android.androidjoke.AndroidActivity;
import com.udacity.gradle.builditbigger.IdlingResource.SimpleIdlingResource;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements EndpointsAsyncTask.DelayCallback {

    // Add a SimpleIdlingResource variable that will be null in production
    @Nullable
    private SimpleIdlingResource mIdlingResource;

    /**
     * Create a method that returns the IdlingResource variable. It will
     * instantiate a new instance of SimpleIdlingResource if the IdlingResource is null.
     * This method will only be called from test.
     */

    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Register the idling resource at launch, before the button is clicked
        getIdlingResource();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void tellJoke(View view) {

        EndpointsAsyncTask task;
        task = new EndpointsAsyncTask(this, this, mIdlingResource);
        task.execute();
    }

    //This method called only during the testing of the EndpointsAsyncTask
    //Callback from EndpointsAsyncTask
    @Override
    public void onDone(ArrayList<String> jokeList) {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        Intent intent = new Intent(this, AndroidActivity.class);
        intent.putStringArrayListExtra(Intent.EXTRA_TEXT, jokeList);
        startActivity(intent);
        progressBar.setVisibility(View.INVISIBLE);
    }

    //Make sure the IdlingResource is registered on resuming the MainActivity, before the button
    //is clicked
    @Override
    protected void onResume() {
        super.onResume();
        getIdlingResource();
    }
}
