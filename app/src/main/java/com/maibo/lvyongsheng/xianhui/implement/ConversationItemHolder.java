package com.maibo.lvyongsheng.xianhui.implement;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVCallback;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMReservedMessageType;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.callback.AVIMConversationCallback;
import com.avos.avoscloud.im.v2.callback.AVIMMessagesQueryCallback;
import com.avos.avoscloud.im.v2.callback.AVIMSingleMessageQueryCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;
import com.maibo.lvyongsheng.xianhui.HelperActivity;
import com.maibo.lvyongsheng.xianhui.NewHandRemindActivity;
import com.maibo.lvyongsheng.xianhui.R;
import com.maibo.lvyongsheng.xianhui.RemindActivity;
import com.maibo.lvyongsheng.xianhui.TaskActivity;
import com.maibo.lvyongsheng.xianhui.constants.Constants;
import com.maibo.lvyongsheng.xianhui.entity.EventDatas;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.leancloud.chatkit.LCChatMessageInterface;
import cn.leancloud.chatkit.cache.LCIMConversationItemCache;
import cn.leancloud.chatkit.event.LCIMConversationItemLongClickEvent;
import cn.leancloud.chatkit.utils.LCIMConstants;
import cn.leancloud.chatkit.utils.LCIMConversationUtils;
import cn.leancloud.chatkit.utils.LCIMLogUtils;
import cn.leancloud.chatkit.viewholder.LCIMCommonViewHolder;
import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by wli on 15/10/8.
 * 会话 item 对应的 holder
 */
public class ConversationItemHolder extends LCIMCommonViewHolder {

    CircleImageView avatarView;
    TextView unreadView;
    TextView messageView;
    TextView timeView;
    TextView nameView;
    RelativeLayout avatarLayout;
    LinearLayout contentLayout;
    LinearLayout ll_all;
    Button bt_delete,bt_read;

    public static ViewHolderCreator HOLDER_CREATOR = new ViewHolderCreator<ConversationItemHolder>() {
        @Override
        public ConversationItemHolder createByViewGroupAndType(ViewGroup parent, int viewType) {
            return new ConversationItemHolder(parent);

        }
    };
    public ConversationItemHolder(ViewGroup root) {
//        super(root.getContext(), root, R.layout.style_conversation_item_list);
        super(root.getContext(), root, R.layout.style_new_conversation_item_list);
        initView(root);
    }

    public void initView(ViewGroup root) {
        avatarView = (CircleImageView) itemView.findViewById(R.id.conversation_item_iv_avatar);
        nameView = (TextView) itemView.findViewById(R.id.conversation_item_tv_name);
        timeView = (TextView) itemView.findViewById(R.id.conversation_item_tv_time);
        unreadView = (TextView) itemView.findViewById(R.id.conversation_item_tv_unread);
        messageView = (TextView) itemView.findViewById(R.id.conversation_item_tv_message);
        avatarLayout = (RelativeLayout) itemView.findViewById(R.id.conversation_item_layout_avatar);
        contentLayout = (LinearLayout) itemView.findViewById(R.id.conversation_item_layout_content);
        SwipeMenuLayout swipe_menu_layout= (SwipeMenuLayout) itemView.findViewById(R.id.swipe_menu_layout);
        //动态设置item的高度
        setItemHeightAndWidth(root, swipe_menu_layout);



        ll_all= (LinearLayout) itemView.findViewById(R.id.ll_all);
        bt_read= (Button) itemView.findViewById(R.id.bt_read);
        bt_delete= (Button) itemView.findViewById(R.id.bt_delete);
    }

    /**
     * 动态设置Item的宽和高
     * @param root
     * @param swipe_menu_layout
     */
    private void setItemHeightAndWidth(ViewGroup root, SwipeMenuLayout swipe_menu_layout) {
        int screenHeight= Util.getScreenHeight(root.getContext());
        int viewHeight= (screenHeight-getStatusBarHeight(root.getContext()))/35;
        ViewGroup.LayoutParams params=swipe_menu_layout.getLayoutParams();
        params.height=viewHeight*3;
        swipe_menu_layout.setLayoutParams(params);

        ViewGroup.LayoutParams params1=avatarLayout.getLayoutParams();
        params1.height=viewHeight*3-30;
        params1.width=viewHeight*3-30;
        avatarLayout.setLayoutParams(params1);
    }

    @Override
    public void bindData(Object o) {
        reset();
        final AVIMConversation conversation = (AVIMConversation) o;
        if (null != conversation) {
            if (null == conversation.getCreatedAt()) {
                conversation.fetchInfoInBackground(new AVIMConversationCallback() {
                    @Override
                    public void done(AVIMException e) {
                        if (e != null) {
                            LCIMLogUtils.logException(e);
                        } else {
                            updateName(conversation);
                            updateIcon(conversation);

                        }
                    }
                });
            } else {
                updateName(conversation);
                updateIcon(conversation);
            }
            updateUnreadCount(conversation);
            updateLastMessageByConversation(conversation);

            ll_all.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onConversationItemClick(conversation);
                }
            });
            bt_read.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //标记已读
                    LCIMConversationItemCache.getInstance().clearUnread(conversation.getConversationId());
                    EventBus.getDefault().post(new EventDatas(Constants.CONVERSATION_READ_STATUS,""));
                }
            });
            bt_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //删除会话
                    EventBus.getDefault().post(new LCIMConversationItemLongClickEvent(conversation));
                }
            });



//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    onConversationItemClick(conversation);
//                    //此处可处理判断点击类型
//                }
//            });
//
//
//            itemView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                    builder.setItems(new String[]{"删除该聊天"}, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            EventBus.getDefault().post(new LCIMConversationItemLongClickEvent(conversation));
//
//                        }
//                    });
//                    AlertDialog dialog = builder.create();
//                    dialog.show();
//                    return false;
//                }
//            });


        }
    }

    /**
     * 一开始的时候全部置为空，避免因为异步请求造成的刷新不及时而导致的展示原有的缓存数据
     */
    private void reset() {
        avatarView.setImageResource(0);
        nameView.setText("");
        //timeView.setText("");
        //messageView.setText("");
        unreadView.setVisibility(View.GONE);

        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
        timeView.setText(format.format(date));
        messageView.setText("我们已经是好友啦，一起来聊天吧!");
    }

    /**
     * 更新 name，单聊的话展示对方姓名，群聊展示所有用户的用户名
     *
     * @param conversation
     */
    private void updateName(final AVIMConversation conversation) {
        LCIMConversationUtils.getConversationName(conversation, new AVCallback<String>() {
            @Override
            protected void internalDone0(String s, AVException e) {
                if (null != e) {
                    LCIMLogUtils.logException(e);
                } else {
                    nameView.setText(s);
                }
            }
        });
    }



    /**
     * 更新 item icon，目前的逻辑为：
     * 单聊：展示对方的头像
     * 群聊：展示一个静态的 icon
     *
     * @param conversation
     */
    private void updateIcon(AVIMConversation conversation) {
        if (null != conversation) {
            if (conversation.isTransient() || conversation.getMembers().size() > 2) {
                avatarView.setImageResource(R.drawable.lcim_group_icon); //聊天室的头像
            } else {
                LCIMConversationUtils.getConversationPeerIcon(conversation, new AVCallback<String>() {
                    @Override
                    protected void internalDone0(String s, AVException e) {
                        if (null != e) {
                            LCIMLogUtils.logException(e);
                        }
                        if (!TextUtils.isEmpty(s)) {
                            Picasso.with(getContext()).load(s).placeholder(R.drawable.lcim_default_avatar_icon).into(avatarView);
                        } else {
                            avatarView.setImageResource(R.drawable.lcim_default_avatar_icon);
                        }
                    }
                });
            }
        }
    }

    /**
     * 更新未读消息数量
     *
     * @param conversation
     */
    private void updateUnreadCount(AVIMConversation conversation) {
        int num = LCIMConversationItemCache.getInstance().getUnreadCount(conversation.getConversationId());
        unreadView.setText(num + "");
        unreadView.setVisibility(num > 0 ? View.VISIBLE : View.GONE);
    }

    /**
     * 更新最后一条消息
     * queryMessages
     *
     * @param conversation
     */
    private void updateLastMessageByConversation(final AVIMConversation conversation) {
        // TODO 此处如果调用 AVIMConversation.getLastMessage 的话会造成一直读取缓存数据造成展示不对
        // 所以使用 queryMessages，但是这个接口还是很难有，需要 sdk 对这个进行支持
        conversation.getLastMessage(new AVIMSingleMessageQueryCallback() {
            @Override
            public void done(AVIMMessage avimMessage, AVIMException e) {
                //处理自定义消息
                if (null != avimMessage) {
                    upMyselfMsg(avimMessage);
                    updateLastMessage(avimMessage);
                } else {
                    conversation.queryMessages(1, new AVIMMessagesQueryCallback() {
                        @Override
                        public void done(List<AVIMMessage> list, AVIMException e) {
                            if (null != e) {
                                LCIMLogUtils.logException(e);
                            }
                            if (null != list && !list.isEmpty()) {
                                updateLastMessage(list.get(0));

                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 更新item展示内容中的自定义消息部分
     * @param avimMessage
     */
    public void upMyselfMsg(AVIMMessage avimMessage){
        AVIMReservedMessageType type = AVIMReservedMessageType.getAVIMReservedMessageType(
                ((AVIMTypedMessage) avimMessage).getMessageType());
        switch (type){
            case TextMessageType:
                if(avimMessage!=null&&((AVIMTextMessage) avimMessage).getAttrs()!=null) {
                    Map<String, Object> map = ((AVIMTextMessage) avimMessage).getAttrs();
                    if (map.containsKey("notice_type")) {
                        String head_url=(String) map.get("avator");
                        Picasso.with(getContext()).load(head_url).placeholder(R.drawable.lcim_default_avatar_icon).into(avatarView);
                    }
                }
                break;
        }

    }

    /**
     * 更新 item 的展示内容，及最后一条消息的内容
     *
     * @param message
     */
    private void updateLastMessage(AVIMMessage message) {
        if (null != message) {
            Date date = new Date(message.getTimestamp());
            SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
            timeView.setText(format.format(date));
            messageView.setText(getMessageeShorthand(getContext(), message));
        }
    }

    private void onConversationItemClick(final AVIMConversation conversation) {
        conversation.queryMessages(new AVIMMessagesQueryCallback() {
            @Override
            public void done(List<AVIMMessage> list, AVIMException e) {
                //这里面的list包括会话内容
                /*((AVIMTextMessage) list.get(0)).getAttrs()!=null*/
                if  (list.size()>0){
                    AVIMReservedMessageType type = AVIMReservedMessageType.getAVIMReservedMessageType(
                            ((AVIMTypedMessage) list.get(0)).getMessageType());
                    switch (type){
                        case TextMessageType:
                            if (((AVIMTextMessage) list.get(0)).getAttrs()!=null){
                                Map<String, Object> map= ((AVIMTextMessage) list.get(0)).getAttrs();
                                if (map.containsKey("notice_type")){
                                    if (map.get("notice_type").equals("common_notice")){
                                        LCIMConversationItemCache.getInstance().clearUnread(conversation.getConversationId());
                                        getContext().startActivity(new Intent(getContext(), RemindActivity.class));
                                        return;
                                    }else if (map.get("notice_type").equals("daily_report")||map.get("notice_type").equals("project_plan")){
                                        LCIMConversationItemCache.getInstance().clearUnread(conversation.getConversationId());
                                        getContext().startActivity(new Intent(getContext(), HelperActivity.class));
                                        return;
                                    }else if (map.get("notice_type").equals("process")){
                                        LCIMConversationItemCache.getInstance().clearUnread(conversation.getConversationId());
                                        getContext().startActivity(new Intent(getContext(), TaskActivity.class));
                                        return;
                                    }else if (map.get("notice_type").equals("tutorial")){
                                        //暂时不做处理,等待web页面完成之后再打开
                                        LCIMConversationItemCache.getInstance().clearUnread(conversation.getConversationId());
                                        getContext().startActivity(new Intent(getContext(), NewHandRemindActivity.class));
                                        return;
                                    }
                                }

                            }
                            break;
                    }
                }


                    try {
                        Intent intent = new Intent();
                        intent.setPackage(getContext().getPackageName());
                        intent.setAction(LCIMConstants.CONVERSATION_ITEM_CLICK_ACTION);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.putExtra(LCIMConstants.CONVERSATION_ID, conversation.getConversationId());
                        getContext().startActivity(intent);
                    } catch (ActivityNotFoundException exception) {
                        Log.i(LCIMConstants.LCIM_LOG_TAG, exception.toString());
                    }

            }
        });


    }



    private static CharSequence getMessageeShorthand(Context context, AVIMMessage message) {

        if (message instanceof AVIMTypedMessage) {
            AVIMReservedMessageType type = AVIMReservedMessageType.getAVIMReservedMessageType(
                    ((AVIMTypedMessage) message).getMessageType());
            switch (type) {
                case TextMessageType:
                    return ((AVIMTextMessage) message).getText();
                case ImageMessageType:
                    return context.getString(R.string.lcim_message_shorthand_image);
                case LocationMessageType:
                    return context.getString(R.string.lcim_message_shorthand_location);
                case AudioMessageType:
                    return context.getString(R.string.lcim_message_shorthand_audio);
                default:
                    CharSequence shortHand = "";
                    if (message instanceof LCChatMessageInterface) {
                        LCChatMessageInterface messageInterface = (LCChatMessageInterface) message;
                        shortHand = messageInterface.getShorthand();
                    }
                    if (TextUtils.isEmpty(shortHand)) {
                        shortHand = context.getString(R.string.lcim_message_shorthand_unknown);
                    }
                    return shortHand;
            }
        } else {
            return message.getContent();
        }

    }
    public  int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result =  context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
