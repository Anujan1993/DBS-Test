package com.example.dbs.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dbs.R;
import com.squareup.picasso.Picasso;

public class FullArticleView extends AppCompatActivity {

    private ImageView imageView;
    private TextView boDy;
    private String articleID;
    private String imageUrl;
    private  String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_article_view);

        final Intent intent = getIntent();
        title= intent.getStringExtra("Title");
        imageUrl= intent.getStringExtra("Url");
        articleID = intent.getStringExtra("PostID");

        imageView = (ImageView) findViewById(R.id.AvterImg);
        boDy = (TextView)findViewById(R.id.body);

        getSupportActionBar().setTitle(title);
        String imageUri = imageUrl;
        Picasso.get().load(imageUri).into(imageView);



    }
}