package com.maibo.lvyongsheng.xianhui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maibo.lvyongsheng.xianhui.entity.TabMax;
import com.maibo.lvyongsheng.xianhui.implement.CloseAllActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.Bind;
import okhttp3.Call;


/**
 * Created by LYS on 2016/9/29.
 */
public class MaxSettingActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener {

    SharedPreferences sp;
    String apiURL;
    String token;
    SeekBar sb_cash, sb_project, sb_product, sb_customer, sb_times;
    TextView tv_cash, tv_mcash, tv_project, tv_mproject, tv_product, tv_mproduct,
            tv_customer, tv_mcustomer, tv_times, tv_mtimes;
    TextView tv_a,tv_b,tv_c,tv_d,tv_certain,back,tv_setting;
    List<TabMax> list;
    float cash,project,product,customer,times;
    String org_id;
    String org_name;
    @Bind(R.id.ll_head)
    LinearLayout ll_head;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_max_setting);
        adapterLitterBar(ll_head);
        CloseAllActivity.getScreenManager().pushActivity(this);
        sp = getSharedPreferences("baseDate", Context.MODE_PRIVATE);
        apiURL = sp.getString("apiURL", null);
        token = sp.getString("token", null);

        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        list=(List<TabMax>) bundle.get("maxList");
        org_name=bundle.getString("org_name");
        org_id=bundle.getString("org_id");


        sb_cash = (SeekBar) findViewById(R.id.sb_cash);
        sb_project = (SeekBar) findViewById(R.id.sb_project);
        sb_product = (SeekBar) findViewById(R.id.sb_product);
        sb_customer = (SeekBar) findViewById(R.id.sb_customer);
        sb_times = (SeekBar) findViewById(R.id.sb_times);
        final SeekBar[] seekBars={sb_cash,sb_project,sb_product,sb_customer,sb_times};

        tv_cash = (TextView) findViewById(R.id.tv_cash);
        tv_mcash = (TextView) findViewById(R.id.tv_mcash);
        tv_project = (TextView) findViewById(R.id.tv_project);
        tv_mproject = (TextView) findViewById(R.id.tv_mproject);
        tv_product = (TextView) findViewById(R.id.tv_product);
        tv_mproduct = (TextView) findViewById(R.id.tv_mproduct);
        tv_customer = (TextView) findViewById(R.id.tv_customer);
        tv_mcustomer = (TextView) findViewById(R.id.tv_mcustomer);
        tv_times = (TextView) findViewById(R.id.tv_times);
        tv_mtimes = (TextView) findViewById(R.id.tv_mtimes);
        tv_certain = (TextView) findViewById(R.id.tv_certain);
//        tv_cash_over= (TextView) findViewById(R.id.tv_cash_over);
//        tv_project_over= (TextView) findViewById(tv_project_over);
//        tv_product_over= (TextView) findViewById(tv_product_over);
        tv_setting= (TextView) findViewById(R.id.tv_setting);
        back= (TextView) findViewById(R.id.back);
        if (!TextUtils.isEmpty(org_name)){
            tv_setting.setText("设置"+" ("+org_name+")");
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView[] textViewLeft={tv_cash,tv_project,tv_product,tv_customer,tv_times};
        final TextView[] textViewRight={tv_mcash,tv_mproject,tv_mproduct,tv_mcustomer,tv_mtimes};

//        for (int i=0;i<3;i++){
//            seekBars[i].setEnabled(false);
//        }
        //颜色变灰
//        tv_cash_over.setVisibility(View.VISIBLE);
//        sb_cash.setEnabled(false);
        sb_cash.setEnabled(false);
        sb_project.setEnabled(false);
        sb_product.setEnabled(false);
//        tv_project_over.setVisibility(View.VISIBLE);
//        tv_product_over.setVisibility(View.VISIBLE);

        tv_a = (TextView) findViewById(R.id.tv_a);
        setDangWei(tv_a,0,seekBars, textViewRight);
        tv_b = (TextView) findViewById(R.id.tv_b);
        setDangWei(tv_b,1,seekBars, textViewRight);
        tv_c = (TextView) findViewById(R.id.tv_c);
        setDangWei(tv_c,2,seekBars, textViewRight);
        tv_d = (TextView) findViewById(R.id.tv_d);
        tv_d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seekBars[0].setEnabled(true);
                seekBars[1].setEnabled(true);
                seekBars[2].setEnabled(true);
                //颜色恢复
//                tv_cash_over.setVisibility(View.INVISIBLE);
//                tv_project_over.setVisibility(View.INVISIBLE);
//                tv_product_over.setVisibility(View.INVISIBLE);
            }
        });

        //设置SeekBar的初始值
        for (int i=0;i<list.size();i++){
            TabMax tabMax=list.get(i);
            if (i==3){
                seekBars[i].setMax(tabMax.getMax()*2);
            }else{
                seekBars[i].setMax(tabMax.getMax());
            }

//            seekBars[i].setProgress((int)list.get(i).getValue());
            if (i<3){
//                seekBars[i].setEnabled(false);
            }
            if (i==3){
                seekBars[i].setProgress((int)list.get(i).getValue()*2);
                textViewRight[i].setText(tabMax.getValue()+tabMax.getUnit());
            }else{
                seekBars[i].setProgress((int)list.get(i).getValue());
                textViewRight[i].setText(tabMax.getValue()+tabMax.getUnit());
            }
            textViewLeft[i].setText(tabMax.getName());
        }
        //给提交结果附上初始值
        cash=list.get(0).getValue();
        project=list.get(1).getValue();
        product=list.get(2).getValue();
        customer=list.get(3).getValue();
        times=list.get(4).getValue();

        sb_cash.setOnSeekBarChangeListener(this);
        sb_project.setOnSeekBarChangeListener(this);
        sb_product.setOnSeekBarChangeListener(this);
        sb_customer.setOnSeekBarChangeListener(this);
        sb_times.setOnSeekBarChangeListener(this);

    }
    //设置A,B,C三个档位
    private void setDangWei(TextView tv,final int what,final SeekBar[] seekBars, final TextView[] textViewRight) {
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //当点击三个档位中的任何一个时，前三项设为不可滑动状态&&颜色变灰
                for (int i=0;i<3;i++){
                    seekBars[i].setEnabled(false);
                }
                //颜色变灰
//                tv_cash_over.setVisibility(View.VISIBLE);
//                tv_project_over.setVisibility(View.VISIBLE);
//                tv_product_over.setVisibility(View.VISIBLE);
                //把档位的值保存在服务器
                    cash=list.get(0).getDefaults()[what];
                    project=list.get(1).getDefaults()[what];
                    product=list.get(2).getDefaults()[what];
                    customer=list.get(3).getDefaults()[what];
                    times=list.get(4).getDefaults()[what];
                for (int i=0;i<3;i++){
                    TabMax tm=list.get(i);
                    seekBars[i].setProgress((int)tm.getDefaults()[what]);
                    textViewRight[i].setText(tm.getDefaults()[what]+tm.getUnit());
                }
            }
        });
    }

    //提交最值
    public void submit(View view){
        final ProgressDialog dialog=new ProgressDialog(this);
        dialog.setMessage("设置中...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(true);
        dialog.setIndeterminate(false);
        dialog.show();
        OkHttpUtils
                .post()
                .url(apiURL+"/rest/employee/setdailyreportsetting")
                .addParams("token",token)
                .addParams("org_id",org_id)
                .addParams("cash_amount",cash+"")
                .addParams("project_amount",project+"")
                .addParams("product_amount",product+"")
                .addParams("room_turnover",customer+"")
                .addParams("employee_hours",times+"")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        JsonObject jo=new JsonParser().parse(response).getAsJsonObject();
                        String status=jo.get("status").getAsString();
                        String message=jo.get("message").getAsString();
                        App.showToast(getApplicationContext(),message);
                        setResult(16);
                        finish();
                        dialog.dismiss();
                    }
                });
    }
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch(seekBar.getId()){
            case R.id.sb_cash:
                cash=seekBar.getProgress();
                if (cash<1)
                    cash=1;
                tv_mcash.setText(cash+list.get(0).getUnit());
                break;
            case R.id.sb_project:
                project=seekBar.getProgress();
                if (project<1)
                    project=1;
                tv_mproject.setText(project+list.get(1).getUnit());
                break;
            case R.id.sb_product:
                product=seekBar.getProgress();
                if (product<1)
                    product=1;
                tv_mproduct.setText(product+list.get(2).getUnit());
                break;
            case R.id.sb_customer:
                DecimalFormat df=new DecimalFormat("0.0");
                customer =Float.parseFloat(df.format((float)seekBar.getProgress()/2));
                if (customer<1)
                    customer=1;
                tv_mcustomer.setText(customer+list.get(3).getUnit());
                break;
            case R.id.sb_times:
                times=seekBar.getProgress();
                if (times<1)
                    times=1;
                tv_mtimes.setText(times+list.get(4).getUnit());
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        CloseAllActivity.getScreenManager().popActivity(this);
    }
}
