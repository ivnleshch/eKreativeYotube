package com.playlist.youtube.ivleshch.youtubeplaylist.rest;


import com.playlist.youtube.ivleshch.youtubeplaylist.data.retrofit.playlist.Playlist;
import com.playlist.youtube.ivleshch.youtubeplaylist.data.retrofit.video.Video;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitApiInterface {
    @GET("youtube/v3/playlistItems")
    Call<Playlist> getPlayList(
            @Query("part") String part,
            @Query("playlistId") String playlistId,
            @Query("key") String key,
            @Query("maxResults") int maxResults,
            @Query("pageToken") String pageToken);
    @GET("youtube/v3/videos")
    Call<Video> getVideos(
            @Query("part") String part,
            @Query("id") String ids,
            @Query("key") String key);
}