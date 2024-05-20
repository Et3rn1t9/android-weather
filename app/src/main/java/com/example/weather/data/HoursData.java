package com.example.weather.data;

public class HoursData {
    private String time;
    private String temp;
    private String icon;

    public HoursData(String time, String temp, String icon) {
        this.time = time;
        this.temp = temp;
        this.icon = icon;
    }
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }
}
