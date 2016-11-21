package com.maibo.lvyongsheng.xianhui.entity;

import cn.leancloud.chatkit.LCChatKitUser;

/**
 * Created by LYS on 2016/10/25.
 */
public class NewLCChatKitUser {
    private LCChatKitUser LCCKUser;
    private String firstName;
    private int type;

    public NewLCChatKitUser(LCChatKitUser LCCKUser, String firstName, int type) {
        this.LCCKUser = LCCKUser;
        this.firstName = firstName;
        this.type = type;
    }

    public LCChatKitUser getLCCKUser() {
        return LCCKUser;
    }

    public void setLCCKUser(LCChatKitUser LCCKUser) {
        this.LCCKUser = LCCKUser;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
