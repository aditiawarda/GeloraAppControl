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
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gelora.geloraappcontrol.adapter.AdapterListControl;
import com.gelora.geloraappcontrol.model.Control;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RecyclerView controlRV;
    private Control[] controls;
    private AdapterListControl adapterListControl;
    SwipeRefreshLayout swipeRefreshLayout;

    LinearLayout countNotification, resetUser, permohonanMasukBTN, pengumunanSetBTN;
    TextView countNotificationTV;
    //TextToSpeech TTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resetUser = findViewById(R.id.reset_user);
        permohonanMasukBTN = findViewById(R.id.permohonan_masuk_btn);
        countNotification = findViewById(R.id.count_notification);
        countNotificationTV = findViewById(R.id.count_notif_tv);
        pengumunanSetBTN = findViewById(R.id.set_pengumunan_btn);

        controlRV = findViewById(R.id.list_contol_rv);
        controlRV.setLayoutManager(new LinearLayoutManager(this));
        controlRV.setNestedScrollingEnabled(false);
        controlRV.setHasFixedSize(true);
        controlRV.setItemAnimator(new DefaultItemAnimator());

        LocalBroadcastManager.getInstance(this).registerReceiver(onControl, new IntentFilter("aktifkan"));
        LocalBroadcastManager.getInstance(this).registerReceiver(offControl, new IntentFilter("non-aktifkan"));

        swipeRefreshLayout = findViewById(R.id.swipe_to_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_green_dark, android.R.color.holo_blue_dark, android.R.color.holo_orange_dark, android.R.color.holo_red_dark);
        swipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
            swipeRefreshLayout.setRefreshing(false);
            getControl();
        }, 800));

        pengumunanSetBTN.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ListPengumumanActivity.class);
            startActivity(intent);
        });

        resetUser.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ResetUserActivity.class);
            startActivity(intent);
        });

        getControl();

    }

    public BroadcastReceiver onControl = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            String idControl = intent.getStringExtra("id_control");
            String statusControl = "1";
            setControl(idControl,statusControl);
        }
    };

    public BroadcastReceiver offControl = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            String idControl = intent.getStringExtra("id_control");
            String statusControl = "0";
            setControl(idControl,statusControl);
        }
    };

    private void getControl() {
        RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());
        final String url = "https://geloraaksara.co.id/absen-online/api/list_control";
        //connectionFailed();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    Log.e("PaRSE JSON", response + "");
                    try {
                        String list_control = response.getString("data");
                        GsonBuilder builder =new GsonBuilder();
                        Gson gson = builder.create();
                        controls = gson.fromJson(list_control, Control[].class);
                        adapterListControl = new AdapterListControl(controls,MainActivity.this);
                        controlRV.setAdapter(adapterListControl);
                        getCountNotification();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace);

        requestQueue.add(request);

        request.setRetryPolicy(new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    private void setControl(String idControl, String statusControl) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        final String url = "https://geloraaksara.co.id/absen-online/api/set_control";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // response
                    Log.d("Success.Response", response.toString());
                    getControl();
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
                params.put("id", idControl);
                params.put("status", statusControl);
                return params;
            }
        };

        postRequest.setRetryPolicy(new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(postRequest);

    }

    private void getCountNotification() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        final String url = "https://geloraaksara.co.id/absen-online/api/get_list_permohonan_izin_hrd";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // response
                    JSONObject data;
                    try {
                        Log.d("Success.Response", response.toString());
                        data = new JSONObject(response);
                        String status = data.getString("status");
                        if (status.equals("Success")){
                            String count = data.getString("count");
                            if (count.equals("0")){
                                countNotification.setVisibility(View.GONE);
                            } else {
                                countNotification.setVisibility(View.VISIBLE);
                                countNotificationTV.setText(count);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
                params.put("request", "request");
                return params;
            }
        };

        requestQueue.add(postRequest);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getControl();
        getCountNotification();
    }

}