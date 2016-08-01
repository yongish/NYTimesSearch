package com.codepath.nytimessearch;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.nytimessearch.models.Article;
import com.codepath.nytimessearch.utils.ViewHolderNoImage;
import com.codepath.nytimessearch.utils.ViewHolderWithImage;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticleArrayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivImage) ImageView imageView;
        @BindView(R.id.tvTitle) TextView tvTitle;

        public ViewHolder(View articleView) {
            super(articleView);
            ButterKnife.bind(this, articleView);
        }
    }

    private List<Article> mArticles;
    private Context mContext;

    public ArticleArrayAdapter(Context context, List<Article> articles) {
        mArticles = articles;
        mContext = context;
    }

    private final int NOIMAGE = 0, IMAGE = 1;

    @Override
    public int getItemCount() {
        return mArticles.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mArticles.get(position).getThumbNail().length() == 0) {
            return NOIMAGE;
        } else if (mArticles.get(position).getThumbNail().length() > 0) {
            return IMAGE;
        }
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case NOIMAGE:
                View v1 = inflater.inflate(R.layout.item_article_no_image, parent, false);
                viewHolder = new ViewHolderNoImage(v1);
                break;
            default:
                View v2 = inflater.inflate(R.layout.item_article_result, parent, false);
                viewHolder = new ViewHolderWithImage(v2);
                break;
        }
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case NOIMAGE:
                ViewHolderNoImage vh1 = (ViewHolderNoImage) viewHolder;
                configureViewHolderNoImage(vh1, position);
                break;
            case IMAGE:
                ViewHolderWithImage vh2 = (ViewHolderWithImage) viewHolder;
                configureViewHolderWithImage(vh2, position);
                break;
            default:
                ViewHolder vh = (ViewHolder) viewHolder;
                configureDefaultViewHolder(vh, position);
                break;
        }
    }

    private void configureDefaultViewHolder(ViewHolder vh, int position) {
        vh.tvTitle.setText(mArticles.get(position).getHeadline());
        Picasso.with(getContext()).load(mArticles.get(position).getThumbNail()).into(vh.imageView);
    }

    private void configureViewHolderNoImage(ViewHolderNoImage vh1, int position) {
        Article article = mArticles.get(position);
        vh1.getTvTitle().setText(article.getHeadline());
    }

    private void configureViewHolderWithImage(ViewHolderWithImage vh2, int position) {
        Article article = mArticles.get(position);
        vh2.getTvTitle().setText(article.getHeadline());
        Picasso.with(getContext()).load(article.getThumbNail()).into(vh2.getIvImage());
    }

    private Context getContext() {
        return mContext;
    }
}
