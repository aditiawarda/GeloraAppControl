package com.gelora.geloraappcontrol;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gelora.geloraappcontrol.adapter.AdapterListUser;
import com.gelora.geloraappcontrol.model.UserSearch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ResetUserActivity extends AppCompatActivity {

    EditText keywordUserED;
    TextView backBTN;

    private RecyclerView userRV;
    private UserSearch[] userSearches;
    private AdapterListUser adapterListUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_user);

        keywordUserED = findViewById(R.id.keyword_user_ed);
        backBTN = findViewById(R.id.back_btn);

        userRV = findViewById(R.id.list_user_rv);
        userRV.setLayoutManager(new LinearLayoutManager(this));
        userRV.setHasFixedSize(true);
        userRV.setNestedScrollingEnabled(false);
        userRV.setItemAnimator(new DefaultItemAnimator());

        LocalBroadcastManager.getInstance(this).registerReceiver(aktif, new IntentFilter("aktif"));
        LocalBroadcastManager.getInstance(this).registerReceiver(reset, new IntentFilter("reset"));

        showSoftKeyboard(keywordUserED);

        backBTN.setOnClickListener(v -> onBackPressed());

        keywordUserED.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                String keyword = keywordUserED.getText().toString();
                getUser(keyword);
            }

        });

        keywordUserED.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
            return false;
        });

    }

    private void getUser(String keyword) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        final String url = "https://geloraaksara.co.id/absen-online/api/cari_user";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // response
                    try {
                        Log.d("Success.Response", response);
                        JSONObject data = new JSONObject(response);
                        String list = data.getString("data");
                        GsonBuilder builder = new GsonBuilder();
                        Gson gson = builder.create();
                        userSearches = gson.fromJson(list, UserSearch[].class);
                        adapterListUser = new AdapterListUser(userSearches, ResetUserActivity.this);
                        userRV.setAdapter(adapterListUser);
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
                params.put("keyword_user", keyword);
                return params;
            }
        };

        requestQueue.add(postRequest);

    }

    public BroadcastReceiver aktif = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            String nik = intent.getStringExtra("nik");
            String statusUser = "1";
            setUser(nik,statusUser);
        }
    };

    public BroadcastReceiver reset = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            String nik = intent.getStringExtra("nik");
            String statusUser = "0";
            setUser(nik,statusUser);
        }
    };


    private void setUser(String nik, String statusUser) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        final String url = "https://geloraaksara.co.id/absen-online/api/set_user";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // response
                    Log.d("Success.Response", response.toString());
                    getUser(keywordUserED.getText().toString());
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
                params.put("nik", nik);
                params.put("status", statusUser);
                return params;
            }
        };

        postRequest.setRetryPolicy(new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(postRequest);

    }

    public void showSoftKeyboard(View view){
        if(view.requestFocus()){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

}