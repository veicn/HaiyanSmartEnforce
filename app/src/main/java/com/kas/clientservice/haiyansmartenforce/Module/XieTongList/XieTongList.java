package com.kas.clientservice.haiyansmartenforce.Module.XieTongList;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.kas.clientservice.haiyansmartenforce.Base.BaseActivity;
import com.kas.clientservice.haiyansmartenforce.Http.RequestUrl;
import com.kas.clientservice.haiyansmartenforce.MyApplication;
import com.kas.clientservice.haiyansmartenforce.R;
import com.kas.clientservice.haiyansmartenforce.User.UserSingleton;
import com.kas.clientservice.haiyansmartenforce.Utils.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Response;
import smartenforce.util.ToastUtil;

/**
 * Created by DELL_Zjcoms02 on 2018/6/25.
 */

public class XieTongList extends BaseActivity implements View.OnClickListener{
    @BindView(R.id.tv_header_title)
    TextView tv_title;
    @BindView(R.id.iv_heaer_back)
    ImageView iv_back;
    @BindView(R.id.ltv_ajbl)
    ListView ltv_ajbl;
    XieTongListAdapter adapter;
    Intent intent;
    MyApplication app;
    int pageNum = 1;
    ArrayList<Project_Info.RtnBean> list=new ArrayList<Project_Info.RtnBean>();


    @Override
    protected int getLayoutId() {
        return R.layout.activity_xietong_list;
    }

    @Override
    protected String getTAG() {
        return this.toString();
    }

    @Override
    protected void initResAndListener() {
        super.initResAndListener();
        tv_title.setText("案件办理");
        app =(MyApplication)getApplication();

        iv_back.setOnClickListener(this);
        query();
        initAdapter();

    }
    private void initAdapter(){
        adapter=new XieTongListAdapter(list,mContext);
        ltv_ajbl.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        ltv_ajbl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent=new Intent(mContext,XieTongDetalis.class);
                intent.putExtra("projcode",list.get(position).projcode);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_heaer_back:
                finish();
                break;

        }

    }

    //TODO
    private void query() {
        showLoadingDialog();
        OkHttpUtils.post().url(RequestUrl.URLLIST)
                .addParams("optionName", RequestUrl.haiyanlist)
                .addParams("departcode",  UserSingleton.USERINFO.getDepartcode())
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtils.showToast(mContext,"程序异常");
            }
            @Override
            public void onResponse(String response, int id) {
                    Project_Info info=gson.fromJson(response,Project_Info.class);
                    if(info.isState()){
                        Project_Info.RtnBean[] bean=gson.fromJson(gson.toJson(info.getRtn()), Project_Info.RtnBean[].class);
                        list.clear();
                        Collections.addAll(list,bean);
                        adapter.notifyDataSetChanged();
                    }else {
                        ToastUtils.showToast(mContext,"获取失败");
                    }
                dismissLoadingDialog();
                }


        });
    }

}