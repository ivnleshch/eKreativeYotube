package com.playlist.youtube.ivleshch.youtubeplaylist.data.retrofit.playlist;

public class PageInfo {
    private int totalResults;
    private int resultsPerPage;

    public PageInfo() {
    }

    public int getTotalResults() {
        return totalResults;
    }
    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getResultsPerPage() {
        return resultsPerPage;
    }
    public void setResultsPerPage(int resultsPerPage) {
        this.resultsPerPage = resultsPerPage;
    }
}