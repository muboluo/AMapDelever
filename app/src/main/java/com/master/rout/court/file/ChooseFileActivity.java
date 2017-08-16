package com.master.rout.court.file;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.Toast;

/**
 * 设置文件界面
 */
public class ChooseFileActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup
        .OnCheckedChangeListener {

    private PopupWindow popupWindow; // 举报信息窗口
    private View popup_view; //pop up view
    private View root_view; //根 view
    private Button btn_choose_files_number;
    private Button btn_pre_step;
    private Button btn_next_step;

    private RadioGroup rg_file_priority;

    private int mFilesNums; //当前文件数量
    private boolean mFileHasPriority; //文件是否有优先级

    public static String FILE_NUMS = "file_nums";
    public static String FILE_HAS_PRIORITY = "file_has_priority";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_choose_file);

        root_view = findViewById(R.id.root_view);

        btn_choose_files_number = (Button) findViewById(R.id.btn_choose_files_number);
        btn_pre_step = (Button) findViewById(R.id.btn_pre_step);
        btn_next_step = (Button) findViewById(R.id.btn_next_step);
        btn_choose_files_number.setOnClickListener(this);
        btn_pre_step.setOnClickListener(this);
        btn_next_step.setOnClickListener(this);


        rg_file_priority = (RadioGroup) findViewById(R.id.rg_file_priority);
        rg_file_priority.setOnCheckedChangeListener(this);

        initPopUpWindow();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        popupWindow.setOnDismissListener(null);

    }

    /**
     * 初始化 选择文件列表的popupwindow
     */
    private void initPopUpWindow() {
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(Color.TRANSPARENT);
        popup_view = LayoutInflater.from(ChooseFileActivity.this).inflate(R.layout.menu_select_report_layout, null);

        popup_view.findViewById(R.id.cancel_btn).setOnClickListener(this);
        popup_view.findViewById(R.id.textview_file_no_1).setOnClickListener(this);
        popup_view.findViewById(R.id.textview_file_no_2).setOnClickListener(this);
        popup_view.findViewById(R.id.textview_file_no_3).setOnClickListener(this);
        popup_view.findViewById(R.id.textview_file_no_4).setOnClickListener(this);
        popupWindow = new PopupWindow(popup_view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                .WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(colorDrawable);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(R.style.popwin_anim_style);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_choose_files_number:

                popupWindow.showAtLocation(root_view, Gravity.BOTTOM, 0, 0);
                break;

            case R.id.cancel_btn:
                popupWindow.dismiss(); //取消举报内容
                break;


            case R.id.textview_file_no_1: //这块，要改成radiobutton，暂时先写成 textview。
                checkReport(1);
                break;
            case R.id.textview_file_no_2:
                checkReport(2);
                break;
            case R.id.textview_file_no_3:
                checkReport(3);
                break;
            case R.id.textview_file_no_4:
                checkReport(4);
                break;

            case R.id.btn_pre_step:
                finish();
                break;

            case R.id.btn_next_step:

                //跳转到具体路径规划界面。

                if (mFilesNums == 0) {
                    Toast.makeText(ChooseFileActivity.this, "请选择文件数量", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(ChooseFileActivity.this, SetRouteActivity.class);
                intent.putExtra(FILE_NUMS, mFilesNums);
                intent.putExtra(FILE_HAS_PRIORITY, mFileHasPriority);
                startActivity(intent);

                break;

        }


    }

    private void checkReport(int i) {

        mFilesNums = i;
        btn_choose_files_number.setText("当前文件数量为 " + i + " 件，点击可重新设置");
        popupWindow.dismiss();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        if (checkedId == R.id.rb_priority_negative) {

            mFileHasPriority = false;

        } else {

            mFileHasPriority = true;

        }
    }
}
