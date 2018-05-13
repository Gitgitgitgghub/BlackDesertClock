package hello780831.gmail.com.blackdesertclock.Preference;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import hello780831.gmail.com.blackdesertclock.Application;
import hello780831.gmail.com.blackdesertclock.R;

/**
 * Created by kami on 2018/3/14.
 */

public class DontAlarmTimeSetDialog extends Dialog implements View.OnClickListener {
    //設定時段
    private Spinner from;
    private Spinner to;
    private TextView dailogMsg;
    private int fromValue;
    private int toValue;
    //設定星期
    private CheckBox checkBox1;
    private CheckBox checkBox2;
    private CheckBox checkBox3;
    private CheckBox checkBox4;
    private CheckBox checkBox5;
    private CheckBox checkBox6;
    private CheckBox checkBox7;
    //共用
    private int typeDateOrTime;
    private Button yes_btn;
    private Button no_btn;
    private Context context;
    private OnYesClickListener onYesClickListener;
    private OnNoClickListener onNoClickListener;
    String[] time = {"0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24"};

    public DontAlarmTimeSetDialog(@NonNull Context context,int typeDateOrTime) {
        super(context);
        this.context = context;
        this.typeDateOrTime = typeDateOrTime;

    }
    public interface OnYesClickListener{
        void onClick(int from, int to);
        void onClick(boolean[] date);
    }
    public interface OnNoClickListener{
        void onClick();
    }
    public void setOnYesClickListener(OnYesClickListener onYesClickListener){
        this.onYesClickListener = onYesClickListener;
    }
    public void setOnNoClickListener(OnNoClickListener onNoClickListener){
        this.onNoClickListener = onNoClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (typeDateOrTime == Application.TYPE_TIME){
            typeTime();
        }else {
            typeDate();
        }
        yes_btn.setOnClickListener(this);
        no_btn.setOnClickListener(this);

    }
    private void typeDate(){
        setContentView(R.layout.dont_alarm_date_dialog_layout);
        setCanceledOnTouchOutside(false);
        yes_btn = findViewById(R.id.yes_btn_date);
        no_btn = findViewById(R.id.no_btn_date);
        checkBox1 = findViewById(R.id.checkBox1);
        checkBox2 = findViewById(R.id.checkBox2);
        checkBox3 = findViewById(R.id.checkBox3);
        checkBox4 = findViewById(R.id.checkBox4);
        checkBox5 = findViewById(R.id.checkBox5);
        checkBox6 = findViewById(R.id.checkBox6);
        checkBox7 = findViewById(R.id.checkBox7);
    }
    private void typeTime(){
        setContentView(R.layout.dont_alarm_dialog_layout);
        setCanceledOnTouchOutside(false);
        yes_btn = findViewById(R.id.yes_btn);
        no_btn = findViewById(R.id.no_btn);
        from = findViewById(R.id.from_spinner);
        to = findViewById(R.id.to_spinner);
        dailogMsg = findViewById(R.id.dailogMsg_txt);
        dailogMsg.setText("請設定時段。全天設定0到24");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,time);
        from.setAdapter(arrayAdapter);
        to.setAdapter(arrayAdapter);
        from.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                fromValue = Integer.parseInt(time[i]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        to.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                toValue = Integer.parseInt(time[i]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    @Override
    public void onClick(View view) {
        if (typeDateOrTime == Application.TYPE_TIME){
            switch (view.getId()){
                case R.id.yes_btn:
                    if (toValue > fromValue){
                        onYesClickListener.onClick(fromValue,toValue);
                    }else {
                        dailogMsg.setText("注意：左邊數字不能大於或等於右邊");
                        return;
                    }
                    break;
                case R.id.no_btn:
                    onNoClickListener.onClick();
                    break;
            }
        }else {
            switch (view.getId()){
                case R.id.yes_btn_date:
                    boolean[] date = {checkBox1.isChecked(),checkBox2.isChecked(),checkBox3.isChecked(),checkBox4.isChecked(),
                            checkBox5.isChecked(),checkBox6.isChecked(),checkBox7.isChecked()};
                    onYesClickListener.onClick(date);
                    break;
                case R.id.no_btn_date:
                    onNoClickListener.onClick();
                    break;
            }

        }

    }
}
