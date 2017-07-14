package com.clwater.wifihelper.Ui;


import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.clwater.wifihelper.Adapter.DividerItemDecoration;
import com.clwater.wifihelper.Adapter.NormalRecyclerViewAdapter;
import com.clwater.wifihelper.EventBus.EventBus_showinfo;
import com.clwater.wifihelper.EventBus.EventBus_statu;
import com.clwater.wifihelper.R;

import com.clwater.wifihelper.Utils.WIFI;
import com.clwater.wifihelper.Utils.WifiSearcher;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.recycleview_mian_list) RecyclerView recycleview_mian_list;
    @BindView(R.id.button_main_scan) Button button_main_scan;

    private List<WIFI> list = new ArrayList<WIFI>();
    private NormalRecyclerViewAdapter nrAdapter ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        init();

        scan();
    }

    private void init() {
        recycleview_mian_list.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview
        nrAdapter = new NormalRecyclerViewAdapter(this , list);
        recycleview_mian_list.setAdapter(nrAdapter);
        recycleview_mian_list.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
    }

    @OnClick(R.id.button_main_scan)
    public void button_main_scan_onclick(){
        list.clear();
        nrAdapter.notifyDataSetChanged();
        scan();
//        getAsynHttp();
    }

    private void scan() {
        WifiSearcher wifiSearcher = new WifiSearcher(this , new WifiSearcher.SearchWifiListener(){
            @Override
            public void onSearchWifiFailed(WifiSearcher.ErrorType errorType) {
                Toast.makeText(MainActivity.this , "error" , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSearchWifiSuccess(List<WIFI> results) {
                Log.d("gzb" , "scan");
                for (WIFI wifi : results){
//                    Log.d("gzb" , wifi.getSsid() + wifi.getBssid() + wifi.getPwd());
                    WIFI _wifi = new WIFI();
                    _wifi.setBssid(wifi.getBssid());
                    _wifi.setSsid(wifi.getSsid());
                    _wifi.setStatu("未查询");
                    list.add(_wifi);
                }
                nrAdapter.notifyDataSetChanged();
                getWfisattu();
            }

        });
        wifiSearcher.search();
    }


    private  void getWfisattu(){
        for (int i = 0 ; i < list.size() ; i++){
//        for (int i = 0 ; i < 3 ; i++){
            WIFI wifi = list.get(i);
//            Log.d("gzb" , wifi.getSsid() + wifi.getBssid() + wifi.getPwd());
            getInfoFromWeb(wifi , i);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void changeStatu(EventBus_statu e){
        list.get(e.getIndex()).setStatu(e.getStatu());
        nrAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showInfo(EventBus_showinfo e){
        final WIFI wifi = list.get(e.getIndex());

        if (wifi.getStatu().equals("查询失败")){
            MaterialDialog.Builder builder = new MaterialDialog.Builder(this)
                    .title(wifi.getSsid())
                    .positiveText("查询失败");

            MaterialDialog dialog = builder.build();
            dialog.show();
        }else {


            MaterialDialog.Builder builder =  new MaterialDialog.Builder(this)
                    .title(wifi.getSsid())
                    .items(wifi.getPwd())
                    .content("可能有多个密码 点击对应的密码进行复制")
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            Log.d("gzb" , "which:" + which);
                            ClipboardManager cmb = (ClipboardManager)MainActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                            cmb.setText(wifi.getPwd().get(which));
                            Toast.makeText(MainActivity.this , "密码复制成功" , Toast.LENGTH_SHORT).show();
                        }
                    });




            MaterialDialog dialog = builder.build();
            dialog.show();
        }





    }

    private void getInfoFromWeb(final WIFI wifi , final int index) {
        OkHttpClient mOkHttpClient=new OkHttpClient();
        String token = String.valueOf(Math.random()*1000);
        String url = String.format("http://api.wifi4.cn/Wifi.Info?token=%s&ssid=%s&bssid=%s" , token , wifi.getSsid() , wifi.getBssid());
        Request.Builder requestBuilder = new Request.Builder().url(url);
        requestBuilder.method("GET",null);
        Request request = requestBuilder.build();
        Call mcall= mOkHttpClient.newCall(request);
        mcall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String _response = response.body().string();
                Log.i("gzb", wifi.getSsid() + "  " + _response);
                List<String> pwd = new ArrayList<String>();

                String statu ;
                pwd = WifiInfoAnaylis(_response);
                if (pwd.size() < 1){
                    statu = "查询失败";

                }else {
                    statu = "查询成功";
                    list.get(index).setPwd(pwd);
                }


                EventBus_statu e = new EventBus_statu();
                e.setStatu(statu);
                e.setIndex(index);
                EventBus.getDefault().post(e);

            }
        });


    }

    private List<String> WifiInfoAnaylis(String response) {
        List<String> list = new ArrayList<String>();
        try {
            JSONObject _jsobject = (JSONObject) new JSONTokener(response).nextValue();
            String status = _jsobject.getString("status");
//            Log.d("gzb" , status);
            if (status.equals("error")){
                return list;
            }

            JSONObject _data = _jsobject.getJSONObject("data");
            JSONArray pwds = _data.getJSONArray("pwds");

            for (int i = 0 ; i < pwds.length() ; i++){
                list.add(pwds.get(i).toString());
            }


        } catch (JSONException e) {
            return list;

        }

        return list;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
