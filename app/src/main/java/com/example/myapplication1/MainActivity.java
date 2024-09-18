package com.example.myapplication1;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private Button searchButton;
    private ImageView imageView;
    private Button likeButton;
    private Button dislikeButton;
    private Button visitWebsiteButton;
    private Button downloadButton;
    private Button infoButton;
    private TextView likeCounter;
    private TextView dislikeCounter;
    private RequestQueue requestQueue;

    private static final String API_KEY = "";
    private static final String BASE_URL = "https://pixabay.com/api/";

    private boolean imageLoaded = false;
    private int likeCount = 0;
    private int dislikeCount = 0;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.edit_message);
        searchButton = findViewById(R.id.search_button);
        imageView = findViewById(R.id.image_view);
        likeButton = findViewById(R.id.like_button);
        dislikeButton = findViewById(R.id.dislike_button);
        visitWebsiteButton = findViewById(R.id.visit_website_button);
        downloadButton = findViewById(R.id.download_button);
        infoButton = findViewById(R.id.info_button);
        likeCounter = findViewById(R.id.like_counter);
        dislikeCounter = findViewById(R.id.dislike_counter);

        requestQueue = Volley.newRequestQueue(this);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = editText.getText().toString().trim();
                if (!query.isEmpty()) {
                    searchImage(query);
                }
            }
        });

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageLoaded) {
                    likeCount++;
                    likeCounter.setText(String.valueOf(likeCount));
                    likeButton.setEnabled(false);
                    dislikeButton.setEnabled(false);
                }
            }
        });

        dislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageLoaded) {
                    dislikeCount++;
                    dislikeCounter.setText(String.valueOf(dislikeCount));
                    likeButton.setEnabled(false);
                    dislikeButton.setEnabled(false);
                }
            }
        });

        visitWebsiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUrl != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(imageUrl));
                    startActivity(intent);
                }
            }
        });

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUrl != null && imageLoaded) {
                    downloadImage(imageUrl);
                }
            }
        });

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AuthorInfoActivity.class);
                startActivity(intent);
            }
        });
    }

    private void searchImage(String query) {
        String encodedQuery;
        try {
            encodedQuery = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            encodedQuery = query;
        }

        String url = BASE_URL + "?key=" + API_KEY + "&q=" + encodedQuery + "&image_type=photo";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray hits = response.getJSONArray("hits");
                            if (hits.length() > 0) {
                                JSONObject imageObject = hits.getJSONObject(0);
                                imageUrl = imageObject.getString("webformatURL");

                                ImageRequest imageRequest = new ImageRequest(imageUrl,
                                        new Response.Listener<Bitmap>() {
                                            @Override
                                            public void onResponse(Bitmap response) {
                                                imageView.setImageBitmap(response);
                                                imageLoaded = true;
                                                visitWebsiteButton.setEnabled(true);
                                                downloadButton.setEnabled(true);
                                                likeButton.setEnabled(true);
                                                dislikeButton.setEnabled(true);
                                            }
                                        },
                                        0,
                                        0,
                                        ImageView.ScaleType.CENTER_CROP,
                                        Bitmap.Config.ARGB_8888,
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                // Handle error
                                            }
                                        });

                                requestQueue.add(imageRequest);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    private void downloadImage(String imageUrl) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(imageUrl));
        request.setTitle("Downloading Image");
        request.setDescription("Please wait...");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "downloaded_image.jpg");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            downloadManager.enqueue(request);
        }
    }
}
