package cn.bgs.breakpointresume;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import cn.bgs.breakpointresume.bean.DownLoadFile;
import cn.bgs.breakpointresume.service.DownLoadService;

public class MainActivity extends AppCompatActivity {

    private static ProgressBar bar;
    private Button start;
    private Button pause;
    private Button cancel;
    private String download_url ="http://111.62.242.28/imtt.dd.qq.com/16891/299871E10331CE65BE335CBCE150F322.apk?mkey=5a602b403ef36e2a&f=a601&c=0&fsname=com.tencent.mm_6.6.1_1220.apk&csr=1bbd&p=.apk";
    private DownLoadFile info;
    private Intent intent1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initData() {
        info = new DownLoadFile();
        info.setUrl(download_url);
    }

    private void initView() {
        bar = findViewById(R.id.progressBar);
        start = findViewById(R.id.start);
        pause = findViewById(R.id.pause);
        cancel = findViewById(R.id.cancel);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDownload();

            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopDownload();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                destoryDownload();
            }
        });
    }

    private void startDownload() {
        intent1 = new Intent(MainActivity.this,DownLoadService.class);
        intent1.setAction("start");
        intent1.putExtra("file", info);
        startService(intent1);
    }

    private void destoryDownload() {
        Intent intent=new Intent(MainActivity.this,DownLoadService.class);
        intent.setAction("cancel");
        intent.putExtra("file", info);
        startService(intent);
    }

    private void stopDownload() {
        Intent intent=new Intent(MainActivity.this,DownLoadService.class);
        intent.setAction("pause");
        startService(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopDownload();
        stopService(intent1);
    }
    // 继承BroadcastReceivre基类
    public static class MyBroadcastReceiver extends BroadcastReceiver {

        // 复写onReceive()方法
        // 接收到广播后，则自动调用该方法
        @Override
        public void onReceive(Context context, Intent intent) {
            //写入接收广播后的操作
            int now = intent.getIntExtra("now", 0);
            int length = intent.getIntExtra("length", 0);
            if (length!=0)
            bar.setMax(length);
            bar.setProgress(now);
        }
    }

}

