package com.example.android.androidjoke;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class AndroidActivity extends AppCompatActivity {

    private TextView mJokeView;
    private TextView mWelcomeBackView;
    private Button mJokeButton;
    private ArrayList<String> mJokeArray;
    private int mListIndex;
    private static final String SHAREDPREFERENCES_KEY = "JokeNumber";
    private static final String SAVEDINSTANCESTATE_JOKE_TEXT_KEY = "joke_text";
    private static final String SAVEDINSTANCESTATE_ARRAYINDEX_KEY = "ListIndex";
    private static final String TAG = AndroidActivity.class.getSimpleName();

    Intent mNavigateToMainActivityIntent;
    SharedPreferences mSharedPref;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android);

        mSharedPref = getPreferences(Context.MODE_PRIVATE);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //https://stackoverflow.com/questions/41465053/
        // how-to-call-activity-from-a-library-module-in-android-studio
        try {
            mNavigateToMainActivityIntent = new Intent(this,
                    Class.forName("com.udacity.gradle.builditbigger.MainActivity"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        //Get references to layout views and button
        mJokeView = (TextView) findViewById(R.id.joke_tv);
        mJokeButton = (Button) findViewById(R.id.button_next_joke);
        mWelcomeBackView = (TextView) findViewById(R.id.welcome_back);

        //Get the intent with associated data that launched this activity
        Intent intent = getIntent();
        if (intent.hasExtra(Intent.EXTRA_TEXT)) {
            if (intent.getExtras() != null) {
                mJokeArray = intent.getStringArrayListExtra(Intent.EXTRA_TEXT);
            }
        }

        //Save UI state on device rotation and after return from up navigation
        if (savedInstanceState != null) {
            mListIndex = savedInstanceState.getInt(SAVEDINSTANCESTATE_ARRAYINDEX_KEY, 0);
            mJokeView.setText(savedInstanceState.getString(SAVEDINSTANCESTATE_JOKE_TEXT_KEY));
        } else if (mSharedPref.contains(SHAREDPREFERENCES_KEY)) {
            mListIndex = mSharedPref.getInt(SHAREDPREFERENCES_KEY, 0);
            mWelcomeBackView.setVisibility(View.VISIBLE);

            //Adjust index value if needed, following AsyncTaskTest completion
            if (mListIndex == -1) {
                mListIndex++;
            }

            mJokeView.setText(mJokeArray.get(mListIndex));
        } else {
            mListIndex = -1;
        }

        mJokeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mListIndex < mJokeArray.size() - 1) {
                    mListIndex++;
                } else {
                    mListIndex = 0;
                }
                mWelcomeBackView.setVisibility(View.INVISIBLE);
                mJokeView.setText(mJokeArray.get(mListIndex));
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVEDINSTANCESTATE_ARRAYINDEX_KEY, mListIndex);
        outState.putString(SAVEDINSTANCESTATE_JOKE_TEXT_KEY, mJokeView.getText().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        getMenuInflater().inflate(R.menu.menu_main_androidjokes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                startActivity(mNavigateToMainActivityIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Preserve the current joke shown in the UI upon up navigation to previous activity
    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putInt(SHAREDPREFERENCES_KEY, mListIndex);
        editor.apply();
    }
}
