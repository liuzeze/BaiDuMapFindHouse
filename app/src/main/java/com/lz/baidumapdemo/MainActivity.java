package com.lz.baidumapdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.SpatialRelationUtil;
import com.lz.baidumapdemo.poi.PoiActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainContarct {

    private View mFloat;
    private MapView mMapView;
    private BaiduMap mBaiduMap;

    private MainMapBean mMainMapBean = new MainMapBean();

    private Marker mMarker;
    private MainPresenter mMainPresenter;
    /**
     * 添加点、线、多边形、圆、文字
     */
    List<LatLng> points = new ArrayList<>();
    private static final String mMARKERDATA = "MARKERDATA";
    private TextView mTextView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private View mBootomSheetView;
    private BottomSheetWidget mBottomSheetWidget;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFloat = (View) findViewById(R.id.floats);
        mMapView = (MapView) findViewById(R.id.bmapView);


        mMainPresenter = new MainPresenter(this);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMaxAndMinZoomLevel(20, 6);
        mMainPresenter.startLocation(mBaiduMap);
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        intListener();
        mBootomSheetView = findViewById(R.id.bottomSheetLayout);

        mBottomSheetWidget = new BottomSheetWidget(mBootomSheetView);


    }


    private void intListener() {

        mFloat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int x = (int) motionEvent.getX();

                int y = (int) motionEvent.getY();
                LatLng geoPoint = mBaiduMap.getProjection().fromScreenLocation(new Point(x, y));
                if (!points.contains(geoPoint)) {
                    points.add(geoPoint);
                    if (points.size() > 3) {
                        OverlayOptions ooPolyline = new PolylineOptions().width(10).color(0xAAFF0000).points(points);
                        mBaiduMap.addOverlay(ooPolyline);
                    }
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    mFloat.setVisibility(View.GONE);
                    mMainPresenter.drawPolygon(points, mBaiduMap);

                }
                return false;
            }
        });

        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                Bundle extraInfo = marker.getExtraInfo();
                MyItem myItem = (MyItem) extraInfo.getSerializable(mMARKERDATA);
                if (mMainMapBean.mZoom != 3) {
                    MapStatus.Builder builder = new MapStatus.Builder();
                    builder.target(myItem.getPosition()).zoom(mMainMapBean.mZoom == 1 ? 13.5f : 17.5f);
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                } else {
                    if (mMarker != marker) {
                        if (mTextView != null) {
                            mTextView.setBackgroundResource(R.drawable.ic_orange_point);
                            marker.setIcon(BitmapDescriptorFactory.fromView(mTextView));
                        }
                        if (mMarker != null) {
                            mTextView.setBackgroundResource(R.drawable.ic_white_point);
                            mMarker.setIcon(BitmapDescriptorFactory.fromView(mTextView));
                        }
                        mMarker = marker;

                    }
                    mBottomSheetWidget.collapsed();

                }
                //放大显示
                return false;
            }
        });

        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {

                int zoom = mMainMapBean.zoomType(mapStatus.zoom);

                if (zoom != mMainMapBean.mZoom && !mMainMapBean.isCavens) {
                    mMapView.getMap().clear();
                    mMainMapBean.showMakerLatLngsList.clear();
                }
                mMainMapBean.mZoom = zoom;

                mMainPresenter.getMarkerData(mBaiduMap.getMapStatus().zoom);


            }
        });

    }


    public void cavens(View view) {
        if (mBaiduMap.getMapStatus().zoom > 13) {
            mMainMapBean.isCavens = true;
            mMapView.getMap().clear();
            mMainMapBean.showMakerLatLngsList.clear();
            mFloat.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(MainActivity.this, "请放大地图", Toast.LENGTH_SHORT).show();
        }
    }

    public void poiSearch(View view) {
        startActivity(new Intent(this, PoiActivity.class));

    }

    public void location(View view) {
        mMainPresenter.startLocation(mBaiduMap);

    }

    public void clear(View view) {
        mMainMapBean.isCavens = false;
        mMapView.getMap().clear();
        mMainMapBean.showMakerLatLngsList.clear();
        mFloat.setVisibility(View.GONE);
        points.clear();
        mMainPresenter.getMarkerData(mBaiduMap.getMapStatus().zoom);
    }


    private void addLayerMarkers() {
        mMainMapBean.mScreenLatLng.clear();
        if (mMainMapBean.isCavens) {
            mMainMapBean.mScreenLatLng.addAll(points);
        } else {
            if (mBaiduMap.getProjection() != null) {
                int scaleControlViewHeight = mMapView.getMeasuredHeight();
                int scaleControlViewWidth = mMapView.getMeasuredWidth();

                LatLng geoPoint = mBaiduMap.getProjection().fromScreenLocation(new Point(0, 0));

                mMainMapBean.mScreenLatLng.add(geoPoint);
                geoPoint = mBaiduMap.getProjection().fromScreenLocation(new Point(0, scaleControlViewWidth));
                mMainMapBean.mScreenLatLng.add(geoPoint);
                geoPoint = mBaiduMap.getProjection().fromScreenLocation(new Point(scaleControlViewWidth, scaleControlViewHeight));
                mMainMapBean.mScreenLatLng.add(geoPoint);
                geoPoint = mBaiduMap.getProjection().fromScreenLocation(new Point(scaleControlViewHeight, 0));
                mMainMapBean.mScreenLatLng.add(geoPoint);
            }
        }

        //构建marker图标
        BitmapDescriptor bitmap = null;
        MarkerOptions ptionMarker = null;
        if (mTextView == null) {
            View inflate = View.inflate(MainActivity.this, R.layout.item_bubbo, null);
            mTextView = inflate.findViewById(R.id.tv_bubbo);
            mTextView.setTextColor(Color.BLACK);
            mTextView.setGravity(Gravity.CENTER);
        }
        mTextView.setBackgroundResource(R.drawable.ic_white_point);
        for (int i = 0; i < mMainMapBean.mRequestMakerLatLngsList.size(); i++) {

            LatLng position = mMainMapBean.mRequestMakerLatLngsList.get(i).getPosition();
            if (mMainMapBean.showMakerLatLngsList.get("" + position.latitude + position.longitude) == null) {
                if (SpatialRelationUtil.isPolygonContainsPoint(mMainMapBean.mScreenLatLng, mMainMapBean.mRequestMakerLatLngsList.get(i).getPosition())) {
                    mTextView.setText(mMainMapBean.mRequestMakerLatLngsList.get(i).name);
                    bitmap = BitmapDescriptorFactory.fromView(mTextView);
                    // 构建MarkerOption，用于在地图上添加Marker o
                    ptionMarker = new MarkerOptions().icon(bitmap).position(mMainMapBean.mRequestMakerLatLngsList.get(i).getPosition());
                    // 生长动画
                    ptionMarker.animateType(MarkerOptions.MarkerAnimateType.grow);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(mMARKERDATA, mMainMapBean.mRequestMakerLatLngsList.get(i));
                    ptionMarker.extraInfo(bundle);

                    // 在地图上添加Marker，并显示
                    mBaiduMap.addOverlay(ptionMarker);
                    //设置Marker覆盖物的ZIndex
                    mMainMapBean.showMakerLatLngsList.put("" + position.latitude + position.longitude, mMainMapBean.mRequestMakerLatLngsList.get(i));
                    ptionMarker.zIndex(i);
                }
            }
        }

    }


    @Override
    public void getMarkerDataSuccess(List<MyItem> latLngsList) {
        mMainMapBean.mRequestMakerLatLngsList.clear();
        mMainMapBean.mRequestMakerLatLngsList.addAll(latLngsList);
        addLayerMarkers();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mBottomSheetWidget.closeBottomSheet()) {
            super.onBackPressed();
        }
    }
}
