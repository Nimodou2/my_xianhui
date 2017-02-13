package com.maibo.lvyongsheng.xianhui.serviceholdermessage;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.avos.avoscloud.AVCallback;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMTypedMessage;
import com.avos.avoscloud.im.v2.AVIMTypedMessageHandler;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.avos.avoscloud.im.v2.messages.AVIMTextMessage;

import cn.leancloud.chatkit.LCChatKit;
import cn.leancloud.chatkit.LCChatKitUser;
import cn.leancloud.chatkit.cache.LCIMConversationItemCache;
import cn.leancloud.chatkit.cache.LCIMProfileCache;
import cn.leancloud.chatkit.event.LCIMIMTypeMessageEvent;
import cn.leancloud.chatkit.utils.LCIMConstants;
import cn.leancloud.chatkit.utils.LCIMConversationUtils;
import cn.leancloud.chatkit.utils.LCIMLogUtils;
import cn.leancloud.chatkit.utils.LCIMNotificationUtils;
import de.greenrobot.event.EventBus;

/**
 * Created by LYS on 2017/1/9.
 */

public class MyselfMessageHandler extends AVIMTypedMessageHandler<AVIMTypedMessage> {
    private Context context;

    public MyselfMessageHandler(Context context) {
        this.context = context.getApplicationContext();
    }
    public void onMessage(AVIMTypedMessage message, AVIMConversation conversation, AVIMClient client) {
        if(message != null && message.getMessageId() != null) {
            if(LCChatKit.getInstance().getCurrentUserId() == null) {
                LCIMLogUtils.d(new String[]{"selfId is null, please call LCChatKit.open!"});
                client.close((AVIMClientCallback)null);
            } else if(!client.getClientId().equals(LCChatKit.getInstance().getCurrentUserId())) {
                client.close((AVIMClientCallback)null);
            } else if(!message.getFrom().equals(client.getClientId())) {
                if(LCIMNotificationUtils.isShowNotification(conversation.getConversationId())) {
                    this.sendNotification(message, conversation);
                }

                LCIMConversationItemCache.getInstance().increaseUnreadCount(message.getConversationId());
                this.sendEvent(message, conversation);
            } else {
                LCIMConversationItemCache.getInstance().insertConversation(message.getConversationId());
            }

        } else {
            LCIMLogUtils.d(new String[]{"may be SDK Bug, message or message id is null"});
        }
    }

    public void onMessageReceipt(AVIMTypedMessage message, AVIMConversation conversation, AVIMClient client) {
        super.onMessageReceipt(message, conversation, client);
    }

    private void sendEvent(AVIMTypedMessage message, AVIMConversation conversation) {
        LCIMIMTypeMessageEvent event = new LCIMIMTypeMessageEvent();
        event.message = message;
        event.conversation = conversation;
        EventBus.getDefault().post(event);
    }

    private void sendNotification(final AVIMTypedMessage message, final AVIMConversation conversation) {
        if(null != conversation && null != message) {
            final String notificationContent = message instanceof AVIMTextMessage ?((AVIMTextMessage)message).getText():this.context.getString(cn.leancloud.chatkit.R.string.lcim_unspport_message_type);
            //发送系统消息通知
            LCIMConversationUtils.getConversationName(conversation, new AVCallback<String>() {
                @Override
                protected void internalDone0(String s, AVException e) {
                    if (null != e) {
                        LCIMLogUtils.logException(e);
                    } else {
                       //1、判断是否为系统消息
                        if (!TextUtils.isEmpty(conversation.getName())){
                            String title = conversation.getName();
                            Intent intent = MyselfMessageHandler.this.getIMNotificationIntent(conversation.getConversationId(), message.getFrom());
                            LCIMNotificationUtils.showNotification(MyselfMessageHandler.this.context, title, notificationContent, (String)null, intent);
                        }

                    }
                }
            });

            //发送普通离线消息通知
            LCIMProfileCache.getInstance().getCachedUser(message.getFrom(), new AVCallback<LCChatKitUser>() {
                @Override
                protected void internalDone0(LCChatKitUser lcChatKitUser, AVException e) {
                    if(e != null) {
                        LCIMLogUtils.logException(e);
                    } else if(null != lcChatKitUser) {
                        String title = lcChatKitUser.getUserName();
                        Intent intent = MyselfMessageHandler.this.getIMNotificationIntent(conversation.getConversationId(), message.getFrom());
                        LCIMNotificationUtils.showNotification(MyselfMessageHandler.this.context, title, notificationContent, (String)null, intent);
                    }
                }
            });
        }

    }

    private Intent getIMNotificationIntent(String conversationId, String peerId) {
        Intent intent = new Intent();
        intent.setAction(LCIMConstants.CHAT_NOTIFICATION_ACTION);
        intent.putExtra(LCIMConstants.CONVERSATION_ID, conversationId);
        intent.putExtra(LCIMConstants.PEER_ID, peerId);
        intent.setPackage(this.context.getPackageName());
        intent.addCategory("android.intent.category.DEFAULT");
        return intent;
    }
}
