package com.camerasurfaceview.activity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.camerasurfaceview.R;
import com.camerasurfaceview.api.ApiService;
import com.camerasurfaceview.api.model.YTubeIDModel;
import com.camerasurfaceview.api.responce.YTubeIDResponce;
import com.camerasurfaceview.common.App;
import com.camerasurfaceview.common.CustomProgressDialog;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ActYouTube extends AppCompatActivity {

    String TAG = "ActYouTube";

    ArrayList<YTubeIDModel> arrYTubeID = new ArrayList<YTubeIDModel>();
    int page = 1;
    String strTotalResult = "0";

    YouTubeAdapter youTubeAdapter;

    BottomSheetDialog mBottomSheetDialog;
    BottomSheetArrayAdapter bottomSheetArrayAdapter;

    /*-------- for the api call--------*/
    ApiService apiService;
    Retrofit retrofitApiCall;
    Call callApiMethod;
    CustomProgressDialog customProgressDialog;

    //https://stackoverflow.com/questions/14366648/how-can-i-get-a-channel-id-from-youtube
    //https://stackoverflow.com/questions/17680310/android-youtube-player-how-do-i-get-the-list-of-videos-in-a-channel-and-choose
    //https://stackoverflow.com/questions/26199933/youtube-api-3-0-search-videos-and-get-video-statistics-at-single-request


    //1. https://www.googleapis.com/youtube/v3/search?key=AIzaSyDRIYeF1GmtUSygkQsjhstbyfBLSMY5wS8=&part=snippet,id&order=date&maxResults=50
    //1. withSearchKeyWord:-- https://www.googleapis.com/youtube/v3/search?key=AIzaSyDRIYeF1GmtUSygkQsjhstbyfBLSMY5wS8=&q=love&part=snippet,id&order=date&maxResults=50
    //2. http://www.youtube.com/watch?v={video_id_here}


    @BindView(R.id.edtSearch)
    MaterialEditText edtSearch;

    @BindView(R.id.ivSearch)
    ImageView ivSearch;

    @BindView(R.id.ivHome)
    ImageView ivHome;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @BindView(R.id.llRootLay)
    LinearLayout llRootLay;

    @BindView(R.id.materialRefreshLayout)
    MaterialRefreshLayout materialRefreshLayout;

    String strSearch = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_youtube);

        ButterKnife.bind(this);

        setRetrofit();
        initialize();
        clickEvent();
        callVideosList(page);
    }


    private void setRetrofit() {
        try {
            retrofitApiCall = new Retrofit.Builder()
                    .baseUrl(App.strBaseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofitApiCall.create(ApiService.class);
            customProgressDialog = new CustomProgressDialog(ActYouTube.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initialize() {
        try {


            materialRefreshLayout.setIsOverLay(true);
            materialRefreshLayout.setWaveShow(true);
            materialRefreshLayout.setWaveColor(0x55ffffff);
            materialRefreshLayout.setLoadMore(true);


            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ActYouTube.this);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setHasFixedSize(true);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clickEvent() {
        try {

            ivSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    strSearch = edtSearch.getText().toString().trim();

                    if (strSearch.length() > 0) {
                        page = 1;
                        arrYTubeID = new ArrayList<>();

                        callVideosList(page);
                    } else {
                        strSearch = "";
                        App.showToast("Type something to search.");
                    }
                }
            });

            ivHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    arrYTubeID = new ArrayList<>();

                    Intent i1 = new Intent(ActYouTube.this, ActDownloadProgress.class);
                    startActivity(i1);

                    strSearch = "";

                    /*edtSearch.setText("");
                    callVideosList("");*/
                }
            });


            materialRefreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
                @Override
                public void onRefresh(final MaterialRefreshLayout materialRefreshLayout) {
                    //refreshing...
                    if (App.isInternetAvail(ActYouTube.this)) {

                        page = 1;
                        arrYTubeID = new ArrayList<>();

                        callVideosList(page);


                    } else {
                        App.showToast("refresh_error");
                    }
                }

                @Override
                public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                    try {


                        if (App.isInternetAvail(ActYouTube.this)) {

                            if (arrYTubeID != null && strTotalResult.equalsIgnoreCase("" + arrYTubeID.size())) {
                                if (arrYTubeID.size() >= App.strTotalVideo) {
                                    App.showToast("no more video found.");
                                    materialRefreshLayout.setLoadMore(false);
                                }

                                materialRefreshLayout.finishRefresh();
                                materialRefreshLayout.finishRefreshLoadMore();
                            } else {
                                page = page + 1;

                                callVideosList(page);
                            }
                        } else {
                            App.showToast("no internet found");

                            materialRefreshLayout.finishRefresh();
                            materialRefreshLayout.finishRefreshLoadMore();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void callVideosList(int strPageNo) {
        try {

            //https://www.googleapis.com/youtube/v3/search?
            // key=AIzaSyDRIYeF1GmtUSygkQsjhstbyfBLSMY5wS8=
            // &part=snippet,id
            // &order=date
            // &maxResults=50

            if (App.isInternetAvail(ActYouTube.this)) {
                customProgressDialog.show();

                callApiMethod = apiService.getVideosID(
                        App.strGoogleKey,
                        "snippet,id",
                        "date",
                        "" + App.strTotalVideo,
                        strSearch
                );

                App.showLog(TAG,
                        "getVideosID------\n"
                                + "key=" + App.strGoogleKey
                                + "&part=" + "snippet,id"
                                + "&order=" + "date"
                                + "&maxResults=" + App.strTotalVideo
                                + "&q=" + strSearch
                );


                callApiMethod.enqueue(new Callback<YTubeIDResponce>() {
                    @Override
                    public void onResponse(Call<YTubeIDResponce> call, Response<YTubeIDResponce> response) {
                        try {
                            customProgressDialog.dismiss();
                            // load more refresh complete
                            materialRefreshLayout.finishRefresh();
                            materialRefreshLayout.finishRefreshLoadMore();


                            YTubeIDResponce model = response.body();
                            //Response{protocol=h2, code=404, message=, url=https://www.googleapis.com/youtube/v3/search?&key=AIzaSyDRIYeF1GmtUSygkQsjhstbyfBLSMY5wS8&part=snippet,id&order=date&maxResults=20}

                            if (model == null) {
                                //404 or the response cannot be converted to User.
                                App.showLog(TAG, "----------------NULL RESPONCE SOMETHING WRONG----------------");

                                ResponseBody responseBody = response.errorBody();
                                if (responseBody != null) {
                                    try {

                                        App.showLog(TAG, "----------------ERROOR----------------" + responseBody.string());
                                        App.showToast("message_UnknownError");

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {

                                strTotalResult = model.pageInfo.totalResults;
                                arrYTubeID.addAll(model.items);

                                if (model.pageInfo.totalResults != null) {
                                    strTotalResult = model.pageInfo.totalResults;

                                    if (Integer.parseInt(strTotalResult) > App.strTotalVideo) {
                                        materialRefreshLayout.setLoadMore(true);
                                    } else {
                                        materialRefreshLayout.setLoadMore(false);
                                    }


                                }

                                if (arrYTubeID != null && page > 1) {
                                    youTubeAdapter.notifyDataSetChanged();
                                } else {
                                    youTubeAdapter = new YouTubeAdapter(ActYouTube.this, arrYTubeID);
                                    recyclerView.setAdapter(youTubeAdapter);
                                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                                }


                            }


                        } catch (Exception e) {
                            customProgressDialog.dismiss();
                            e.printStackTrace();

                            // load more refresh complete
                            materialRefreshLayout.finishRefresh();
                            materialRefreshLayout.finishRefreshLoadMore();
                        }
                    }

                    @Override
                    public void onFailure(Call<YTubeIDResponce> call, Throwable t) {
                        App.showLog(TAG, "----------------onFailure----------------");
                        t.printStackTrace();
                        customProgressDialog.dismiss();
                        App.showToast("message_UnknownError");

                        // load more refresh complete
                        materialRefreshLayout.finishRefresh();
                        materialRefreshLayout.finishRefreshLoadMore();
                    }
                });

            } else {
                App.showToast("message_NoInternetConnection");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class YouTubeAdapter extends RecyclerView.Adapter<YouTubeAdapter.VersionViewHolder> {
        ArrayList<YTubeIDModel> arrYTubeIDModel;
        Context mContext;

        public YouTubeAdapter(Context context, ArrayList<YTubeIDModel> arrYTubeIDModel) {
            this.arrYTubeIDModel = arrYTubeIDModel;
            mContext = context;
        }

        @Override
        public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_youtube, viewGroup, false);
            VersionViewHolder viewHolder = new VersionViewHolder(view);
            return viewHolder;
        }


        @Override
        public void onBindViewHolder(final VersionViewHolder viewHolder, final int i) {
            try {

                final YTubeIDModel model = arrYTubeIDModel.get(i);

                viewHolder.ivVideoImg.setImageURI(Uri.parse(model.snippet.thumbnails.highUrl.url));
                viewHolder.tvTitle.setText("" + model.snippet.title);

                viewHolder.llRootLay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent i1 = new Intent(ActYouTube.this, ActYTubePlay.class);
                        i1.putExtra("YTubeIDModel", model);
                        startActivity(i1);
                    }
                });

                viewHolder.ivDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        customProgressDialog.show();
                        getYoutubeDownloadUrl("http://www.youtube.com/watch?v=" + model.id.videoId);
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return arrYTubeIDModel.size();
        }


        class VersionViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle;
            ImageView ivDownload;
            SimpleDraweeView ivVideoImg;
            LinearLayout llRootLay;

            public VersionViewHolder(View itemView) {
                super(itemView);

                llRootLay = (LinearLayout) itemView.findViewById(R.id.llRootLay);
                ivVideoImg = (SimpleDraweeView) itemView.findViewById(R.id.ivVideoImg);
                tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
                ivDownload = (ImageView) itemView.findViewById(R.id.ivDownload);
            }
        }


    }


    /*
    * Download Video
    * */
    public void getYoutubeDownloadUrl(String youtubeLink) {

        try {


            new YouTubeExtractor(this) {
                @Override
                public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
                    if (ytFiles == null) {
                        App.showToast("No video found for download.");
                        finish();
                        customProgressDialog.dismiss();
                        return;
                    }


                    if (ytFiles != null && ytFiles.size() > 0) {
                        ArrayList<YtFile> arrYouTube = new ArrayList<>();
                        arrYouTube.addAll(asList(ytFiles));

                        showBottomDialogMonthYear(arrYouTube, vMeta);
                    } else {
                        customProgressDialog.dismiss();
                        App.showToast("No video found for download.");
                    }


                }
            }.extract(youtubeLink, true, false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <C> List<C> asList(SparseArray<C> sparseArray) {
        if (sparseArray == null) return null;
        List<C> arrayList = new ArrayList<C>(sparseArray.size());
        for (int i = 0; i < sparseArray.size(); i++)
            arrayList.add(sparseArray.valueAt(i));
        return arrayList;
    }

    public void showBottomDialogMonthYear(ArrayList<YtFile> arrYouTube, VideoMeta vMeta) {
        try {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.bottomsheet_youtube, null, false);


            RecyclerView recyclerBottomSheet = (RecyclerView) view.findViewById(R.id.recyclerBottomSheet);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ActYouTube.this);
            recyclerBottomSheet.setLayoutManager(linearLayoutManager);
            recyclerBottomSheet.setHasFixedSize(true);

            bottomSheetArrayAdapter = new BottomSheetArrayAdapter(ActYouTube.this, arrYouTube, vMeta);
            recyclerBottomSheet.setAdapter(bottomSheetArrayAdapter);
            recyclerBottomSheet.setItemAnimator(new DefaultItemAnimator());


            mBottomSheetDialog = new BottomSheetDialog(ActYouTube.this, R.style.BottomSheetDialog);
            mBottomSheetDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mBottomSheetDialog.setContentView(view);
            mBottomSheetDialog.setCancelable(true);

            mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
            mBottomSheetDialog.show();
            customProgressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class BottomSheetArrayAdapter extends RecyclerView.Adapter<BottomSheetArrayAdapter.VersionViewHolder> {
        ArrayList<YtFile> arrYouTube;
        Context mContext;
        VideoMeta vMeta;

        public BottomSheetArrayAdapter(Context context, ArrayList<YtFile> arrYouTube, VideoMeta vMeta) {
            this.arrYouTube = arrYouTube;
            this.vMeta = vMeta;
            mContext = context;

        }

        @Override
        public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_bottomsheet, viewGroup, false);
            VersionViewHolder viewHolder = new VersionViewHolder(view);
            return viewHolder;
        }


        @Override
        public void onBindViewHolder(final VersionViewHolder versionViewHolder, final int i) {
            try {

                final YtFile modelYouTube = arrYouTube.get(i);

                // App.showLog(TAG, i+": "+modelYouTube);

                /*YtFile
                 {
                     format=
                         Format
                         {
                            itag=17,
                            ext='3gp',
                            height=144,
                            fps=30,
                            vCodec=null,
                            aCodec=null,
                            audioBitrate=24,
                            isDashContainer=false,
                            isHlsContent=false
                         },
                         url='https://r5---sn-cvh76n7k.googlevideo.com/videoplayback?initcwndbps=571250&clen=74965653&ipbits=0&signature=AB245D1F998F004C1E4F3A87FBF9FA7A26FB6C8A.5CB011A4BF2EA3B70866D1AA9E0ED352E29FAC3E&mm=31%2C26&mn=sn-cvh76n7k%2Csn-h5576n7k&id=o-AK_H_Gn4Wovy87_4ppoXpER3DJkATyxP2S0EthOgc1y_&mv=m&mt=1519296061&gir=yes&ms=au%2Conr&ip=106.201.230.252&key=yt6&gcr=in&requiressl=yes&ei=tZ6OWozGEM27ogPG6LzQBQ&fvip=2&lmt=1519295102905151&mime=video%2F3gpp&c=WEB&expire=1519317781&source=youtube&sparams=clen%2Cdur%2Cei%2Cgcr%2Cgir%2Cid%2Cinitcwndbps%2Cip%2Cipbits%2Citag%2Clmt%2Cmime%2Cmm%2Cmn%2Cms%2Cmv%2Cpl%2Crequiressl%2Csource%2Cexpire&dur=7632.909&itag=17&pl=22'
                 }*/


                /*
                vMeta: VideoMeta
                {
                    videoId='Po9CO7CoOBo',
                    title='Kashmir’s First AC Train Chugs To Give Flip To Valley Tourism',
                    author='Greater Kashmir',
                    channelId='UCmzsSbFbWqDRNDem1idwNyQ',
                    videoLength=173,
                    viewCount=99,
                    isLiveStream=false
                 }
                */


                String videoType = "" + modelYouTube.getFormat().getExt();
                String videoQuality = "" + modelYouTube.getFormat().getHeight();
                String videoDuration = "" + App.timeConversion("" + vMeta.getVideoLength());


                //vMeta: VideoMeta{videoId='Po9CO7CoOBo', title='Kashmir’s First AC Train Chugs To Give Flip To Valley Tourism', author='Greater Kashmir', channelId='UCmzsSbFbWqDRNDem1idwNyQ', videoLength=173, viewCount=99, isLiveStream=false}
                // App.showLog(TAG, "vMeta: "+vMeta);
                // App.showLog(TAG, "modelYouTube: "+modelYouTube);

                versionViewHolder.tvVideoType.setText("" + videoType);
                versionViewHolder.tvVideoQuality.setText("" + videoQuality);
                versionViewHolder.tvVideoDuration.setText("" + videoDuration);

                versionViewHolder.llMainLay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String videoFullPath;
                        String videoTitle = vMeta.getTitle();

                        if (videoTitle.length() > 55) {
                            videoFullPath = videoTitle.substring(0, 55) + "." + modelYouTube.getFormat().getExt();
                        } else {
                            videoFullPath = videoTitle + "." + modelYouTube.getFormat().getExt();
                        }
                        videoFullPath = videoFullPath.replaceAll("\\\\|>|<|\"|\\||\\*|\\?|%|:|#|/", "");


                        mBottomSheetDialog.dismiss();
                        //App.showToast("Video will start download.");

                        Intent i1 = new Intent(ActYouTube.this, ActDownloadProgress.class);
                        i1.putExtra("from", "ActYouTube");
                        i1.putExtra("VideoUrl", modelYouTube.getUrl());
                        i1.putExtra("VideoTitle", videoTitle);
                        i1.putExtra("VideoFullPath", videoFullPath);
                        startActivity(i1);

                        //downloadFromUrl(modelYouTube.getUrl(), videoTitle, videoFullPath);
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return arrYouTube.size();
        }


        class VersionViewHolder extends RecyclerView.ViewHolder {
            TextView tvVideoType, tvVideoQuality, tvVideoDuration;
            LinearLayout llMainLay;

            public VersionViewHolder(View itemView) {
                super(itemView);

                tvVideoType = (TextView) itemView.findViewById(R.id.tvVideoType);
                tvVideoQuality = (TextView) itemView.findViewById(R.id.tvVideoQuality);
                tvVideoDuration = (TextView) itemView.findViewById(R.id.tvVideoDuration);
                llMainLay = (LinearLayout) itemView.findViewById(R.id.llMainLay);
            }
        }


    }

    private void downloadFromUrl(String youtubeDlUrl, String downloadTitle, String videoFullPath) {
        //App.showLog(TAG, "downloadFromUrl");
        Uri uri = Uri.parse(youtubeDlUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle(downloadTitle);

        //App.showLog(TAG, "uri: "+uri);
        // App.showLog(TAG, "downloadTitle: "+downloadTitle);

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, videoFullPath);

        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

}
