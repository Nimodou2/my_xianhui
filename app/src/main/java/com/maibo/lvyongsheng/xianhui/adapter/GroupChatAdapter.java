package com.maibo.lvyongsheng.xianhui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.maibo.lvyongsheng.xianhui.R;
import com.maibo.lvyongsheng.xianhui.entity.NewLCChatKitUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.leancloud.chatkit.LCChatKitUser;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by LYS on 2017/3/2.
 */

public class GroupChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    List<LCChatKitUser> datas;
    LayoutInflater mLayoutInflater;
    int screenHeight;
    private String[] mContactNames; // 联系人名称字符串数组
    private List<String> mContactList; // 联系人名称List（转换成拼音）
    private List<NewLCChatKitUser> resultList; // 最终结果（包含分组的字母）
    private List<String> characterList; // 字母List
    private Map<String, Integer> chooseStates;
    private String buffer="";//记录被选中的user

    public enum ITEM_TYPE {
        OPEN_HAVED_GROUP,
        ITEM_TYPE_CHARACTER,
        ITEM_TYPE_CONTACT
    }

    public GroupChatAdapter(Context mContext, List<LCChatKitUser> datas, int screenHeight) {
        this.mContext = mContext;
        this.datas = datas;
        this.screenHeight = screenHeight;
        mLayoutInflater = LayoutInflater.from(mContext);
        chooseStates = new HashMap<>();
        dealDatas(datas);

    }

    /**
     * 变更数据，刷新适配器
     * @param datas
     */
    public void setDatas(List<LCChatKitUser> datas){
        this.datas=datas;
        Log.e("data.size",datas.size()+"");
        dealDatas(datas);
        notifyDataSetChanged();
    }

    /**
     * 处理数据，构造成要展示的样子
     *
     * @param datas
     */
    private void dealDatas(List<LCChatKitUser> datas) {
        mContactNames = new String[datas.size()];
        for (int i = 0; i < datas.size(); i++) {
            mContactNames[i] = datas.get(i).getUserName();
        }
        mContactList = new ArrayList<>();
        Map<String, LCChatKitUser> map = new IdentityHashMap<>();//key值可以重复

        for (int i = 0; i < mContactNames.length; i++) {
            //将汉字转化成拼音
            String pinyin = PinyinHelper.convertToPinyinString(mContactNames[i], "", PinyinFormat.WITHOUT_TONE);
            map.put(pinyin, datas.get(i));
            mContactList.add(pinyin);
        }
        //从A-Z排序
        Collections.sort(mContactList, new ContactComparator());
        resultList = new ArrayList<>();//最终数据集
        characterList = new ArrayList<>();//大写的首字母集合
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE.OPEN_HAVED_GROUP.ordinal()) {
            return new HaveCreatedGroupViewHolder(mLayoutInflater.inflate(R.layout.style_choose_group, parent, false));
        } else if (viewType == ITEM_TYPE.ITEM_TYPE_CHARACTER.ordinal()) {
            return new CharacterHolder(mLayoutInflater.inflate(R.layout.item_character_new, parent, false));
        } else {
            return new ContactHolder(mLayoutInflater.inflate(R.layout.style_group_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof CharacterHolder) {
            ((CharacterHolder) holder).mTextView.setText(resultList.get(position - 1).getFirstName());
        } else if (holder instanceof ContactHolder) {
            ((ContactHolder) holder).mTextView.setText(resultList.get(position - 1).getLCCKUser().getUserName());
            Picasso.with(mContext).load(resultList.get(position - 1).getLCCKUser().getAvatarUrl()).into(((ContactHolder) holder).img_friend_avatar);
            String userID2 = resultList.get(position - 1).getLCCKUser().getUserId();
            if (chooseStates.get(userID2)==null|| chooseStates.get(userID2) == 0){
                ((ContactHolder) holder).iv_yes.setVisibility(View.INVISIBLE);
            }else{
                ((ContactHolder) holder).iv_yes.setVisibility(View.VISIBLE);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String userID = resultList.get(position - 1).getLCCKUser().getUserId();
                    if (chooseStates.get(userID) == null || chooseStates.get(userID) == 0) {
                        ((ContactHolder) holder).iv_yes.setVisibility(View.VISIBLE);
                        buffer+=","+userID;
                        chooseStates.put(userID, 1);
                    } else {
                        buffer.replace(","+userID,"");
                        ((ContactHolder) holder).iv_yes.setVisibility(View.INVISIBLE);
                        chooseStates.put(userID, 0);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return resultList == null ? 0 : resultList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ITEM_TYPE.OPEN_HAVED_GROUP.ordinal();
        } else {
            return resultList.get(position - 1).getType();
        }
    }

    public class HaveCreatedGroupViewHolder extends RecyclerView.ViewHolder {
        TextView tv_choose_group;
        LinearLayout ll_choose_group;
        HaveCreatedGroupViewHolder(View view) {
            super(view);
            ll_choose_group= (LinearLayout) view.findViewById(R.id.ll_choose_group);
            tv_choose_group= (TextView) view.findViewById(R.id.tv_choose_group);
            setItemHeightAndWidth(ll_choose_group,tv_choose_group);
        }
    }

    /**
     * 字母
     */
    public class CharacterHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        LinearLayout ll_character_text;

        CharacterHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.characters);
            ll_character_text = (LinearLayout) view.findViewById(R.id.ll_character_text);
            ViewGroup.LayoutParams params = ll_character_text.getLayoutParams();
            params.height = screenHeight;
            Log.e("screenHeight", screenHeight + "");
            ll_character_text.setLayoutParams(params);

        }
    }

    /**
     * 用户
     */
    public class ContactHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        CircleImageView img_friend_avatar;
        LinearLayout ll_content_person;
        ImageView iv_no, iv_yes;

        ContactHolder(View view) {
            super(view);
            img_friend_avatar = (CircleImageView) view.findViewById(R.id.img_friend_avatar);
            mTextView = (TextView) view.findViewById(R.id.tv_friend_name);
            ll_content_person = (LinearLayout) view.findViewById(R.id.ll_content_person);
            iv_no = (ImageView) view.findViewById(R.id.iv_no);
            iv_yes = (ImageView) view.findViewById(R.id.iv_yes);
            setContentPersonItemHeightAndWidth(ll_content_person, img_friend_avatar);

        }
    }

    /**
     * 返回字母对应的位置
     *
     * @param character
     * @return
     */
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

    /**
     * 获取群组建对象
     * @return
     */
    public List<String> getGroupUser(){
        String buffer2="";
        List<String> groupUser=new ArrayList<>();
        if (buffer.length()>0){
            buffer2=buffer.substring(1);
            String strBuf[]= buffer2.split(",");
            for (int i=0;i<strBuf.length;i++){
                groupUser.add(strBuf[i]);
            }
            return groupUser;
        }else{
            return null;
        }
    }

    /**
     * 字母排序
     */
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


    /**
     * 动态适配提醒、工作、任务 的高度
     *
     * @param textView
     */
    private void setItemHeightAndWidth(LinearLayout linearLayout, TextView textView) {
        ViewGroup.LayoutParams params = linearLayout.getLayoutParams();
        params.height = screenHeight * 3;
        linearLayout.setLayoutParams(params);

        LinearLayout.LayoutParams params_text = (LinearLayout.LayoutParams) textView.getLayoutParams();
//        params_text.width = screenHeight * 3;
        params_text.height = screenHeight * 3;
        textView.setLayoutParams(params_text);
    }

    /**
     * 适配RecyclerView中条目的高度
     *
     * @param linearLayout
     * @param textView
     */
    private void setContentPersonItemHeightAndWidth(LinearLayout linearLayout, CircleImageView textView) {
        ViewGroup.LayoutParams params = linearLayout.getLayoutParams();
        params.height = screenHeight * 3;
        linearLayout.setLayoutParams(params);

        LinearLayout.LayoutParams params_text = (LinearLayout.LayoutParams) textView.getLayoutParams();
        params_text.width = screenHeight * 3 - 30;
        params_text.height = screenHeight * 3 - 30;
        textView.setLayoutParams(params_text);
    }

}
