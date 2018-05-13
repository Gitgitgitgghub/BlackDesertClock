package hello780831.gmail.com.blackdesertclock;



import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CompoundButton;

import android.widget.ExpandableListView;
import android.widget.ImageView;

import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;


import java.util.ArrayList;
import java.util.Calendar;

import java.util.Date;
import java.util.List;




/**
 * Created by kami on 2018/2/20.
 */

public class MyAdapter extends BaseExpandableListAdapter {
    private final String TAG = "kk";
    private String[] groupList;
    private List<ArrayList<Clock>> childList;
    private Context context;
    private GroupHolder groupHolder = null;
    private ItemHolder itemHolder = null;
    private AlertDialog alertDialog;
    private DataBase dataBase;
    private ArrayList<String[]> typeList;
    private ArrayList<Integer[]> type_img;
    private String[] type;



    public MyAdapter(List<ArrayList<Clock>> childList) {
        this.childList = childList;
    }

    public MyAdapter(String[] groupList, List<ArrayList<Clock>> childList, ArrayList<String[]> typeList, ArrayList<Integer[]> type_img, Context context, DataBase dataBase) {
        this.groupList = groupList;
        this.childList = childList;
        this.context = context;
        this.dataBase = dataBase;
        this.typeList = typeList;
        this.type_img = type_img;
    }
    @Override
    public View getGroupView(final int i, boolean b, View view, ViewGroup viewGroup) {
        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.group_layout,null);
            groupHolder = new GroupHolder();
            groupHolder.textView = view.findViewById(R.id.group_txt);
            groupHolder.add_btn = view.findViewById(R.id.add_btn);
            groupHolder.indicator = view.findViewById(R.id.indicator_img);
            view.setTag(groupHolder);
        }else {
            groupHolder = (GroupHolder) view.getTag();
        }
        if (b) {
            groupHolder.add_btn.setVisibility(View.VISIBLE);
            groupHolder.indicator.setImageResource(R.drawable.open);
        }else {
            groupHolder.add_btn.setVisibility(View.INVISIBLE);
            groupHolder.indicator.setImageResource(R.drawable.close);
        }
        groupHolder.textView.setText(groupList[i]);
        groupHolder.add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (i){
                    case 0:
                        childList.get(i).add(new Clock(i,0,false,false,0,null,0,0));
                        break;
                    case 1:
                        childList.get(i).add(new Clock(i,0,false,false,0,null,0,0));
                        break;
                    case 2:
                        childList.get(i).add(new Clock(i,0,false,false,0,null,0,0));
                        break;
                    case 3:
                        childList.get(i).add(new Clock(i,0,false,false,0,null,0,0));
                        break;
                }
                notifyDataSetChanged();
            }
        });
        return view;
    }

    @Override
    public View getChildView(final int groupPosition, final int i1, final boolean b, View view, ViewGroup viewGroup) {
        view = initChildView(view,groupPosition);
        final Clock clock = childList.get(groupPosition).get(i1);
        clock.setGroup(groupPosition); //先記錄每個鬧鐘的位置
        clock.setItem(i1);
        type = typeList.get(groupPosition);
        Integer[] typeimg = type_img.get(groupPosition);
        itemHolder.type_txt.setText(type[clock.getType()]);
        itemHolder.type_img.setImageResource(typeimg[clock.getType()]);
        itemHolder.settime_txt.setText("間隔時間："+clock.getHour()+"時"+clock.getMinute()+"分");
        if (clock.getAlarmTime() != null){
            itemHolder.alarmTime_txt.setText("啟動時間："+clock.getAlarmTime().getHours()+"時"+clock.getAlarmTime().getMinutes()+"分");
        }
        if (clock.isStart()){
            itemHolder.del_button.setEnabled(false);
            itemHolder.init_btn.setEnabled(false);
            itemHolder.set_btn.setEnabled(false);
            itemHolder.del_button.setTextColor(context.getResources().getColor(R.color.gray));
            itemHolder.init_btn.setTextColor(context.getResources().getColor(R.color.gray));
            itemHolder.set_btn.setTextColor(context.getResources().getColor(R.color.gray));
            itemHolder.init_btn.setEnabled(false);
            itemHolder.set_btn.setEnabled(false);
        }else {
            itemHolder.del_button.setEnabled(true);
            itemHolder.init_btn.setEnabled(true);
            itemHolder.set_btn.setEnabled(true);
            itemHolder.del_button.setTextColor(context.getResources().getColor(R.color.white));
            itemHolder.init_btn.setTextColor(context.getResources().getColor(R.color.white));
            itemHolder.set_btn.setTextColor(context.getResources().getColor(R.color.white));
        }
        itemHolder.repeat_btn.setChecked(clock.isRepeat());
        itemHolder.start_btn.setChecked(clock.isStart());
        itemHolder.choose_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                alertDialog = new AlertDialog.Builder(context).setTitle("請選擇鬧鐘類型").setSingleChoiceItems(type,
                        clock.getType(), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                clock.setType(i);
                                notifyDataSetChanged();
                                alertDialog.dismiss();
                            }
                        }).show();
            }
        });
        itemHolder.del_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //刪除前先取消註冊鬧鐘
                clock.setStart(false);
                clock.setAlarmTime(null);
                setAlarm(clock,groupPosition,i1);
                childList.get(groupPosition).remove(i1);
                notifyDataSetChanged();
            }
        });
        itemHolder.init_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog pickerDialog = new TimePickerDialog(context,android.R.style.Theme_DeviceDefault_Dialog, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        Calendar tmp = Calendar.getInstance();
                        tmp.set(Calendar.HOUR_OF_DAY ,i);
                        tmp.set(Calendar.MINUTE,i1);
                        clock.setAlarmTime(tmp.getTime());
                        notifyDataSetChanged();
                    }
                },0,0,true);
                pickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取得現在時間", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       Calendar initTmp = Calendar.getInstance();
                        initTmp.add(Calendar.HOUR_OF_DAY,clock.getHour());
                        initTmp.add(Calendar.MINUTE,clock.getMinute());
                        clock.setAlarmTime(initTmp.getTime());
                        notifyDataSetChanged();

                    }
                });
                pickerDialog.show();
            }
        });
        itemHolder.set_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog pickerDialog = new TimePickerDialog(context,android.R.style.Theme_DeviceDefault_Dialog,new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        clock.setMinute(i1);
                        clock.setHour(i);
                        notifyDataSetChanged();
                    }
                },0,0,true);
                pickerDialog.show();
            }
        });
        itemHolder.repeat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clock.getHour() == 0 && clock.getMinute() == 0){
                    Toast.makeText(context,"沒有設定間隔時間不能使用反覆功能",Toast.LENGTH_SHORT).show();
                    clock.setRepeat(false);
                }else {
                    clock.setRepeat(!clock.isRepeat());
                }
                notifyDataSetChanged();
            }
        });
        itemHolder.start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clock.setStart(!clock.isStart());
                Calendar systemTime = Calendar.getInstance();
                systemTime.set(Calendar.SECOND,0);
                if (clock.getAlarmTime() == null){
                    Toast.makeText(context,"初始時間默認為現在時間",Toast.LENGTH_SHORT).show();
                    Calendar init = Calendar.getInstance();
                    init.add(Calendar.HOUR_OF_DAY,clock.getHour());
                    init.add(Calendar.MINUTE,clock.getMinute());
                    clock.setAlarmTime(init.getTime());
                }else if (clock.getAlarmTime().getTime() < systemTime.getTimeInMillis()){
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(clock.getAlarmTime());
                    calendar.add(Calendar.HOUR,24);
                    Log.d(TAG, "onClick: "+calendar.getTime());
                    clock.setAlarmTime(calendar.getTime());
                }
                setAlarm(clock,groupPosition,i1);
                notifyDataSetChanged();
            }
        });

        return view;
    }
    @Override
    public int getGroupCount() {
        return groupList.length;
    }

    @Override
    public int getChildrenCount(int i) {
        return childList.get(i).size();
    }

    @Override
    public Object getGroup(int i) {
        return groupList[i];
    }

    @Override
    public Object getChild(int i, int i1) {
        return childList.get(i).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
    private View initChildView(View view,int groupPosition){
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_layout, null);
            itemHolder = new ItemHolder();
            itemHolder.init_btn = view.findViewById(R.id.init_btn);
            itemHolder.alarmTime_txt = view.findViewById(R.id.alarmTime_txt);
            itemHolder.del_button = view.findViewById(R.id.del_button);
            itemHolder.set_btn = view.findViewById(R.id.set_btn);
            itemHolder.settime_txt = view.findViewById(R.id.settime_txt);
            itemHolder.repeat_btn = view.findViewById(R.id.repeat_btn);
            itemHolder.start_btn = view.findViewById(R.id.start_btn);
            itemHolder.type_img = view.findViewById(R.id.type_img);
            itemHolder.choose_btn = view.findViewById(R.id.choose_btn);
            itemHolder.type_txt = view.findViewById(R.id.type_txt);
            view.setTag(itemHolder);
        }else {
            itemHolder = (ItemHolder) view.getTag();
            itemHolder.alarmTime_txt.setText("啟動時間：--時--分");
        }
        return view;
    }
    private void setAlarm(Clock clock,int group ,int item){
        int hour = 25;
        if (clock.getAlarmTime() != null){
            Calendar calendar =Calendar.getInstance();
            calendar.setTime(clock.getAlarmTime());
            hour = calendar.get(Calendar.HOUR_OF_DAY);
        }
        Intent intent = new Intent(context,AlarmActivity.class);
        intent.addCategory("第"+(group+1)+"組"+"第"+(item+1)+"個");
        intent.putExtra("hour",hour);
        intent.putExtra("group",group);
        intent.putExtra("item",item);
        intent.putExtra("msg",type[clock.getType()]);
        intent.putExtra("clockType",Application.TYPE_NORMAL_CLOCK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,30678,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (clock.isStart()){
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,clock.getAlarmTime().getTime(),pendingIntent);
            Log.d(TAG, "第"+(group+1)+"組"+"第"+(item+1)+"個"+"設置");
        }else {
            Log.d(TAG, "第"+(group+1)+"組"+"第"+(item+1)+"個"+"取消");
            alarmManager.cancel(pendingIntent);
            intent = null; pendingIntent = null; alarmManager = null;
            clock.setAlarmTime(null);
            notifyDataSetChanged();
        }
        dataBase.saveData(childList);
    }
    private class GroupHolder{
        ImageView indicator;
        TextView textView;
        Button add_btn;
    }
    private class ItemHolder{
        Button init_btn;
        ImageView type_img;
        Button choose_btn;
        TextView type_txt;
        TextView alarmTime_txt;
        TextView settime_txt;
        Button del_button;
        Button set_btn;
        ToggleButton repeat_btn;
        ToggleButton start_btn;
    }
}

