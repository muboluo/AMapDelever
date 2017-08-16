package com.master.rout.court.file;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索
 */
public class SearchActivity extends AppCompatActivity implements View.OnClickListener, AdapterView
        .OnItemClickListener, TextWatcher, Inputtips.InputtipsListener {

    private static final String TAG = "SearchActivity";
    private ImageView img_back;
    private EditText edit_query_point;
    private ListView list_view_poi;

    private MyAdapter myAdapter;
    private InputtipsQuery mInputQuery;//查询poi结果
    private Inputtips mInputTips;//输入建议
    private ArrayList<Tip> mSearchResultList = new ArrayList<>();
    public static final String SEARCH_RESULT = "search_result";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_poi);

        img_back = (ImageView) findViewById(R.id.img_back);
        img_back.setOnClickListener(this);
        edit_query_point = (EditText) findViewById(R.id.edit_query_point);

        edit_query_point.addTextChangedListener(this);

        list_view_poi = (ListView) findViewById(R.id.list_view_poi);

        myAdapter = new MyAdapter();
        list_view_poi.setAdapter(myAdapter);

        list_view_poi.setOnItemClickListener(this);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.img_back:
                finish();

                break;

        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        Intent data = new Intent();

        data.putExtra(SEARCH_RESULT, mSearchResultList.get(position));
        setResult(RESULT_OK, data);
        finish();

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        //在这里处理用户输入的信息

        // TODO: 2017/8/9 0009  这里 默认设置北京地区的结果集合
        //第二个参数传入null或者“”代表在全国进行检索，否则按照传入的city进行检索
        if (mInputQuery == null) {

            mInputQuery = new InputtipsQuery(s.toString(), "010");
            mInputQuery.setCityLimit(true);//限制在当前城市
            mInputTips = new Inputtips(SearchActivity.this, mInputQuery);
            mInputTips.setInputtipsListener(this);
        } else {
            mInputQuery.setType(s.toString());
        }

        mInputTips.requestInputtipsAsyn();


    }

    @Override
    protected void onPause() {
        edit_query_point.removeTextChangedListener(this);
        super.onPause();
    }

    @Override
    public void onGetInputtips(List<Tip> list, int i) {

        mSearchResultList.removeAll(null);
        mSearchResultList.addAll(list);
        myAdapter.notifyDataSetChanged();
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
                convertView = View.inflate(SearchActivity.this, R.layout.searth_poi_item, null);
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
