package com.lz.baidumapdemo;

import com.baidu.mapapi.model.LatLng;

import java.io.Serializable;

/**
 * -----------作者----------日期----------变更内容-----
 * -          刘泽      2018-11-08       创建class
 */
public class MyItem implements Serializable {
    private final LatLng mPosition;
    public String name = "";

    public MyItem(LatLng position, String name) {
        mPosition = position;
        this.name = name;
    }

    public MyItem(LatLng latLng) {
        mPosition = latLng;
    }

    public LatLng getPosition() {
        return mPosition;
    }


    @Override
    public boolean equals(Object o) {
        MyItem item = (MyItem) o;
        return mPosition.latitude == item.mPosition.latitude && mPosition.longitude == item.mPosition.longitude;
    }
}
