package com.clwater.wifihelper.Ui;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.clwater.wifihelper.Adapter.NormalRecyclerViewAdapter;
import com.clwater.wifihelper.R;

import com.clwater.wifihelper.Utils.WIFI;
import com.clwater.wifihelper.Utils.WifiSearcher;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

        init();

//        scan();
    }

    private void init() {
        recycleview_mian_list.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于listview
        nrAdapter = new NormalRecyclerViewAdapter(this , list);
        recycleview_mian_list.setAdapter(nrAdapter);
    }

    @OnClick(R.id.button_main_scan)
    public void button_main_scan_onclick(){
        scan();
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
                    Log.d("gzb" , wifi.getSsid() + wifi.getBssid() + wifi.getPwd());
                    WIFI _wifi = new WIFI();
                    _wifi.setBssid(wifi.getBssid());
                    _wifi.setSsid(wifi.getSsid());
                    list.add(_wifi);
                    Log.d("gzb" , list.size() + "");
                }
                nrAdapter.notifyDataSetChanged();
            }

        });
        wifiSearcher.search();
    }

}
