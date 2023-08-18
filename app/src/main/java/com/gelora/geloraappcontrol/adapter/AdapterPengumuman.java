package com.gelora.geloraappcontrol.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gelora.geloraappcontrol.DeviceDetailActivity;
import com.gelora.geloraappcontrol.ListPengumumanActivity;
import com.gelora.geloraappcontrol.R;
import com.gelora.geloraappcontrol.ResetUserActivity;
import com.gelora.geloraappcontrol.ViewImageActivity;
import com.gelora.geloraappcontrol.model.DataPengumuman;
import com.gelora.geloraappcontrol.model.UserSearch;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterPengumuman extends RecyclerView.Adapter<AdapterPengumuman.MyViewHolder> {

    private DataPengumuman[] data;
    private Context mContext;

    public AdapterPengumuman(DataPengumuman[] data, ListPengumumanActivity context) {
        this.data = data;
        this.mContext = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_pengumuman,viewGroup,false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {
        final DataPengumuman dataPengumuman = data[i];

        String status = dataPengumuman.getPengumuman_status();

        if (status.equals("1")){
            myViewHolder.controlSwitch.setChecked(true);
        } else {
            myViewHolder.controlSwitch.setChecked(false);
        }

        myViewHolder.controlSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myViewHolder.controlSwitch.isChecked()) {
                    //di aktifkan
                    Intent intent = new Intent("aktifkan");
                    intent.putExtra("id",dataPengumuman.getId());
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                } else {
                    //di nonaktifkan
                    Intent intent = new Intent("non-aktifkan");
                    intent.putExtra("id",dataPengumuman.getId());
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                }
            }
        });

        myViewHolder.titleTV.setText(dataPengumuman.getPengumuman_title());
        myViewHolder.dateTV.setText("Dibuat pada "+dataPengumuman.getPengumuman_date().substring(8,10)+"/"+dataPengumuman.getPengumuman_date().substring(5,7)+"/"+dataPengumuman.getPengumuman_date().substring(0,4));

    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView titleTV, dateTV;
        Switch controlSwitch;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTV = itemView.findViewById(R.id.title_tv);
            dateTV = itemView.findViewById(R.id.date_tv);
            controlSwitch = itemView.findViewById(R.id.control_switch);
        }
    }
}
