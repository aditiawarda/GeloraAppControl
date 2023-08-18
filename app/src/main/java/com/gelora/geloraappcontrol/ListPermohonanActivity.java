package com.gelora.geloraappcontrol;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gelora.geloraappcontrol.adapter.AdapterPermohonanIzin;
import com.gelora.geloraappcontrol.model.ListPermohonanIzin;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ListPermohonanActivity extends AppCompatActivity {

    TextView backBTN;

    private RecyclerView dataNotifikasiRV;
    private ListPermohonanIzin[] listPermohonanIzins;
    private AdapterPermohonanIzin adapterPermohonanIzin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_permohonan);

        backBTN = findViewById(R.id.back_btn);
        dataNotifikasiRV = findViewById(R.id.list_permohonan_rv);

        dataNotifikasiRV.setLayoutManager(new LinearLayoutManager(this));
        dataNotifikasiRV.setHasFixedSize(true);
        dataNotifikasiRV.setItemAnimator(new DefaultItemAnimator());

        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getData();

    }

    private void getData() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        final String url = "https://geloraaksara.co.id/absen-online/api/get_list_permohonan_izin_hrd";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // response
                        JSONObject data = null;
                        try {
                            Log.d("Success.Response", response.toString());
                            data = new JSONObject(response);
                            String status = data.getString("status");
                            if (status.equals("Success")){
                                String count = data.getString("count");
                                String count_data = data.getString("count_data");

                                if (count.equals("0")) {
                                    //countPartIn.setVisibility(View.GONE);
                                    if(count_data.equals("0")){
                                        dataNotifikasiRV.setVisibility(View.GONE);
                                        //noDataPart.setVisibility(View.VISIBLE);
                                        //loadingDataPart.setVisibility(View.GONE);
                                    } else {
                                        //noDataPart.setVisibility(View.GONE);
                                        //loadingDataPart.setVisibility(View.GONE);
                                        dataNotifikasiRV.setVisibility(View.VISIBLE);
                                        String data_permohonan_masuk = data.getString("data");
                                        GsonBuilder builder = new GsonBuilder();
                                        Gson gson = builder.create();
                                        listPermohonanIzins = gson.fromJson(data_permohonan_masuk, ListPermohonanIzin[].class);
                                        adapterPermohonanIzin = new AdapterPermohonanIzin(listPermohonanIzins,ListPermohonanActivity.this);
                                        dataNotifikasiRV.setAdapter(adapterPermohonanIzin);
                                    }
                                } else {
                                    //countPartIn.setVisibility(View.VISIBLE);
                                    //noDataPart.setVisibility(View.GONE);
                                    //loadingDataPart.setVisibility(View.GONE);
                                    dataNotifikasiRV.setVisibility(View.VISIBLE);
                                    String data_permohonan_masuk = data.getString("data");
                                    GsonBuilder builder = new GsonBuilder();
                                    Gson gson = builder.create();
                                    listPermohonanIzins = gson.fromJson(data_permohonan_masuk, ListPermohonanIzin[].class);
                                    adapterPermohonanIzin = new AdapterPermohonanIzin(listPermohonanIzins,ListPermohonanActivity.this);
                                    dataNotifikasiRV.setAdapter(adapterPermohonanIzin);
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        //connectionFailed();
                    }
                }
        )
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("request", "request");
                return params;
            }
        };

        requestQueue.add(postRequest);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

}