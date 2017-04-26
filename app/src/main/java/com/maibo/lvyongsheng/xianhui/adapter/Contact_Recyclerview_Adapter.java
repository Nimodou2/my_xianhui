package com.maibo.lvyongsheng.xianhui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.maibo.lvyongsheng.xianhui.R;
import com.maibo.lvyongsheng.xianhui.SearchPeopleActivity;
import com.maibo.lvyongsheng.xianhui.constants.Constants;
import com.maibo.lvyongsheng.xianhui.entity.EventDatas;
import com.maibo.lvyongsheng.xianhui.entity.NewLCChatKitUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.leancloud.chatkit.LCChatKitUser;
import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.maibo.lvyongsheng.xianhui.R.id.imageView3;

/**
 * Created by Administrator on 2017/4/6.
 */

public class Contact_Recyclerview_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private String[] mContactNames; // 联系人名称字符串数组
    private List<String> mContactList; // 联系人名称List（转换成拼音）
    private List<NewLCChatKitUser> resultList; // 最终结果（包含分组的字母）
    private List<String> characterList; // 字母List
    private List<LCChatKitUser> LCChat;
    List<LCChatKitUser> datas;//去除自己的数据
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private int screenHeight;

    int num = 0;

    public enum ITEM_TYPE {
        //三种多布局
        SEARCH,
        ITEM_TYPE_CHARACTER,
        ITEM_TYPE_CONTACT
    }

    public Contact_Recyclerview_Adapter(Context mContext, List<LCChatKitUser> LCChat,String displayName,int screenHeight ) {
        this.screenHeight = screenHeight;
        this.mContext = mContext;
        this.LCChat = LCChat;
        mLayoutInflater=LayoutInflater.from(mContext);
        //去除联系人中的自己消息（因为个人信息也包括在联系人列表中）
        datas = new ArrayList<>();
        for (int i = 0; i < LCChat.size(); i++) {
            if (!LCChat.get(i).getUserName().equals(displayName)) {
                datas.add(LCChat.get(i));
            }
        }
        mContactNames = new String[datas.size()];
        for (int i = 0; i < datas.size(); i++) {
            mContactNames[i] = datas.get(i).getUserName();
        }
        handleContact();
    }
    private void handleContact() {
        //将姓名转成拼音后存放的集合
        mContactList = new ArrayList<>();
        Map<String, LCChatKitUser> map = new IdentityHashMap<>();
        //user_id具有唯一性，而名字可能出现重复

        for (int i = 0; i < mContactNames.length; i++) {
            //将汉字转化成拼音
            String pinyin = PinyinHelper.convertToPinyinString(mContactNames[i], "", PinyinFormat.WITHOUT_TONE);
            map.put(pinyin, datas.get(i));
            mContactList.add(pinyin);
        }
        //从A-Z排序
        Collections.sort(mContactList, new Contact_Recyclerview_Adapter.ContactComparator());

        resultList = new ArrayList<>();
        characterList = new ArrayList<>();

        for (int i = 0; i < mContactList.size(); i++) {
            String name = mContactList.get(i);
            //将拼音首字母取出并转换成大写字母
            String character = (name.charAt(0) + "").toUpperCase(Locale.ENGLISH);
            //去除集合中重复元素
            if (!characterList.contains(character)) {
                if (character.hashCode() >= "A".hashCode() && character.hashCode() <= "Z".hashCode()) { // 是字母
                    characterList.add(character);
                    resultList.add(new NewLCChatKitUser(null, character, ITEM_TYPE.ITEM_TYPE_CHARACTER.ordinal()));
                } else {
                    if (!characterList.contains("#")) {
                        characterList.add("#");
                        resultList.add(new NewLCChatKitUser(null, "#", ITEM_TYPE.ITEM_TYPE_CHARACTER.ordinal()));
                    }
                }
            }
            resultList.add(new NewLCChatKitUser(map.get(name), "", ITEM_TYPE.ITEM_TYPE_CONTACT.ordinal()));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position==0){
            return ITEM_TYPE.SEARCH.ordinal();
        }else{
            return  resultList.get(position - 1).getType();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==ITEM_TYPE.SEARCH.ordinal()){
            return  new SearchHolder(mLayoutInflater.from(mContext).inflate(R.layout.style_lianxiren_search,parent,false));
        }else if(viewType==ITEM_TYPE.ITEM_TYPE_CONTACT.ordinal()){
            return  new Contac_ViewHolder(mLayoutInflater.from(mContext).inflate(R.layout.common_user_item,parent,false));
        }else {
            return  new CharacterHolder(mLayoutInflater.from(mContext).inflate(R.layout.item_character_new,parent,false));
        }
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof SearchHolder){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //跳转到查询界面
                    Intent intent = new Intent(mContext, SearchPeopleActivity.class);
                    mContext.startActivity(intent);
                }
            });
        }else if(holder instanceof Contac_ViewHolder){
            ((Contac_ViewHolder) holder).mTextView.setText(resultList.get(position-1).getLCCKUser().getUserName());
            Picasso.with(mContext).load(resultList.get(position - 1).getLCCKUser().getAvatarUrl()).into(((Contac_ViewHolder) holder).img_friend_avatar);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //此处监听录音权限是否开启
                    EventDatas eventDatas = new EventDatas(Constants.OPEN_ALL_PERMISSION, resultList.get(position - 1).getLCCKUser().getUserId());
                    EventBus.getDefault().post(eventDatas);
                }
            });
         }else if(holder instanceof CharacterHolder){
            ((CharacterHolder) holder).mTextView.setText(resultList.get(position - 1).getFirstName());
            num++;
        }
    }

    //与旁边的导航栏相关
    public int getScrollPosition(String character) {
        if (characterList.contains(character)) {
            for (int i = 0; i < resultList.size(); i++) {
                if (resultList.get(i).getFirstName().equals(character)) {
                    return i + 1;
                }
            }
        }

        return -1; // -1不会滑动
    }
    @Override
    public int getItemCount() {
        return resultList == null ? 0 : resultList.size() +1;
    }

    public class ContactComparator implements Comparator<String> {

        @Override
        public int compare(String o1, String o2) {
            int c1 = (o1.charAt(0) + "").toUpperCase().hashCode();
            int c2 = (o2.charAt(0) + "").toUpperCase().hashCode();

            boolean c1Flag = (c1 < "A".hashCode() || c1 > "Z".hashCode()); // 不是字母
            boolean c2Flag = (c2 < "A".hashCode() || c2 > "Z".hashCode()); // 不是字母
            if (c1Flag && !c2Flag) {
                return 1;
            } else if (!c1Flag && c2Flag) {
                return -1;
            }
            return c1 - c2;
        }

    }

    //联系人的viewholder
    public class Contac_ViewHolder extends RecyclerView.ViewHolder{
        private TextView mTextView;
        private CircleImageView img_friend_avatar;
        private RelativeLayout ll_content_person;
        public Contac_ViewHolder(View itemView) {
            super(itemView);
            img_friend_avatar = (CircleImageView) itemView.findViewById(R.id.img_friend_avatar);
            mTextView = (TextView) itemView.findViewById(R.id.tv_friend_name);
            ll_content_person = (RelativeLayout) itemView.findViewById(R.id.ll_content_person);
            setContentPersonItemHeightAndWidth(ll_content_person, img_friend_avatar);
        }
    }

    //搜索条的viewholder
    public class SearchHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_search;
        ImageView imageView;
        SearchHolder(View view) {
            super(view);
            ll_search = (LinearLayout) view.findViewById(R.id.ll_search);
            imageView = (ImageView) view.findViewById(imageView3);
            ViewGroup.LayoutParams params = ll_search.getLayoutParams();
            params.height = screenHeight * 2;
            ll_search.setLayoutParams(params);

            LinearLayout.LayoutParams params_image = (LinearLayout.LayoutParams) imageView.getLayoutParams();
            params_image.height = (int) (screenHeight * (1.2));
            params_image.width = (int) (screenHeight * (1.2));
            imageView.setLayoutParams(params_image);
        }
    }
    //间隔字母的holder
    public class CharacterHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        LinearLayout ll_character_text;

        CharacterHolder(View view) {
            super(view);

            mTextView = (TextView) view.findViewById(R.id.characters);
            ll_character_text = (LinearLayout) view.findViewById(R.id.ll_character_text);
            //ViewGroup.LayoutParams params = ll_character_text.getLayoutParams();
           // params.height = screenHeight*1.2;
           // ll_character_text.setLayoutParams(params);

        }
    }
    /**
     * 适配RecyclerView中条目的高度
     *
     * @param linearLayout
     * @param textView
     */
    private void setContentPersonItemHeightAndWidth(RelativeLayout linearLayout, CircleImageView textView) {
        ViewGroup.LayoutParams params = linearLayout.getLayoutParams();
        params.height = screenHeight * 3;
        linearLayout.setLayoutParams(params);

        RelativeLayout.LayoutParams params_text = (RelativeLayout.LayoutParams) textView.getLayoutParams();
        params_text.width = screenHeight * 3 - 30;
        params_text.height = screenHeight * 3 - 30;
        textView.setLayoutParams(params_text);
    }
}
