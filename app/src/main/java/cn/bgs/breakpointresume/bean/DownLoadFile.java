package cn.bgs.breakpointresume.bean;

import java.io.Serializable;

/**
 * Created by Vincent on 2018/1/4.
 * 记录下载文件的数据
 */

public class DownLoadFile implements Serializable {
    private String url;//URL
    private int length;//长度|结束位置
    private int start;//开始长度
    private int now;//当前进度

    public DownLoadFile() {
    }

    public DownLoadFile(String url, int length, int start, int now) {
        this.url = url;
        this.length = length;
        this.start = start;
        this.now = now;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getNow() {
        return now;
    }

    public void setNow(int now) {
        this.now = now;
    }
}
