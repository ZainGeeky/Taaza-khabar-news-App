package com.example.newsapp;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WhatsappActivity extends AppCompatActivity {
    private EditText whatsappNumberEditText;
    private CheckBox scienceCheckbox, healthCheckbox, technologyCheckbox, entertainmentCheckbox, sportsCheckbox;
    private Button applyButton, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.whatsapp_layout);

        whatsappNumberEditText = findViewById(R.id.whatsapp_number_edittext);
        applyButton = findViewById(R.id.apply_button);
        backButton = findViewById(R.id.back_button);


        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the WhatsApp number entered by the user
                String whatsappNumber = whatsappNumberEditText.getText().toString();

                // Get the selected categories
                List<String> selectedCategories = new ArrayList<>();
                if (scienceCheckbox.isChecked()) {
                    selectedCategories.add("science");
                }
                if (healthCheckbox.isChecked()) {
                    selectedCategories.add("health");
                }
                if (technologyCheckbox.isChecked()) {
                    selectedCategories.add("technology");
                }
                if (entertainmentCheckbox.isChecked()) {
                    selectedCategories.add("entertainment");
                }
                if (sportsCheckbox.isChecked()) {
                    selectedCategories.add("sports");
                }

                fetchNewsArticlesForCategory(whatsappNumber, selectedCategories);

                // Print selected categories for debugging
                Log.d("SelectedCategories", selectedCategories.toString());
            }
        });


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle back button click
                Intent intent = new Intent(WhatsappActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void sendNewsArticle(String phoneNumber, ModelClass article) {
        String headline = article.getTitle();
        String articleUrl = article.getUrl();

        // Format the message with the news article headline and URL
        String message = "Check out this news article:\n" +
                "Headline: " + headline + "\n" +
                "URL: " + articleUrl;

        TwilioHelper.sendMessage(phoneNumber, message);
    }

    private void fetchNewsArticlesForCategory(String whatsappNumber, List<String> categories) {
        String country = "in";
        int pageSize = 1; // Fetch only one news article per category
        String apiKey = "b5453742409f44f0a26995bb4ccc18ac";

        for (String category : categories) {
            ApiUtilities.getApiInterface().getCategoryNews(country, category, pageSize, apiKey)
                    .enqueue(new Callback<mainNews>() {
                        @Override
                        public void onResponse(Call<mainNews> call, Response<mainNews> response) {
                            if (response.isSuccessful()) {
                                ModelClass article = response.body().getArticles().get(0); // Get the first article
                                sendNewsArticle(whatsappNumber, article);
                                Toast.makeText(WhatsappActivity.this, "Successfully fetched news article for category: " + category, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(WhatsappActivity.this, "Failed to fetch news articles for category: " + category, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<mainNews> call, Throwable t) {
                            Toast.makeText(WhatsappActivity.this, "Failed to fetch news articles for category: " + category, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

}





