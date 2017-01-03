/*
 * Copyright (C) 2015 Karumi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.maibo.lvyongsheng.xianhui.myinterface;

import android.util.Log;

import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.maibo.lvyongsheng.xianhui.MainActivity;

public class SamplePermissionListener implements PermissionListener {

  private final MainActivity activity;

  public SamplePermissionListener(MainActivity activity) {
    this.activity = activity;
  }

  @Override public void onPermissionGranted(PermissionGrantedResponse response) {
    activity.showPermissionGranted(response.getPermissionName(),0);
    Log.e("xiugai:","ddd");
  }

  @Override public void onPermissionDenied(PermissionDeniedResponse response) {
    //此时则是取消了设置，底部出现手动设置的对话框
    activity.showPermissionDenied(response.getPermissionName(), response.isPermanentlyDenied());
    Log.e("xiugai:","eee");
  }

  @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission,
      PermissionToken token) {
    //此时需要打开自己的dialog，询问是否要打开权限
    activity.showPermissionRationale(token);
    Log.e("xiugai:","fff");
  }
}
