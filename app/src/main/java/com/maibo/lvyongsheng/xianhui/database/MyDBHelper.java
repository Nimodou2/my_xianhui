package com.maibo.lvyongsheng.xianhui.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by LYS on 2017/1/16.
 */

public class MyDBHelper extends SQLiteOpenHelper {
    public  MyDBHelper(Context context,int version){
        super(context,"xianhui.db",null,version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建表类别表
//        String org_name, String vip_star, int customer_id, String fullname,
//                String avator_url, String days, int project_total, int status,Long current_time
        String sql = "CREATE TABLE IF NOT EXISTS helpercustomer" +//支出和收入的类型
                "([_id] INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +//id
                "[org_name] varchar(200)," +
                "[vip_star] varchar(200)," +
                "[customer_id] integer(200)," +
                "[fullname] varchar(16)," +
                "[avator_url] varchar(200)," +
                "[days] varchar(16)," +
                "[project_total] integer(200),"+
                "[status] integer(200),"+
                "[current_time] integer(200);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
