package org.example.apitesting;

public class AddressDTO {
    private int apt;
    private String street;
    public void setApt(int apt) {
        this.apt = apt;
    }
    public int getApt() {
        return this.apt;
    }
    public void setStreet(String street) {
        this.street = street;
    }
    public String getStreet() {
        return this.street;
    }
}
