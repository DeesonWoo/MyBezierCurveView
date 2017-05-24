package com.example.pc.mybeziercurveview.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.pc.mybeziercurveview.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView quad = (TextView) findViewById(R.id.quadratic);
        TextView quadratic_show = (TextView) findViewById(R.id.quadratic_show);
        TextView cubic = (TextView) findViewById(R.id.cubic);
        TextView cubic_show = (TextView) findViewById(R.id.cubic_show);
        TextView quartic_show = (TextView) findViewById(R.id.quartic_show);

        quad.setOnClickListener(this);
        quadratic_show.setOnClickListener(this);
        cubic.setOnClickListener(this);
        cubic_show.setOnClickListener(this);
        quartic_show.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){
            case R.id.quadratic:
                intent = new Intent(MainActivity.this,QuadraticActivity.class);
            break;
            case R.id.quadratic_show:
                intent = new Intent(MainActivity.this,QuadraticShowActivity.class);
            break;
            case R.id.cubic:
                intent = new Intent(MainActivity.this,CubicActivity.class);
            break;
            case R.id.cubic_show:
                intent = new Intent(MainActivity.this,CubicShowActivity.class);
            break;
            case R.id.quartic_show:
                intent = new Intent(MainActivity.this,QuarticActivity.class);
            break;
        }
        if(null!=intent){
            startActivity(intent);
        }
    }
}
