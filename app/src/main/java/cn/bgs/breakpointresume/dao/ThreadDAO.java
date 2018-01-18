package cn.bgs.breakpointresume.dao;

import java.util.List;

import cn.bgs.breakpointresume.bean.DownLoadFile;

/**
 * Created by Vincent on 2018/1/4.
 */

public interface ThreadDAO {
    //插入一条数据
    public void insert(DownLoadFile info);
    //根据URL删除一条数据
    public void delete(String url);
    //根据URL更新一条进度
    public void update(String url,int finished);
    //根据URL找到一条数据
    public List<DownLoadFile> get(String url);
    //是否存在
    public boolean isExits(String url);
}
