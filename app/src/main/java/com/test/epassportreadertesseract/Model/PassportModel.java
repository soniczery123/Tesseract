package com.test.epassportreadertesseract.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PassportModel {

    @SerializedName("PassportType")
    @Expose
    private String passportType;
    @SerializedName("Surname")
    @Expose
    private String surname;
    @SerializedName("Firstname")
    @Expose
    private String firstname;
    @SerializedName("PassportNo")
    @Expose
    private String passportNo;
    @SerializedName("Nationality")
    @Expose
    private String nationality;
    @SerializedName("CitizenID")
    @Expose
    private String citizenID;
    @SerializedName("DOB")
    @Expose
    private String dOB;
    @SerializedName("Sex")
    @Expose
    private String sex;
    @SerializedName("ExpDate")
    @Expose
    private String expDate;

    public String getPassportType() {
        return passportType;
    }

    public void setPassportType(String passportType) {
        this.passportType = passportType;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getPassportNo() {
        return passportNo;
    }

    public void setPassportNo(String passportNo) {
        this.passportNo = passportNo;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getCitizenID() {
        return citizenID;
    }

    public void setCitizenID(String citizenID) {
        this.citizenID = citizenID;
    }

    public String getDOB() {
        return dOB;
    }

    public void setDOB(String dOB) {
        this.dOB = dOB;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }
}