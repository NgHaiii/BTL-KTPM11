package com.roommanagement.auth;

import javafx.beans.property.*;

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
        private final SimpleStringProperty name, size, type, status;
        public RoomEntry(String name, String size, String type, String status) {
            this.name = new SimpleStringProperty(name);
            this.size = new SimpleStringProperty(size);
            this.type = new SimpleStringProperty(type);
            this.status = new SimpleStringProperty(status);
        }
        public String getName() { return name.get(); }
        public String getSize() { return size.get(); }
        public String getType() { return type.get(); }
        public String getStatus() { return status.get(); }
        public void setStatus(String status) { this.status.set(status); }
        @Override
        public String toString() { return getName(); }
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