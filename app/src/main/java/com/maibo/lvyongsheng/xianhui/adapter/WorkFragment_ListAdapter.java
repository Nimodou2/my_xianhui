 package com.maibo.lvyongsheng.xianhui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.maibo.lvyongsheng.xianhui.R;

 /**
 * Created by Administrator on 2017/4/6.
 */

public class WorkFragment_ListAdapter extends BaseAdapter{
    private String[] title;
    private int logo[];
    private Context context;
    public static final int TYPP_ITEM=0 ,TYPE_XIAN=1;
    public WorkFragment_ListAdapter(int[] logo, Context context, String[] title) {
        this.logo = logo;
        this.context = context;
        this.title = title;
    }

    @Override
    public int getItemViewType(int position) {
        if(position%2==0){
            return TYPP_ITEM;
        }
        return TYPE_XIAN;
    }

    @Override
    public int getCount() {
        if(title!=null&&title.length>0) {
            return title.length*2;
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return title[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        switch (getItemViewType(position)){
            case TYPP_ITEM://正常
               return initViewHolder_Work(position,convertView,parent);
            case TYPE_XIAN://线
                return  initViewHolder_Text(position,convertView,parent);
            default:return null;
        }
    }

     private View initViewHolder_Text(int position, View convertView, ViewGroup parent) {
         ViewHolder_Text viewHolder_text;
         if(convertView==null){
             convertView= LayoutInflater.from(context).inflate(R.layout.workfragement_listview_item_fengexian,null);
             viewHolder_text=new ViewHolder_Text();
             convertView.setTag(viewHolder_text);
         }else {
             viewHolder_text= (ViewHolder_Text) convertView.getTag();
         }
         return convertView;
     }

     private View initViewHolder_Work(int position, View convertView, ViewGroup parent) {
         ViewHolder_Work viewHolder_work;
         if(convertView==null){
             convertView= LayoutInflater.from(context).inflate(R.layout.workall_recyclerview_itemlayout,null);
             viewHolder_work=new ViewHolder_Work();
             viewHolder_work.image_logo= (ImageView) convertView.findViewById(R.id.workall_recyclerview_itemlayout_textview_logo);
             viewHolder_work.text_title= (TextView) convertView.findViewById(R.id.workall_recyclerview_itemlayout_textview_what);
             convertView.setTag(viewHolder_work);
         }else {
             viewHolder_work= (ViewHolder_Work) convertView.getTag();
         }
         switch (position){
             case 0:
                 setdataThis(viewHolder_work,position);
                 break;
             case 2:
                 setdataThis(viewHolder_work,position-1);
                 break;
             case 4:
                 setdataThis(viewHolder_work,position-2);
                 break;
             case 6:
                 setdataThis(viewHolder_work,position-3);
                 break;
             case 8:
                 setdataThis(viewHolder_work,position-4);
                 break;
             case 10:
                 setdataThis(viewHolder_work,position-5);
                 break;
             case 12:
                 setdataThis(viewHolder_work,position-6);
                 break;
             case 14:
                 setdataThis(viewHolder_work,position-7);
                 break;
             default:break;
         }

         return convertView;
     }

     public void setdataThis(ViewHolder_Work viewHolder_work, int dataThis) {
         viewHolder_work.text_title.setText(title[dataThis]);
         viewHolder_work.image_logo.setImageResource(logo[dataThis]);
     }

     public  class ViewHolder_Work{
        private ImageView image_logo;
        private TextView text_title;
    }
    public class ViewHolder_Text{
        private TextView text_xian;
    }

     @Override
     public int getViewTypeCount() {
         return 2;
     }
 }
