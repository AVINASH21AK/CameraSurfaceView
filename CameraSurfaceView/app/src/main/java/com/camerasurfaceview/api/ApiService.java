package com.camerasurfaceview.api;

import com.camerasurfaceview.api.responce.YTubeIDResponce;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by avinash.kahar on 2/21/2018.
 */

public interface ApiService {

    //https://www.googleapis.com/youtube/v3/search?
    // key=AIzaSyDRIYeF1GmtUSygkQsjhstbyfBLSMY5wS8=
    // &part=snippet,id
    // &order=date
    // &maxResults=50
    @GET("search")
    Call<YTubeIDResponce> getVideosID(
            @Query("key") String key,
            @Query("part") String part,
            @Query("order") String order,
            @Query("maxResults") String maxResults,
            @Query("q") String q
    );

}
