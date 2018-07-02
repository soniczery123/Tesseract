package com.test.epassportreadertesseract;

public class People {
    private String PassportType;
    private String Surname;
    private String Firstname;
    private String PassportNo;
    private String Nationality;
    private String CitizenID;
    private String DOB;
    private String Sex;
    private String ExpDate;

    public People(String passportType, String surname, String firstname, String passportNo, String nationality, String citizenID, String DOB, String sex, String expDate) {

        this.PassportType = passportType;
        this.Surname = surname;
        this.Firstname = firstname;
        this.PassportNo = passportNo;
        this.Nationality = nationality;
        this.CitizenID = citizenID;
        this.DOB = DOB;
        this.Sex = sex;
        this.ExpDate = expDate;
    }

    public String getPassportType() {
        return this.PassportType;
    }

    public void setPassportType(String passportType) {
        this.PassportType = passportType;
    }

    public String getSurname() {
        return this.Surname;
    }

    public void setSurname(String surname) {
        this.Surname = surname;
    }

    public String getFirstname() {
        return this.Firstname;
    }

    public void setFirstname(String firstname) {
        this.Firstname = firstname;
    }

    public String getPassportNo() {
        return this.PassportNo;
    }

    public void setPassportNo(String passportNo) {
        this.PassportNo = passportNo;
    }

    public String getNationality() {
        return this.Nationality;
    }

    public void setNationality(String nationality) {
        this.Nationality = nationality;
    }

    public String getCitizenID() {
        return this.CitizenID;
    }

    public void setCitizenID(String citizenID) {
        this.CitizenID = citizenID;
    }

    public String getDOB() {
        return this.DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getSex() {
        return this.Sex;
    }

    public void setSex(String sex) {
        this.Sex = sex;
    }

    public String getExpDate() {
        return this.ExpDate;
    }

    public void setExpDate(String expDate) {
        this.ExpDate = expDate;
    }


}
