package com.playlist.youtube.ivleshch.youtubeplaylist.data.retrofit.playlist;

public class Snippet {

    private String title;
    private String description;
    private int position;
    private String playlistId;
    private Thumbnails thumbnails;
    private ResourceId resourceId;

    public Snippet() {
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public int getPosition() {
        return position;
    }
    public void setPosition(int position) {
        this.position = position;
    }

    public String getPlaylistId() {
        return playlistId;
    }
    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    public Thumbnails getThumbnails() {
        return thumbnails;
    }
    public void setThumbnails(Thumbnails thumbnails) {
        this.thumbnails = thumbnails;
    }

    public ResourceId getResourceId() {
        return resourceId;
    }
    public void setResourceId(ResourceId resourceId) {
        this.resourceId = resourceId;
    }
}
