package com.maibo.lvyongsheng.xianhui.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.maibo.lvyongsheng.xianhui.Contact_OnlyActivity;
import com.maibo.lvyongsheng.xianhui.FourStep_Activity;
import com.maibo.lvyongsheng.xianhui.HelperActivity;
import com.maibo.lvyongsheng.xianhui.R;
import com.maibo.lvyongsheng.xianhui.RemindActivity;
import com.maibo.lvyongsheng.xianhui.TaskActivity;
import com.maibo.lvyongsheng.xianhui.adapter.WorkFragment_ListAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class WorkAll_Fragment extends Fragment {

    private View view ;
    private ListView listview;
    private int logo[];
    private String list_title[];
    private WorkFragment_ListAdapter adapter;
    private Context mcontext;
    private Intent thisintent;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mcontext=context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_work_all_, container, false);
        initview();
        initdate();
        return view;
    }
    private void initview() {
        listview = (ListView) view.findViewById(R.id.workall_fragment_listview);
    }
    private void initdate() {
        logo=new int[]{R.mipmap.logo_work_1,R.mipmap.logo_work_2,R.mipmap.logo_work_3,R.mipmap.logo_work_4,R.mipmap.logo_work_5,R.mipmap.logo_work_6,R.mipmap.logo_work_7,R.mipmap.logo_work_8};
        list_title=new String[]{"报表","提醒","进度","产品","项目","同事","顾客","通讯录"};
        adapter=new WorkFragment_ListAdapter(logo,mcontext,list_title);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch (position){
                        case 0://报表
                            thisintent=new Intent(mcontext, HelperActivity.class);
                            break;
                        case 2://提醒
                            thisintent=new Intent(mcontext, RemindActivity.class);
                            break;
                        case 4://进度
                            thisintent=new Intent(mcontext, TaskActivity.class);
                            break;
                        case 6://产品
                            thisintent=new Intent(mcontext, FourStep_Activity.class);
                            thisintent.putExtra("type",4);
                            break;
                        case 8://项目
                            thisintent=new Intent(mcontext, FourStep_Activity.class);
                            thisintent.putExtra("type",3);
                            break;
                        case 10://技师
                            thisintent=new Intent(mcontext, FourStep_Activity.class);
                            thisintent.putExtra("type",2);
                            break;
                        case 12://客户
                            thisintent=new Intent(mcontext, FourStep_Activity.class);
                            thisintent.putExtra("type",1);
                            break;
                        case 14://联系人
                            thisintent=new Intent(mcontext, Contact_OnlyActivity.class);
                            break;
                        default:
                            break;
                    }
                if(thisintent!=null){
                    startActivity(thisintent);
                }
            }
        });
    }
}
