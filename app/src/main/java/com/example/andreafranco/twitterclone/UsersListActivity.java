package com.example.andreafranco.twitterclone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class UsersListActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ArrayList<Object> mUsers;
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        mUsers = new ArrayList<>();

        mAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_checked, mUsers);
        ListView usersListView = findViewById(R.id.users_listview);
        usersListView.setAdapter(mAdapter);
        usersListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CheckedTextView checkedTextView = (CheckedTextView) view;
                if (checkedTextView.isChecked()) {
                    Log.d(TAG, "Checked");
                } else {
                    Log.d(TAG, "UnChecked");
                }
            }
        });

        ParseQuery<ParseUser> userList = ParseUser.getQuery();
        userList.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        userList.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null && objects.size() > 0) {
                    for (ParseUser user : objects) {
                        mUsers.add(user.getUsername());
                    }

                    mAdapter.notifyDataSetChanged();
                }
            }
        });

    }
}
