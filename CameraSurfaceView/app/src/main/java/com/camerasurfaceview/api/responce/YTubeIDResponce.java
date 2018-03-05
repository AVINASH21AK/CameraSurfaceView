package com.camerasurfaceview.api.responce;

import com.camerasurfaceview.api.model.YTubeIDModel;
import com.camerasurfaceview.api.model.YTubeRefreshTotalModel;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

public class YTubeIDResponce {

    @SerializedName("items")
    public ArrayList<YTubeIDModel> items;

    @SerializedName("pageInfo")
    public YTubeRefreshTotalModel pageInfo;


}
