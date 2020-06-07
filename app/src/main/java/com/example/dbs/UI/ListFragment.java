package com.example.dbs.UI;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dbs.R;
import com.example.dbs.adaper.PostAdaper;
import com.example.dbs.api.NetworkClient;
import com.example.dbs.api.RequestInterface;
import com.example.dbs.entitys.Articles;
import com.example.dbs.entitys.SingleArticles;
import com.example.dbs.response.FullArticle;
import com.example.dbs.response.ListArticle;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<ListArticle> Data ;
    private int postID;
    private String Title;
    private String Body;
    private String text;
    private Integer Date;
    private String imgUrl;
    private Articles articles;
    private RequestInterface requestInterface;
    private SingleArticles singleArticles;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_article,container,false);

        Data = new ArrayList<>();
        recyclerView = (RecyclerView)view.findViewById(R.id.ListOfPost);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        requestInterface = NetworkClient.retrofit.create(RequestInterface.class);
        ConnectivityManager cm =
                (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConnected == true){
            lodeRecyclerView();
            SingleSave();
            deleteWhichDeleted();
        }
        else {
            NoInternetFunction();
        }
        return view;
    }

    private void lodeRecyclerView() {
        Call<List<ListArticle>> call = requestInterface.getPostJson();
        call.enqueue(new Callback<List<ListArticle>>() {
            @Override
            public void onResponse(Call<List<ListArticle>> call, Response<List<ListArticle>> response) {
               // Toast.makeText(getActivity(), "Request Success", Toast.LENGTH_LONG).show();
                if(response != null && response.body() != null){
                    //Articles article= new Articles();
                    //article.deleteAll(Articles.class);
                    for(int i = 0; i<response.body().size(); i++) {
                        postID = response.body().get(i).getId().intValue();
                        Title = String.valueOf(response.body().get(i).getTitle());
                        Date = response.body().get(i).getLastUpdate().intValue();
                        Body = String.valueOf(response.body().get(i).getShortDescription());
                        imgUrl = String.valueOf(response.body().get(i).getAvatar());

                        Articles articles = Articles.find(Articles.class, "articleID ="+ response.body().get(i).getId().intValue()).get(0);
                       // articles = new Articles();
                        if (Date == articles.getLast_update()) {
                            articles.setArticleID(postID);
                            articles.setTitle(Title);
                            articles.setLast_update(Date);
                            articles.setShort_description(Body);
                            articles.setAvatar(imgUrl);
                            articles.save();
                        }
                        else if(articles == null) {
                            articles.setArticleID(postID);
                            articles.setTitle(Title);
                            articles.setLast_update(Date);
                            articles.setShort_description(Body);
                            articles.setAvatar(imgUrl);
                            articles.save();
                        }
                    }
                    Data.addAll(response.body());
                    PostAdaper postAdaper = new PostAdaper(getActivity(),Data);
                    recyclerView.setAdapter(postAdaper);
                }
                else{
                    ((BaseActivity)getActivity()).dialogBoxBase("Error","Server Error 429 - Too Many Requests");
                }
            }
            @Override
            public void onFailure(Call<List<ListArticle>> call, Throwable t) {
              //  Toast.makeText(getActivity(), "Request Not", Toast.LENGTH_LONG).show();
                ((BaseActivity)getActivity()).dialogBoxBase("Error","Server Requests Not Success ");
            }
        });
    }
    private void NoInternetFunction(){
        List<Articles> postsList = Articles.listAll(Articles.class);
        ArrayList<ListArticle> postArrayList = new ArrayList<>();
        if(postsList.size() != 0) {
            for (int i = 0; i < postsList.size(); i++) {
                ListArticle lp = new ListArticle();
                lp.setId(postsList.get(i).getArticleID());
                lp.setTitle(postsList.get(i).getTitle());
                lp.setLastUpdate(postsList.get(i).getLast_update());
                lp.setShortDescription(postsList.get(i).getShort_description());
                lp.setAvatar(postsList.get(i).getAvatar());
                postArrayList.add(lp);
            }
            PostAdaper postAdaper = new PostAdaper(getActivity(), postArrayList);
            recyclerView.setAdapter(postAdaper);
        }
        else{
            ((BaseActivity)getActivity()).dialogBoxBase("Error","No data on database ");
        }
    }
    private void SingleSave(){
        for (int i=0; i<Data.size();i++) {
            Call<FullArticle> calls = requestInterface.groupList(String.valueOf(Data.get(i).getId()));
            calls.enqueue(new Callback<FullArticle>() {
                @Override
                public void onResponse(Call<FullArticle> calls, Response<FullArticle> responses) {
                    if(responses != null && responses.body() != null) {
                        text = responses.body().getText();
                        int id = responses.body().getId();
                        SingleArticles singleArticles = SingleArticles.find(SingleArticles.class, "articleID ="+id).get(0);
                        if(text == singleArticles.getText()){
                            singleArticles.setArticleID(id);
                            singleArticles.setText(text);
                            singleArticles.save();
                        }
                        else if(singleArticles == null){
                            //new record save
                            singleArticles.setArticleID(id);
                            singleArticles.setText(text);
                            singleArticles.save();
                        }
                    }
                    else {
                        ((BaseActivity)getActivity()).dialogBoxBase("Error","Server Error 429 - Too Many Requests ");
                    }
                }
                @Override
                public void onFailure(Call<FullArticle> calls, Throwable t) {
                    //Toast.makeText(FullArticleView.this, "Request Not", Toast.LENGTH_LONG).show();
                    ((BaseActivity)getActivity()).dialogBoxBase("Error","Server Requests Not Success");
                }
            });
        }
    }
    private void deleteWhichDeleted(){
        // If the data was deleted on the saver it will be on local server to delete that on local server
        ArrayList<Integer> localSore = new ArrayList<>();
        List<Articles> postsList = Articles.listAll(Articles.class);
        for (int i=0; i<postsList.size() ;i++){
            localSore.add(postsList.get(i).getArticleID());
        }
        ArrayList<Integer> serverStore = new ArrayList<>();
        for (int i=0; i<Data.size() ;i++){
            serverStore.add(Data.get(i).getId());
        }
        localSore.removeAll(serverStore);
        for (int i=0; i<localSore.size();i++){
            Articles.deleteAll(Articles.class, "articleID = ?", String.valueOf(localSore.get(i)));
            SingleArticles.deleteAll(SingleArticles.class, "articleID = ?", String.valueOf(localSore.get(i)));
        }
    }
}
