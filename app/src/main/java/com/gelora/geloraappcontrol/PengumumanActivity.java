package com.gelora.geloraappcontrol;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gelora.geloraappcontrol.kalert.KAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PengumumanActivity extends AppCompatActivity {

    TextView backBTN, pengumumanTitleTV, pengumumanDescTV, pengumumanDatePilih;
    LinearLayout pengumumanDate, submitBTN;
    String dateChoice = "";
    KAlertDialog pDialog;
    private int i = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengumuman);
        backBTN = findViewById(R.id.back_btn);
        pengumumanTitleTV = findViewById(R.id.pengumuman_title_tv);
        pengumumanDescTV = findViewById(R.id.pengumuman_desc_tv);
        pengumumanDatePilih = findViewById(R.id.pengumuman_date_pilih);
        pengumumanDate = findViewById(R.id.pengumuman_date);
        submitBTN = findViewById(R.id.submit_btn);

        backBTN.setOnClickListener(v -> onBackPressed());

        pengumumanDate.setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) PengumumanActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
            View view = PengumumanActivity.this.getCurrentFocus();
            if (view == null) {
                view = new View(PengumumanActivity.this);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            datePicker();
        });

        submitBTN.setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) PengumumanActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
            View view = PengumumanActivity.this.getCurrentFocus();
            if (view == null) {
                view = new View(PengumumanActivity.this);
            }
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            if(pengumumanTitleTV.getText().toString().equals("")){
                if(pengumumanDescTV.getText().toString().equals("")){
                    if(dateChoice.equals("")){
                        // isi tanggal, desc dan title
                        new KAlertDialog(PengumumanActivity.this, KAlertDialog.ERROR_TYPE)
                            .setTitleText("Perhatian")
                            .setContentText("Harap isi semua data!")
                            .setConfirmText("    OK    ")
                            .setConfirmClickListener(AppCompatDialog::dismiss)
                            .show();
                    } else {
                        // isi desc dan title
                        new KAlertDialog(PengumumanActivity.this, KAlertDialog.ERROR_TYPE)
                                .setTitleText("Perhatian")
                                .setContentText("Harap isi judul dan deskripsi pengumuman!")
                                .setConfirmText("    OK    ")
                                .setConfirmClickListener(AppCompatDialog::dismiss)
                                .show();
                    }
                } else {
                    if(dateChoice.equals("")){
                        // isi tanggal dan title
                        new KAlertDialog(PengumumanActivity.this, KAlertDialog.ERROR_TYPE)
                                .setTitleText("Perhatian")
                                .setContentText("Harap isi judul dan tanggal pengumuman!")
                                .setConfirmText("    OK    ")
                                .setConfirmClickListener(AppCompatDialog::dismiss)
                                .show();
                    } else {
                        // isi title
                        new KAlertDialog(PengumumanActivity.this, KAlertDialog.ERROR_TYPE)
                                .setTitleText("Perhatian")
                                .setContentText("Harap isi judul pengumuman!")
                                .setConfirmText("    OK    ")
                                .setConfirmClickListener(AppCompatDialog::dismiss)
                                .show();
                    }
                }
            } else {
                if(pengumumanDescTV.getText().toString().equals("")){
                    if(dateChoice.equals("")){
                        // isi tanggal, desc
                        new KAlertDialog(PengumumanActivity.this, KAlertDialog.ERROR_TYPE)
                                .setTitleText("Perhatian")
                                .setContentText("Harap isi deskripsi dan tanggal pengumuman!")
                                .setConfirmText("    OK    ")
                                .setConfirmClickListener(AppCompatDialog::dismiss)
                                .show();
                    } else {
                        // isi desc
                        new KAlertDialog(PengumumanActivity.this, KAlertDialog.ERROR_TYPE)
                                .setTitleText("Perhatian")
                                .setContentText("Harap isi deskripsi pengumuman!")
                                .setConfirmText("    OK    ")
                                .setConfirmClickListener(AppCompatDialog::dismiss)
                                .show();
                    }
                } else {
                    if(dateChoice.equals("")){
                        // isi tanggal
                        new KAlertDialog(PengumumanActivity.this, KAlertDialog.ERROR_TYPE)
                                .setTitleText("Perhatian")
                                .setContentText("Harap isi tanggal pengumuman!")
                                .setConfirmText("    OK    ")
                                .setConfirmClickListener(AppCompatDialog::dismiss)
                                .show();
                    } else {
                        // lengkap
                        new KAlertDialog(PengumumanActivity.this, KAlertDialog.WARNING_TYPE)
                                .setTitleText("Kirim pengumuman?")
                                .setContentText("Yakin untuk kirim pengumuman sekarang?")
                                .setCancelText("TIDAK")
                                .setConfirmText("   YA   ")
                                .showCancelButton(true)
                                .setCancelClickListener(AppCompatDialog::dismiss)
                                .setConfirmClickListener(sDialog -> {
                                    sDialog.dismiss();

                                    pDialog = new KAlertDialog(PengumumanActivity.this, KAlertDialog.PROGRESS_TYPE)
                                            .setTitleText("Loading");
                                    pDialog.show();
                                    pDialog.setCancelable(false);
                                    new CountDownTimer(1000, 500) {
                                        public void onTick(long millisUntilFinished) {
                                            i++;
                                            switch (i) {
                                                case 0:
                                                    pDialog.getProgressHelper().setBarColor(ContextCompat.getColor
                                                            (PengumumanActivity.this, R.color.colorGradien));
                                                    break;
                                                case 1:
                                                    pDialog.getProgressHelper().setBarColor(ContextCompat.getColor
                                                            (PengumumanActivity.this, R.color.colorGradien2));
                                                    break;
                                                case 2:
                                                case 6:
                                                    pDialog.getProgressHelper().setBarColor(ContextCompat.getColor
                                                            (PengumumanActivity.this, R.color.colorGradien3));
                                                    break;
                                                case 3:
                                                    pDialog.getProgressHelper().setBarColor(ContextCompat.getColor
                                                            (PengumumanActivity.this, R.color.colorGradien4));
                                                    break;
                                                case 4:
                                                    pDialog.getProgressHelper().setBarColor(ContextCompat.getColor
                                                            (PengumumanActivity.this, R.color.colorGradien5));
                                                    break;
                                                case 5:
                                                    pDialog.getProgressHelper().setBarColor(ContextCompat.getColor
                                                            (PengumumanActivity.this, R.color.colorGradien6));
                                                    break;
                                            }
                                        }
                                        public void onFinish() {
                                            i = -1;
                                            postPengumuman();
                                        }
                                    }.start();

                                })
                                .show();
                    }
                }
            }

        });

    }

    @SuppressLint("SimpleDateFormat")
    private void datePicker(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            android.icu.util.Calendar cal = android.icu.util.Calendar.getInstance();
            @SuppressLint({"DefaultLocale", "SetTextI18n"})
            DatePickerDialog dpd = new DatePickerDialog(PengumumanActivity.this, (view1, year, month, dayOfMonth) -> {

                dateChoice = String.format("%d", year)+"-"+String.format("%02d", month + 1)+"-"+String.format("%02d", dayOfMonth);

                String input_date = dateChoice;
                SimpleDateFormat format1=new SimpleDateFormat("yyyy-MM-dd");
                Date dt1= null;
                try {
                    dt1 = format1.parse(input_date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                DateFormat format2 = new SimpleDateFormat("EEE");
                String finalDay = format2.format(dt1);
                String hariName = "";

                if (finalDay.equals("Mon") || finalDay.equals("Sen")) {
                    hariName = "Senin";
                } else if (finalDay.equals("Tue") || finalDay.equals("Sel")) {
                    hariName = "Selasa";
                } else if (finalDay.equals("Wed") || finalDay.equals("Rab")) {
                    hariName = "Rabu";
                } else if (finalDay.equals("Thu") || finalDay.equals("Kam")) {
                    hariName = "Kamis";
                } else if (finalDay.equals("Fri") || finalDay.equals("Jum")) {
                    hariName = "Jumat";
                } else if (finalDay.equals("Sat") || finalDay.equals("Sab")) {
                    hariName = "Sabtu";
                } else if (finalDay.equals("Sun") || finalDay.equals("Min")) {
                    hariName = "Minggu";
                }

                String dayDate = input_date.substring(8,10);
                String yearDate = input_date.substring(0,4);
                String bulanValue = input_date.substring(5,7);
                String bulanName;

                switch (bulanValue) {
                    case "01":
                        bulanName = "Januari";
                        break;
                    case "02":
                        bulanName = "Februari";
                        break;
                    case "03":
                        bulanName = "Maret";
                        break;
                    case "04":
                        bulanName = "April";
                        break;
                    case "05":
                        bulanName = "Mei";
                        break;
                    case "06":
                        bulanName = "Juni";
                        break;
                    case "07":
                        bulanName = "Juli";
                        break;
                    case "08":
                        bulanName = "Agustus";
                        break;
                    case "09":
                        bulanName = "September";
                        break;
                    case "10":
                        bulanName = "Oktober";
                        break;
                    case "11":
                        bulanName = "November";
                        break;
                    case "12":
                        bulanName = "Desember";
                        break;
                    default:
                        bulanName = "Not found!";
                        break;
                }

                pengumumanDatePilih.setText(hariName+", "+ Integer.parseInt(dayDate) +" "+bulanName+" "+yearDate);

            }, cal.get(android.icu.util.Calendar.YEAR), cal.get(android.icu.util.Calendar.MONTH), cal.get(android.icu.util.Calendar.DATE));
            dpd.show();
        }

    }

    private void postPengumuman() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        final String url = "https://hrisgelora.co.id/api/set_pengumuman";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // response
                    JSONObject data;
                    try {
                        Log.d("Success.Response", response);
                        data = new JSONObject(response);
                        String status = data.getString("status");
                        if (status.equals("Success")) {

                            pengumumanTitleTV.setText("");
                            pengumumanDescTV.setText("");
                            pengumumanDatePilih.setText("");
                            dateChoice = "";

                            pengumumanTitleTV.clearFocus();
                            pengumumanDescTV.clearFocus();

                            pDialog.setTitleText("Terkirim")
                                    .setContentText("Pengumuman berhasil terkirim!")
                                    .setConfirmText("    OK    ")
                                    .setConfirmClickListener(sDialog -> {
                                        pDialog.dismiss();
                                        onBackPressed();
                                    })
                                    .changeAlertType(KAlertDialog.SUCCESS_TYPE);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // error
                    Log.d("Error.Response", error.toString());
                    pDialog.setTitleText("Opss")
                        .setContentText("Terjadi kesalahan!")
                        .setConfirmText("    OK    ")
                        .changeAlertType(KAlertDialog.ERROR_TYPE);
                }
        )
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                params.put("pengumuman_title", pengumumanTitleTV.getText().toString());
                params.put("pengumuman_desc", pengumumanDescTV.getText().toString());
                params.put("pengumuman_date", dateChoice);
                return params;
            }
        };

        requestQueue.add(postRequest);

    }

}