package com.lz.baidumapdemo;

import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * -----------作者----------日期----------变更内容-----
 * -          刘泽      2018-11-08       创建class
 */
public class MainMapBean {
    public List<MyItem> mRequestMakerLatLngsList = new ArrayList<>();
    public HashMap<String, MyItem> showMakerLatLngsList = new HashMap<>();
    public List<LatLng> mScreenLatLng = new ArrayList<>();
    public int mZoom;
    public boolean isCavens;

    public int zoomType(float zoom) {
        if (zoom > 17) {
            return 3;
        } else if (zoom >= 13) {
            return 2;
        } else {
            return 1;
        }

    }
}
