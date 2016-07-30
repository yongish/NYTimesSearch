package com.codepath.nytimessearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.codepath.nytimessearch.Article;
import com.codepath.nytimessearch.ArticleArrayAdapter;
import com.codepath.nytimessearch.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {

    EditText etQuery;
    GridView gvResults;
    Button btnSearch;

    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;

    String begin_date;
    String sort;
    Set<String> news_desk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupViews();
    }

    public void setupViews() {
        etQuery = (EditText) findViewById(R.id.etQuery);
        gvResults = (GridView) findViewById(R.id.gvResults);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        articles = new ArrayList<>();
        adapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(adapter);

        // hook up listener for grid click
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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

        news_desk = new HashSet<>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
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

    public void onArticleSearch(View view) {
        String query = etQuery.getText().toString();

//        Toast.makeText(this, "Searching for" + query, Toast.LENGTH_LONG).show();
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";

        RequestParams params = new RequestParams();
        params.put("api-key", "034ea0fa1f7942099700467445e5c69f");
        params.put("page", 0);
        if (query.length() > 0) params.put("q", query);
        if (begin_date != null) {
            params.put("begin_date", begin_date);
        }
        if (sort != null) {
            params.put("sort", sort);
        }
        if (news_desk != null && !news_desk.isEmpty()) {
            String nd = TextUtils.join("%20", news_desk);
            params.put("fq", "news_desk:(" + nd + ")");
        }

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                JSONArray articleJsonResults = null;

                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    adapter.addAll(Article.fromJSONArray(articleJsonResults));
                    Log.d("DEBUG", articleJsonResults.toString());
                } catch (JSONException e) {
                   e.printStackTrace();
                }
            }
        });
    }

    private final int REQUEST_CODE = 20;
    public boolean openSettings(MenuItem item) {
        Intent i = new Intent(SearchActivity.this, SettingsActivity.class);
        startActivityForResult(i, REQUEST_CODE);
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            begin_date = convertDateFormat(data.getExtras().getString("begin"));
            sort = data.getExtras().getString("sort");

            if (data.getExtras().getBoolean("arts")) news_desk.add("\"arts\"");
            if (data.getExtras().getBoolean("fashion")) news_desk.add("\"fashion\"");
            if (data.getExtras().getBoolean("sports")) news_desk.add("\"sports\"");
        }
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

}
