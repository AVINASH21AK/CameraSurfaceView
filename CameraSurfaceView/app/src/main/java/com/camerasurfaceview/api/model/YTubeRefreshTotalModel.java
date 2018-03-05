package com.camerasurfaceview.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class YTubeRefreshTotalModel implements Serializable {

    //https://www.googleapis.com/youtube/v3/search?key=AIzaSyDRIYeF1GmtUSygkQsjhstbyfBLSMY5wS8=&part=snippet,id&order=date&maxResults=50

    @SerializedName("totalResults")
    public String totalResults;

}
