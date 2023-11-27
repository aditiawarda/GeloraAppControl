package com.gelora.geloraappcontrol.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.gelora.geloraappcontrol.R;
import com.gelora.geloraappcontrol.ResetUserActivity;
import com.gelora.geloraappcontrol.ViewImageActivity;
import com.gelora.geloraappcontrol.model.UserSearch;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterListUser extends RecyclerView.Adapter<AdapterListUser.MyViewHolder> {

    private final UserSearch[] data;
    private final Context mContext;

    public AdapterListUser(UserSearch[] data, ResetUserActivity context) {
        this.data = data;
        this.mContext = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_user,viewGroup,false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {
        final UserSearch userSearch = data[i];

        String status = userSearch.getStatus();

        if (status.equals("1")){
            myViewHolder.controlSwitch.setChecked(true);
        } else {
            myViewHolder.controlSwitch.setChecked(false);
        }

        myViewHolder.controlSwitch.setOnClickListener(v -> {
            if (myViewHolder.controlSwitch.isChecked()) {
                //di aktifkan
                Intent intent = new Intent("aktif");
                intent.putExtra("nik",userSearch.getNIK());
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            } else {
                //di nonaktifkan
                Intent intent = new Intent("reset");
                intent.putExtra("nik",userSearch.getNIK());
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            }
        });

        if(userSearch.getAvatar()!=null){
            Picasso.get().load("https://geloraaksara.co.id/absen-online/upload/avatar/"+userSearch.getAvatar()).networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(myViewHolder.profileImage);
        }

        myViewHolder.namaTV.setText(userSearch.getNama());
        myViewHolder.nikTV.setText(userSearch.getNIK());
        myViewHolder.jabatanTV.setText(userSearch.getJabatan());
        myViewHolder.bagianTV.setText(userSearch.getBagian());
        myViewHolder.departemenTV.setText(userSearch.getDepartemen());
        myViewHolder.deviceUse.setText(userSearch.getDevice());

        myViewHolder.profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, ViewImageActivity.class);
            intent.putExtra("url","https://geloraaksara.co.id/absen-online/upload/avatar/"+userSearch.getAvatar());
            mContext.startActivity(intent);
        });

        myViewHolder.deviceIdDetail.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, DeviceDetailActivity.class);
            intent.putExtra("nik",userSearch.getNIK());
            intent.putExtra("nama",userSearch.getNama());
            mContext.startActivity(intent);
        });

        if(String.valueOf(userSearch.getNo_hp()).equals("")||userSearch.getNo_hp()==null){
            myViewHolder.chatBTN.setVisibility(View.GONE);
        } else {
            myViewHolder.chatBTN.setOnClickListener(v -> {
                String contact = String.valueOf(userSearch.getNo_hp());
                contact = "62" + contact.substring(1, contact.length());
                Intent webIntent = new Intent(Intent.ACTION_VIEW); webIntent.setData(Uri.parse("https://api.whatsapp.com/send?phone=+"+contact+"&text="));
                mContext.startActivity(webIntent);
            });
        }

    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView namaTV, nikTV, jabatanTV, bagianTV, departemenTV, deviceUse;
        LinearLayout deviceIdDetail, chatBTN;
        CircleImageView profileImage;
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch controlSwitch;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            namaTV = itemView.findViewById(R.id.nama_tv);
            nikTV = itemView.findViewById(R.id.nik_tv);
            jabatanTV = itemView.findViewById(R.id.jabatan_tv);
            bagianTV = itemView.findViewById(R.id.bagian_tv);
            departemenTV = itemView.findViewById(R.id.dept_tv);
            controlSwitch = itemView.findViewById(R.id.control_switch);
            profileImage = itemView.findViewById(R.id.profile_image);
            deviceIdDetail = itemView.findViewById(R.id.device_id_detail);
            deviceUse = itemView.findViewById(R.id.device_use);
            chatBTN = itemView.findViewById(R.id.chat_btn);
        }
    }
}
