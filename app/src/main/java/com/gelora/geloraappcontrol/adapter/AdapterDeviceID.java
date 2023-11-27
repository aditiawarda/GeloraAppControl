package com.gelora.geloraappcontrol.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gelora.geloraappcontrol.DeviceDetailActivity;
import com.gelora.geloraappcontrol.MainActivity;
import com.gelora.geloraappcontrol.R;
import com.gelora.geloraappcontrol.model.Control;
import com.gelora.geloraappcontrol.model.DeviceID;

public class AdapterDeviceID extends RecyclerView.Adapter<AdapterDeviceID.MyViewHolder> {

    private final DeviceID[] data;
    private final Context mContext;

    public AdapterDeviceID(DeviceID[] data, DeviceDetailActivity context) {
        this.data = data;
        this.mContext = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_device_id,viewGroup,false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {
        final DeviceID deviceID = data[i];
        String status = deviceID.getStatus_aktif();

        myViewHolder.deviceIDKey.setText(deviceID.getDevice_id());
        myViewHolder.createdDate.setText(deviceID.getCreated_at());

        if (status.equals("1")){
            myViewHolder.controlSwitch.setChecked(true);
        } else {
            myViewHolder.controlSwitch.setChecked(false);
        }

        myViewHolder.controlSwitch.setOnClickListener(v -> {
            if (myViewHolder.controlSwitch.isChecked()) {
                //di aktifkan
                Intent intent = new Intent("aktifkan");
                intent.putExtra("id",deviceID.getId());
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            } else {
                //di nonaktifkan
                Intent intent = new Intent("non-aktifkan");
                intent.putExtra("id",deviceID.getId());
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView deviceIDKey, createdDate;
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch controlSwitch;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceIDKey = itemView.findViewById(R.id.device_id_key);
            controlSwitch = itemView.findViewById(R.id.control_switch);
            createdDate = itemView.findViewById(R.id.timestamp_created);
        }
    }
}
