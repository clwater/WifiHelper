package com.clwater.wifihelper.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clwater.wifihelper.EventBus.EventBus_showinfo;
import com.clwater.wifihelper.R;
import com.clwater.wifihelper.Utils.WIFI;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by gengzhibo on 17/7/11.
 */

public class NormalRecyclerViewAdapter extends RecyclerView.Adapter<NormalRecyclerViewAdapter.NormalTextViewHolder> {
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private List<WIFI> list  = new ArrayList<WIFI>();

    public NormalRecyclerViewAdapter(Context context , List<WIFI> list) {
//        mTitles = context.getResources().getStringArray(R.array.titles);
        this.list = list;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public NormalTextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NormalTextViewHolder(mLayoutInflater.inflate(R.layout.adapter_normal_text, parent, false));
    }

    @Override
    public void onBindViewHolder(NormalTextViewHolder holder, int position) {
        WIFI wifi = list.get(position);
        holder.text_view_ssid.setText(wifi.getSsid());
        holder.text_view_bssid.setText(wifi.getBssid());
        holder.textview_adapter_statu.setText(wifi.getStatu());
        if (wifi.getStatu().equals("查询失败")){
            holder.textview_adapter_statu.setTextColor(Color.parseColor("#ff0000"));
        }else if (wifi.getStatu().equals("查询成功")){
            holder.textview_adapter_statu.setTextColor(Color.parseColor("#00ff00"));
        }

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class NormalTextViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_view_ssid) TextView text_view_ssid;
        @BindView(R.id.text_view_bssid) TextView text_view_bssid;
        @BindView(R.id.textview_adapter_statu) TextView textview_adapter_statu;

        NormalTextViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("gzb", "onClick--> position = " + getPosition());
                    EventBus_showinfo e = new EventBus_showinfo();
                    e.setIndex(getPosition());
                    EventBus.getDefault().post(e);
                }
            });
        }
    }
}