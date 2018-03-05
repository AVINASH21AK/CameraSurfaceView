package com.camerasurfaceview.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class YTubeIDModel implements Serializable {

    //https://www.googleapis.com/youtube/v3/search?key=AIzaSyDRIYeF1GmtUSygkQsjhstbyfBLSMY5wS8=&part=snippet,id&order=date&maxResults=50


    /*
    * Main
    * */
    @SerializedName("id")
    public ID id;

    @SerializedName("snippet")
    public SNIPPET snippet;


    /*
    * Sub of above
    * */
    public class ID implements Serializable{
        @SerializedName("videoId")
        public String videoId;
    }

    public class SNIPPET implements Serializable{
        @SerializedName("title")
        public String title;

        @SerializedName("thumbnails")
        public THUMBNAILS thumbnails;

    }

    public class THUMBNAILS implements Serializable{
        @SerializedName("high")
        public HIGH highUrl;
    }

    public class HIGH implements Serializable{
        @SerializedName("url")
        public String url;
    }

}
