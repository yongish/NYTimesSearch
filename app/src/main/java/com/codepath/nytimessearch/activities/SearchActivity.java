package com.codepath.nytimessearch.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.codepath.nytimessearch.ArticleArrayAdapter;
import com.codepath.nytimessearch.utils.EndlessRecyclerViewScrollListener;
import com.codepath.nytimessearch.utils.ItemClickSupport;
import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.fragments.SettingsFragment;
import com.codepath.nytimessearch.models.Article;
import com.codepath.nytimessearch.models.Settings;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity implements SettingsFragment.SettingsListener {

    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;

    String begin_date;
    String sort;
    Set<String> news_desk;
    @BindView(R.id.toolbar) Toolbar toolbar;

    AsyncHttpClient client;
    String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
    RequestParams params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        begin_date = "";
        sort = "";
        news_desk = new HashSet<>();

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setupViews();

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().equals("Filter")) showSettingsDialog();
                return true;
            }
        });
    }

    public void setupViews() {
        client = new AsyncHttpClient();
        articles = new ArrayList<>();
        RecyclerView rvArticles = (RecyclerView) findViewById(R.id.rvArticles);
        adapter = new ArticleArrayAdapter(this, articles);
        rvArticles.setAdapter(adapter);

        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
        rvArticles.setLayoutManager(gridLayoutManager);

        news_desk = new HashSet<>();

        ItemClickSupport.addTo(rvArticles).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked (RecyclerView recyclerView, int position, View v) {
                // create an intent to display the article
                Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
                // get the article to display
                Article article = articles.get(position);
                // pass in that article into intent
                i.putExtra("article", article);
                // launch the activity
                startActivity(i);
            }
        });

        rvArticles.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                customLoadMoreDataFromApi(page);
            }
        });
    }

    public void customLoadMoreDataFromApi(int offset) {
        // NYTimes API does not allow pagination beyond page 100, but this may change, so we do not
        // put any check for page number.
        String page = String.valueOf(offset);
        if (offset > 0) params.remove("page");
        params.add("page", String.valueOf(offset));

        if (isNetworkAvailable() && isOnline()) {
            client.get(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("DEBUG", response.toString());
                    JSONArray articleJsonResults;

                    try {
                        articleJsonResults = response.getJSONObject("response").getJSONArray("docs");

                        int curSize = adapter.getItemCount();
                        articles.addAll(Article.fromJSONArray(articleJsonResults));
                        adapter.notifyItemRangeInserted(curSize, articles.size());
                        Log.d("DEBUG", articleJsonResults.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Check your Internet connection", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                onArticleSearch(query);

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onArticleSearch(String query) {
        params = new RequestParams();
        params.put("api-key", "034ea0fa1f7942099700467445e5c69f");
        params.put("page", 0);
        if (query.length() > 0) params.put("q", query);
        if (begin_date.length() > 0) {
            params.put("begin_date", begin_date);
        }
        if (sort.length() > 0) {
            params.put("sort", sort);
        }
        if (!news_desk.isEmpty()) {
            String nd = TextUtils.join("%20", news_desk);
            params.put("fq", "news_desk:(" + nd + ")");
        }

        customLoadMoreDataFromApi(0);
    }

    private void showSettingsDialog() {
        begin_date = "";    // Clear old filters.
        sort = "";
        news_desk = new HashSet<>();

        FragmentManager fm = getSupportFragmentManager();
        SettingsFragment settingsFragment = SettingsFragment.newInstance("Settings");
        settingsFragment.show(fm, "fragment_settings");
    }

    @Override
    public void onFinishEditDialog (Settings settings) {
        begin_date = convertDateFormat(settings.getBegin());
        sort = settings.getSort();
        if (settings.getArts()) news_desk.add("\"arts\"");
        if (settings.getFashion()) news_desk.add("\"fashion\"");
        if (settings.getSports()) news_desk.add("\"sports\"");
    }

    public String convertDateFormat(String d) {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String newDateString = "";
        try {
            Date date = df.parse(d);
            newDateString = formatter.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return newDateString;
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }

}
