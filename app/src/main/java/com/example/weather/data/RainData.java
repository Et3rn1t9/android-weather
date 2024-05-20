package com.example.weather.data;

public class RainData {
    private String time; // 时间
    private String minRainInfo; // 分钟级降水信息
    private String type;

    public RainData(String time, String minRainInfo, String type) {
        this.time = time;
        this.minRainInfo = minRainInfo;
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public String getMinRainInfo() {
        return minRainInfo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
