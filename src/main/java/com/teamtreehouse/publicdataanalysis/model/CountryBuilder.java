package com.teamtreehouse.publicdataanalysis.model;

// builder for country objects
public class CountryBuilder {

    private String code;
    private String name;
    private Double internetUsers;
    private Double adultLiteracyRate;

    public CountryBuilder(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public CountryBuilder withInternetUsers(Double internetUsers) {
        this.internetUsers = internetUsers;
        return this;
    }

    public CountryBuilder withAdultLiteracyRate(Double adultLiteracyRate) {
        this.adultLiteracyRate = adultLiteracyRate;
        return this;
    }

    public Country build() {
        return new Country(this);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getInternetUsers() {
        return internetUsers;
    }

    public void setInternetUsers(Double internetUsers) {
        this.internetUsers = internetUsers;
    }

    public Double getAdultLiteracyRate() {
        return adultLiteracyRate;
    }

    public void setAdultLiteracyRate(Double adultLiteracyRate) {
        this.adultLiteracyRate = adultLiteracyRate;
    }
}
