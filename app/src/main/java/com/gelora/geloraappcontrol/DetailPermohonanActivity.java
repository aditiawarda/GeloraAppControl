package com.gelora.geloraappcontrol;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.shasin.notificationbanner.Banner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gelora.geloraappcontrol.kalert.KAlertDialog;
import com.google.zxing.WriterException;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class DetailPermohonanActivity extends AppCompatActivity {

    TextView backBTN, appoveStatusHRD, idPermohonanTV, namaKaryawanTV, nikKaryawanTV, bagianKaryawanTV, jabatanKaryawanTV, alasanIzinTV, tglMulaiTV, tglAkhirTV, totalHariTV, tglPermohonanTV, pemohonTV, supervisorTV, hrdTV;
    String uriImage, uriImage2, idIzinRecord, statusKondisi = "", kode;
    LinearLayout viewSuratSakitBTN, downloadBTN, suratIzinPart, rejectedMark, acceptedMark, homeBTN, approvedBTN, rejectedBTN, actionPart;
    SwipeRefreshLayout refreshLayout;
    ImageView ttdPemohon, ttdSupervisor, qrDocument;
    KAlertDialog pDialog;
    Bitmap bitmap;
    View rootview;
    private int i = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_permohonan);

        backBTN = findViewById(R.id.back_btn);

        rootview = findViewById(android.R.id.content);
        refreshLayout = findViewById(R.id.swipe_to_refresh_layout);
        approvedBTN = findViewById(R.id.appoved_btn);
        rejectedBTN = findViewById(R.id.rejected_btn);
        namaKaryawanTV = findViewById(R.id.nama_karyawan_tv);
        nikKaryawanTV = findViewById(R.id.nik_karyawan_tv);
        bagianKaryawanTV = findViewById(R.id.bagian_karyawan_tv);
        jabatanKaryawanTV = findViewById(R.id.jabatan_karyawan_tv);
        alasanIzinTV = findViewById(R.id.alasan_izin_tv);
        tglMulaiTV = findViewById(R.id.tgl_mulai_tv);
        tglAkhirTV = findViewById(R.id.tgl_akhir_tv);
        totalHariTV = findViewById(R.id.jumlah_hari_tv);
        tglPermohonanTV = findViewById(R.id.tgl_permohonan_tv);
        pemohonTV = findViewById(R.id.pemohon_tv);
        supervisorTV = findViewById(R.id.supervisor_tv);
        hrdTV = findViewById(R.id.hrd_tv);
        ttdPemohon = findViewById(R.id.ttd_pemohon);
        ttdSupervisor = findViewById(R.id.ttd_supervisor);
        actionPart = findViewById(R.id.action_part);
        rejectedMark = findViewById(R.id.rejected_mark);
        acceptedMark = findViewById(R.id.accepted_mark);
        suratIzinPart = findViewById(R.id.surat_izin_part);
        downloadBTN = findViewById(R.id.download_btn);
        qrDocument = findViewById(R.id.qr_document);
        appoveStatusHRD = findViewById(R.id.appove_status_hrd);
        viewSuratSakitBTN = findViewById(R.id.view_surat_sakit_btn);

        idIzinRecord = getIntent().getExtras().getString("id_izin");

        refreshLayout.setColorSchemeResources(android.R.color.holo_green_dark, android.R.color.holo_blue_dark, android.R.color.holo_orange_dark, android.R.color.holo_red_dark);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        getDataDetailPermohonan();
                    }
                }, 800);
            }
        });

        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        downloadBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new KAlertDialog(DetailPermohonanActivity.this, KAlertDialog.WARNING_TYPE)
                        .setTitleText("Perhatian")
                        .setContentText("Unduh File Permohonan?")
                        .setCancelText("TIDAK")
                        .setConfirmText("   YA   ")
                        .showCancelButton(true)
                        .setCancelClickListener(new KAlertDialog.KAlertClickListener() {
                            @Override
                            public void onClick(KAlertDialog sDialog) {
                                sDialog.dismiss();
                            }
                        })
                        .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                            @Override
                            public void onClick(KAlertDialog sDialog) {
                                sDialog.dismiss();

                                File file = saveBitMap(DetailPermohonanActivity.this, suratIzinPart);
                                if (file != null) {
                                    final KAlertDialog pDialog = new KAlertDialog(DetailPermohonanActivity.this, KAlertDialog.PROGRESS_TYPE)
                                            .setTitleText("Loading");
                                    pDialog.show();
                                    pDialog.setCancelable(false);
                                    new CountDownTimer(2000, 1000) {
                                        public void onTick(long millisUntilFinished) {
                                            i++;
                                            switch (i) {
                                                case 0:
                                                    pDialog.getProgressHelper().setBarColor(ContextCompat.getColor
                                                            (DetailPermohonanActivity.this, R.color.colorGradien));
                                                    break;
                                                case 1:
                                                    pDialog.getProgressHelper().setBarColor(ContextCompat.getColor
                                                            (DetailPermohonanActivity.this, R.color.colorGradien2));
                                                    break;
                                                case 2:
                                                case 6:
                                                    pDialog.getProgressHelper().setBarColor(ContextCompat.getColor
                                                            (DetailPermohonanActivity.this, R.color.colorGradien3));
                                                    break;
                                                case 3:
                                                    pDialog.getProgressHelper().setBarColor(ContextCompat.getColor
                                                            (DetailPermohonanActivity.this, R.color.colorGradien4));
                                                    break;
                                                case 4:
                                                    pDialog.getProgressHelper().setBarColor(ContextCompat.getColor
                                                            (DetailPermohonanActivity.this, R.color.colorGradien5));
                                                    break;
                                                case 5:
                                                    pDialog.getProgressHelper().setBarColor(ContextCompat.getColor
                                                            (DetailPermohonanActivity.this, R.color.colorGradien6));
                                                    break;
                                            }
                                        }
                                        public void onFinish() {
                                            i = -1;
                                            pDialog.setTitleText("Unduh Berhasil")
                                                    .setContentText("File permohonan berhasil diunduh")
                                                    .setCancelText("TUTUP")
                                                    .setConfirmText("LIHAT")
                                                    .showCancelButton(true)
                                                    .setCancelClickListener(new KAlertDialog.KAlertClickListener() {
                                                        @Override
                                                        public void onClick(KAlertDialog sDialog) {
                                                            sDialog.dismiss();
                                                        }
                                                    })
                                                    .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                                                        @Override
                                                        public void onClick(KAlertDialog sDialog) {
                                                            sDialog.dismiss();
                                                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriImage2));
                                                            startActivity(intent);
                                                        }
                                                    })
                                                    .changeAlertType(KAlertDialog.SUCCESS_TYPE);
                                        }
                                    }.start();

                                    Log.i("TAG", "Drawing saved to the gallery!");
                                } else {
                                    Log.i("TAG", "Oops! Image could not be saved.");
                                }

                            }
                        })
                        .show();

            }
        });

        approvedBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new KAlertDialog(DetailPermohonanActivity.this, KAlertDialog.WARNING_TYPE)
                        .setTitleText("Setujui?")
                        .setContentText("Yakin untuk disetujui sekarang?")
                        .setCancelText("TIDAK")
                        .setConfirmText("   YA   ")
                        .showCancelButton(true)
                        .setCancelClickListener(new KAlertDialog.KAlertClickListener() {
                            @Override
                            public void onClick(KAlertDialog sDialog) {
                                sDialog.dismiss();
                            }
                        })
                        .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                            @Override
                            public void onClick(KAlertDialog sDialog) {
                                sDialog.dismiss();

                                pDialog = new KAlertDialog(DetailPermohonanActivity.this, KAlertDialog.PROGRESS_TYPE).setTitleText("Loading");
                                pDialog.show();
                                pDialog.setCancelable(false);
                                new CountDownTimer(1000, 500) {
                                    public void onTick(long millisUntilFinished) {
                                        i++;
                                        switch (i) {
                                            case 0:
                                                pDialog.getProgressHelper().setBarColor(ContextCompat.getColor
                                                        (DetailPermohonanActivity.this, R.color.colorGradien));
                                                break;
                                            case 1:
                                                pDialog.getProgressHelper().setBarColor(ContextCompat.getColor
                                                        (DetailPermohonanActivity.this, R.color.colorGradien2));
                                                break;
                                            case 2:
                                            case 6:
                                                pDialog.getProgressHelper().setBarColor(ContextCompat.getColor
                                                        (DetailPermohonanActivity.this, R.color.colorGradien3));
                                                break;
                                            case 3:
                                                pDialog.getProgressHelper().setBarColor(ContextCompat.getColor
                                                        (DetailPermohonanActivity.this, R.color.colorGradien4));
                                                break;
                                            case 4:
                                                pDialog.getProgressHelper().setBarColor(ContextCompat.getColor
                                                        (DetailPermohonanActivity.this, R.color.colorGradien5));
                                                break;
                                            case 5:
                                                pDialog.getProgressHelper().setBarColor(ContextCompat.getColor
                                                        (DetailPermohonanActivity.this, R.color.colorGradien6));
                                                break;
                                        }
                                    }

                                    public void onFinish() {
                                        i = -1;
                                        approvedAction();
                                    }
                                }.start();

                            }
                        })
                        .show();

            }
        });

        rejectedBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new KAlertDialog(DetailPermohonanActivity.this, KAlertDialog.WARNING_TYPE)
                        .setTitleText("Tolak?")
                        .setContentText("Yakin untuk tolak permohonan?")
                        .setCancelText("TIDAK")
                        .setConfirmText("   YA   ")
                        .showCancelButton(true)
                        .setCancelClickListener(new KAlertDialog.KAlertClickListener() {
                            @Override
                            public void onClick(KAlertDialog sDialog) {
                                sDialog.dismiss();
                            }
                        })
                        .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                            @Override
                            public void onClick(KAlertDialog sDialog) {
                                sDialog.dismiss();

                                pDialog = new KAlertDialog(DetailPermohonanActivity.this, KAlertDialog.PROGRESS_TYPE).setTitleText("Loading");
                                pDialog.show();
                                pDialog.setCancelable(false);
                                new CountDownTimer(1000, 500) {
                                    public void onTick(long millisUntilFinished) {
                                        i++;
                                        switch (i) {
                                            case 0:
                                                pDialog.getProgressHelper().setBarColor(ContextCompat.getColor
                                                        (DetailPermohonanActivity.this, R.color.colorGradien));
                                                break;
                                            case 1:
                                                pDialog.getProgressHelper().setBarColor(ContextCompat.getColor
                                                        (DetailPermohonanActivity.this, R.color.colorGradien2));
                                                break;
                                            case 2:
                                            case 6:
                                                pDialog.getProgressHelper().setBarColor(ContextCompat.getColor
                                                        (DetailPermohonanActivity.this, R.color.colorGradien3));
                                                break;
                                            case 3:
                                                pDialog.getProgressHelper().setBarColor(ContextCompat.getColor
                                                        (DetailPermohonanActivity.this, R.color.colorGradien4));
                                                break;
                                            case 4:
                                                pDialog.getProgressHelper().setBarColor(ContextCompat.getColor
                                                        (DetailPermohonanActivity.this, R.color.colorGradien5));
                                                break;
                                            case 5:
                                                pDialog.getProgressHelper().setBarColor(ContextCompat.getColor
                                                        (DetailPermohonanActivity.this, R.color.colorGradien6));
                                                break;
                                        }
                                    }

                                    public void onFinish() {
                                        i = -1;
                                        rejectedAction();
                                    }
                                }.start();

                            }
                        })
                        .show();

            }
        });

        getDataDetailPermohonan();

    }

    private void getDataDetailPermohonan() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        final String url = "https://geloraaksara.co.id/absen-online/api/get_permohonan_izin_detail";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(String response) {
                        // response
                        JSONObject data = null;
                        try {
                            Log.d("Success.Response", response.toString());
                            data = new JSONObject(response);
                            String status = data.getString("status");
                            if (status.equals("Success")) {
                                generateQRCode();
                                JSONObject detail = data.getJSONObject("data");
                                String nik_karyawan = detail.getString("NIK");
                                String nama_karyawan = detail.getString("NmKaryawan");
                                String bagian = detail.getString("NmDept");
                                String jabatan = detail.getString("NmJabatan");
                                String alasan = detail.getString("keterangan");
                                String tgl_mulai = detail.getString("tanggal_mulai");
                                String tgl_akhir = detail.getString("tanggal_akhir");
                                String tgl_permohonan = detail.getString("tanggal");
                                String digital_signature = detail.getString("digital_signature");
                                String tipe_izin = detail.getString("tipe_izin");

                                if(tipe_izin.equals("5")){
                                    String foto_surat_sakit = detail.getString("foto_surat_sakit");
                                    String url_surat_sakit = "https://geloraaksara.co.id/absen-online/upload/surat_sakit/"+foto_surat_sakit;
                                    viewSuratSakitBTN.setVisibility(View.VISIBLE);
                                    viewSuratSakitBTN.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url_surat_sakit));
                                            startActivity(intent);
                                        }
                                    });
                                } else {
                                    viewSuratSakitBTN.setVisibility(View.GONE);
                                }

                                namaKaryawanTV.setText(nama_karyawan.toUpperCase());
                                nikKaryawanTV.setText(nik_karyawan);
                                bagianKaryawanTV.setText(bagian);
                                jabatanKaryawanTV.setText(jabatan);
                                alasanIzinTV.setText(alasan);

                                String input_date_mulai = tgl_mulai;
                                String dayDateMulai = input_date_mulai.substring(8,10);
                                String yearDateMulai = input_date_mulai.substring(0,4);
                                String bulanValueMulai = input_date_mulai.substring(5,7);
                                String bulanNameMulai;

                                switch (bulanValueMulai) {
                                    case "01":
                                        bulanNameMulai = "Januari";
                                        break;
                                    case "02":
                                        bulanNameMulai = "Februari";
                                        break;
                                    case "03":
                                        bulanNameMulai = "Maret";
                                        break;
                                    case "04":
                                        bulanNameMulai = "April";
                                        break;
                                    case "05":
                                        bulanNameMulai = "Mei";
                                        break;
                                    case "06":
                                        bulanNameMulai = "Juni";
                                        break;
                                    case "07":
                                        bulanNameMulai = "Juli";
                                        break;
                                    case "08":
                                        bulanNameMulai = "Agustus";
                                        break;
                                    case "09":
                                        bulanNameMulai = "September";
                                        break;
                                    case "10":
                                        bulanNameMulai = "Oktober";
                                        break;
                                    case "11":
                                        bulanNameMulai = "November";
                                        break;
                                    case "12":
                                        bulanNameMulai = "Desember";
                                        break;
                                    default:
                                        bulanNameMulai = "Not found";
                                        break;
                                }

                                tglMulaiTV.setText(String.valueOf(Integer.parseInt(dayDateMulai))+" "+bulanNameMulai+" "+yearDateMulai);

                                String input_date_akhir = tgl_akhir;
                                String dayDateAkhir = input_date_akhir.substring(8,10);
                                String yearDateAkhir = input_date_akhir.substring(0,4);
                                String bulanValueAkhir = input_date_akhir.substring(5,7);
                                String bulanNameAkhir;

                                switch (bulanValueAkhir) {
                                    case "01":
                                        bulanNameAkhir = "Januari";
                                        break;
                                    case "02":
                                        bulanNameAkhir = "Februari";
                                        break;
                                    case "03":
                                        bulanNameAkhir = "Maret";
                                        break;
                                    case "04":
                                        bulanNameAkhir = "April";
                                        break;
                                    case "05":
                                        bulanNameAkhir = "Mei";
                                        break;
                                    case "06":
                                        bulanNameAkhir = "Juni";
                                        break;
                                    case "07":
                                        bulanNameAkhir = "Juli";
                                        break;
                                    case "08":
                                        bulanNameAkhir = "Agustus";
                                        break;
                                    case "09":
                                        bulanNameAkhir = "September";
                                        break;
                                    case "10":
                                        bulanNameAkhir = "Oktober";
                                        break;
                                    case "11":
                                        bulanNameAkhir = "November";
                                        break;
                                    case "12":
                                        bulanNameAkhir = "Desember";
                                        break;
                                    default:
                                        bulanNameAkhir = "Not found";
                                        break;
                                }

                                tglAkhirTV.setText(String.valueOf(Integer.parseInt(dayDateAkhir))+" "+bulanNameAkhir+" "+yearDateAkhir);

                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                                Date dateBefore = sdf.parse(String.valueOf(tgl_mulai));
                                Date dateAfter = sdf.parse(String.valueOf(tgl_akhir));
                                long timeDiff = Math.abs(dateAfter.getTime() - dateBefore.getTime());
                                long daysDiff = TimeUnit.DAYS.convert(timeDiff, TimeUnit.MILLISECONDS);

                                totalHariTV.setText("( "+String.valueOf(daysDiff+1)+" Hari )");

                                String input_date_permohonan = tgl_permohonan;
                                String dayDatePermohonan = input_date_permohonan.substring(8,10);
                                String yearDatePermohonan = input_date_permohonan.substring(0,4);
                                String bulanValuePermohonan = input_date_permohonan.substring(5,7);
                                String bulanNamePermohonan;

                                switch (bulanValuePermohonan) {
                                    case "01":
                                        bulanNamePermohonan = "Januari";
                                        break;
                                    case "02":
                                        bulanNamePermohonan = "Februari";
                                        break;
                                    case "03":
                                        bulanNamePermohonan = "Maret";
                                        break;
                                    case "04":
                                        bulanNamePermohonan = "April";
                                        break;
                                    case "05":
                                        bulanNamePermohonan = "Mei";
                                        break;
                                    case "06":
                                        bulanNamePermohonan = "Juni";
                                        break;
                                    case "07":
                                        bulanNamePermohonan = "Juli";
                                        break;
                                    case "08":
                                        bulanNamePermohonan = "Agustus";
                                        break;
                                    case "09":
                                        bulanNamePermohonan = "September";
                                        break;
                                    case "10":
                                        bulanNamePermohonan = "Oktober";
                                        break;
                                    case "11":
                                        bulanNamePermohonan = "November";
                                        break;
                                    case "12":
                                        bulanNamePermohonan = "Desember";
                                        break;
                                    default:
                                        bulanNamePermohonan = "Not found";
                                        break;
                                }

                                tglPermohonanTV.setText("JAKARTA, "+String.valueOf(Integer.parseInt(dayDatePermohonan))+" "+bulanNamePermohonan+" "+yearDatePermohonan);

                                String url = "https://geloraaksara.co.id/absen-online/upload/digital_signature/"+digital_signature;

                                Picasso.get().load(url).networkPolicy(NetworkPolicy.NO_CACHE)
                                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                                        .into(ttdPemohon);

                                String shortName = nama_karyawan;
                                if(shortName.contains(" ")){
                                    shortName = shortName.substring(0, shortName.indexOf(" "));
                                    pemohonTV.setText(shortName.toUpperCase());
                                }

                                supervisorTV.setVisibility(View.VISIBLE);
                                String approver = detail.getString("approver");
                                String signature_approver = detail.getString("signature_approver");

                                String url_approver = "https://geloraaksara.co.id/absen-online/upload/digital_signature/"+signature_approver;

                                Picasso.get().load(url_approver).networkPolicy(NetworkPolicy.NO_CACHE)
                                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                                        .into(ttdSupervisor);

                                String shortName2 = approver;
                                if(shortName2.contains(" ")){
                                    shortName2 = shortName2.substring(0, shortName2.indexOf(" "));
                                    supervisorTV.setText(shortName2.toUpperCase());
                                }

                                String status_approve_hrd = detail.getString("status_approve_hrd");
                                if (status_approve_hrd.equals("1")){
                                    actionPart.setVisibility(View.GONE);
                                    acceptedMark.setVisibility(View.VISIBLE);
                                    rejectedMark.setVisibility(View.GONE);
                                    appoveStatusHRD.setVisibility(View.VISIBLE);
                                } else if (status_approve_hrd.equals("2")){
                                    actionPart.setVisibility(View.GONE);
                                    acceptedMark.setVisibility(View.GONE);
                                    rejectedMark.setVisibility(View.VISIBLE);
                                    appoveStatusHRD.setVisibility(View.GONE);
                                } else {
                                    actionPart.setVisibility(View.VISIBLE);
                                    acceptedMark.setVisibility(View.GONE);
                                    rejectedMark.setVisibility(View.GONE);
                                    appoveStatusHRD.setVisibility(View.GONE);
                                }

                            }

                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Error.Response", error.toString());
                        //connectionFailed();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_izin_record", idIzinRecord);
                return params;
            }
        };

        requestQueue.add(postRequest);

    }

    private void approvedAction(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        final String url = "https://geloraaksara.co.id/absen-online/api/approve_action_hrd";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(String response) {
                        // response
                        try {
                            Log.d("Success.Response", response.toString());
                            JSONObject data = new JSONObject(response);
                            String status = data.getString("status");

                            if(status.equals("Success")){
                                getDataDetailPermohonan();
                                actionPart.setVisibility(View.GONE);
                                pDialog.setTitleText("Berhasil Disetujui")
                                        .setConfirmText("OK")
                                        .changeAlertType(KAlertDialog.SUCCESS_TYPE);
                            } else {
                                actionPart.setVisibility(View.VISIBLE);
                                pDialog.setTitleText("Gagal Disetujui")
                                        .setConfirmText("OK")
                                        .changeAlertType(KAlertDialog.ERROR_TYPE);
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
                        connectionFailed();
                    }
                }
        )
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("id_izin_record", idIzinRecord);
                params.put("updated_at", getTimeStamp());

                return params;
            }
        };

        requestQueue.add(postRequest);

    }

    private void rejectedAction(){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        final String url = "https://geloraaksara.co.id/absen-online/api/reject_action_hrd";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(String response) {
                        // response
                        try {
                            Log.d("Success.Response", response.toString());
                            JSONObject data = new JSONObject(response);
                            String status = data.getString("status");

                            if(status.equals("Success")){
                                getDataDetailPermohonan();
                                actionPart.setVisibility(View.GONE);
                                pDialog.setTitleText("Berhasil Ditolak")
                                        .setConfirmText("OK")
                                        .changeAlertType(KAlertDialog.SUCCESS_TYPE);
                            } else {
                                actionPart.setVisibility(View.VISIBLE);
                                pDialog.setTitleText("Gagal Ditolak")
                                        .setConfirmText("OK")
                                        .changeAlertType(KAlertDialog.ERROR_TYPE);
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
                        connectionFailed();
                    }
                }
        )
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("id_izin_record", idIzinRecord);
                params.put("updated_at", getTimeStamp());

                return params;
            }
        };

        requestQueue.add(postRequest);

    }

    private void connectionFailed(){
        Banner.make(rootview, DetailPermohonanActivity.this, Banner.WARNING, "Koneksi anda terputus!", Banner.BOTTOM, 4000).show();
    }

    private void createPDF(Bitmap bitmap) {
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(960,1280,1).create();
        PdfDocument.Page page = pdfDocument.startPage(myPageInfo);

        page.getCanvas().drawBitmap(bitmap,10,10, null);
        pdfDocument.finishPage(page);

        File pictureFileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),"Absensi App");
        if (!pictureFileDir.exists()) {
            boolean isDirectoryCreated = pictureFileDir.mkdirs();
            if(!isDirectoryCreated)
                Log.i("ATG", "Can't create directory to save the image");
            return;
        }
        String filename = pictureFileDir.getPath() +File.separator+ System.currentTimeMillis()+".pdf";
        File myPDFFile = new File(filename);

        try {
            myPDFFile.createNewFile();
            pdfDocument.writeTo(new FileOutputStream(myPDFFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        scanDocuments(DetailPermohonanActivity.this,myPDFFile.getAbsolutePath());
        pdfDocument.close();
    }

    private File saveBitMap(Context context, View drawView){
        File pictureFileDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"Absensi App");
        if (!pictureFileDir.exists()) {
            boolean isDirectoryCreated = pictureFileDir.mkdirs();
            if(!isDirectoryCreated)
                Log.i("ATG", "Can't create directory to save the image");
            return null;
        }
        String filename = pictureFileDir.getPath() +File.separator+ System.currentTimeMillis()+".jpg";
        File pictureFile = new File(filename);
        bitmap = getBitmapFromView(drawView);
        //Bitmap resized = Bitmap.createScaledBitmap(bitmap, 300, 300, false);
        try {
            pictureFile.createNewFile();
            FileOutputStream oStream = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, oStream); //relative
            createPDF(bitmap);
            oStream.flush();
            oStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("TAG", "There was an issue saving the image.");
        }
        scanGallery(context,pictureFile.getAbsolutePath());
        return pictureFile;
    }


    private Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        }   else{
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    private void scanGallery(Context cntx, String path) {
        try {
            MediaScannerConnection.scanFile(cntx, new String[] { path },null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    uriImage = String.valueOf(uri);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void scanDocuments(Context cntx, String path) {
        try {
            MediaScannerConnection.scanFile(cntx, new String[] { path },null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    uriImage2 = String.valueOf(uri);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getTimeStamp() {
        @SuppressLint("SimpleDateFormat")
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private void generateQRCode(){
        if (TextUtils.isEmpty(idIzinRecord)){
            Banner.make(rootview,DetailPermohonanActivity.this,Banner.ERROR,"QR Code gagal di generate",Banner.BOTTOM,2000).show();
            return;
        }
        try {
            QRGEncoder qrgEncoder = new QRGEncoder(idIzinRecord,null, QRGContents.Type.TEXT,200);
            Bitmap bitmap = qrgEncoder.encodeAsBitmap();
            qrDocument.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }


}