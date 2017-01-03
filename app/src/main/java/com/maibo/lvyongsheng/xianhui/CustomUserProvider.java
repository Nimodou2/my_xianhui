package com.maibo.lvyongsheng.xianhui;

import java.util.ArrayList;
import java.util.List;

import cn.leancloud.chatkit.LCChatKitUser;
import cn.leancloud.chatkit.LCChatProfileProvider;
import cn.leancloud.chatkit.LCChatProfilesCallBack;

/**
 * Created by wli on 15/12/4.
 * 实现自定义用户体系
 */
public class CustomUserProvider implements LCChatProfileProvider {

  public static String myConversationID="";

  public static CustomUserProvider customUserProvider;
  public static List<LCChatKitUser> partUsers = new ArrayList<LCChatKitUser>();
  public synchronized static CustomUserProvider getInstance() {

    if (null == customUserProvider) {
      customUserProvider = new CustomUserProvider();
    }
    return customUserProvider;
  }
  public CustomUserProvider(){}

  /**
   * 服务器：用户信息
   */

  /**
   * 实现该方法可以传入自己的用户体系
   * @param list
   * @param callBack
     */
  @Override
  public void fetchProfiles(List<String> list, LCChatProfilesCallBack callBack) {
    List<LCChatKitUser> userList = new ArrayList<LCChatKitUser>();
    for (String userId : list) {
      for (LCChatKitUser user : partUsers) {
        if (user.getUserId().equals(userId)) {
          userList.add(user);
          break;
        }
      }
    }
    callBack.done(userList, null);
  }
  public List<LCChatKitUser> getAllUsers() {
    return partUsers;
  }
}
