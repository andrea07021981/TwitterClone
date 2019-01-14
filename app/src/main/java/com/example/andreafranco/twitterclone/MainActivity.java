package com.example.andreafranco.twitterclone;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity {

    EditText mUsernameEditText, mPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUsernameEditText = findViewById(R.id.username_edittext);
        mPasswordEditText = findViewById(R.id.password_edittext);
    }

    private void redirectUser() {
        if (ParseUser.getCurrentUser() != null) {
            Intent intent = new Intent(this, UsersListActivity.class);
            startActivity(intent);
        }
    }
    public void loginClick(View view) {
        //Check whether the user exist or not
        final String username = mUsernameEditText.getText().toString();
        final String password = mPasswordEditText.getText().toString();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Username and password are mandatory", Toast.LENGTH_SHORT).show();
        } else {
            ParseUser.logInInBackground(username, password, new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (e == null && user != null) {
                        //LOGIN DONE
                        redirectUser();
                    } else {
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this)
                                .setCancelable(false)
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //Sign up a new user
                                        ParseUser newUser = new ParseUser();
                                        newUser.setUsername(username);
                                        newUser.setPassword(password);
                                        newUser.signUpInBackground(new SignUpCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    //SIGNUP COMPLETED
                                                    redirectUser();
                                                }
                                            }
                                        });
                                    }
                                })
                                .setNegativeButton("Cancell", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();
                                    }
                                })
                                .setTitle("Login")
                                .setMessage("Username doesn't exist, Would you like to create a new one?");
                        AlertDialog alertDialog = alertBuilder.create();
                        alertDialog.show();
                    }
                }
            });
        }
    }
}
