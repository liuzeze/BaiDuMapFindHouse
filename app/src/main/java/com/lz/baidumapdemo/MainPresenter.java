package com.lz.baidumapdemo;

import android.graphics.Point;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * -----------作者----------日期----------变更内容-----
 * -          刘泽      2018-11-08       创建class
 */
public class MainPresenter {

    private final MainContarct mContarct;
    List<LatLng> mScreenLatLng = new ArrayList<LatLng>();

    private PolygonOptions mOoPolyline;

    public MainPresenter(MainContarct contarct) {
        mContarct = contarct;
    }

    /**
     * 定位当前位置
     *
     * @param baiduMap
     */
    public void startLocation(final BaiduMap baiduMap) {
        final BDLocation mLocalLocation = LocationUtils.getInstance().mLocalLocation;
        if (mLocalLocation != null) {
            location(baiduMap, mLocalLocation);
            getMarkerData(baiduMap.getMapStatus().zoom);
        } else {
            LocationUtils.getInstance().setOnLocationListener(new LocationUtils.OnLocationListener() {
                @Override
                public void onSuccess(BDLocation location) {
                    location(baiduMap, location);
                    getMarkerData(baiduMap.getMapStatus().zoom);
                }

                @Override
                public void onFaild() {
                    Toast.makeText(App.mApp, "定位失败,请检查定位权限是否开启", Toast.LENGTH_SHORT).show();
                }
            }).start();
        }
    }

    /**
     * 定位移动
     *
     * @param baiduMap
     * @param mLocalLocation
     */
    private void location(BaiduMap baiduMap, BDLocation mLocalLocation) {
        MyLocationData myLocationData = new MyLocationData.Builder()
                .accuracy(mLocalLocation.getRadius())
                .latitude(mLocalLocation.getLatitude())
                .longitude(mLocalLocation.getLongitude()).build();
        baiduMap.setMyLocationData(myLocationData);

        LatLng ll = new LatLng(mLocalLocation.getLatitude(),
                mLocalLocation.getLongitude());
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(ll).zoom(16.0f);
        baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
    }

    /**
     * 绘制触摸范围
     *
     * @param points   触摸点
     * @param baiduMap 地图对象
     */
    public void drawPolygon(List<LatLng> points, BaiduMap baiduMap) {

        //绘制面
        if (mOoPolyline == null) {

            mOoPolyline = new PolygonOptions()
                    .points(points)
                    .stroke(new Stroke(10, 0xAAFF0000))
                    .fillColor(0xAAFFFF00);
        } else {
            mOoPolyline.points(points);
        }
        baiduMap.addOverlay(mOoPolyline);


        //计算缩放比例
        List<Double> latitudeList = new ArrayList<Double>();
        List<Double> longitudeList = new ArrayList<Double>();

        for (int i = 0; i < points.size(); i++) {
            double latitude = points.get(i).latitude;
            double longitude = points.get(i).longitude;
            latitudeList.add(latitude);
            longitudeList.add(longitude);
        }
        Double maxLatitude = Collections.max(latitudeList);
        Double minLatitude = Collections.min(latitudeList);
        Double maxLongitude = Collections.max(longitudeList);
        Double minLongitude = Collections.min(longitudeList);


        int zoom[] = {10, 20, 50, 100, 200, 500, 1000, 2000, 5000, 1000, 2000, 25000, 50000, 100000, 200000, 500000, 1000000, 2000000};

        // 创建点坐标A
        LatLng pointA = new LatLng(maxLongitude, maxLatitude);
        // 创建点坐标B
        LatLng pointB = new LatLng(minLongitude, minLatitude);
        //获取两点距离,保留小数点后两位
        double distance = DistanceUtil.getDistance(pointA, pointB);
        for (int i = 0; i < zoom.length; i++) {
            int zoomNow = zoom[i];
            if (zoomNow - distance > 0) {
                float level = 18 - i + 3.7f;
                //设置地图显示级别为计算所得level
                baiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(level).build()));
                break;
            }
        }

        //定位中心点
        LatLng center = new LatLng((maxLatitude + minLatitude) / 2, (maxLongitude + minLongitude) / 2);
        MapStatusUpdate status1 = MapStatusUpdateFactory.newLatLng(center);
        baiduMap.animateMapStatus(status1, 500);

    }

    /**
     * 接口获取图层数据集
     *
     * @param zoom
     */

    public void getMarkerData(final float zoom) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<MyItem> mRequestMakerLatLngsList = new ArrayList<>();

                for (int i = 0; i < 5; i++) {
                    LatLng llA = new LatLng(39.903105, 116.400294);
                    LatLng llB = new LatLng(40.964105, 115.451294);
                    LatLng llC = new LatLng(39.969105, 116.402294);
                    LatLng llD = new LatLng(41.916105, 117.443294);
                    LatLng llE = new LatLng(39.967105, 116.404294);
                    LatLng llF = new LatLng(40.968105, 115.425294);
                    LatLng llG = new LatLng(39.990105, 116.486294);

                    String name = "";
                    if (zoom > 17) {
                        name = "三级标题";
                    } else if (zoom >= 13) {
                        name = "二级标题";
                    } else {
                        name = "一级标题";
                    }
                    mRequestMakerLatLngsList.add(new MyItem(llB, name));
                    mRequestMakerLatLngsList.add(new MyItem(llC, name));
                    mRequestMakerLatLngsList.add(new MyItem(llD, name));
                    mRequestMakerLatLngsList.add(new MyItem(llE, name));
                    mRequestMakerLatLngsList.add(new MyItem(llF, name));
                    mRequestMakerLatLngsList.add(new MyItem(llG, name));
                    mRequestMakerLatLngsList.add(new MyItem(llA, name));

                }
                mContarct.getMarkerDataSuccess(mRequestMakerLatLngsList);
                //大到小

            }
        }).start();
    }

}
