package com.example.weather.data;

public class MyCity {
    private String city_name; //名字

    private String city_id;  //城市id

    public MyCity(String city_name,String city_id){
        this.city_name = city_name;
        this.city_id = city_id;
    }
    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getCity_name() {
        return city_name;
    }


    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }


}
