package com.codepath.nytimessearch.models;

public class Settings {
    private String begin;
    private String sort;
    private Boolean arts;
    private Boolean fashion;
    private Boolean sports;

    public String getBegin() {
        return begin;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Boolean getArts() {
        return arts;
    }

    public void setArts(Boolean arts) {
        this.arts = arts;
    }

    public Boolean getFashion() {
        return fashion;
    }

    public void setFashion(Boolean fashion) {
        this.fashion = fashion;
    }

    public Boolean getSports() {
        return sports;
    }

    public void setSports(Boolean sports) {
        this.sports = sports;
    }
}
