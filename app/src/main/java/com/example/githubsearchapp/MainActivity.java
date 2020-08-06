package com.example.githubsearchapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    //views
    EditText searchEditText;
    Button searchButton;
    String res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init views
        searchEditText = findViewById(R.id.et_search);
        searchButton = findViewById(R.id.btn_search);

        //add listener for button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultsActivityIntent = new Intent(MainActivity.this, ResultsActivity.class);
                resultsActivityIntent.putExtra("data", searchEditText.getText().toString());
                startActivity(resultsActivityIntent);
            }
        });

    }






}