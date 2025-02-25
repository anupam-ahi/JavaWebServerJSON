package org.example.apitesting;

public class OutputPerson {
    String firstname, lastname;
    int age;
    private AddressDTO address;
    public void setfirstname(String firstname) {
        this.firstname = firstname;
    }
    public void setlastname(String lastname) {
        this.lastname = lastname;
    }
    public void setage(int age) {
        this.age = age;
    }
    public String getFirstname(){
        return this.firstname;
    }
    public String getLastname(){
        return this.lastname;
    }
    public int getAge(){
        return this.age;
    }
    public AddressDTO getAddress(){
        return this.address;
    }
    public void setAddress(AddressDTO address){
        this.address = address;
    }



}
