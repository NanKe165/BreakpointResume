package cn.bgs.breakpointresume.service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import org.apache.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cn.bgs.breakpointresume.bean.DownLoadFile;
import cn.bgs.breakpointresume.util.DownLoadUtil;

/**
 * Created by Vincent on 2018/1/4.
 */

public class DownLoadService extends Service {

    private String DOWNLOAD_PATH= Environment.getExternalStorageDirectory()+"/breakPoint/";
    private String FILE_NAME="breakPoint.apk";
    DownLoadUtil downLoadUtil;
    public String DOWNLOAD_START="start";
    public String DOWNLOAD_PAUSE="pause";
    public String DOWNLOAD_CANCEL="cancel";

    private  Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==0){
                DownLoadFile fileinfo= (DownLoadFile) msg.obj;
                downLoadUtil = new DownLoadUtil(DownLoadService.this,fileinfo);
                downLoadUtil.setPause(false);
                downLoadUtil.download();
            }
        }
    };
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction()==DOWNLOAD_START) {
            final DownLoadFile fileinfo = (DownLoadFile) intent.getSerializableExtra("file");
            startDownload(fileinfo);
        }else if (intent.getAction()==DOWNLOAD_PAUSE){
            if (null!=downLoadUtil){
                downLoadUtil.setPause(true);
            }
        }else if (intent.getAction()==DOWNLOAD_CANCEL){
            if (null!=downLoadUtil) {
                final DownLoadFile fileinfo = (DownLoadFile) intent.getSerializableExtra("file");
                downLoadUtil.cancel(fileinfo.getUrl());
            }

        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void startDownload(final DownLoadFile fileinfo) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection=null;
                RandomAccessFile randomAccessFile=null;
                try {
                    URL url=new URL(fileinfo.getUrl());
                    urlConnection= (HttpURLConnection) url.openConnection();
                    urlConnection.setConnectTimeout(3000);
                    urlConnection.setRequestMethod("GET");
                    int length=-1;
                    if (urlConnection.getResponseCode()== HttpStatus.SC_OK){
                        //获得文件长度
                        length = urlConnection.getContentLength();
                    }
                    if (length<0){
                        return;
                    }
                    //创建相同大小的本地文件
                    File dir=new File(DOWNLOAD_PATH);
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    File file = new File(dir, FILE_NAME);
                    randomAccessFile=new RandomAccessFile(file,"rwd");
                    randomAccessFile.setLength(length);
                    //长度给fileInfo对象
                    fileinfo.setLength(length);
                    //TODO 通过Handler将对象传递给Service
                    handler.obtainMessage(0, fileinfo).sendToTarget();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
