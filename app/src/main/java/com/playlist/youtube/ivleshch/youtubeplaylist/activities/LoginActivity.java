package com.playlist.youtube.ivleshch.youtubeplaylist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.playlist.youtube.ivleshch.youtubeplaylist.R;

public class LoginActivity extends AppCompatActivity {

    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private Profile currentProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        currentProfile = Profile.getCurrentProfile();
        if (currentProfile != null) {
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            if (!accessToken.getToken().isEmpty() && !accessToken.isExpired()) {
                startMainActivity();
            } else {
                LoginManager.getInstance().logOut();  // Force logout
            }
        }

        // Request Login
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton)findViewById(R.id.btnFacebook);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                startMainActivity();
            }

            @Override
            public void onCancel() {
                Toast.makeText(
                        LoginActivity.this,
                        getResources().getString(R.string.login_attempt_failed),
                        Toast.LENGTH_LONG
                ).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(
                        LoginActivity.this,
                        error.getLocalizedMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void startMainActivity() {
        Intent intent = new Intent(getApplicationContext(), PlayListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
