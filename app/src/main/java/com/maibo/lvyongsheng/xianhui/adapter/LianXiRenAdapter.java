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
import com.maibo.lvyongsheng.xianhui.HelperActivity;
import com.maibo.lvyongsheng.xianhui.R;
import com.maibo.lvyongsheng.xianhui.RemindActivity;
import com.maibo.lvyongsheng.xianhui.SearchPeopleActivity;
import com.maibo.lvyongsheng.xianhui.TaskActivity;
import com.maibo.lvyongsheng.xianhui.WorkActivity;
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
 * Created by LYS on 2016/10/25.
 */
public class LianXiRenAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private int screenHeight;
    private String[] mContactNames; // 联系人名称字符串数组
    private List<String> mContactList; // 联系人名称List（转换成拼音）
    private List<NewLCChatKitUser> resultList; // 最终结果（包含分组的字母）
    private List<String> characterList; // 字母List
    private List<LCChatKitUser> LCChat;
    List<LCChatKitUser> datas;//去除自己的数据
    //ProgressDialog dialog;
    int num = 0;

    public enum ITEM_TYPE {
        SEARCH,
        ZHU_SHOU,
        REMIND,
        WORK,
        TASK,
        ITEM_TYPE_CHARACTER,
        ITEM_TYPE_CONTACT
    }

    public LianXiRenAdapter(Context context, List<LCChatKitUser> LCChat, String displayName, int screenHeight) {
        this.LCChat = LCChat;
        this.screenHeight = screenHeight;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
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
        Collections.sort(mContactList, new ContactComparator());

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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE.SEARCH.ordinal()) {
            return new SearchHolder(mLayoutInflater.inflate(R.layout.style_lianxiren_search, parent, false));
        } else if (viewType == ITEM_TYPE.ZHU_SHOU.ordinal()) {
            return new ZhuShouHolder(mLayoutInflater.inflate(R.layout.style_lianxiren_zhushou, parent, false));
        } else if (viewType == ITEM_TYPE.REMIND.ordinal()) {
            return new RemindHolder(mLayoutInflater.inflate(R.layout.style_lianxiren_remind, parent, false));
        } else if (viewType == ITEM_TYPE.WORK.ordinal()) {
            return new WorkHolder(mLayoutInflater.inflate(R.layout.style_lianxiren_work, parent, false));
        } else if (viewType == ITEM_TYPE.TASK.ordinal()) {
            return new TaskHolder(mLayoutInflater.inflate(R.layout.style_lianxiren_task, parent, false));
        } else if (viewType == ITEM_TYPE.ITEM_TYPE_CHARACTER.ordinal()) {
            return new CharacterHolder(mLayoutInflater.inflate(R.layout.item_character_new, parent, false));
        } else {
            return new ContactHolder(mLayoutInflater.inflate(R.layout.common_user_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof SearchHolder) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //跳转到查询界面
                    Intent intent = new Intent(mContext, SearchPeopleActivity.class);
                    mContext.startActivity(intent);
                }
            });
        } else if (holder instanceof ZhuShouHolder) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, HelperActivity.class);
                    mContext.startActivity(intent);
                }
            });
        } else if (holder instanceof RemindHolder) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, RemindActivity.class);
                    mContext.startActivity(intent);
                }
            });
        } else if (holder instanceof WorkHolder) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, WorkActivity.class);
                    mContext.startActivity(intent);
                }
            });
        } else if (holder instanceof TaskHolder) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, TaskActivity.class);
                    mContext.startActivity(intent);
                }
            });

        } else if (holder instanceof CharacterHolder) {
            ((CharacterHolder) holder).mTextView.setText(resultList.get(position - 5).getFirstName());
            num++;
        } else if (holder instanceof ContactHolder) {
            ((ContactHolder) holder).mTextView.setText(resultList.get(position - 5).getLCCKUser().getUserName());
            Picasso.with(mContext).load(resultList.get(position - 5).getLCCKUser().getAvatarUrl()).into(((ContactHolder) holder).img_friend_avatar);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //此处监听录音权限是否开启
                    EventDatas eventDatas = new EventDatas(Constants.OPEN_ALL_PERMISSION, resultList.get(position - 5).getLCCKUser().getUserId());
                    EventBus.getDefault().post(eventDatas);
                }
            });
        }

    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0) {
            return ITEM_TYPE.SEARCH.ordinal();
        } else if (position == 1) {
            return ITEM_TYPE.ZHU_SHOU.ordinal();
        } else if (position == 2) {
            return ITEM_TYPE.REMIND.ordinal();
        } else if (position == 3) {
            return ITEM_TYPE.WORK.ordinal();
        } else if (position == 4) {
            return ITEM_TYPE.TASK.ordinal();
        } else {
            return resultList.get(position - 5).getType();
        }

    }

    @Override
    public int getItemCount() {
        return resultList == null ? 0 : resultList.size() + 5;
    }

    public class CharacterHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        LinearLayout ll_character_text;

        CharacterHolder(View view) {
            super(view);

            mTextView = (TextView) view.findViewById(R.id.characters);
            ll_character_text = (LinearLayout) view.findViewById(R.id.ll_character_text);
            ViewGroup.LayoutParams params = ll_character_text.getLayoutParams();
            params.height = screenHeight;
            ll_character_text.setLayoutParams(params);

        }
    }

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

    public class ZhuShouHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_zhushou_all;
        TextView tv_helper_head;

        ZhuShouHolder(View view) {
            super(view);
            ll_zhushou_all = (LinearLayout) view.findViewById(R.id.ll_zhushou_all);
            tv_helper_head = (TextView) view.findViewById(R.id.tv_helper_head);
            setItemHeightAndWidth(ll_zhushou_all, tv_helper_head);

        }

    }


    public class RemindHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_remind_all;
        TextView tv_remind;

        RemindHolder(View view) {
            super(view);
            ll_remind_all = (LinearLayout) view.findViewById(R.id.ll_remind_all);
            tv_remind = (TextView) view.findViewById(R.id.tv_remind);
            setItemHeightAndWidth(ll_remind_all, tv_remind);
        }
    }

    public class WorkHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_work_all;
        TextView tv_work;

        WorkHolder(View view) {
            super(view);
            ll_work_all = (LinearLayout) view.findViewById(R.id.ll_work_all);
            tv_work = (TextView) view.findViewById(R.id.tv_work);
            setItemHeightAndWidth(ll_work_all, tv_work);
        }
    }

    public class TaskHolder extends RecyclerView.ViewHolder {
        LinearLayout ll_task_all;
        TextView tv_head_task;

        TaskHolder(View view) {
            super(view);
            ll_task_all = (LinearLayout) view.findViewById(R.id.ll_task_all);
            tv_head_task = (TextView) view.findViewById(R.id.tv_head_task);
            setItemHeightAndWidth(ll_task_all, tv_head_task);
        }
    }

    public class ContactHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        CircleImageView img_friend_avatar;
        RelativeLayout ll_content_person;

        ContactHolder(View view) {
            super(view);
            img_friend_avatar = (CircleImageView) view.findViewById(R.id.img_friend_avatar);
            mTextView = (TextView) view.findViewById(R.id.tv_friend_name);
            ll_content_person = (RelativeLayout) view.findViewById(R.id.ll_content_person);
            setContentPersonItemHeightAndWidth(ll_content_person, img_friend_avatar);

        }
    }

    public int getScrollPosition(String character) {
        if (characterList.contains(character)) {
            for (int i = 0; i < resultList.size(); i++) {
                if (resultList.get(i).getFirstName().equals(character)) {
                    return i + 5;
                }
            }
        }

        return -1; // -1不会滑动
    }

    /**
     * 动态适配提醒、工作、任务 的高度
     *
     * @param linearLayout
     * @param textView
     */
    private void setItemHeightAndWidth(LinearLayout linearLayout, TextView textView) {
        ViewGroup.LayoutParams params = linearLayout.getLayoutParams();
        params.height = screenHeight * 3;
        linearLayout.setLayoutParams(params);

        LinearLayout.LayoutParams params_text = (LinearLayout.LayoutParams) textView.getLayoutParams();
        params_text.width = screenHeight * 3 - 30;
        params_text.height = screenHeight * 3 - 30;
        textView.setLayoutParams(params_text);
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
}

