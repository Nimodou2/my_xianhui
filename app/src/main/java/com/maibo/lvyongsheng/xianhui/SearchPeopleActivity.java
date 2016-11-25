package com.maibo.lvyongsheng.xianhui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.maibo.lvyongsheng.xianhui.entity.SearchPeople;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import cn.leancloud.chatkit.LCChatKitUser;
import cn.leancloud.chatkit.activity.LCIMConversationActivity;
import cn.leancloud.chatkit.utils.LCIMConstants;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by LYS on 2016/11/21.
 */

public class SearchPeopleActivity extends Activity {
    SearchView search_people;
    ListView lv_seached_people;
    List<LCChatKitUser> users;
    List<String> names;
    TextView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        setContentView(R.layout.activity_search_people);
        search_people= (SearchView) findViewById(R.id.search_people);
            int id = search_people.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
            TextView hintText= (TextView) findViewById(id);
            hintText.setTextSize(14);
            hintText.setTextColor(getResources().getColor(R.color.gray_weixin));
            Class<?> c=search_people.getClass();
            try {
                Field f=c.getDeclaredField("mSearchPlate");//通过反射，获得类对象的一个属性对象
                f.setAccessible(true);//设置此私有属性是可访问的
//                View v=(View) f.get(search_people);//获得属性的值
//                v.setBackgroundResource(R.drawable.searchview_shap_all_white_bg);//设置此view的背景
            } catch (Exception e) {
                e.printStackTrace();
            }
        back= (TextView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        lv_seached_people= (ListView) findViewById(R.id.lv_seached_people);
        search_people.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {//当点击搜索按钮时执行
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {//当搜索框内容变化时执行
                //进行数据查询
                List<SearchPeople> searchPeoples=queryDatas(newText);
                //准备适配器所需数据
                setQueryAdapter(searchPeoples);
                return false;
            }
        });
    }
    private void initData(){
        users=CustomUserProvider.getInstance(this).getAllUsers();
        names=new ArrayList<>();
        for (LCChatKitUser lcChatKitUser:users){
            names.add(lcChatKitUser.getUserName());
        }
    }

    /**
     * 数据查询
     * @param newText
     * @return
     */
    private List<SearchPeople> queryDatas(String newText){
        List<SearchPeople> searchPeoples=new ArrayList<SearchPeople>();
        String[] text=new String[newText.length()];
        for (int j=0;j<newText.length();j++){
            text[j]=String.valueOf(newText.charAt(j));
        }

        for (int i=0;i<names.size();i++){
            int times=0;
            for (int p=0;p<text.length;p++){
                int isContent=names.get(i).indexOf(text[p]);
                if (isContent!=-1){
                    //记录下来当前i值和进入该判断语句的次数
                    times++;
                    Log.e("次数",i+"");
                }
            }
            searchPeoples.add(new SearchPeople(times,i));
        }
        return searchPeoples;
    }

    /**
     * 提供Adapter数据
     * @param searchPeoples
     */
    private void setQueryAdapter(List<SearchPeople> searchPeoples){
        //处理数据，得到适配器所需数据
        List<LCChatKitUser> lcChatKitUsers=new ArrayList<>();
        List<Integer> times=new ArrayList<>();
        //找出times中最大值
        int max=0;
        for (int i=0;i<searchPeoples.size();i++){
            if (searchPeoples.get(i).getTimes()>max){
                max=searchPeoples.get(i).getTimes();
            }
        }

        for (int i=0;i<searchPeoples.size();i++){
            if (searchPeoples.get(i).getTimes()>0&&searchPeoples.get(i).getTimes()==max){
                lcChatKitUsers.add(users.get(searchPeoples.get(i).getPosition()));
                times.add(searchPeoples.get(i).getTimes());
            }
        }
        lv_seached_people.setAdapter(new MyAdapter(lcChatKitUsers,times));

    }

    private class MyAdapter extends BaseAdapter{
        List<LCChatKitUser> lcChatKitUsers;
        List<Integer> times;
        MyAdapter( List<LCChatKitUser> lcChatKitUsers, List<Integer> times){
            this.lcChatKitUsers=lcChatKitUsers;
            this.times=times;
            //将数据按匹配度进行分组

        }
        @Override
        public int getCount() {
            return lcChatKitUsers.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v=View.inflate(SearchPeopleActivity.this,R.layout.style_search,null);
            LCChatKitUser user=lcChatKitUsers.get(i);
            CircleImageView img_head_search= (CircleImageView) v.findViewById(R.id.img_head_search);
            Picasso.with(SearchPeopleActivity.this).load(user.getAvatarUrl()).into(img_head_search);
            TextView tv_name_search= (TextView) v.findViewById(R.id.tv_name_search);
            tv_name_search.setText(user.getUserName());
            startConversationActivity(tv_name_search,user);
            return v;
        }
    }
    /**
     * 跳转到聊天界面
     */
    private void startConversationActivity(TextView tv_name_search,final LCChatKitUser user){
        tv_name_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchPeopleActivity.this, LCIMConversationActivity.class);
                intent.putExtra(LCIMConstants.PEER_ID, user.getUserId());
                startActivity(intent);
                finish();
            }
        });
    }
}
