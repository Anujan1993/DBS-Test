package com.example.dbs.UI;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import com.example.dbs.entitys.Article;
import com.example.dbs.entitys.SingleArticle;
import com.example.dbs.response.FullArticle;
import com.example.dbs.response.ListArticle;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<ListArticle> dateArray ;
    private int idOfArticle;
    private String titleOfArticle;
    private String bodyArticle;
    private String text;
    private long dateOfUpdate;
    private String imgUrl;
    private RequestInterface requestInterface;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_article,container,false);

        dateArray = new ArrayList<>();
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
            deleteWhichDeleted();
        }
        else {
            NoInternetFunction();
        }
        return view;
    }

    private void lodeRecyclerView() {
        /*** Get Articles for list ***/
        Call<List<ListArticle>> call = requestInterface.getPostJson();
        call.enqueue(new Callback<List<ListArticle>>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(Call<List<ListArticle>> call, Response<List<ListArticle>> response) {
                if(response != null && response.body() != null){
                    for(int i = 0; i<response.body().size(); i++) {
                        idOfArticle = response.body().get(i).getId().intValue();
                        titleOfArticle = String.valueOf(response.body().get(i).getTitle());
                        dateOfUpdate = response.body().get(i).getLastUpdate();
                        bodyArticle = String.valueOf(response.body().get(i).getShortDescription());
                        imgUrl = String.valueOf(response.body().get(i).getAvatar());
                        /*** Check date of update with local store and API ***/
                        List<Article> articles = Article.find(Article.class, "articleID ="+ response.body().get(i).getId().intValue());
                        if(articles == null || articles.size() == 0) {
                            Article article= new Article();
                            article.setArticleID(idOfArticle);
                            article.setTitle(titleOfArticle);
                            article.setLast_update(dateOfUpdate);
                            article.setShort_description(bodyArticle);
                            article.setAvatar(imgUrl);
                            article.save();
                        }
                        else {
                            if (dateOfUpdate <= articles.get(0).getLast_update()) {
                                long aArticleID = articles.get(0).getID();
                                Article article1 = Article.findById(Article.class, aArticleID);
                                article1.setTitle(titleOfArticle);
                                article1.setLast_update(dateOfUpdate);
                                article1.setShort_description(bodyArticle);
                                article1.setAvatar(imgUrl);
                                article1.save();
                            }
                        }
                    }
                    dateArray.addAll(response.body());
                    Collections.sort(dateArray,
                            Comparator.comparingLong(ListArticle::getLastUpdate).reversed());
                    PostAdaper postAdaper = new PostAdaper(getActivity(),dateArray);
                    recyclerView.setAdapter(postAdaper);
                }
                else{
                    ((BaseActivity)getActivity()).dialogBoxBase("Error","Server Error 429 - Too Many Requests");
                }
            }
            @Override
            public void onFailure(Call<List<ListArticle>> call, Throwable t) {
                ((BaseActivity)getActivity()).dialogBoxBase("Error","Server Requests Not Success ");
            }
        });
    }
    private void NoInternetFunction(){
        /*** No Internet But get data from Local Storage ***/
        List<Article> postsList = Select.from(Article.class).orderBy("last_update DESC").list();
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
    private void deleteWhichDeleted(){
        /*** if any data deleted on API delete from Local Store ***/
        ArrayList<Integer> localSore = new ArrayList<>();
        List<Article> postsList = Article.listAll(Article.class);
        for (int i=0; i<postsList.size() ;i++){
            localSore.add(postsList.get(i).getArticleID());
        }
        ArrayList<Integer> serverStore = new ArrayList<>();
        for (int i=0; i<dateArray.size() ;i++){
            serverStore.add(dateArray.get(i).getId());
        }
        localSore.removeAll(serverStore);
        for (int i=0; i<localSore.size();i++){
            Article.deleteAll(Article.class, "articleID = ?", String.valueOf(localSore.get(i)));
            SingleArticle.deleteAll(SingleArticle.class, "articleID = ?", String.valueOf(localSore.get(i)));
        }
    }
}
