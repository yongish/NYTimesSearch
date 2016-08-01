package com.codepath.nytimessearch.utils;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.nytimessearch.R;

public class ViewHolderWithImage extends RecyclerView.ViewHolder {
    private ImageView ivImage;
    private TextView tvTitle;

    public ViewHolderWithImage(View v) {
        super(v);
        ivImage = (ImageView) v.findViewById(R.id.ivImage);
        tvTitle = (TextView) v.findViewById(R.id.tvTitle);
    }

    public ImageView getIvImage() {
        return ivImage;
    }

    public TextView getTvTitle() {
        return tvTitle;
    }
}
