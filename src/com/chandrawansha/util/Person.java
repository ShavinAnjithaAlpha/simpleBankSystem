package com.chandrawansha.util;

import java.sql.Date;

public class Person {

    private int personId;
    private String firstName;
    private String lastName;
    private String address;
    private Date birthDay;
    private String phoneNumber;
    private String email;
    private String idNumber;

    public Person(int personId, String firstName, String lastName, String address, Date birthDay, String phoneNumber, String email, String idNumber) {
        if (personId < 0){
            throw new IllegalArgumentException("person id shoud not be a negative value");
        }
        this.personId = personId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.birthDay = birthDay;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.idNumber = idNumber;
    }

    public Person(int personId, String firstName, String lastName, String address, Date birthDay ,String idNumber) {
        if (personId < 0)
            throw new IllegalArgumentException("person id shoud not be a negative value");
        this.personId = personId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.birthDay = birthDay;
        this.idNumber = idNumber;
    }

    // getters
    public int getPersonId() {
        return personId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public Date getBirthDay() {
        return birthDay;
    }

    // universal getter
    public String get(PersonFields personFields){
        switch (personFields){
            case FIRST_NAME:{
                return firstName;
            }
            case LAST_NAME:{
                return lastName;
            }
            case ADDRESS:{
                return address;
            }
            case PHONE_NUMBER:{
                return phoneNumber;
            }
            case EMAIL:{
                return email;
            }
            case NIC_NUMBER:{
                return idNumber;
            }
            default:{
                return null;
            }
        }
    }

    // setters

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber.length() == 10) {
            this.phoneNumber = phoneNumber;
        } else {
            throw new IllegalArgumentException("phone number must be 10 charactors");
        }
    }

    public void setEmail(String email) {
        if (email.indexOf("@") >= 0) {
            this.email = email;
        } else {
            throw new IllegalArgumentException("this is not the email address");
        }
    }

    @Override
    public String toString() {
        return String.format("Person ID : %d%nName : %S %S%nAddress : %s%nBirth Day : %s%nPhone Number : %s%nEmail : %s%nNIC Number : %S%n"
                , personId, firstName, lastName, address, birthDay.toString() ,phoneNumber, email, idNumber);
    }
}
