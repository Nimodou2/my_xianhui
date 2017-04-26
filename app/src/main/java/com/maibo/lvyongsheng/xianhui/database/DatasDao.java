package com.maibo.lvyongsheng.xianhui.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.maibo.lvyongsheng.xianhui.entity.HelperCustomer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LYS on 2017/1/16.
 */

public class DatasDao {
    private MyDBHelper dbHelper;
    public  DatasDao(Context context,int versation){
        dbHelper=new MyDBHelper(context,versation);
    }

    /**
     * 插入顾客数据
     * @param hcList
     */
    public void insertCustomer(List<HelperCustomer> hcList){

        SQLiteDatabase sd=dbHelper.getWritableDatabase();
        for (HelperCustomer hc:hcList){
            ContentValues cv=new ContentValues();
            cv.put("org_name",hc.getOrg_name());
            cv.put("vip_star",hc.getVip_star());
            cv.put("customer_id",hc.getCustomer_id());
            cv.put("fullname",hc.getFullname());
            cv.put("avator_url",hc.getAvator_url());
            cv.put("days",hc.getDays());
            cv.put("project_total",hc.getProject_total());
            cv.put("status",hc.getStatus());
            cv.put("current_time",hc.getCurrent_time());
            sd.insert("helpercustomer",null,cv);
        }
        sd.close();
    }

    /**
     * 清空表中所有数据
     */
    public void deleAllDatas(){
        SQLiteDatabase sd=dbHelper.getWritableDatabase();
        sd.delete("helpercustomer",null,null);
        sd.close();

    }
    //        String org_name, String vip_star, int customer_id, String fullname,
//                String avator_url, String days, int project_total, int status,Long current_time

    /**
     * 查询顾客数据
     * @return
     */
    public List<HelperCustomer> queryAllCustomer(){
        SQLiteDatabase sd=dbHelper.getReadableDatabase();
        List<HelperCustomer> lhc=new ArrayList<>();
        Cursor cursor=sd.rawQuery("select * from helpercustomer",null);
        if (cursor.getCount()==0){
            cursor.close();
            sd.close();
            return null;
        }else{
            while (cursor.moveToNext()){
                String org_name = cursor.getString(cursor.getColumnIndex("org_name"));
                String vip_star = cursor.getString(cursor.getColumnIndex("vip_star"));
                int customer_id=cursor.getInt(cursor.getColumnIndex("customer_id"));
                String fullname=cursor.getString(cursor.getColumnIndex("fullname"));
                String avator_url=cursor.getString(cursor.getColumnIndex("avator_url"));
                String days=cursor.getString(cursor.getColumnIndex("days"));
                int project_total=cursor.getInt(cursor.getColumnIndex("project_total"));
                int status=cursor.getInt(cursor.getColumnIndex("status"));
                long current_time=cursor.getLong(cursor.getColumnIndex("current_time"));
                lhc.add(new HelperCustomer(org_name,vip_star,customer_id,fullname,avator_url,days,project_total,status,current_time));
            }
            cursor.close();
            sd.close();
            return lhc;
        }
    }

}
