package hello780831.gmail.com.blackdesertclock;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReBootReceiver extends BroadcastReceiver {
    private Context context;
    private DataBase dataBase;
    private List<ArrayList<Clock>> childList;
    private ArrayList<Clock> alarmTimeList;
    private ArrayList<String[]> typeList;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        if (intent.getAction() != null &&intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
            Log.d("kk", "重新註冊鬧鐘");
            dataBase = DataBase.getDataBase(context);
            childList = dataBase.queryAll();
            typeList = getTypeList(context);
            for (int i = 0;i <childList.size();i++){
                ArrayList<Clock> dataList = childList.get(i);
                for (int k = 0;k <dataList.size();k++){
                    Clock clock = dataList.get(k);
                    if (clock.isStart()){
                        if (clock.getAlarmTime().getTime() < Calendar.getInstance().getTimeInMillis()){
                            clock.setStart(false);
                            clock.setRepeat(false);
                        }else {
                            reSetAlarmTime(clock);
                        }
                    }
                }
            }

        }
    }
    private void reSetAlarmTime(Clock clock){
        Intent intent = new Intent(context,AlarmActivity.class);
        intent.addCategory("第"+(clock.getGroup()+1)+"組"+"第"+(clock.getItem()+1)+"個");
        intent.putExtra("group",clock.getGroup());
        intent.putExtra("item",clock.getItem());
        intent.putExtra("msg",typeList.get(clock.getGroup())[clock.getType()]);
        intent.putExtra("clockType",Application.TYPE_NORMAL_CLOCK);
        Calendar calendar =Calendar.getInstance();
        calendar.setTime(clock.getAlarmTime());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        intent.putExtra("hour",hour);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,30678,intent,PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,clock.getAlarmTime().getTime(),pendingIntent);
        Log.d("KK", "第"+(clock.getGroup()+1)+"組"+"第"+(clock.getItem()+1)+"個"+"設置");
    }
    private ArrayList<String[]> getTypeList(Context context){
        typeList = new ArrayList<>();
        typeList.add(context.getResources().getStringArray(R.array.type_group1));
        typeList.add(context.getResources().getStringArray(R.array.type_group2));
        typeList.add(context.getResources().getStringArray(R.array.type_group3));
        typeList.add(context.getResources().getStringArray(R.array.type_group4));
        return typeList;
    }
}
