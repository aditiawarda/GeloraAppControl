package com.gelora.geloraappcontrol;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gelora.geloraappcontrol.adapter.AdapterDeviceID;
import com.gelora.geloraappcontrol.model.DeviceID;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DeviceDetailActivity extends AppCompatActivity {

    String nikUser, namaUser;
    TextView namaUserTV, nikUserTV, backBTN;

    private RecyclerView deviceRV;
    private DeviceID[] deviceIDS;
    private AdapterDeviceID adapterDeviceID;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_detail);

        nikUser = getIntent().getExtras().getString("nik");
        namaUser = getIntent().getExtras().getString("nama");

        LocalBroadcastManager.getInstance(this).registerReceiver(onDevice, new IntentFilter("aktifkan"));
        LocalBroadcastManager.getInstance(this).registerReceiver(offDevice, new IntentFilter("non-aktifkan"));

        deviceRV = findViewById(R.id.list_device_rv);
        deviceRV.setLayoutManager(new LinearLayoutManager(this));
        deviceRV.setHasFixedSize(true);
        deviceRV.setNestedScrollingEnabled(false);
        deviceRV.setItemAnimator(new DefaultItemAnimator());

        namaUserTV = findViewById(R.id.nama_user);
        nikUserTV = findViewById(R.id.nik_user);
        backBTN = findViewById(R.id.back_btn);

        swipeRefreshLayout = findViewById(R.id.swipe_to_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_green_dark, android.R.color.holo_blue_dark, android.R.color.holo_orange_dark, android.R.color.holo_red_dark);
        swipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
            swipeRefreshLayout.setRefreshing(false);
            getDevice(nikUser);
        }, 800));

        backBTN.setOnClickListener(v -> onBackPressed());

        namaUserTV.setText(namaUser.toUpperCase());
        nikUserTV.setText(nikUser);

        getDevice(nikUser);

    }

    public BroadcastReceiver onDevice = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            String id = intent.getStringExtra("id");
            String status = "1";
            setDevice(id, status);
        }
    };

    public BroadcastReceiver offDevice= new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            String id = intent.getStringExtra("id");
            String status = "0";
            setDevice(id, status);
        }
    };

    private void getDevice(String nik) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        final String url = "https://hrisgelora.co.id/api/get_device_id";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // response
                    try {
                        Log.d("Success.Response", response);

                        JSONObject data = new JSONObject(response);
                        String list = data.getString("data");
                        GsonBuilder builder = new GsonBuilder();
                        Gson gson = builder.create();
                        deviceIDS = gson.fromJson(list, DeviceID[].class);
                        adapterDeviceID = new AdapterDeviceID(deviceIDS, DeviceDetailActivity.this);
                        deviceRV.setAdapter(adapterDeviceID);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // error
                    Log.d("Error.Response", error.toString());
                }
        )
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                params.put("NIK", nik);
                return params;
            }
        };

        requestQueue.add(postRequest);

    }

    private void setDevice(String id, String status) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        final String url = "https://hrisgelora.co.id/api/set_device_id";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // response
                    Log.d("Success.Response", response.toString());
                    getDevice(nikUser);
                },
                error -> {
                    // error
                    Log.d("Error.Response", error.toString());
                }
        )
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                params.put("id", id);
                params.put("status", status);
                return params;
            }
        };

        postRequest.setRetryPolicy(new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(postRequest);

    }

}