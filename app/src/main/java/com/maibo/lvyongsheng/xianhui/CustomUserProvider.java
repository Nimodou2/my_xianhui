package com.maibo.lvyongsheng.xianhui;

import android.content.Context;

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

  public static CustomUserProvider customUserProvider;
  public static List<LCChatKitUser> partUsers = new ArrayList<LCChatKitUser>();
    //Context context可能有问题？？？？？
  public synchronized static CustomUserProvider getInstance(Context context) {

    if (null == customUserProvider) {
      customUserProvider = new CustomUserProvider();
    }
    return customUserProvider;
  }
  public CustomUserProvider(){}
//  public CustomUserProvider(Context context) {
//    SharedPreferences sp= context.getSharedPreferences("baseDate",Context.MODE_PRIVATE);
//    String apiURL=sp.getString("apiURL",null);
//    String token = sp.getString("token",null);
//    final String name=sp.getString("displayname",null);
//    Log.e("name",sp.getString("displayname","hehe")+"token"+token);
//    //初始化用户信息
//    OkHttpUtils
//            .post()
//            .url(apiURL+"/rest/employee/getuserlist")
//            .addParams("token",token)
//            .build()
//            .execute(new StringCallback() {
//              @Override
//              public void onError(Call call, Exception e, int id) {
//
//              }
//              @Override
//              public void onResponse(String response, int id) {
//                //获取员工信息
//                JsonObject object = new JsonParser().parse(response).getAsJsonObject();
//                JsonArray array = object.get("data").getAsJsonArray();
//                partUsers.clear();
//               for (JsonElement jsonElement:array){
//                 JsonObject jObject = jsonElement.getAsJsonObject();
//                 String names=jObject.get("display_name").getAsString();
//                 String guid=jObject.get("guid").getAsString();
//                 String avator_url=jObject.get("avator_url").getAsString();
//                 if (!name.equals(names))
//                    partUsers.add(new LCChatKitUser(guid, names, avator_url));
//
//               }
//                Log.e("partUser.size",partUsers.size()+"");
//              }
//            });
//  }



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
  //清除缓存
  public static void cleanAllUser(){
    customUserProvider=null;
    //partUsers.clear();
  }
}
