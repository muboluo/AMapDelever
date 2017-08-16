package com.master.rout.court.file;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.view.RouteOverLay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.autonavi.tbt.TrafficFacilityInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 设置路径页面。
 */
public class SetRouteActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher, AdapterView
        .OnItemClickListener, Inputtips.InputtipsListener, View.OnTouchListener, RouteSearch.OnRouteSearchListener,
        AMapNaviListener {


    private static final int START_SEARCH_ACTIVITY = 9527; // 跳转code
    private static final String SEARCH_POINT = "search_point";//搜索位置
    private MapView mMapView;
    private ImageView img_back;
    private EditText ed_start_point;
    private EditText ed_pass_point_1;
    private EditText ed_pass_point_2;
    private EditText ed_pass_point_3;
    private EditText ed_end_point;

    private ImageView iv_clear_start;
    private ImageView iv_clear_point_1;
    private ImageView iv_clear_point_2;
    private ImageView iv_clear_point_3;
    private ImageView iv_clear_end;

    private ListView list_view_poi;

    private MyAdapter myAdapter;

    private Button btn_start_search;


    private int mFileNums;//文件数
    private boolean mHasFilePriority;//文件是否有优先级
    private ArrayList<Tip> mSearchResultList = new ArrayList<>();
    private InputtipsQuery mInputQuery;//查询poi结果
    private Inputtips mInputTips;//输入建议


    LatLonPoint startPoint;// 起点
    LatLonPoint endPoint;


    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;

    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            //首先，可以判断AMapLocation对象不为空，当定位错误码类型为0时定位成功。
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    //可在其中解析amapLocation获取相应内容。


                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }

        }
    };

    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    private int mCurrentPos = -1;//当前点击位置
    //private int mSearchPos = -1;//当前搜索的位置
    private RouteSearch mRouteSearch;
    private AMap mAmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_set_route);

        mFileNums = getIntent().getIntExtra(ChooseFileActivity.FILE_NUMS, 1);
        mHasFilePriority = getIntent().getBooleanExtra(ChooseFileActivity.FILE_HAS_PRIORITY, false);

        initViews();

        initLocation();

        initMaps(savedInstanceState);

        initRoutSearch();

    }

    private void initRoutSearch() {
        //初始化路径规划
        mRouteSearch = new RouteSearch(this);
        mRouteSearch.setRouteSearchListener(this);
    }

    private void initViews() {

        img_back = (ImageView) findViewById(R.id.img_back);
        ed_start_point = (EditText) findViewById(R.id.ed_start_point);
        ed_pass_point_1 = (EditText) findViewById(R.id.ed_pass_point_1);
        ed_pass_point_2 = (EditText) findViewById(R.id.ed_pass_point_2);
        ed_pass_point_3 = (EditText) findViewById(R.id.ed_pass_point_3);
        ed_end_point = (EditText) findViewById(R.id.ed_end_point);
        iv_clear_start = (ImageView) findViewById(R.id.iv_clear_start);
        iv_clear_point_1 = (ImageView) findViewById(R.id.iv_clear_point_1);
        iv_clear_point_2 = (ImageView) findViewById(R.id.iv_clear_point_2);
        iv_clear_point_3 = (ImageView) findViewById(R.id.iv_clear_point_3);
        iv_clear_end = (ImageView) findViewById(R.id.iv_clear_end);
        btn_start_search = (Button) findViewById(R.id.btn_start_search);
        img_back.setOnClickListener(this);
        iv_clear_start.setOnClickListener(this);
        iv_clear_point_1.setOnClickListener(this);
        iv_clear_point_2.setOnClickListener(this);
        iv_clear_point_3.setOnClickListener(this);
        iv_clear_end.setOnClickListener(this);
        btn_start_search.setOnClickListener(this);

        ed_start_point.addTextChangedListener(this);
        ed_pass_point_1.addTextChangedListener(this);
        ed_pass_point_2.addTextChangedListener(this);
        ed_pass_point_3.addTextChangedListener(this);
        ed_end_point.addTextChangedListener(this);

        ed_start_point.setOnTouchListener(this);
        ed_pass_point_1.setOnTouchListener(this);
        ed_pass_point_2.setOnTouchListener(this);
        ed_pass_point_3.setOnTouchListener(this);
        ed_end_point.setOnTouchListener(this);


        //判断当前文件数量
        if (mFileNums >= 2) {
            ed_pass_point_1.setVisibility(View.VISIBLE);
            iv_clear_point_1.setVisibility(View.VISIBLE);
        }
        if (mFileNums >= 3) {
            ed_pass_point_2.setVisibility(View.VISIBLE);
            iv_clear_point_2.setVisibility(View.VISIBLE);
        }
        if (mFileNums >= 4) {
            ed_pass_point_3.setVisibility(View.VISIBLE);
            iv_clear_point_3.setVisibility(View.VISIBLE);
        }


        list_view_poi = (ListView) findViewById(R.id.list_view_poi);

        myAdapter = new MyAdapter();
        list_view_poi.setAdapter(myAdapter);

        list_view_poi.setOnItemClickListener(this);


    }


    private void initLocation() {


        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);


        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();

        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。默认高精度
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

        //获取一次定位结果：
        //该方法默认为false。
        //mLocationOption.setOnceLocation(true);

        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。
        // 如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        //mLocationOption.setOnceLocationLatest(true);

        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        mLocationOption.setInterval(1000);

        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);

        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(20000);

        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();


    }

    /**
     * 选择起点Action标志位
     */
    private boolean mapClickStartReady;


    /**
     * 地图对象
     */
    private MapView mRouteMapView;
    private Marker mStartMarker;
    private Marker mEndMarker;
    private NaviLatLng startLatlng;
    private NaviLatLng endLatlng;
    private List<NaviLatLng> startList = new ArrayList<NaviLatLng>();

    /**
     * 选择终点Aciton标志位
     */
    private boolean mapClickEndReady;

    /**
     * 途径点坐标集合
     */
    private List<NaviLatLng> wayList = new ArrayList<NaviLatLng>();
    /**
     * 终点坐标集合［建议就一个终点］
     */
    private List<NaviLatLng> endList = new ArrayList<NaviLatLng>();

    /**
     * 导航对象(单例)
     */
    private AMapNavi mAMapNavi;


    private void initMaps(Bundle savedInstanceState) {

        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map_set_rout);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);


        mAmap = mMapView.getMap();

        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle
        // .LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。
        // （1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。

        //连续定位、蓝点不会移动到地图中心点，定位点依照设备方向旋转，并且蓝点会跟随设备移动。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        mAmap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        //aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
        mAmap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。

        mAmap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //控制选起点
                if (mapClickStartReady) {
                    startLatlng = new NaviLatLng(latLng.latitude, latLng.longitude);
                    mStartMarker.setPosition(latLng);
                    startList.clear();
                    startList.add(startLatlng);
                    mapClickStartReady = false;
                }
                //控制选终点
                if (mapClickEndReady) {
                    endLatlng = new NaviLatLng(latLng.latitude, latLng.longitude);
                    mEndMarker.setPosition(latLng);
                    endList.clear();
                    endList.add(endLatlng);
                    mapClickEndReady = false;
                }

            }
        });
        // 初始化Marker添加到地图
        mStartMarker = mAmap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(), R.drawable.start))));
        mEndMarker = mAmap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(), R.drawable.end))));

        mAMapNavi = AMapNavi.getInstance(getApplicationContext());
        mAMapNavi.addAMapNaviListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();

        // TODO: 2017/8/9 0009  if is stop in onstop methord
        mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
        mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。

        ed_start_point.removeTextChangedListener(this);
        ed_pass_point_1.removeTextChangedListener(this);
        ed_pass_point_2.removeTextChangedListener(this);
        ed_pass_point_3.removeTextChangedListener(this);
        ed_end_point.removeTextChangedListener(this);


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

//        if (mSearchPos == mCurrentPos) {
//            return;
//        }
//        mSearchPos = mCurrentPos;

        if (s.toString().length() == 0) {

            mSearchResultList.clear();
            myAdapter.notifyDataSetChanged();
            list_view_poi.setVisibility(View.GONE);
            return;
        }


        //在这里处理用户输入的信息

        // TODO: 2017/8/9 0009  这里 默认设置北京地区的结果集合
        //第二个参数传入null或者“”代表在全国进行检索，否则按照传入的city进行检索
        if (mInputQuery == null) {

            mInputQuery = new InputtipsQuery(s.toString(), "010");
            mInputQuery.setCityLimit(true);//限制在当前城市
            mInputTips = new Inputtips(this, mInputQuery);
            mInputTips.setInputtipsListener(this);
        } else {
            mInputQuery = new InputtipsQuery(s.toString(), "010");
            mInputTips.setQuery(mInputQuery);
        }

        mInputTips.requestInputtipsAsyn();


    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (mCurrentPos == -1) {
            return;
        }

        switch (mCurrentPos) {
            case 0:
                ed_start_point.setText(mSearchResultList.get(position).getName());
                startPoint = mSearchResultList.get(position).getPoint();
                break;
            case 1:
                ed_pass_point_1.setText(mSearchResultList.get(position).getName());
                break;
            case 2:
                ed_pass_point_2.setText(mSearchResultList.get(position).getName());
                break;
            case 3:
                ed_pass_point_3.setText(mSearchResultList.get(position).getName());
                break;
            case 4:
                endPoint = mSearchResultList.get(position).getPoint();
                ed_end_point.setText(mSearchResultList.get(position).getName());
                break;

        }

        //mSearchPos = -1;
        mCurrentPos = -1;

        list_view_poi.setVisibility(View.GONE);

        setRoutSearch();

    }

    private void setRoutSearch() {

        if (startPoint == null && endPoint == null) {
            return;
        }
        //查询路径
        //判断是否进行路径规划
        // fromAndTo包含路径规划的起点和终点，drivingMode表示驾车模式
        // 第三个参数表示途经点（最多支持16个），第四个参数表示避让区域（最多支持32个），第五个参数表示避让道路
        //通过 DriveRouteQuery(RouteSearch.FromAndTo fromAndTo, int mode, List<LatLonPoint> passedByPoints,
        // List<List<LatLonPoint>> avoidpolygons, String avoidRoad) 设置搜索条件，方法对应的参数说明如下：
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(startPoint, endPoint);

        int drivingMode = RouteSearch.DRIVING_MULTI_CHOICE_AVOID_CONGESTION;
        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, drivingMode, null, null, "");

    }

    @Override
    public void onGetInputtips(List<Tip> list, int i) {

        mSearchResultList.clear();
        mSearchResultList.addAll(list);
        if (list != null && list.size() > 0) {

            list_view_poi.setVisibility(View.VISIBLE);
        } else {
            list_view_poi.setVisibility(View.GONE);

        }
        myAdapter.notifyDataSetChanged();

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.img_back:
                finish();
                break;
            case R.id.iv_clear_start:
                ed_start_point.setText("");
                break;
            case R.id.iv_clear_point_1:
                ed_pass_point_1.setText("");
                break;
            case R.id.iv_clear_point_2:
                ed_pass_point_2.setText("");
                break;
            case R.id.iv_clear_point_3:
                ed_pass_point_3.setText("");
                break;
            case R.id.iv_clear_end:
                ed_end_point.setText("");
                break;
            case R.id.btn_start_search:
                startSearch();
                break;
        }
    }

    private void startSearch() {
        clearRoute();

    }

    /**
     * 保存当前算好的路线
     */
    private SparseArray<RouteOverLay> routeOverlays = new SparseArray<RouteOverLay>();


    /**
     * 清除当前地图上算好的路线
     */
    private void clearRoute() {
        for (int i = 0; i < routeOverlays.size(); i++) {
            RouteOverLay routeOverlay = routeOverlays.valueAt(i);
            routeOverlay.removeFromMap();
        }
        routeOverlays.clear();
    }

    /**
     * 路线计算成功标志位
     */
    private boolean calculateSuccess = false;

    /**
     * 算路线
     */
    private void drawRoutes(int routeId, AMapNaviPath path) {
        calculateSuccess = true;
        mAmap.moveCamera(CameraUpdateFactory.changeTilt(0));
        RouteOverLay routeOverLay = new RouteOverLay(mAmap, path, this);
        routeOverLay.setTrafficLine(false);
        routeOverLay.addToMap();
        routeOverlays.put(routeId, routeOverLay);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {


        if (event.getAction() == MotionEvent.ACTION_DOWN) {

            switch (v.getId()) {
                case R.id.ed_start_point:
                    mCurrentPos = 0;
                    break;
                case R.id.ed_pass_point_1:
                    mCurrentPos = 1;

                    break;
                case R.id.ed_pass_point_2:
                    mCurrentPos = 2;
                    break;
                case R.id.ed_pass_point_3:
                    mCurrentPos = 3;
                    break;
                case R.id.ed_end_point:
                    mCurrentPos = 4;
                    break;
            }
        }

        return false;
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {


    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {

        //清空上次计算的路径列表。
        routeOverlays.clear();
        HashMap<Integer, AMapNaviPath> paths = mAMapNavi.getNaviPaths();
        for (int i = 0; i < ints.length; i++) {
            AMapNaviPath path = paths.get(ints[i]);
            if (path != null) {
                drawRoutes(ints[i], path);
            }
        }

    }

    @Override
    public void onCalculateRouteFailure(int i) {

        calculateSuccess = false;
        Toast.makeText(getApplicationContext(), "计算路线失败，errorcode＝" + i, Toast.LENGTH_SHORT).show();
    }


    /**
     * **************************************************
     * 在算路页面，以下接口全不需要处理，在以后的版本中我们会进行优化***********************************************************************************************
     **/
    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onInitNaviSuccess() {

    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onTrafficStatusUpdate() {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onGetNavigationText(int i, String s) {

    }

    @Override
    public void onGetNavigationText(String s) {

    }

    @Override
    public void onEndEmulatorNavi() {

    }

    @Override
    public void onArriveDestination() {

    }


    @Override
    public void onReCalculateRouteForYaw() {

    }

    @Override
    public void onReCalculateRouteForTrafficJam() {

    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onGpsOpenStatus(boolean b) {

    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {

    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {

    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {

    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {

    }

    @Override
    public void hideCross() {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void hideLaneInfo() {

    }


    @Override
    public void notifyParallelRoad(int i) {

    }


    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

    }

    @Override
    public void onPlayRing(int i) {

    }


    public class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mSearchResultList.size();
        }

        @Override
        public Object getItem(int position) {
            return mSearchResultList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
                convertView = View.inflate(SetRouteActivity.this, R.layout.searth_poi_item, null);
                holder = new Holder();
                holder.tv_file_poi_item = (TextView) convertView.findViewById(R.id.tv_file_poi_item);
                holder.tv_file_name_item = (TextView) convertView.findViewById(R.id.tv_file_name_item);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            //name:点卯海鲜 district:北京市西城区 adcode:110102
            holder.tv_file_name_item.setText(mSearchResultList.get(position).getName());
            holder.tv_file_poi_item.setText(mSearchResultList.get(position).getDistrict());
//            holder.tv_file_name_item.setText();

            return convertView;
        }


    }

    private static class Holder {
        TextView tv_file_poi_item;
        TextView tv_file_name_item;
    }
}
