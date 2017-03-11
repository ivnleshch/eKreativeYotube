package com.playlist.youtube.ivleshch.youtubeplaylist.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.playlist.youtube.ivleshch.youtubeplaylist.R;
import com.playlist.youtube.ivleshch.youtubeplaylist.data.Constants;
import com.playlist.youtube.ivleshch.youtubeplaylist.data.VideoClip;
import com.playlist.youtube.ivleshch.youtubeplaylist.data.retrofit.playlist.Item;
import com.playlist.youtube.ivleshch.youtubeplaylist.data.retrofit.playlist.Playlist;
import com.playlist.youtube.ivleshch.youtubeplaylist.data.retrofit.video.ItemV;
import com.playlist.youtube.ivleshch.youtubeplaylist.data.retrofit.video.Video;
import com.playlist.youtube.ivleshch.youtubeplaylist.rest.RestApiFactory;
import com.playlist.youtube.ivleshch.youtubeplaylist.rest.RetrofitApiInterface;
import com.playlist.youtube.ivleshch.youtubeplaylist.utils.AdapterPlayList;
import com.playlist.youtube.ivleshch.youtubeplaylist.utils.EndlessRecyclerViewScrollListener;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Ivleshch on 10.03.2017.
 */

public class PlayListFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ProgressBar progressBar;
    private RetrofitApiInterface api;
    private SharedPreferences sharedPref;
    private AdapterPlayList adapterPlayList;
    private String currentList;
    private String nextPageToken;
    private boolean isAttached;
    private ArrayList<VideoClip> videoClips;
    private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;
    private Realm realm;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        isAttached = true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());

        videoClips = new ArrayList<>();

        initRecyclerView(view);
        initProgressBar(view);
        api = RestApiFactory.getRetrofitApiInterface();
        if (getChosenList() != -1) {  // Some playlist has been already chosen
            displayPlaylist();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        isAttached = false;
    }

    private int getChosenList() {
        int chosenList = sharedPref.getInt(Constants.CURRENT_LIST_CARRIER, -1);
        switch (chosenList) {
            case 0:
                currentList = Constants.PLAYLIST1;
                break;
            case 1:
                currentList = Constants.PLAYLIST2;
                break;
            case 2:
                currentList = Constants.PLAYLIST3;
                break;
            default:
                break;
        }
        return chosenList;
    }

    private void initRecyclerView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(null);
    }

    private void initProgressBar(View view) {
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
    }

    private void displayPlaylist() {
        adapterPlayList = new AdapterPlayList(getContext(), currentList);
        recyclerView.setAdapter(adapterPlayList);
        endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            // Next method runs if we are in the bottom of playlist
            // (5 items before the end of the list)
            @Override
            public void loadMore() {
                if (nextPageToken != null) {
                    if (isNetworkAvailable(getContext())) {
                        progressBar.setVisibility(View.VISIBLE);
                        loadPlayList(nextPageToken);
                    } else {
                        Snackbar snackbar = Snackbar.make(recyclerView, R.string.no_connection, Snackbar.LENGTH_SHORT);
                        snackbar.show();
                        endlessRecyclerViewScrollListener.setLoading(false);
                    }
                }
            }
        };
        recyclerView.addOnScrollListener(endlessRecyclerViewScrollListener);

        if (isNetworkAvailable(getContext())) {
            progressBar.setVisibility(View.VISIBLE);
            loadPlayList(nextPageToken);
        } else {
            Snackbar snackbar = Snackbar.make(recyclerView, R.string.no_connection, Snackbar.LENGTH_SHORT);
            snackbar.show();
            endlessRecyclerViewScrollListener.setLoading(false);
        }
    }

    /**
     * Loads next page of the playlist.
     * @param pageToken a token for the next page of the data.
     */
    private void loadPlayList(String pageToken) {
        if (!isAttached) {
            return;
        }
        Call<Playlist> call = api.getPlayList(
                "snippet", currentList,
                getString(R.string.youtube_api_key), 10, pageToken);
        call.enqueue(new Callback<Playlist>() {
            @Override
            public void onResponse(Call<Playlist> call, Response<Playlist> response) {
                loadVideoDurations(response.body());
            }

            @Override
            public void onFailure(Call<Playlist> call, Throwable t) {
                Log.v("Retrofit", t.getMessage());
                progressBar.setVisibility(View.GONE);
                endlessRecyclerViewScrollListener.setLoading(false);
            }
        });
    }

    /**
     * Another method that loads duration times for items in the playlist.
     * @param playlist is a current playlist.
     */
    private void loadVideoDurations(final Playlist playlist) {
        if (!isAttached) {
            return;
        }
        Call<Video> call = api.getVideos(
                "contentDetails", genStringOfIds(playlist),
                getString(R.string.youtube_api_key));
        call.enqueue(new Callback<Video>() {
            @Override
            public void onResponse(Call<Video> call, Response<Video> response) {

                // Save the data
                putEntiesToDB(playlist, response.body());
                // Update the list view
                nextPageToken = playlist.getNextPageToken();
            }

            @Override
            public void onFailure(Call<Video> call, Throwable t) {
                Log.v("Retrofit", t.getMessage());
                progressBar.setVisibility(View.GONE);
                endlessRecyclerViewScrollListener.setLoading(false);
            }
        });
    }

    /**
     * Generates a comma separated string of ids of videos contained in a playlist.
     * @param playlist is a playlist.
     * @return a string of ids.
     */
    private String genStringOfIds(Playlist playlist) {
        StringBuilder stringBuilder = new StringBuilder();
        String delim = "";
        for (Item item : playlist.getItems()) {
            stringBuilder.append(delim).append(item.getSnippet().getResourceId().getVideoId());
            delim = ",";
        }
        return stringBuilder.toString();
    }


    /**
     * Save data to the Realm
     * @param playlist is a list with new clips (+10 items)
     */
    private void putEntiesToDB(Playlist playlist, Video videos) {
        for (int i = 0; i < playlist.getItems().size(); i++) {
            Item item = playlist.getItems().get(i);
            ItemV itemV = videos.getItems().get(i);

            VideoClip videoClip = new VideoClip();
            videoClip.setItemId(item.getSnippet().getResourceId().getVideoId());
            videoClip.setTitle(item.getSnippet().getTitle());
            videoClip.setDescription(item.getSnippet().getDescription());
            videoClip.setDuration(itemV.getContentDetails().getDuration());
            videoClip.setThumbnail(item.getSnippet().getThumbnails().getMedium().getUrl());
            videoClip.setPlaylistId(item.getSnippet().getPlaylistId());


            videoClips.add(videoClip);
        }

        realm = Realm.getDefaultInstance();

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.insertOrUpdate(videoClips);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {

                realm = Realm.getDefaultInstance();

                RealmResults<VideoClip> videoClips = realm.where(VideoClip.class)
                        .equalTo("playlistId", currentList)
                        .findAll();

                List<VideoClip> listClips = realm.copyFromRealm(videoClips);

                adapterPlayList.setPlaylist(listClips);
                adapterPlayList.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                endlessRecyclerViewScrollListener.setLoading(false);


            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                progressBar.setVisibility(View.GONE);
                endlessRecyclerViewScrollListener.setLoading(false);
            }
        });

    }

    /**
     * This method checks the Internet connection.
     * @param context is a current context.
     * @return true if connection is available and false otherwise.
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

}
