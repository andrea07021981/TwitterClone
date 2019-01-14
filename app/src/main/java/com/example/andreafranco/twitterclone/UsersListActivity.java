package com.example.andreafranco.twitterclone;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class UsersListActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String FOLLOWING_COLUMN = "isFollowing";

    private ArrayList<String> mUsers;
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);
        setTitle(ParseUser.getCurrentUser().getUsername() + "'s following users");
        mUsers = new ArrayList<>();

        mAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_checked, mUsers);
        final ListView usersListView = findViewById(R.id.users_listview);
        usersListView.setAdapter(mAdapter);
        usersListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CheckedTextView checkedTextView = (CheckedTextView) view;
                if (checkedTextView.isChecked()) {
                    Log.d(TAG, "Checked");
                    ParseUser.getCurrentUser().add(FOLLOWING_COLUMN, mUsers.get(i));
                } else {
                    Log.d(TAG, "UnChecked");
                    ParseUser.getCurrentUser().getList(FOLLOWING_COLUMN).remove(mUsers.get(i));
                    List tempUsers = ParseUser.getCurrentUser().getList(FOLLOWING_COLUMN);
                    ParseUser.getCurrentUser().remove(FOLLOWING_COLUMN);
                    ParseUser.getCurrentUser().put(FOLLOWING_COLUMN, tempUsers);
                }
                ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(UsersListActivity.this, "Changes saved", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UsersListActivity.this, "Error saving: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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

                    List<Object> listFollowingUsers = ParseUser.getCurrentUser().getList(FOLLOWING_COLUMN);
                    for (String user : mUsers) {
                        if (listFollowingUsers != null && listFollowingUsers.contains(user)) {
                            usersListView.setItemChecked(mUsers.indexOf(user), true);
                        }
                    }
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {

            case R.id.tweet_item:
                createTweet();
                break;

            case R.id.logout_item:
                logOut();
                break;

            case R.id.feed_item_item:
                Intent feeedActivityIntent = new Intent(this, FeedActivity.class);
                startActivity(feeedActivityIntent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createTweet() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Send a Tweet");
        final EditText messageEditText = new EditText(this);
        alertBuilder.setView(messageEditText);
        alertBuilder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ParseObject tweetParseObject = new ParseObject("Tweet");
                tweetParseObject.add("tweet", messageEditText.getText().toString());
                tweetParseObject.add("username", ParseUser.getCurrentUser().getUsername());
                tweetParseObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(getApplicationContext(), "Tweet sent!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Error logging out: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertBuilder.create().show();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("Info message")
                .setMessage("Would you like to log out?")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logOut();
                    }
                }).show();
    }

    private void logOut() {
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    //Log out done
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Error logging out: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
