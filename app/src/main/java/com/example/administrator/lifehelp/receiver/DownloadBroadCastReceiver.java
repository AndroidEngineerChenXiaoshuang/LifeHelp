package com.example.administrator.lifehelp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.example.administrator.lifehelp.application.MyApplication;
import com.example.administrator.lifehelp.service.DownloadService;

/**
 * 用于得到生活帮帮软件本地广播事件
 * Created by Jam on 2017/5/22 0022.
 */

public class DownloadBroadCastReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()){
            case "com.example.administrator.lifehelp.receiver.DownloadBroadCastReceiver.DOWNLOADFAILURE":
            {
                Intent startDownload = new Intent(MyApplication.getContext(), DownloadService.class);
                intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                context.startService(startDownload);
                break;
            }
            case "com.example.administrator.lifehelp.receiver.DownloadBroadCastReceiver.DOWNLOADPAUSE":
            {
                DownloadService.setPause(true);
                break;
            }
            case "com.example.administrator.lifehelp.receiver.DownloadBroadCastReceiver.DOWNLOADRESTART":
            {
                DownloadService.setPause(false);
                Intent startDownload = new Intent(MyApplication.getContext(),DownloadService.class);
                intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                context.startService(startDownload);
                break;
            }
        }
    }
}
