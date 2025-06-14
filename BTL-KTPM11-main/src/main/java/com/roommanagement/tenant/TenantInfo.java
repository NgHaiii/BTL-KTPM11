package com.roommanagement.tenant;

public class TenantInfo {
    private String roomName;
    private String phone;
    private String address;

    public TenantInfo(String roomName, String phone, String address) {
        this.roomName = roomName;
        this.phone = phone;
        this.address = address;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }
} 
