package com.example.andreafranco.twitterclone;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        setTitle("Your feeds");

        final ListView feedsListView = findViewById(R.id.feeds_listview);
        final List<Map<String, String>> feedsList = new ArrayList();

        final SimpleAdapter simpleAdapter = new SimpleAdapter(
                this,
                feedsList,
                android.R.layout.simple_list_item_2,
                new String[]{"content","username"},
                new int[] {android.R.id.text1, android.R.id.text2}) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View view = super.getView(position, convertView, parent);
                TextView textView1 = view.findViewById(android.R.id.text1);
                textView1.setTextColor(Color.BLACK);
                TextView textView2 = view.findViewById(android.R.id.text2);
                textView2.setTextColor(Color.BLACK);
                return view;
            }
        };

        feedsListView.setAdapter(simpleAdapter);
        ParseQuery<ParseObject> parseQuery = new ParseQuery<ParseObject>("Tweet");
        parseQuery.whereContainedIn("username", ParseUser.getCurrentUser().getList(UsersListActivity.FOLLOWING_COLUMN));
        parseQuery.orderByDescending("createdAt");
        parseQuery.setLimit(20);
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() > 0) {
                    for (ParseObject object : objects) {
                        Map<String,String> map = new HashMap<>();
                        map.put("content", object.get("tweet").toString().replaceAll("\\[", "").replaceAll("\\]",""));
                        map.put("username", object.get("username").toString().replaceAll("\\[", "").replaceAll("\\]",""));
                        feedsList.add(map);
                    }
                    simpleAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}
