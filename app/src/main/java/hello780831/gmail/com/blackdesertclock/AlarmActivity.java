package hello780831.gmail.com.blackdesertclock;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AlarmActivity extends AppCompatActivity implements View.OnClickListener{
    private List<ArrayList<Clock>> childList;
    private MediaPlayer mediaPlayer = null;
    private Intent intent;
    private Clock clock;
    private DataBase dataBase;
    private Button posi_btn;
    private Button nega_btn;
    private Button neut_btn;
    private TextView msg_txt;
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
    private EnergyClock energyClock = null;
    private Vibrator vibrator;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private SharedPreferences preferences;
    private int clockType = -1;
    private boolean dontAlarm;
    private int intDayOfWeek;
    private boolean[] date = {true,true,true,true,true,true,true};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.SCREEN_DIM_WAKE_LOCK,"bring");
        setContentView(R.layout.activity_alarm);
        initView();
        wakeLock.acquire();
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(new long[]{1000,1000},0);
        dontAlarm = preferences.getBoolean(Application.PREFERENCE_KEY_DONTALARMSWITCH,false);
        clockType = intent.getIntExtra("clockType",Application.TYPE_NORMAL_CLOCK);
        if (dontAlarm && !canPlayRing()){
            return;
        }else {
            playRing();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        msg_txt.setText(intent.getStringExtra("msg"));
        posi_btn.setOnClickListener(this);
        nega_btn.setOnClickListener(this);
        neut_btn.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (wakeLock != null){
            wakeLock.release();
            wakeLock = null;
        }
    }

    private void initView(){
        nega_btn = findViewById(R.id.nega_button);
        posi_btn = findViewById(R.id.posi_btn);
        neut_btn = findViewById(R.id.close_ring_btn);
        msg_txt = findViewById(R.id.msg_txt);
        intent = getIntent();
        dataBase = DataBase.getDataBase(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
    }
    private boolean canPlayRing(){
        SharedPreferences sharedPreferences = getSharedPreferences(Application.PREFERENCE_NEAE,MODE_PRIVATE);
        intDayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-1;
        int hour = intent.getIntExtra("hour",25);
        for (int i = 0 ;i< date.length ;i++){
            date[i] = sharedPreferences.getBoolean(Application.PREFERENCE_DAY+i,true);
        }
        if (date[intDayOfWeek] && hour>= sharedPreferences.getInt(Application.PREFERENCE_FROM,0)
                && hour < sharedPreferences.getInt(Application.PREFERENCE_TO,24)){
            return false;
        }else {
            return true;
        }
    }
    private void playRing(){
        String ringStr = preferences.getString(Application.PREFERENCE_KEY_RINGTONE,"default");
        Log.d("kk", "playRing: "+ringStr);
        if (ringStr.equals("default") || ringStr == null){
            mediaPlayer = MediaPlayer.create(this, RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM));
        }else {
            Uri uri = Uri.parse(ringStr);
            mediaPlayer = MediaPlayer.create(this,uri);
        }
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.posi_btn:
                if (clockType == Application.TYPE_ENERGY_CLOCK){
                    energyClock = dataBase.queryEnergyClock();
                    Toast.makeText(AlarmActivity.this,"鬧鐘已關閉",Toast.LENGTH_SHORT).show();
                    energyClock.setStart(false);
                    energyClock.setTargetTime(null);
                    dataBase.saveEnergyClock(energyClock);
                }else {
                    childList = dataBase.queryAll();
                    clock = childList.get(intent.getIntExtra("group",0)).get(intent.getIntExtra("item",0));
                    if (clock.isRepeat()){
                        Calendar calendar = Calendar.getInstance();
                        if (intent.getIntExtra("group",0) == 3){
                            calendar.setTime(clock.getAlarmTime());
                            Log.d("kk", "onClick: "+"實際時間");
                        }
                        calendar.add(Calendar.HOUR,clock.getHour());
                        calendar.add(Calendar.MINUTE,clock.getMinute());
                        clock.setAlarmTime(calendar.getTime());
                        pendingIntent = PendingIntent.getActivity(AlarmActivity.this,30678,intent,PendingIntent.FLAG_CANCEL_CURRENT);
                        alarmManager = (AlarmManager) AlarmActivity.this.getSystemService(ALARM_SERVICE);
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP,clock.getAlarmTime().getTime(),pendingIntent);
                    }else {
                        clock.setAlarmTime(null);
                        clock.setStart(false);
                    }
                    dataBase.saveData(childList);
                }
                if (mediaPlayer != null){
                    mediaPlayer.stop();
                }
                vibrator.cancel();
                this.finish();
                break;
            case R.id.nega_button:
                if (mediaPlayer != null){
                    mediaPlayer.stop();
                }
                pendingIntent = PendingIntent.getActivity(AlarmActivity.this,30678,intent,PendingIntent.FLAG_CANCEL_CURRENT);
                alarmManager = (AlarmManager) AlarmActivity.this.getSystemService(ALARM_SERVICE);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP,Calendar.getInstance().getTimeInMillis()+5*60*1000,pendingIntent);
                AlarmActivity.this.finish();
                break;
            case R.id.close_ring_btn:
                if (mediaPlayer != null){
                    mediaPlayer.stop();
                }
                break;
        }

    }
}
