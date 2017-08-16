package com.master.rout.court.file;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.model.AMapNaviLocation;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, INaviInfoCallback {


    private Button btn_start_send;
    private Button btn_choose_files;

//    private AMapNaviView mAMapNaviView;
//
//    private AMapNavi mAMapNavi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


//        mAMapNaviView = (AMapNaviView) findViewById(R.id.navi_view);
//        mAMapNaviView.onCreate(savedInstanceState);
//        mAMapNaviView.setAMapNaviViewListener(this);
//
//
//        mAMapNavi = AMapNavi.getInstance(getApplicationContext());
//        mAMapNavi.addAMapNaviListener(this);


        btn_start_send = (Button) findViewById(R.id.btn_start_send);
        btn_start_send.setOnClickListener(this);

        btn_choose_files = (Button) findViewById(R.id.btn_choose_files);
        btn_choose_files.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_start_send:

                LatLng epoint = new LatLng(39.935039, 116.492446);
                Poi epoi = new Poi("乐视大厦", epoint, "");
                AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(), new AmapNaviParams(epoi),
                        MainActivity.this);
                break;

            case R.id.btn_choose_files:

                startActivity(new Intent(MainActivity.this, ChooseFileActivity.class));

                break;


        }


    }


    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onGetNavigationText(String s) {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onArriveDestination(boolean b) {

    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {

    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }

    @Override
    public void onStopSpeaking() {

    }
}
