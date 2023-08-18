package com.gelora.geloraappcontrol.model;


public class DeviceID {

    private String id;
    private String NIK;
    private String device_id;
    private String status_aktif;
    private String created_at;

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

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getStatus_aktif() {
        return status_aktif;
    }

    public void setStatus_aktif(String status_aktif) {
        this.status_aktif = status_aktif;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
