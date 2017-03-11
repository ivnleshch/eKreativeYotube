package com.playlist.youtube.ivleshch.youtubeplaylist.utils;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Ivleshch on 10.03.2017.
 */

public abstract class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {
    // The minimum amount of items to have below your current scroll position before loading more
    private int visibleThreshold = 5;
    // The current offset index of data you have loaded
    private int previousTotalItemCount = 0;
    // True if we are still waiting for the last set of data to load.
    private boolean loading = false;

    private LinearLayoutManager linearLayoutManager;

    public EndlessRecyclerViewScrollListener(LinearLayoutManager linearLayoutManager) {
        this.linearLayoutManager = linearLayoutManager;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    @Override
    public void onScrolled(RecyclerView view, int dx, int dy) {
        if (dy > 0) {  //check for scroll down
            // Get the number of items already loaded
            int totalItemCount = linearLayoutManager.getItemCount();
            // Get last item visible on the screen
            int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
            // If it isn’t currently loading, we check to see if we have breached
            // the visibleThreshold and need to reload more data.
            // If we do need to reload some more data, we execute loadMore to fetch the data.
            if (!loading && (lastVisibleItemPosition + visibleThreshold) > totalItemCount) {
                loading = true;
                loadMore();
            }
        }
    }

    public abstract void loadMore();

}