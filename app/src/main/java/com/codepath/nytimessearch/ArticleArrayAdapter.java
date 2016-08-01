package com.codepath.nytimessearch;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.nytimessearch.models.Article;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ArticleArrayAdapter extends RecyclerView.Adapter<ArticleArrayAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView tvTitle;

        public ViewHolder(View articleView) {
            super(articleView);

            imageView = (ImageView) articleView.findViewById(R.id.ivImage);
            tvTitle = (TextView) articleView.findViewById(R.id.tvTitle);

        }
    }

    private List<Article> mArticles;
    private Context mContext;

    public ArticleArrayAdapter(Context context, List<Article> articles) {
        mArticles = articles;
        mContext = context;
    }

    private Context getContext() {
        return mContext;
    }

    @Override
    public ArticleArrayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View articleView = inflater.inflate(R.layout.item_article_result, parent, false);

        ViewHolder viewHolder = new ViewHolder(articleView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ArticleArrayAdapter.ViewHolder viewHolder, int position) {
        Article article = mArticles.get(position);

        ImageView imageView = viewHolder.imageView;
        imageView.setImageResource(0);
        // populate the thumbnail image
        // remote download the image in the background
        String thumbnail = article.getThumbNail();
        if (!TextUtils.isEmpty(thumbnail)) {
            Picasso.with(getContext()).load(thumbnail).into(imageView);
        }

        TextView textView = viewHolder.tvTitle;
        textView.setText(article.getHeadline());
    }

    @Override
    public int getItemCount() {
        return mArticles.size();
    }
}
