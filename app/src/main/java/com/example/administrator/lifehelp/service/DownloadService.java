package com.example.administrator.lifehelp.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import com.example.administrator.lifehelp.R;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Jam on 2017/5/18 0018.
 * 用于专门提供更新下载
 */

public class DownloadService extends Service {

    public String downloadUrl;
    public int progress;
    public RandomAccessFile randomAccessFile;
    public OkHttpClient okHttpClient;
    public Request request;
    public String path;
    public NotificationManager notificationManager;
    public long downloadLength = 0;
    public long contentLength;
    public File file;   //蚂蚁帮文件夹
    public File fileApk; //更新文件夹

    //下载失败
    public static final int DOWNLOAD_FAILURE = 0;
    //暂停下载
    public static final int DOWNLOAD_PAUSE = 1;
    //下载完成
    public static final int DOWNLOAD_FINISH = 2;
    //开始下载
    public static final int DOWNLOAD_START = 3;

    public static boolean isPause = false;

    //强制更新
    public static final int MandatoryUpdate = 1;
    //选择新更新
    public static final int ThinkeUpdate = 2;

    public int downloadType ;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new DownloadBinder();
    }

    public class DownloadBinder extends Binder{

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(downloadType==0){
            downloadType = intent.getIntExtra("DownloadType",0);
        }
        if(downloadUrl==null){
            downloadUrl = intent.getStringExtra("DownloadUrl");
        }
        fileApk = new File(file.getPath(),"/"+getFileName(downloadUrl));
        if(fileApk.exists()){
            downloadLength = fileApk.length();
        }
        Log.d("Jam", String.valueOf(downloadLength));
        DownloadFileAsncyTask downloadFile = new DownloadFileAsncyTask();
        downloadFile.execute(downloadUrl);
        return super.onStartCommand(intent,flags,startId);
    }

    public String getFileName(String url){
        if(url!=null){
            int lastIndex = url.lastIndexOf("/");
            return url.substring(lastIndex+1);
        }
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        file = new File(path,"蚂蚁帮");
        if(!file.exists()){
            file.mkdirs();
        }
    }

    public Notification getNotification(int progress){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        Intent intent = new Intent();
        intent.setAction("com.example.administrator.lifehelp.receiver.DownloadBroadCastReceiver.DOWNLOADPAUSE");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0,intent,0);
        builder.setContentIntent(pendingIntent);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("正在升级帮帮客户端,点击可暂停!");
        builder.setContentText(progress+"%");
        builder.setProgress(100,progress,false);
        return builder.build();
    }

    /**
     * @param title     设置通知的标题
     * @param pendingIntent 待执行的意图
     * @return 通知类
     *
     */
    public Notification getNotifications(String title,PendingIntent pendingIntent){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(title);
        if(pendingIntent!=null){
            builder.setContentIntent(pendingIntent);
        }
        builder.setAutoCancel(true);
        return builder.build();
    }

    public void sendNotification(){
        startForeground(1,getNotification(0));
    }


    public class DownloadFileAsncyTask extends AsyncTask<String,Integer,Integer>{

        public HandlerInfo handlerInfo;
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            handlerInfo = new HandlerInfo();
            sendNotification();
        }

        @Override
        protected Integer doInBackground(String... params) {
            int result = DOWNLOAD_FAILURE;
            contentLength = getContentLength();
            //下载的文件长度等于0证明失败
            if(contentLength==0){
                Log.d("Jam","contentLength == 0");
                return DOWNLOAD_FAILURE;
            }
            //如果获取到下载的文件的字节流长度等于文件的长度证明下载完成
            if(contentLength==downloadLength){
                result = DOWNLOAD_FINISH;
                return result;
            }
            okHttpClient = new OkHttpClient();
            request = new Request.Builder().addHeader("RANGE","bytes="+downloadLength+"-").url(downloadUrl).build();
            try {
                Response response = okHttpClient.newCall(request).execute();
                if(response!=null) {
                    handlerInfo.sendEmptyMessage(DOWNLOAD_START);
                    randomAccessFile = new RandomAccessFile(fileApk,"rw");
                    randomAccessFile.seek(downloadLength);
                    InputStream inputStream = response.body().byteStream();
                    BufferedInputStream bufferInputStream = new BufferedInputStream(inputStream);
                    int len;
                    byte[] bytes = new byte[10240];
                    int history = 0;
                    while((len = bufferInputStream.read(bytes))!=-1&&!isPause){
                        randomAccessFile.write(bytes,0,len);
                        downloadLength+=len;
                        progress = (int) (downloadLength*100/contentLength);
                        //为了防止应用吃急当更新的进度大于上次更新值10就可以发送通知
                        if(progress-10>=history){
                            history+=10;
                            publishProgress(progress);
                        }
                    }
                    randomAccessFile.close();
                    bufferInputStream.close();
                    if(downloadLength==contentLength){
                        result = DOWNLOAD_FINISH;
                    }else if(isPause){      //如果是暂停导致停止下载
                        result = DOWNLOAD_PAUSE;
                    }else{
                        result = DOWNLOAD_FAILURE;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }



        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            notificationManager.notify(1,getNotification(values[0]));
            if(downloadType==MandatoryUpdate&&startOnUpdateInfo!=null){
                startOnUpdateInfo.setPorgress(values[0]);
            }
        }
        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if(result == DOWNLOAD_FINISH){
                notificationManager.notify(2,getNotifications("下载完成",null));
                if(okDownload!=null){
                    okDownload.finish(fileApk);
                    stopForeground(true);
                }
            }else if(result==DOWNLOAD_FAILURE){
                //如果在下载中途中因为用户或者服务器的原因停止了下载将会执行的函数
                handlerInfo.sendEmptyMessage(DOWNLOAD_FAILURE);
            }else if(result==DOWNLOAD_PAUSE){
                //暂停下载
                handlerInfo.sendEmptyMessage(DOWNLOAD_PAUSE);
            }
        }
        private Long getContentLength(){
            OkHttpClient okhttpClient = new OkHttpClient();
            Request request = new Request.Builder().url(downloadUrl).build();
            Response response = null;
            try {
                response = okhttpClient.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return response!=null?response.body().contentLength():0L;
        }
    }

    //完成下载后
    public interface finishDownload{
        void finish(File filepath);
    }

    public static finishDownload okDownload;

    public static void setOkDownload(finishDownload okDownload){
        DownloadService.okDownload = okDownload;
    }

    public class HandlerInfo extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case DOWNLOAD_FAILURE:
                {
                    Intent intent = new Intent();
                    intent.setAction("com.example.administrator.lifehelp.receiver.DownloadBroadCastReceiver.DOWNLOADFAILURE");
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(DownloadService.this, 0, intent, 0);
                    notificationManager.notify(1, getNotifications("下载失败,点击重试!", pendingIntent));
                    Toast.makeText(DownloadService.this, "下载失败!", Toast.LENGTH_SHORT).show();
                }
                    break;
                case DOWNLOAD_PAUSE:
                {
                    Intent intent = new Intent();
                    intent.setAction("com.example.administrator.lifehelp.receiver.DownloadBroadCastReceiver.DOWNLOADRESTART");
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(DownloadService.this,0,intent,0);
                    notificationManager.notify(1,getNotifications("下载暂停,点击开始",pendingIntent));
                    Toast.makeText(DownloadService.this, "暂停下载", Toast.LENGTH_SHORT).show();
                }
                    break;
                case DOWNLOAD_START:
                {
                    Toast.makeText(DownloadService.this, "开始下载",Toast.LENGTH_SHORT).show();
                }
                    break;
            }
        }
    }

    //点击暂停
    public static void setPause(boolean isPause){
        DownloadService.isPause = isPause;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Jam","Service onDestroy");
    }

    public interface onUpdate {
        void setPorgress(int progress);
        void onFailure();
    }

    public static onUpdate startOnUpdateInfo;

    public static void setOnUpdateProgress(onUpdate startOnUpdateInfo){
        DownloadService.startOnUpdateInfo = startOnUpdateInfo;
    }



}
