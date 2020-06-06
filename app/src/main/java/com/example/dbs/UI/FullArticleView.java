package com.example.dbs.UI;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dbs.R;
import com.example.dbs.api.NetworkClient;
import com.example.dbs.api.RequestInterface;
import com.example.dbs.entitys.Articles;
import com.example.dbs.entitys.SingleArticles;
import com.example.dbs.response.FullArticle;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FullArticleView extends AppCompatActivity implements View.OnClickListener {

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
        toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Intent intent = getIntent();
        // title= intent.getStringExtra("Title");
        // title2= intent.getStringExtra("Title");
        title= "Testing";
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

        edit.setOnClickListener(this);
        cancel.setOnClickListener(this);
        save.setOnClickListener(this);

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
        Call<FullArticle> calls = requestUserInterface.groupList(articleID);
        calls.enqueue(new Callback<FullArticle>() {

            @Override
            public void onResponse(Call<FullArticle> calls, Response<FullArticle> response) {
                text = response.body().getText();
                boDy.setText(text);
                boDy2.setText(text);
            }
            @Override
            public void onFailure(Call<FullArticle> calls, Throwable t) {
                Toast.makeText(FullArticleView.this, "Request Not", Toast.LENGTH_LONG).show();

            }
        });
    }
    private void noIternet() {
        List<SingleArticles> singleArticles = SingleArticles.findWithQuery
                (SingleArticles.class, "Select * from SingleArticles where articleID = ?", articleID);
        singleArticleID = singleArticles.get(Integer.parseInt(articleID)).getID();
        text = singleArticles.get(Integer.parseInt(articleID)).getText();
        boDy.setText(String.valueOf(text));
        boDy2.setText(String.valueOf(text));
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
                SingleArticles singleArticl = SingleArticles.findById(SingleArticles.class, singleArticleID);
                singleArticl.setText(String.valueOf(boDy2));
                singleArticl.save();
                String time= String.valueOf(System.currentTimeMillis());
                Articles articles1 = Articles.find(Articles.class, "articleID ="+singleArticleID).get(0);
                articles1.setLast_update(Integer.valueOf(time));
                singleArticl.save();

                AlertDialog alertDialog = new AlertDialog.Builder(FullArticleView.this).create();
                alertDialog.setTitle("Save Status");
                alertDialog.setMessage("Your Data Saved Successfully");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                alertDialog.show();
                edit.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.GONE);
                viewLay.setVisibility(View.VISIBLE);
                editLay.setVisibility(View.GONE);
                header.setText(title);
                break;
        }
    }
}
