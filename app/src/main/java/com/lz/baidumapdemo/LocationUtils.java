package com.lz.baidumapdemo;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.map.MyLocationData;

import static com.baidu.location.BDLocation.LOCATION_WHERE_UNKNOW;

/**
 * -----------作者----------日期----------变更内容-----
 * -          刘泽      2018-11-08       创建class
 */
public class LocationUtils {
    private static LocationUtils mLocationUtils = new LocationUtils();
    private final LocationClient mLocationClient;
    private OnLocationListener mOnLocationListener;
    public static BDLocation mLocalLocation;
    public static double mLongitude;
    public static double mLatitude;

    public static LocationUtils getInstance() {
        return mLocationUtils;
    }

    private LocationUtils() {
        mLocationClient = new LocationClient(App.mApp);
        mLocationClient.registerLocationListener(new BDAbstractLocationListener() {


            @Override
            public void onReceiveLocation(BDLocation location) {
                if (location != null) {
                    int errorCode = location.getLocType();
                    if (LOCATION_WHERE_UNKNOW == errorCode) {
                        if (mOnLocationListener != null) {
                            mOnLocationListener.onFaild();
                        }
                        return;
                    }

                    mLocalLocation = location;
                    mLatitude = location.getLatitude();
                    mLongitude = location.getLongitude();
                    if (mOnLocationListener != null) {
                        mOnLocationListener.onSuccess(location);
                    }


                } else {
                    if (mOnLocationListener != null) {
                        mOnLocationListener.onFaild();
                    }
                }
            }
        });

        LocationClientOption option = new LocationClientOption();

        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);

        option.setOpenGps(true);

        option.setLocationNotify(true);

        mLocationClient.setLocOption(option);

    }

    public void start() {
        mLocationClient.start();

    }

    public void stop() {
        mLocationClient.stop();

    }

    public LocationUtils setOnLocationListener(OnLocationListener onLocationListener) {
        mOnLocationListener = onLocationListener;
        return getInstance();
    }

    public interface OnLocationListener {
        void onSuccess(BDLocation location);

        void onFaild();
    }
}
