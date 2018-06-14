package com.kas.clientservice.haiyansmartenforce.tcsf.aty;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ReplacementTransformationMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.bigkoo.pickerview.TimePickerView;
import com.kas.clientservice.haiyansmartenforce.R;
import com.kas.clientservice.haiyansmartenforce.User.UserInfo;
import com.kas.clientservice.haiyansmartenforce.tcsf.adapter.ExitListAdapter;
import com.kas.clientservice.haiyansmartenforce.tcsf.base.BaseActivity;
import com.kas.clientservice.haiyansmartenforce.tcsf.base.HTTP_HOST;
import com.kas.clientservice.haiyansmartenforce.tcsf.base.NetResultBean;
import com.kas.clientservice.haiyansmartenforce.tcsf.base.TitleActivity;
import com.kas.clientservice.haiyansmartenforce.tcsf.bean.TcListBeanResult;
import com.kas.clientservice.haiyansmartenforce.tcsf.intf.BeanCallBack;
import com.kas.clientservice.haiyansmartenforce.tcsf.util.DateUtil;
import com.kas.clientservice.haiyansmartenforce.tcsf.util.TimePickerUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import org.feezu.liuli.timeselector.Utils.TextUtil;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询页面
 */
public class QueryActivity extends TitleActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    private Spinner sp_province, sp_ABC;
    private EditText et_cp_num;
    private TextView tev_query;
    private RecyclerView rv;

    private TextView tev_pwbh;

    String[] arr_province;
    String[] arr_abc;
    String province = "浙";
    String A2Z = "F";
    String[] arr;
    private ExitListAdapter adapter;
    private ArrayList<TcListBeanResult> list = new ArrayList<TcListBeanResult>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.sp_province:
                province = arr_province[position];
                break;
            case R.id.sp_ABC:
                A2Z = arr_abc[position];
                break;
        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tev_query:
                doQueryList();
                break;
            case R.id.tev_pwbh:
                List<UserInfo.TollCollectorBean.RoadBean> roadBeanList = getRoadBeanList();
                if (roadBeanList == null || roadBeanList.size() == 0) {
                    show("获取车位列表为空");
                } else {
                    arr = new String[roadBeanList.size()];
                    for (int i = 0; i < roadBeanList.size(); i++) {
                        arr[i] = roadBeanList.get(i).Berthname;
                    }

                    new AlertView(null, null, null, null, arr, aty, null, new OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int position) {
                            tev_pwbh.setText(arr[position]);
                        }
                    }).show();


                }


                break;


        }
    }


    private void doQueryList() {
        String cp = et_cp_num.getText().toString().trim();
        String pwbh = tev_pwbh.getText().toString();
        String carNumber = province + A2Z + cp;
        OkHttpUtils.post().url(HTTP_HOST.URL_PARK_LIST)
                .addParams("Opername", getOpername())
                .addParams("type", "2")
                .addParams("Berthname", pwbh)
                .addParams("carnum", carNumber)
                .build().execute(new BeanCallBack(aty, null) {
            @Override
            public void handleBeanResult(NetResultBean bean) {
                handleListNetResult(bean);
            }
        });
    }

    private void handleListNetResult(NetResultBean bean) {
        list.clear();
        if (bean.State) {
            if (bean.total > 0) {
                list.addAll(bean.getResultBeanList(TcListBeanResult.class));
            } else {
                show("查询结果为空");
            }
        } else {
            warningShow(bean.ErrorMsg);
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void findViewBy() {
        sp_province = (Spinner) findViewById(R.id.sp_province);
        sp_ABC = (Spinner) findViewById(R.id.sp_ABC);
        et_cp_num = (EditText) findViewById(R.id.et_cp_num);
        tev_query = (TextView) findViewById(R.id.tev_query);
        tev_pwbh = (TextView) findViewById(R.id.tev_pwbh);
        rv = (RecyclerView) findViewById(R.id.rv);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(aty);
        rv.setLayoutManager(layoutManager);


    }

    @Override
    protected void setTitleTxt() {
        tv_header_title.setText("历史查询");
    }

    @Override
    protected void initData() {
        tev_pwbh.setOnClickListener(this);
        sp_province.setOnItemSelectedListener(this);
        sp_ABC.setOnItemSelectedListener(this);
        et_cp_num.setTransformationMethod(new UpperCaseTransform());
        tev_query.setOnClickListener(this);
        arr_province = getResources().getStringArray(R.array.provinceName);
        arr_abc = getResources().getStringArray(R.array.A2Z);

        adapter = new ExitListAdapter(list, aty);
        adapter.setOnItemClickListener(new ExitListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int p) {
                startActivity(new Intent(aty, HistoryActivity.class).putExtra("TcListBeanResult", list.get(p)));


            }
        });
        rv.setAdapter(adapter);
    }


    public class UpperCaseTransform extends ReplacementTransformationMethod {
        @Override
        protected char[] getOriginal() {
            char[] aa = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
            return aa;
        }

        @Override
        protected char[] getReplacement() {
            char[] cc = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
            return cc;
        }
    }
}
