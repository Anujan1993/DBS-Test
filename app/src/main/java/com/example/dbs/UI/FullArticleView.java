package com.example.dbs.UI;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dbs.R;
import com.example.dbs.api.NetworkClient;
import com.example.dbs.api.RequestInterface;
import com.example.dbs.entitys.Article;
import com.example.dbs.entitys.SingleArticle;
import com.example.dbs.response.FullArticle;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FullArticleView extends BaseActivity implements View.OnClickListener {

    private ImageView imageView;
    private ImageView imageView2;
    private TextView boDy;
    private TextView boDy2;
    private TextView header;
    private String articleID;
    private String imageUrl;
    private TextView edit;
    private TextView cancel;
    private Button save;
    private  String title;
    private  String title2;
    private ArrayList<Object> newARRAY;
    private RequestInterface requestUserInterface;
    private String text;
    private Long singleArticleID;
    private FrameLayout viewLay;
    private FrameLayout editLay;
    private androidx.appcompat.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_article_view);
        /*** Custom Toolbar  ***/
        toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /*** Get data from Adapter ***/
        final Intent intent = getIntent();
        title= intent.getStringExtra("Title");
        title2= "Edit " + title;
        imageUrl= intent.getStringExtra("Url");
        articleID = intent.getStringExtra("PostID");

        imageView = (ImageView) findViewById(R.id.AvterImg);
        imageView2 = (ImageView) findViewById(R.id.AvterImg2);
        header=(TextView)findViewById(R.id.header);
        save=findViewById(R.id.Save);
        edit=findViewById(R.id.edit);
        boDy = (TextView)findViewById(R.id.body);
        boDy2 = (TextView)findViewById(R.id.body2);
        cancel=findViewById(R.id.cancel);
        viewLay = findViewById(R.id.Viewframe);
        editLay = findViewById(R.id.EditFrame);

        /*** Button Click ***/
        edit.setOnClickListener(this);
        cancel.setOnClickListener(this);
        save.setOnClickListener(this);

        /*** Set to layout ***/
        header.setText(title);
        String imageUri = imageUrl;
        Picasso.get().load(imageUri).into(imageView);
        Picasso.get().load(imageUri).into(imageView2);
        newARRAY = new ArrayList<>();
        requestUserInterface = NetworkClient.retrofit.create(RequestInterface.class);
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConnected == true){
            getSinglePost();
        }
        else {
            noIternet();
        }
    }
    private void getSinglePost(){
        /*** Get Single Article from API ***/
        Call<FullArticle> calls = requestUserInterface.groupList(articleID);
        calls.enqueue(new Callback<FullArticle>() {
            @Override
            public void onResponse(Call<FullArticle> calls, Response<FullArticle> response) {
                if(response != null && response.body() != null) {
                    text = response.body().getText();
                    boDy.setText(text);
                    boDy2.setText(text);

                    SingleArticle singleArticles = new SingleArticle();
                    singleArticles.setArticleID(Integer.valueOf(articleID));
                    singleArticles.setText(text);
                    singleArticles.save();
                }
                else{
                    dialogBoxBase("Error","Server Error 429 - Too Many Requests");
                }
            }
            @Override
            public void onFailure(Call<FullArticle> calls, Throwable t) {
                dialogBoxBase("Error","Server Requests Not Success");
            }
        });
    }
    private void noIternet() {
        /*** Get Articles form Local Storage ***/
        ArrayList<SingleArticle> singleArticles = (ArrayList<SingleArticle>) SingleArticle.find(SingleArticle.class, "articleID ="+articleID);
        if (singleArticles == null || singleArticles.size() == 0){
            dialogBoxBase("Error","To view this you have to connect your internet, Because you didn't visit...!");
        }
        else {
            singleArticleID = singleArticles.get(0).getID();
            text = singleArticles.get(0).getText();
            boDy.setText(String.valueOf(text));
            boDy2.setText(String.valueOf(text));
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edit:
                edit.setVisibility(View.GONE);
                cancel.setVisibility(View.VISIBLE);
                viewLay.setVisibility(View.GONE);
                editLay.setVisibility(View.VISIBLE);
                header.setText(title2);
                break;
            case R.id.cancel:
                cancel.setVisibility(View.GONE);
                edit.setVisibility(View.VISIBLE);
                viewLay.setVisibility(View.VISIBLE);
                editLay.setVisibility(View.GONE);
                header.setText(title);
                break;
            case R.id.Save:
                SingleArticle singleArticl = SingleArticle.findById(SingleArticle.class, singleArticleID);
                singleArticl.setText(boDy2.getText().toString());
                singleArticl.save();
                boDy.setText(boDy2.getText());
                Article articles1 = Article.find(Article.class, "articleID ="+articleID).get(0);
                articles1.setLast_update(System.currentTimeMillis());
                articles1.save();
                dialogBoxBase("Save Status","Your Data Saved Successfully");
                edit.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.GONE);
                viewLay.setVisibility(View.VISIBLE);
                editLay.setVisibility(View.GONE);
                header.setText(title);
                break;
        }
    }
}
