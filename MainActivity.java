package com.example.newsapp;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.newsapp.Model.Articles;
import com.example.newsapp.Model.Headlines;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    EditText etQuery;
    Button btnSearch, btnAboutUs;
    Dialog dialog;
    private final String API_KEY = "1c8c8a0bf9d84988b4ce96b036d8bf06";
    Adapter adapter;
    List<Articles> articles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = findViewById(R.id.swipeRefresh);
        recyclerView = findViewById(R.id.recyclerView);
        etQuery = findViewById(R.id.etQuery);
        btnSearch = findViewById(R.id.btnSearch);
        btnAboutUs = findViewById(R.id.aboutUs);
        dialog = new Dialog(MainActivity.this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize and set the adapter in a Fragment
        // Initialize and set the adapter
        adapter = new Adapter(this, articles);
        recyclerView.setAdapter(adapter);

        final String country = getCountry();

        swipeRefreshLayout.setOnRefreshListener(() -> retrieveJson(etQuery.getText().toString(), country, API_KEY));

        retrieveJson("", country, API_KEY);

        btnSearch.setOnClickListener(v -> {
            swipeRefreshLayout.setOnRefreshListener(() -> retrieveJson(etQuery.getText().toString(), country, API_KEY));
            retrieveJson(etQuery.getText().toString(), country, API_KEY);
        });

        btnAboutUs.setOnClickListener(v -> showDialog());
    }

    private void showDialog() {
        // Implement your dialog logic here
        // For example:
         dialog.setContentView(R.layout.about_us_pop_up);
         dialog.show();
    }

    private String getCountry() {
        // Implement getCountry logic, for now, returning a default value
        return Locale.getDefault().getCountry().toLowerCase();
    }

    private void retrieveJson(String query, String country, String apiKey) {
        swipeRefreshLayout.setRefreshing(true);

        ApiInterface apiInterface = ApiClient.getInstance(apiKey).getApi();
        Call<Headlines> call;

        if (!query.isEmpty()) {
            call = apiInterface.getSpecificData(query, apiKey);
        } else {
            call = apiInterface.getHeadlines(country, apiKey);
        }

        call.enqueue(new Callback<Headlines>() {
            @Override
            public void onResponse(Call<Headlines> call, Response<Headlines> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null && response.body().getArticles() != null) {
                    articles.clear();
                    articles.addAll(response.body().getArticles());
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<Headlines> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Log.e("NewsApp", "Error: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
