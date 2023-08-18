package com.gelora.geloraappcontrol.model;


public class ListPermohonanIzin {

    private String id;
    private String NIK;
    private String NmKaryawan;
    private String tanggal;
    private String tipe_izin;
    private String deskripsi_izin;
    private String kode_izin;
    private String tanggal_mulai;
    private String tanggal_akhir;
    private String surat_sakit;
    private String keterangan;
    private String status;
    private String created_at;
    private String updated_at;
    private String status_approve;
    private String status_approve_hrd;
    private String status_read;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNIK() {
        return NIK;
    }

    public void setNIK(String NIK) {
        this.NIK = NIK;
    }

    public String getNmKaryawan() {
        return NmKaryawan;
    }

    public void setNmKaryawan(String nmKaryawan) {
        NmKaryawan = nmKaryawan;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getTipe_izin() {
        return tipe_izin;
    }

    public void setTipe_izin(String tipe_izin) {
        this.tipe_izin = tipe_izin;
    }

    public String getDeskripsi_izin() {
        return deskripsi_izin;
    }

    public void setDeskripsi_izin(String deskripsi_izin) {
        this.deskripsi_izin = deskripsi_izin;
    }

    public String getKode_izin() {
        return kode_izin;
    }

    public void setKode_izin(String kode_izin) {
        this.kode_izin = kode_izin;
    }

    public String getTanggal_mulai() {
        return tanggal_mulai;
    }

    public void setTanggal_mulai(String tanggal_mulai) {
        this.tanggal_mulai = tanggal_mulai;
    }

    public String getTanggal_akhir() {
        return tanggal_akhir;
    }

    public void setTanggal_akhir(String tanggal_akhir) {
        this.tanggal_akhir = tanggal_akhir;
    }

    public String getSurat_sakit() {
        return surat_sakit;
    }

    public void setSurat_sakit(String surat_sakit) {
        this.surat_sakit = surat_sakit;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getStatus_approve() {
        return status_approve;
    }

    public void setStatus_approve(String status_approve) {
        this.status_approve = status_approve;
    }

    public String getStatus_approve_hrd() {
        return status_approve_hrd;
    }

    public void setStatus_approve_hrd(String status_approve_hrd) {
        this.status_approve_hrd = status_approve_hrd;
    }

    public String getStatus_read() {
        return status_read;
    }

    public void setStatus_read(String status_read) {
        this.status_read = status_read;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}
