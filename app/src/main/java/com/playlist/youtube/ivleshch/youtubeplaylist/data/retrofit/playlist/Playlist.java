package com.playlist.youtube.ivleshch.youtubeplaylist.data.retrofit.playlist;

import java.util.List;

public class Playlist {

    private String nextPageToken;
    private String prevPageToken;
    private PageInfo pageInfo;
    private List<Item> items;

    public Playlist() {
    }

    public String getNextPageToken() {
        return nextPageToken;
    }
    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public String getPrevPageToken() {
        return prevPageToken;
    }
    public void setPrevPageToken(String prevPageToken) {
        this.prevPageToken = prevPageToken;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }
    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public List<Item> getItems() {
        return items;
    }
    public void setItems(List<Item> items) {
        this.items = items;
    }
}
