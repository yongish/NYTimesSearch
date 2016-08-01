package com.codepath.nytimessearch.utils;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.codepath.nytimessearch.R;

public class ViewHolderNoImage extends RecyclerView.ViewHolder {
    private TextView tvTitle;

    public ViewHolderNoImage(View v) {
        super(v);
        tvTitle = (TextView) v.findViewById(R.id.tvTitle);
    }

    public TextView getTvTitle() {
        return tvTitle;
    }
}
