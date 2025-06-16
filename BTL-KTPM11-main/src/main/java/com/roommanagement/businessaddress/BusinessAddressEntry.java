package com.roommanagement.businessaddress;

public class BusinessAddressEntry {
    private int id;
    private String name;
    private String birthday;
    private String phone;
    private String soNha;
    private String address;
    private String province;
    private String district;
    private String ward;

    public BusinessAddressEntry(int id, String name, String birthday, String phone,
                                String soNha, String address, String province, String district, String ward) {
        this.id = id;
        this.name = name;
        this.birthday = birthday;
        this.phone = phone;
        this.soNha = soNha;
        this.address = address;
        this.province = province;
        this.district = district;
        this.ward = ward;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getBirthday() { return birthday; }
    public String getPhone() { return phone; }
    public String getSoNha() { return soNha; }
    public String getAddress() { return address; }
    public String getProvince() { return province; }
    public String getDistrict() { return district; }
    public String getWard() { return ward; }
} 

