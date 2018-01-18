package cn.bgs.breakpointresume.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Vincent on 2018/1/4.
 */

public class MyDBHelper extends SQLiteOpenHelper {
    public MyDBHelper(Context context) {
        /**
         * 参数说明：
         *
         * 第一个参数：上下文
         * 第二个参数：数据库的名称
         * 第三个参数：null代表的是默认的游标工厂
         * 第四个参数：是数据库的版本号  数据库只能升级,不能降级,版本号只能变大不能变小
         */
        super(context, "breakpoint.db", null, 2);
    }
    /**
     * onCreate是在数据库创建的时候调用的，主要用来初始化数据表结构和插入数据初始化的记录
     *
     * 当数据库第一次被创建的时候调用的方法,适合在这个方法里面把数据库的表结构定义出来.
     * 所以只有程序第一次运行的时候才会执行
     * 如果想再看到这个函数执行，必须写在程序然后重新安装这个app
     */

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table breakpoint (id integer primary key autoincrement, url varchar, length integer,start integer,now integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //sqLiteDatabase.execSQL("alter table breakpoint add column ohter string");
        sqLiteDatabase.execSQL("drop table if exists breakpoint");
        onCreate(sqLiteDatabase);
    }
}
