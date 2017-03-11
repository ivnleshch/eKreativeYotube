package com.playlist.youtube.ivleshch.youtubeplaylist.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.playlist.youtube.ivleshch.youtubeplaylist.R;
import com.playlist.youtube.ivleshch.youtubeplaylist.data.Constants;

;

/**
 * Created by Ivleshch on 10.03.2017.
 */

public class PlayListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener // override onNavigationItemSelected
{

    private NavigationView navigationView;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private SharedPreferences sharedPref;
    private FragmentManager fragmentManager;
    private ProfileTracker profileTracker;
    private AccessTokenTracker accessTokenTracker;
    private AccessToken currentToken;
    private Profile currentProfile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_play_list);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(PlayListActivity.this);
        fragmentManager = getSupportFragmentManager();
        addToolbar();
        initNavigationDrawer();
        if (savedInstanceState == null) {
            setOrUpdatePlaylist();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Start tracking profile and access token changes
        initProfileTracker();
        checkAccessToken();
    }

    @Override
    protected void onPause() {
        // Stop tracking profile and access token changes
        profileTracker.stopTracking();
        accessTokenTracker.stopTracking();
        super.onPause();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_01:
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                saveChosenPlaylist(0);
                setOrUpdatePlaylist();
                break;
            case R.id.menu_item_02:
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                saveChosenPlaylist(1);
                setOrUpdatePlaylist();
                break;
            case R.id.menu_item_03:
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                saveChosenPlaylist(2);
                setOrUpdatePlaylist();
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_logout:
                LoginManager.getInstance().logOut();
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(Constants.USER_NAME_CARRIER, "Anonymous");
                editor.apply();
                Intent intent = new Intent(PlayListActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Save id (number) of current playlist to shared preferences.
     * @param listNumber number of the list.
     */
    private void saveChosenPlaylist(int listNumber) {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(PlayListActivity.this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(Constants.CURRENT_LIST_CARRIER, listNumber);
        editor.apply();
    }

    private void setOrUpdatePlaylist() {
        fragmentManager.beginTransaction()
                .replace(R.id.container_for_list, new PlayListFragment())
                .commit();
    }

    private void addToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    private void initNavigationDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        drawerLayout.addDrawerListener(drawerToggle);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * Checks if user still logged in and starts tracking changes in access token.
     */
    private void checkAccessToken() {
        currentToken = AccessToken.getCurrentAccessToken();
        if (currentToken == null || currentToken.isExpired()) {
            forceLogout();
        }
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null || currentAccessToken.isExpired()) {
                    forceLogout();
                }
            }
        };
        accessTokenTracker.startTracking();
    }

    /**
     * Gets username prom facebook profile and starts tracking user's profile changes.
     */
    private void initProfileTracker() {
        currentProfile = Profile.getCurrentProfile();
        if(currentProfile != null) {
            addUsernameToToolbar(currentProfile.getName());
        }
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                currentProfile = profile2;
                if (currentProfile != null) {
                    addUsernameToToolbar(currentProfile.getName());
                }
            }
        };
        profileTracker.startTracking();
    }

    /**
     * Adds user name to the toolbar.
     * @param username is full name of a user.
     */
    private void addUsernameToToolbar(String username) {
        if (username == null) {return;}
        actionBar.setTitle(username);
    }

    /**
     * Initiates force logout of the user.
     */
    private void forceLogout() {
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


}
