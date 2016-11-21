package com.maibo.lvyongsheng.xianhui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.maibo.lvyongsheng.xianhui.implement.DrawRoundCorner;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import cn.leancloud.chatkit.LCChatKitUser;
import cn.leancloud.chatkit.activity.LCIMConversationActivity;
import cn.leancloud.chatkit.utils.LCIMConstants;
import cn.leancloud.chatkit.viewholder.LCIMCommonViewHolder;
import okhttp3.Call;

/**
 * Created by wli on 15/11/24.
 */
public class ContactItemHolder extends LCIMCommonViewHolder<LCChatKitUser> {

  TextView nameView;
  ImageView avatarView;

  public LCChatKitUser lcChatKitUser;

  public ContactItemHolder(Context context, ViewGroup root) {
    super(context, root, R.layout.common_user_item);
    initView();
  }

  public void initView() {
    nameView = (TextView)itemView.findViewById(R.id.tv_friend_name);
    avatarView = (ImageView)itemView.findViewById(R.id.img_friend_avatar);

    itemView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(getContext(), LCIMConversationActivity.class);
        intent.putExtra(LCIMConstants.PEER_ID, lcChatKitUser.getUserId());
        getContext().startActivity(intent);
      }
    });
  }
  @Override
  public void bindData(LCChatKitUser lcChatKitUser) {
    this.lcChatKitUser = lcChatKitUser;
    final String avatarUrl = lcChatKitUser.getAvatarUrl();
    if (!TextUtils.isEmpty(avatarUrl)) {
      //Picasso.with(getContext()).load(avatarUrl).into(avatarView);
      setHead(avatarUrl,avatarView);
     /* try {
        Bitmap bitmap=Picasso.with(getContext()).load(avatarUrl).get();
        Bitmap bm= DrawRoundCorner.makeRoundCorner(bitmap,63);
        Drawable drawable =new BitmapDrawable(bm);
        avatarView.setImageDrawable(drawable);
      }catch (Exception e){
      }*/

      //avatarView.setImageResource(R.drawable.man_yes);
    } else {
      avatarView.setImageResource(R.drawable.lcim_default_avatar_icon);
    }
    nameView.setText(lcChatKitUser.getUserName());
  }

  public static ViewHolderCreator HOLDER_CREATOR = new ViewHolderCreator<ContactItemHolder>() {
    @Override
    public ContactItemHolder createByViewGroupAndType(ViewGroup parent, int viewType) {
      return new ContactItemHolder(parent.getContext(), parent);
    }
  };

  public void setHead(String avator_url,final ImageView iv_avator){
    //下载头像
    OkHttpUtils
            .get()
            .url(avator_url)
            .build()
            .execute(new BitmapCallback() {
              @Override
              public void onError(Call call, Exception e, int id) {

              }
              @Override
              public void onResponse(Bitmap response, int id) {
                Bitmap bn= DrawRoundCorner.makeRoundCorner(response);
                Drawable drawable =new BitmapDrawable(bn);
                iv_avator.setImageDrawable(drawable);
              }
            });
  }
}
