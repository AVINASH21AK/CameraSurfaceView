package com.camerasurfaceview.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.camerasurfaceview.R;
import com.camerasurfaceview.api.model.YoutubeSqliteModel;
import com.camerasurfaceview.common.App;
import com.camerasurfaceview.common.DatabaseHelper;
import com.camerasurfaceview.common.Preference;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.downloader.Status;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActDownloadProgressCopy extends AppCompatActivity {

    String TAG = "ActDownloadProgress";
    String mVideoUrl, mVideoTitle, mVideoFullPath;

    DatabaseHelper dbHelper;

    @BindView(R.id.btnStart) Button btnStart;
    @BindView(R.id.btnCancal) Button btnCancal;
    @BindView(R.id.tvProgressSize) TextView tvProgressSize;
    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.recyclerView) RecyclerView recyclerView;

    int  downloadId;
    YouTubeAdapter youTubeAdapter;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_download_progresscopy);

        App.setStatusBarGradiant(ActDownloadProgressCopy.this);
        ButterKnife.bind(this);


        getActivityIntent();
        initialize();
        getSqliteData();
        clickEvent();
    }


    public void getActivityIntent(){
        try{

            if(getIntent().hasExtra("from"))
            {
                mVideoUrl = getIntent().getStringExtra("VideoUrl");
                mVideoTitle = getIntent().getStringExtra("VideoTitle");
                mVideoFullPath = getIntent().getStringExtra("VideoFullPath");

                App.showLog(TAG, "mVideoUrl: "+mVideoUrl);
                App.showLog(TAG, "mVideoTitle: "+mVideoTitle);
                App.showLog(TAG, "mVideoFullPath: "+mVideoFullPath);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void initialize(){
        try{

            dbHelper = new DatabaseHelper(this);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ActDownloadProgressCopy.this);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setHasFixedSize(true);

        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public void clickEvent(){
        try{

            btnStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (Status.RUNNING == PRDownloader.getStatus(downloadId)) {
                        PRDownloader.pause(downloadId);
                        return;
                    }

                    btnStart.setEnabled(false);
                    progressBar.setIndeterminate(true);
                    progressBar.getIndeterminateDrawable().setColorFilter(
                            Color.BLUE, android.graphics.PorterDuff.Mode.SRC_IN);

                    if (Status.PAUSED == PRDownloader.getStatus(downloadId)) {
                        PRDownloader.resume(downloadId);
                        return;
                    }
                    downloadId = PRDownloader
                            .download(mVideoUrl, App.strDicYoutube, mVideoFullPath)
                            .build()

                            .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                                @Override
                                public void onStartOrResume() {

                                    YoutubeSqliteModel modelYouTube = new YoutubeSqliteModel();
                                    modelYouTube.id = ""+downloadId;
                                    modelYouTube.videoTitle = mVideoTitle;
                                    modelYouTube.downlaodStatus = Preference.strDownloading;
                                    modelYouTube.videoExt = App.getExtention(mVideoFullPath);
                                    modelYouTube.videoURL = mVideoUrl;

                                    App.showLog(TAG, "insert downloadId: "+downloadId );
                                    //mVideoTitle, Preference.strDownloading, App.getExtention(mVideoFullPath),""
                                    dbHelper.InsertYoutube(modelYouTube);

                                    progressBar.setIndeterminate(false);
                                    btnStart.setEnabled(true);
                                    btnStart.setText(R.string.hintPause);
                                    btnCancal.setEnabled(true);
                                    btnCancal.setText(R.string.hintCancel);
                                }
                            })

                            .setOnPauseListener(new OnPauseListener() {
                                @Override
                                public void onPause() {
                                    btnStart.setText(R.string.hintResume);
                                }
                            })

                            .setOnCancelListener(new OnCancelListener() {
                                @Override
                                public void onCancel() {
                                    downloadId = 0;
                                    btnStart.setText(R.string.hintStart);
                                    btnCancal.setEnabled(false);
                                    progressBar.setProgress(0);
                                    tvProgressSize.setText("");
                                    progressBar.setIndeterminate(false);
                                }
                            })

                            .setOnProgressListener(new OnProgressListener() {
                                @Override
                                public void onProgress(Progress progress) {
                                    long progressPercent = progress.currentBytes * 100 / progress.totalBytes;
                                    progressBar.setProgress((int) progressPercent);
                                    tvProgressSize.setText(getProgressDisplayLine(progress.currentBytes, progress.totalBytes));
                                }
                            })

                            .start(new OnDownloadListener() {
                                @Override
                                public void onDownloadComplete() {
                                    btnStart.setEnabled(false);
                                    btnCancal.setEnabled(false);
                                    btnStart.setText(R.string.hintCompleted);
                                }

                                @Override
                                public void onError(Error error) {
                                    btnStart.setText(R.string.hintStart);
                                    Toast.makeText(getApplicationContext(), getString(R.string.err_DownloadFails) + " " + "13", Toast.LENGTH_SHORT).show();
                                    tvProgressSize.setText("");
                                    progressBar.setProgress(0);
                                    downloadId = 0;
                                    btnCancal.setEnabled(false);
                                    progressBar.setIndeterminate(false);
                                    btnStart.setEnabled(true);
                                }
                            });
                }
            });

            btnCancal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PRDownloader.cancel(downloadId);
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static String getProgressDisplayLine(long currentBytes, long totalBytes) {
        return getBytesToMBString(currentBytes) + "/" + getBytesToMBString(totalBytes);
    }

    private static String getBytesToMBString(long bytes){
        return String.format(Locale.ENGLISH, "%.2fMb", bytes / (1024.00 * 1024.00));
    }


    public void getSqliteData(){
        try{


            ArrayList<YoutubeSqliteModel> arrYoutube = new ArrayList<>();
            arrYoutube = dbHelper.getAllYouTubeSqlite();

            if(arrYoutube != null && arrYoutube.size()>0)
            {
                youTubeAdapter = new YouTubeAdapter(ActDownloadProgressCopy.this, arrYoutube);
                recyclerView.setAdapter(youTubeAdapter);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public class YouTubeAdapter extends RecyclerView.Adapter<YouTubeAdapter.VersionViewHolder> {
        ArrayList<YoutubeSqliteModel> arrYTubeIDModel;
        Context mContext;

        public YouTubeAdapter(Context context, ArrayList<YoutubeSqliteModel> arrYTubeIDModel) {
            this.arrYTubeIDModel = arrYTubeIDModel;
            mContext = context;
        }

        @Override
        public VersionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_downloadyoutubecopy, viewGroup, false);
            VersionViewHolder viewHolder = new VersionViewHolder(view);
            return viewHolder;
        }


        @Override
        public void onBindViewHolder(final VersionViewHolder viewHolder, final int i) {
            try {

                final YoutubeSqliteModel model = arrYTubeIDModel.get(i);

                viewHolder.tvVideoTitle.setText("NAME: "+model.videoTitle+"---video status: "+PRDownloader.getStatus(Integer.parseInt(model.id)));
                App.showLog(TAG, "getdata: "+i+" "+model.id +" - "+PRDownloader.getStatus(Integer.parseInt(model.id)));


                viewHolder.btnVideoStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        PRDownloader.initialize(ActDownloadProgressCopy.this);
                        int videoDownloadID = Integer.parseInt(model.id);


                        final int finalVideoDownloadID = videoDownloadID;
                        videoDownloadID = PRDownloader
                                .download(model.videoURL, App.strDicYoutube, model.videoTitle+model.videoExt)
                                .build()

                                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                                    @Override
                                    public void onStartOrResume() {

                                        if (Status.RUNNING == PRDownloader.getStatus(finalVideoDownloadID)) {
                                            PRDownloader.pause(finalVideoDownloadID);
                                            return;
                                        }

                                        viewHolder.btnVideoStart.setEnabled(false);
                                        viewHolder.videoProgressBar.setIndeterminate(true);
                                        viewHolder.videoProgressBar.getIndeterminateDrawable().setColorFilter(
                                                Color.BLUE, android.graphics.PorterDuff.Mode.SRC_IN);

                                        if (Status.PAUSED == PRDownloader.getStatus(finalVideoDownloadID)) {
                                            PRDownloader.resume(finalVideoDownloadID);

                                            App.showLog(TAG, "setOnStartOrResumeListener");

                                            viewHolder.videoProgressBar.setIndeterminate(false);
                                            viewHolder.btnVideoStart.setEnabled(true);
                                            viewHolder.btnVideoStart.setText(R.string.hintPause);
                                            viewHolder.btnVideoCancal.setEnabled(true);
                                            viewHolder.btnVideoCancal.setText(R.string.hintCancel);

                                            return;
                                        }

                                    }
                                })

                                .setOnPauseListener(new OnPauseListener() {
                                    @Override
                                    public void onPause() {
                                        App.showLog(TAG, "setOnPauseListener");
                                        viewHolder.btnVideoStart.setText(R.string.hintResume);
                                    }
                                })

                                .setOnCancelListener(new OnCancelListener() {
                                    @Override
                                    public void onCancel() {
                                        App.showLog(TAG, "setOnCancelListener");
                                        //videoDownloadID = 0;
                                        viewHolder.btnVideoStart.setText(R.string.hintStart);
                                        viewHolder.btnVideoCancal.setEnabled(false);
                                        viewHolder.videoProgressBar.setProgress(0);
                                        viewHolder.tvVideoProgressSize.setText("");
                                        viewHolder.videoProgressBar.setIndeterminate(false);
                                    }
                                })

                                .setOnProgressListener(new OnProgressListener() {
                                    @Override
                                    public void onProgress(Progress progress) {
                                        long progressPercent = progress.currentBytes * 100 / progress.totalBytes;
                                        viewHolder.videoProgressBar.setProgress((int) progressPercent);
                                        //App.showLog(TAG, "setOnProgressListener: "+progressPercent);
                                        viewHolder.tvVideoProgressSize.setText(getProgressDisplayLine(progress.currentBytes, progress.totalBytes));
                                    }
                                })

                                .start(new OnDownloadListener() {
                                    @Override
                                    public void onDownloadComplete() {
                                        App.showLog(TAG, "start");
                                        viewHolder.btnVideoStart.setEnabled(false);
                                        viewHolder.btnVideoCancal.setEnabled(false);
                                        viewHolder.btnVideoStart.setText(R.string.hintCompleted);
                                    }

                                    @Override
                                    public void onError(Error error) {
                                        App.showLog(TAG, "onError");
                                        viewHolder.btnVideoStart.setText(R.string.hintStart);
                                        Toast.makeText(getApplicationContext(), getString(R.string.err_DownloadFails) + " " + model.videoTitle, Toast.LENGTH_LONG).show();
                                        viewHolder.tvVideoProgressSize.setText("");
                                        viewHolder.videoProgressBar.setProgress(0);
                                        //videoDownloadID = 0;
                                        viewHolder.btnVideoCancal.setEnabled(false);
                                        viewHolder.videoProgressBar.setIndeterminate(false);
                                        viewHolder.btnVideoStart.setEnabled(true);
                                    }
                                });


                    }
                });



                viewHolder.btnVideoCancal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        PRDownloader.cancel(model.id);
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
            TextView tvVideoTitle, tvVideoProgressSize;
            ProgressBar videoProgressBar;
            Button btnVideoCancal, btnVideoStart;

            public VersionViewHolder(View itemView) {
                super(itemView);

                tvVideoTitle = (TextView) itemView.findViewById(R.id.tvVideoTitle);
                tvVideoProgressSize = (TextView) itemView.findViewById(R.id.tvVideoProgressSize);
                videoProgressBar = (ProgressBar) itemView.findViewById(R.id.videoProgressBar);
                btnVideoCancal = (Button) itemView.findViewById(R.id.btnVideoCancal);
                btnVideoStart = (Button) itemView.findViewById(R.id.btnVideoStart);

            }
        }


    }


}
