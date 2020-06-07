package com.example.dbs.adaper;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dbs.R;
import com.example.dbs.UI.FullArticleView;
import com.example.dbs.response.ListArticle;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PostAdaper extends RecyclerView.Adapter<PostAdaper.postViewAdapter>{
    private ArrayList<ListArticle> data1;
    private Context context;

    public PostAdaper(Context ct, ArrayList<ListArticle> s1){
        context = ct;
        data1 = s1;
    }
    @NonNull
    @Override
    public postViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.article_row,parent,false);
        return new postViewAdapter(view);
    }
    @Override
    public void onBindViewHolder(@NonNull postViewAdapter holder, final int position) {
        holder.textView.setText(data1.get(position).getTitle());
        String resultDate = getDate(String.valueOf(data1.get(position).getLastUpdate().intValue()));
        holder.date.setText(resultDate);
        holder.shortDec.setText(data1.get(position).getShortDescription());
        String imageUri = data1.get(position).getAvatar();
        Picasso.get().load(imageUri).into(holder.imageView);
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FullArticleView.class);
                intent.putExtra("Title", data1.get(position).getTitle());
                intent.putExtra("Url", String.valueOf(data1.get(position).getAvatar()));
                intent.putExtra("PostID",String.valueOf( data1.get(position).getId().intValue()));
                context.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return data1.size();
    }
    /**To convert timestamp tp date**/
    private String getDate(String time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(time));
        String date = DateFormat.format("yyyy-MM-dd ", cal).toString();
        return date;
    }
    public class postViewAdapter extends RecyclerView.ViewHolder{
        private TextView textView;
        private  TextView date;
        private  TextView shortDec;
        private ImageView imageView;
        public postViewAdapter(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.Title);
            date = itemView.findViewById(R.id.Date);
            shortDec = itemView.findViewById(R.id.ShortDec);
            imageView = itemView.findViewById(R.id.AvterImg);

        }
    }
}