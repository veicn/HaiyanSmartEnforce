package smartenforce.frament;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.kas.clientservice.haiyansmartenforce.R;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.List;

import smartenforce.adapter.ItemAdapter;
import smartenforce.aty.function4.EnterpriseDetailActivity;
import smartenforce.base.BaseFragment;
import smartenforce.base.HttpApi;
import smartenforce.base.NetResultBean;
import smartenforce.bean.EnterpriseDetailBean;
import smartenforce.impl.BeanCallBack;
import smartenforce.impl.NoFastClickLisener;
import smartenforce.intf.ItemListener;
import smartenforce.widget.MyXRecyclerView;

public class EnterPriseFragment extends BaseFragment implements XRecyclerView.LoadingListener {
    private int PAGE_NUM = 1;
    private View view;
    private EditText edt_name, edt_fzr, edt_jgdm, edt_dz;
    private Button btn_query, btn_add;
    private MyXRecyclerView xrcv_enterrise;
    private ItemAdapter adapter;
    private List<Object> beanList=new ArrayList<Object>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_item_entertrise, container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        edt_name = (EditText) view.findViewById(R.id.edt_name);
        edt_fzr = (EditText) view.findViewById(R.id.edt_fzr);
        edt_jgdm = (EditText) view.findViewById(R.id.edt_jgdm);
        edt_dz = (EditText) view.findViewById(R.id.edt_dz);
        btn_query = (Button) view.findViewById(R.id.btn_query);
        btn_add = (Button) view.findViewById(R.id.btn_add);
        xrcv_enterrise = (MyXRecyclerView) view.findViewById(R.id.xrcv_enterrise);
        xrcv_enterrise.setLoadingListener(this);
        adapter=new ItemAdapter(beanList,context);
        adapter.setListener(new ItemListener() {
            @Override
            public void onItemClick(int P) {
                EnterpriseDetailBean bean= (EnterpriseDetailBean) beanList.get(P-1);
                Intent it=new Intent(aty,EnterpriseDetailActivity.class);
                it.putExtra("EnterpriseDetailBean",bean);
                startActivity(it);
            }
        });
        xrcv_enterrise.setAdapter(adapter);
        btn_query.setOnClickListener(noFastClickLisener);
        btn_add.setOnClickListener(noFastClickLisener);
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
    }

    NoFastClickLisener noFastClickLisener = new NoFastClickLisener() {
        @Override
        public void onNofastClickListener(View v) {
            closeKeybord();
            switch (v.getId()) {
                case R.id.btn_query:
                    PAGE_NUM = 1;
                    doQueryEntertriseList();
                    break;
                case R.id.btn_add:
                    startActivity(new Intent(getContext(), EnterpriseDetailActivity.class));
                    break;


            }
        }
    };

    // 查询企业列表
    private void doQueryEntertriseList() {
        String EntName=getText(edt_name);
        String Legal=getText(edt_fzr);
        String Org=getText(edt_jgdm);
        String Address=getText(edt_dz);
        OkHttpUtils.post().url(HttpApi.URL_ENTERPRISELIST)
                .addParams("EntName", EntName)
                .addParams("Legal", Legal)
                .addParams("Count", "20")
                .addParams("Page", PAGE_NUM+"")
                .addParams("Org", Org+"")
                .addParams("Address", Address+"")
                .build().execute(new BeanCallBack(aty, "查询中") {
            @Override
            public void handleBeanResult(NetResultBean bean) {
                xrcv_enterrise.onComplete();
                if (bean.State ) {
                     List<EnterpriseDetailBean> list = bean.getResultBeanList(EnterpriseDetailBean.class);
                     if (PAGE_NUM==1){
                         beanList.clear();
                     }
                     if (bean.total>0){
                         beanList.addAll(list);
                     }else{
                         if (PAGE_NUM==1){
                             show("查询到数据为空");
                         }else{
                             show("已无更多数据");
                         }

                     }
                     adapter.notifyDataSetChanged();
                } else {
                   warningShow(bean.ErrorMsg);
                }

            }

        });

    }






    @Override
    public void onRefresh() {
        PAGE_NUM = 1;
        doQueryEntertriseList();
    }

    @Override
    public void onLoadMore() {
        PAGE_NUM++;
        doQueryEntertriseList();
    }
}
