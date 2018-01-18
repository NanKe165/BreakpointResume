package cn.bgs.breakpointresume.util;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import org.apache.http.HttpStatus;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import cn.bgs.breakpointresume.bean.DownLoadFile;
import cn.bgs.breakpointresume.dao.ThreadDAO;
import cn.bgs.breakpointresume.dao.ThreadDAOImpl;

/**
 * Created by Vincent on 2018/1/4.
 */

public class DownLoadUtil {
    private int finished;
    private boolean isPause=false;
    ThreadDAO threadDAO;
    DownLoadFile fileInfo;
    Context context;
    private String DOWNLOAD_PATH= Environment.getExternalStorageDirectory()+"/breakPoint/";
    private  String FILE_NAME="breakPoint.apk";
    private String ACTION_UPDATE="ACTION_UPDATE";
    //构造方法略
    public void download(){
        List<DownLoadFile> lists = threadDAO.get(fileInfo.getUrl());
        DownLoadFile info = null;
        if(lists.size() == 0){
            //第一次下载，创建子线程下载
            new MyThread(fileInfo).start();
        }else{
            //中间开始的
            info = lists.get(0);
            new MyThread(info).start();
        }
    }

    public DownLoadUtil(Context context, DownLoadFile file){
        this.context=context;
        fileInfo=file;
        this.threadDAO=new ThreadDAOImpl(context);
    }

    class MyThread extends Thread{
        private DownLoadFile info = null;
        public MyThread(DownLoadFile threadInfo) {
            this.info = threadInfo;
        }
        @Override
        public void run() {
            //向数据库添加线程信息
            if(!threadDAO.isExits(info.getUrl())){
                threadDAO.insert(info);
            }
            HttpURLConnection urlConnection = null;
            RandomAccessFile randomFile =null;
            InputStream inputStream = null;
            try {
                URL url = new URL(info.getUrl());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(3000);
                urlConnection.setRequestMethod("GET");
                //设置下载位置
                int start = info.getStart() + info.getNow();
                urlConnection.setRequestProperty("Range","bytes=" + start + "-" + info.getLength());

                //设置文件写入位置
                File file = new File(DOWNLOAD_PATH,FILE_NAME);
                randomFile = new RandomAccessFile(file, "rwd");
                randomFile.seek(start);
                //向Activity发广播
                Intent intent = new Intent(ACTION_UPDATE);
                intent.putExtra("length",info.getLength());
                context.sendBroadcast(intent);
                finished += info.getNow();
                Intent intent1 = new Intent(ACTION_UPDATE);
                if (urlConnection.getResponseCode() ==  HttpStatus.SC_PARTIAL_CONTENT) {
                    //获得文件流
                    inputStream = urlConnection.getInputStream();
                    byte[] buffer = new byte[512];
                    int len = -1;
                    long time = System.currentTimeMillis();
                    while ((len = inputStream.read(buffer))!= -1){
                        //写入文件
                        randomFile.write(buffer,0,len);
                        //把进度发送给Activity
                        finished += len;
                        //看时间间隔，时间间隔大于500ms再发
                        if(System.currentTimeMillis() - time >500){
                            time = System.currentTimeMillis();
                            intent1.putExtra("now",finished );
                            context.sendBroadcast(intent1);
                        }
                        //判断是否是暂停状态
                        if(isPause){
                            threadDAO.update(info.getUrl(),finished);
                            return; //结束循环
                        }
                    }
                    //删除线程信息
                    threadDAO.delete(info.getUrl());
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {//回收工作略

            }
        }
    }
    public void setPause(Boolean isPause){
        this.isPause=isPause;
    }
    public void cancel(String url){
        //TODO 删除本地文件

        
        threadDAO.delete(url);

    }
}
