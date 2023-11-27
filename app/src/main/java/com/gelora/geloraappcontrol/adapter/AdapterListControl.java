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

import com.gelora.geloraappcontrol.MainActivity;
import com.gelora.geloraappcontrol.R;
import com.gelora.geloraappcontrol.model.Control;

public class AdapterListControl extends RecyclerView.Adapter<AdapterListControl.MyViewHolder> {

    private final Control[] data;
    private final Context mContext;

    public AdapterListControl(Control[] data, MainActivity context) {
        this.data = data;
        this.mContext = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_control,viewGroup,false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {
        final Control control = data[i];
        String status = control.getStatus();

        myViewHolder.controlName.setText(control.getAction());

        if (status.equals("1")){
            myViewHolder.controlSwitch.setChecked(true);
        } else {
            myViewHolder.controlSwitch.setChecked(false);
        }

        myViewHolder.controlSwitch.setOnClickListener(v -> {
            if (myViewHolder.controlSwitch.isChecked()) {
                //di aktifkan
                Intent intent = new Intent("aktifkan");
                intent.putExtra("id_control",control.getId());
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            } else {
                //di nonaktifkan
                Intent intent = new Intent("non-aktifkan");
                intent.putExtra("id_control",control.getId());
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView controlName;
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch controlSwitch;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            controlName = itemView.findViewById(R.id.control_name);
            controlSwitch = itemView.findViewById(R.id.control_switch);
        }
    }
}
