package com.roommanagement.auth;

import javafx.beans.property.*;
import java.util.List;
import java.util.Objects;

public class AdminModel {
    public static class TenantEntry {
        private final SimpleStringProperty name, phone, address, room;
        public TenantEntry(String name, String phone, String address, String room) {
            this.name = new SimpleStringProperty(name);
            this.phone = new SimpleStringProperty(phone);
            this.address = new SimpleStringProperty(address);
            this.room = new SimpleStringProperty(room);
        }
        public String getName() { return name.get(); }
        public String getPhone() { return phone.get(); }
        public String getAddress() { return address.get(); }
        public String getRoom() { return room.get(); }
    }

public static class RoomEntry {
    private String name;
    private String size;
    private String type;
    private String status;
    private String address;
    private String chuHo;

    public RoomEntry(String name, String size, String type, String status, String address, String chuHo) {
        this.name = name;
        this.size = size;
        this.type = type;
        this.status = status;
        this.address = address;
        this.chuHo = chuHo;
    }

    public String getName() { return name; }
    public String getSize() { return size; }
    public String getType() { return type; }
    public String getStatus() { return status; }
    public String getAddress() { return address; }
    public String getChuHo() { return chuHo; }

    public void setName(String name) { this.name = name; }
    public void setSize(String size) { this.size = size; }
    public void setType(String type) { this.type = type; }
    public void setStatus(String status) { this.status = status; }
    public void setAddress(String address) { this.address = address; }
    public void setChuHo(String chuHo) { this.chuHo = chuHo; }
    
    @Override
    public String toString() {
        return name; 
    }
}


    public static class BillEntry {
        private final SimpleIntegerProperty id;
        private final SimpleStringProperty tenantName, description;
        private final SimpleDoubleProperty amount;
        private String status;
        public BillEntry(int id, String tenantName, double amount, String description) {
            this.id = new SimpleIntegerProperty(id);
            this.tenantName = new SimpleStringProperty(tenantName);
            this.amount = new SimpleDoubleProperty(amount);
            this.description = new SimpleStringProperty(description);
        }
        public int getId() { return id.get(); }
        public String getTenantName() { return tenantName.get(); }
        public double getAmount() { return amount.get(); }
        public String getDescription() { return description.get(); }
        public void setStatus(String status) { this.status = status; }

        
    }
}

 class Province {
    private String name;
    private List<District> districts;

    public Province() {}

    public Province(String name, List<District> districts) {
        this.name = name;
        this.districts = districts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<District> getDistricts() {
        return districts;
    }

    public void setDistricts(List<District> districts) {
        this.districts = districts;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Province)) return false;
        Province province = (Province) o;
        return Objects.equals(name, province.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

 class District {
    private String name;
    private List<Ward> wards;

    public District() {}

    public District(String name, List<Ward> wards) {
        this.name = name;
        this.wards = wards;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Ward> getWards() {
        return wards;
    }

    public void setWards(List<Ward> wards) {
        this.wards = wards;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof District)) return false;
        District district = (District) o;
        return Objects.equals(name, district.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}

 class Ward {
    private String name;

    public Ward() {}

    public Ward(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ward)) return false;
        Ward ward = (Ward) o;
        return Objects.equals(name, ward.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}