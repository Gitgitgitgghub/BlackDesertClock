package hello780831.gmail.com.blackdesertclock;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by kami on 2018/3/3.
 */

public class EnergyFragment extends Fragment implements View.OnClickListener {
    private Button hide_btn;
    private TextView targetTime_txt;
    private EditText now_edit;
    private EditText target_edit;
    private CheckBox tree_cb;
    private CheckBox bell_cb;
    private Button check_btn;
    private ToggleButton setAlarm_btn;
    private Calendar alarmTime = null;
    private Intent intent;
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
    private DataBase dataBase;
    private EnergyClock energyClock;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.energy_layout,null,false);
        hide_btn = view.findViewById(R.id.hide_btn);
        targetTime_txt = view.findViewById(R.id.targetTime_txt);
        now_edit = view.findViewById(R.id.now_edit);
        target_edit = view.findViewById(R.id.target_edit);
        tree_cb = view.findViewById(R.id.tree_cb);
        bell_cb = view.findViewById(R.id.bell_cb);
        check_btn = view.findViewById(R.id.check_btn);
        setAlarm_btn = view.findViewById(R.id.setAlarm_btn);
        dataBase = DataBase.getDataBase(getActivity());
        check_btn.setOnClickListener(this);
        setAlarm_btn.setOnClickListener(this);
        hide_btn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onResume() { //設定 各個元件的初始狀態
        super.onResume();
        energyClock = dataBase.queryEnergyClock();
            if (energyClock.getTargetTime() == null) {
                targetTime_txt.setText("目標時間：--時--分");
            }else {
                targetTime_txt.setText("目標時間："+energyClock.getTargetTime().getHours()+"時"+energyClock.getTargetTime().getMinutes()+"分");
            }
            now_edit.setText(energyClock.getNow()+"");
            if (energyClock.getTarget() == 0){
                target_edit.setHint("請輸入數值");
            }else {
                target_edit.setText(energyClock.getTarget()+"");
            }
            tree_cb.setChecked(energyClock.isTree());
            bell_cb.setChecked(energyClock.isBell());
            setAlarm_btn.setChecked(energyClock.isStart());
            setEnable(energyClock.isStart());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.hide_btn:
                Log.d("kk", "onClick: ");
                getFragmentManager().beginTransaction().hide(this).commit();
                break;
            case R.id.check_btn:
                if (target_edit.getText().length() == 0 || Integer.parseInt(target_edit.getText().toString()) == 0){
                    Toast.makeText(getActivity(),"目標能量不可為空或 0",Toast.LENGTH_SHORT).show();
                }else if (Integer.parseInt(target_edit.getText().toString()) <= Integer.parseInt(now_edit.getText().toString())){
                    Toast.makeText(getActivity(),"目標能量不可小於等於現在能量",Toast.LENGTH_SHORT).show();
                }else {
                    getAlarmTime();
                }
                break;
            case R.id.setAlarm_btn:
                if (setAlarm_btn.isChecked()){
                    if (energyClock.getTargetTime() == null){
                        Toast.makeText(getActivity(),"請先計算時間",Toast.LENGTH_SHORT).show();
                        setAlarm_btn.setChecked(false);
                    }else if (energyClock.getTargetTime().getTime() <= Calendar.getInstance().getTimeInMillis()){
                        Toast.makeText(getActivity(),"時間已過期，請重新計算",Toast.LENGTH_SHORT).show();
                        targetTime_txt.setText("目標時間：--時--分");
                        energyClock.setTargetTime(null);
                        setAlarm_btn.setChecked(false);
                    }else {
                        setAlarmTime();
                    }
                    setEnable(setAlarm_btn.isChecked());
                }else {
                    setAlarmTime();
                    setEnable(setAlarm_btn.isChecked());
                }

                break;
        }
    }
    private void getAlarmTime(){
        Double x = 1d;
        if (tree_cb.isChecked()){
            x+=2;
        }
        if (bell_cb.isChecked()){
            x +=1;
        }
        int energy = Integer.parseInt(target_edit.getText().toString()) - Integer.parseInt(now_edit.getText().toString());
        Double count = energy / x ;
        int time = (int)Math.ceil(count)*3;
        Log.d("KK", "getAlarmTime: "+time);
        if (time == 0 ||time%3 != 0){
            time += 3;
        }
        alarmTime = Calendar.getInstance();
        alarmTime.add(Calendar.MINUTE,(int)time);
        energyClock.setTargetTime(alarmTime.getTime());
        targetTime_txt.setText("目標時間："+alarmTime.get(Calendar.HOUR)+"時"+alarmTime.get(Calendar.MINUTE)+"分");
    }
    private void setAlarmTime(){
        intent = new Intent(getActivity(),AlarmActivity.class);
        intent.addCategory("energy");
        intent.putExtra("msg","能量已到達指定目標囉!");
        intent.putExtra("clockType",Application.TYPE_ENERGY_CLOCK);
        pendingIntent = PendingIntent.getActivity(getActivity(),30678,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager = (AlarmManager) getActivity().getSystemService(getActivity().ALARM_SERVICE);
        if (setAlarm_btn.isChecked()){
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,energyClock.getTargetTime().getTime(),pendingIntent);
            Log.d("kk", "能量鬧鐘設定成功");
            setEnable(setAlarm_btn.isChecked());
        }else {
            alarmManager.cancel(pendingIntent);
            intent = null; pendingIntent = null; alarmManager = null;
            setEnable(setAlarm_btn.isChecked());
            Log.d("kk", "能量鬧鐘設定取消 ");
        }
        saveEnergyClock();
    }
    private void saveEnergyClock(){
        if (alarmTime != null){
            Date date = new Date();
            date.setTime(alarmTime.getTimeInMillis());
            energyClock.setTargetTime(date);
        }
        energyClock.setNow(Integer.parseInt(now_edit.getText().toString()));
        energyClock.setTarget(Integer.parseInt(target_edit.getText().toString()));
        energyClock.setTree(tree_cb.isChecked());
        energyClock.setBell(bell_cb.isChecked());
        energyClock.setStart(setAlarm_btn.isChecked());
        dataBase.saveEnergyClock(energyClock);
    }
    private void setEnable(boolean isStart){
        int color;
        if (!isStart){
            color = getActivity().getResources().getColor(R.color.white);
        }else {
            color = getActivity().getResources().getColor(R.color.gray);
        }
        tree_cb.setEnabled(!isStart);bell_cb.setEnabled(!isStart);
        target_edit.setEnabled(!isStart); target_edit.setTextColor(color);
        now_edit.setEnabled(!isStart);now_edit.setTextColor(color);
        check_btn.setEnabled(!isStart);check_btn.setTextColor(color);
    }

}
