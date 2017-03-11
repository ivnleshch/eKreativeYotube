package com.playlist.youtube.ivleshch.youtubeplaylist.data.retrofit.video;

import java.util.List;

public class Video {

    private List<ItemV> items;

    public Video() {
    }

    public List<ItemV> getItems() {
        return items;
    }
    public void setItems(List<ItemV> items) {
        this.items = items;
    }
}
