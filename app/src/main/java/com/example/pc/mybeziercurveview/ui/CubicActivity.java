package com.example.pc.mybeziercurveview.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioGroup;

import com.example.pc.mybeziercurveview.view.MyBezierCurveCubic;
import com.example.pc.mybeziercurveview.R;

/**
 * 三阶
 * Created by Deeson on 2016/7/12.
 */
public class CubicActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    MyBezierCurveCubic bezierCurveCubic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cubic);
        bezierCurveCubic = (MyBezierCurveCubic) findViewById(R.id.bezier_curve_cubic);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        radioGroup.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.button1:
                bezierCurveCubic.setControl(MyBezierCurveCubic.CONTROL_ONE);
                break;
            case R.id.button2:
                bezierCurveCubic.setControl(MyBezierCurveCubic.CONTROL_TWO);
                break;
        }
    }
}
