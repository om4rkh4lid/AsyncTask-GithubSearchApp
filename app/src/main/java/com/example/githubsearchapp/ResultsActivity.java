package com.example.githubsearchapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class ResultsActivity extends AppCompatActivity {

    //constants
    final String baseURI = "https://api.github.com/search/repositories";


    TextView resultsTextView;
    ProgressBar progressBar;
    String searchQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        //enable navigation to the parent by <- arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //init views
        resultsTextView = findViewById(R.id.tv_result);
        progressBar = findViewById(R.id.progressBar);

        //intent that started this activity
        //the intent could be null if the activity is started from a savedInstance state
        Intent intent = getIntent();

        //get the search query from the intent
        String data = null;

        if(intent != null) {
            //check if there is a search query attached
            if (intent.hasExtra("data")) {
                data = (String) intent.getCharSequenceExtra("data");
            }
            if(data != null){
                searchQuery = data;
                URL searchURL = buildUrl(searchQuery);
                if(searchURL != null){
                    new NetworkAsyncTask().execute(searchURL);
                }
            }

        }




    }

    // TODO(2) make a URL Object
    //method that builds a URL from a base URI and parameters, the result will be a URL formatted like:
    //"https://api.github.com/search/repositories?q=WHATEVER+language:assembly&sort=stars&order=desc"
    private URL buildUrl(String query) {
        Uri builtUri = Uri.parse(baseURI).buildUpon()
                .appendQueryParameter("q", query)
                .appendQueryParameter("sort", "stars")
                .appendQueryParameter("order", "desc")
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }


    // TODO(3) - make a subclass of AsyncTask to do the network access on a separate thread
    /**
     * An asynchronous task is defined by a computation that runs on a background thread and whose result is published on the UI thread.
     * An asynchronous task is defined by 3 generic types, called Params (in this case URL object the query),
     * Progress(is there something that should be returned with progress updates? in this case there isn't so we use Void which is a wrapper class around the void type)
     * and Result (in this case we get back a string of results from our query), and 4 steps,
     * called onPreExecute, doInBackground, onProgressUpdate and onPostExecute.*/
    private class NetworkAsyncTask extends AsyncTask<URL, String, String> {

        // invoked on the UI thread before the task is executed.
        // This step is normally used to setup the task, for instance by showing a progress bar in the user interface.
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }


        // invoked on the background thread immediately after onPreExecute() finishes executing.
        // This step is used to perform background computation that can take a long time.
        // The parameters of the asynchronous task are passed to this step.
        // The result of the computation must be returned by this step and will be passed back to the last step.
        // This step can also use publishProgress(Progress...) to publish one or more units of progress.
        // These values are published on the UI thread, in the onProgressUpdate(Progress...) step.
        @Override
        protected String doInBackground(URL... params) {
            //result
            String result = null;

            //search url
            URL url = params[0];

            // TODO(3.1) - make an connection to the url
            // represents a communications link between the application and a URL and can be used both to read from and to write to the resource referenced by the URL.
            // https://developer.android.com/reference/java/net/HttpURLConnection
            HttpURLConnection httpURLConnection = null;
            try {
                httpURLConnection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // TODO(3.2) - get results from the input stream returned by the connection
            // Returns an input stream that reads from this open connection.
            // A SocketTimeoutException can be thrown when reading from the returned
            // input stream if the read timeout expires before data is available for read.
            InputStream input = null;
            try {
                input = httpURLConnection.getInputStream();
                Scanner scanner = new Scanner(input);
                scanner.useDelimiter("\\A");
                if (scanner.hasNext()) {
                    result = scanner.next();
                }
            }catch (Exception x){
                x.printStackTrace();
            }finally {
                //Disconnecting releases the resources held by a connection so they may be closed or reused.
                httpURLConnection.disconnect();
            }

            if(result != null){
                publishProgress("Results Found");
            }else {
                publishProgress("Results Not Found");
            }
            return result;
        }

        // invoked on the UI thread after a call to publishProgress(Progress...).
        // The timing of the execution is undefined.
        // This method is used to display any form of progress in the user interface while the background computation is still executing.
        // For instance, it can be used to animate a progress bar or show logs in a text field.
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Toast.makeText(getApplicationContext(), values[0], Toast.LENGTH_SHORT).show();
        }

        // TODO(3.3) - update the UI with the results
        // invoked on the UI thread after the background computation finishes.
        // The result of the background computation is passed to this step as a parameter.
        @Override
        protected void onPostExecute(String s) {
            progressBar.setVisibility(View.INVISIBLE);
            if(s != null) {
                resultsTextView.setText(s);
            }else {
                resultsTextView.setText("Search returned no results, try again.");
            }
        }
    }

}