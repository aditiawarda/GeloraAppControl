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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gelora.geloraappcontrol.adapter.AdapterPengumuman;
import com.gelora.geloraappcontrol.model.DataPengumuman;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ListPengumumanActivity extends AppCompatActivity {

    LinearLayout newPengumumanBtn;
    TextView backBTN;

    private RecyclerView pengumumanRV;
    private DataPengumuman[] dataPengumuman;
    private AdapterPengumuman adapterPengumuman;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_pengumuman);

        LocalBroadcastManager.getInstance(this).registerReceiver(onFunc, new IntentFilter("aktifkan"));
        LocalBroadcastManager.getInstance(this).registerReceiver(offFunc, new IntentFilter("non-aktifkan"));

        newPengumumanBtn = findViewById(R.id.new_pengumuman_btn);
        pengumumanRV = findViewById(R.id.list_pengumuman_rv);
        backBTN = findViewById(R.id.back_btn);
        pengumumanRV.setLayoutManager(new LinearLayoutManager(this));
        pengumumanRV.setHasFixedSize(true);
        pengumumanRV.setNestedScrollingEnabled(false);
        pengumumanRV.setItemAnimator(new DefaultItemAnimator());

        swipeRefreshLayout = findViewById(R.id.swipe_to_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_green_dark, android.R.color.holo_blue_dark, android.R.color.holo_orange_dark, android.R.color.holo_red_dark);
        swipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
            swipeRefreshLayout.setRefreshing(false);
            getPengumuman();
        }, 800));

        backBTN.setOnClickListener(v -> onBackPressed());

        newPengumumanBtn.setOnClickListener(view -> {
            Intent intent = new Intent(ListPengumumanActivity.this, PengumumanActivity.class);
            startActivity(intent);
        });

        getPengumuman();

    }

    public BroadcastReceiver onFunc = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            String id = intent.getStringExtra("id");
            String status = "1";
            setPengumuman(id, status);
        }
    };

    public BroadcastReceiver offFunc= new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            String id = intent.getStringExtra("id");
            String status = "0";
            setPengumuman(id, status);
        }
    };

    private void setPengumuman(String id, String status) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        final String url = "https://hrisgelora.co.id/api/set_pengumuman_update";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // response
                    Log.d("Success.Response", response);
                    getPengumuman();
                },
                error -> {
                    // error
                    Log.d("Error.Response", error.toString());
                    //connectionFailed();
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

    private void getPengumuman() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        final String url = "https://hrisgelora.co.id/api/list_data_pengumuman";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // response
                    Log.d("Success.Response", response);
                    JSONObject data;
                    try {
                        data = new JSONObject(response);
                        String status = data.getString("status");

                        if (status.equals("Success")){
                            String list = data.getString("data");
                            GsonBuilder builder =new GsonBuilder();
                            Gson gson = builder.create();
                            dataPengumuman = gson.fromJson(list, DataPengumuman[].class);
                            adapterPengumuman = new AdapterPengumuman(dataPengumuman, ListPengumumanActivity.this);
                            pengumumanRV.setAdapter(adapterPengumuman);
                        }
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
                params.put("request", "request");
                return params;
            }
        };

        postRequest.setRetryPolicy(new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(postRequest);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getPengumuman();
    }

}