package cn.bgs.breakpointresume.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import cn.bgs.breakpointresume.bean.DownLoadFile;
import cn.bgs.breakpointresume.db.MyDBHelper;

/**
 * Created by Vincent on 2018/1/4.
 */
public class ThreadDAOImpl implements ThreadDAO {

    private MyDBHelper mMyDBHelper;
    /**
     * dao类需要实例化数据库Help类,只有得到帮助类的对象我们才可以实例化 SQLiteDatabase
     * @param context
     */
    public ThreadDAOImpl(Context context){
        mMyDBHelper=new MyDBHelper(context);
    }

    @Override
    public void insert(DownLoadFile info) {
        // 增删改查每一个方法都要得到数据库，然后操作完成后一定要关闭
        // getWritableDatabase(); 执行后数据库文件才会生成
        // 数据库文件利用DDMS可以查看，在 data/data/包名/databases 目录下即可查看
        int length = info.getLength();
        int now = info.getNow();
        int start = info.getStart();
        String url = info.getUrl();
        SQLiteDatabase sqLiteDatabase =  mMyDBHelper.getWritableDatabase();
        sqLiteDatabase.beginTransaction();  //开始事务
        ContentValues contentValues = new ContentValues();
        contentValues.put("url",url);
        contentValues.put("length",length);
        contentValues.put("start",start);
        contentValues.put("now",now);
        sqLiteDatabase.insertOrThrow("breakpoint",null,contentValues);
        sqLiteDatabase.setTransactionSuccessful();  //设置事务成功完成
        sqLiteDatabase.endTransaction();    //结束事务

    }
    @Override
    public void delete(String url) {
        SQLiteDatabase sqLiteDatabase = mMyDBHelper.getWritableDatabase();
        int deleteResult = sqLiteDatabase.delete("breakpoint", "url=?", new String[]{url});
        sqLiteDatabase.close();
    }
    @Override
    public void update(String url, int finished) {
        SQLiteDatabase sqLiteDatabase = mMyDBHelper.getWritableDatabase();
        ContentValues contentValues =new ContentValues();
        contentValues.put("now", finished);
        sqLiteDatabase.update("breakpoint", contentValues, "url=?", new String[]{url});
        sqLiteDatabase.close();
    }
    @Override
    public List<DownLoadFile> get(String url) {
        ArrayList<DownLoadFile> persons = new ArrayList<DownLoadFile>();
        SQLiteDatabase readableDatabase = mMyDBHelper.getReadableDatabase();
        // 查询比较特别,涉及到 cursor
        //Cursor cursor = readableDatabase.rawQuery("select * from breakpoint where url=?",null);
        Cursor cursor = readableDatabase.query("breakpoint",null,"url=?",new String[]{url},null,null,null);
        if(cursor.moveToNext()){
            DownLoadFile file=new DownLoadFile();
           file.setUrl(cursor.getString(cursor.getColumnIndex("url")));
           file.setLength(cursor.getInt(cursor.getColumnIndex("length")));
           file.setNow(cursor.getInt(cursor.getColumnIndex("now")));
           file.setStart(cursor.getInt(cursor.getColumnIndex("start")));
           persons.add(file);
        }
        cursor.close(); // 记得关闭 corsor
        readableDatabase.close(); // 关闭数据库
        return persons;
    }
    @Override
    public boolean isExits(String url) {
        List<DownLoadFile> downLoadFiles = get(url);
        if (downLoadFiles.size()==0||null==downLoadFiles)
        return false;
        return true;
    }

}
