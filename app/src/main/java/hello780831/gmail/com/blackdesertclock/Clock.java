package hello780831.gmail.com.blackdesertclock;

import android.util.Log;

import java.util.Date;

/**
 * Created by kami on 2018/2/24.
 */

public class Clock {
    int group;
    int item;
    boolean start;
    boolean repeat;
    int type;
    Date alarmTime;
    int hour;
    int minute;

    public Clock() {
    }

    public Clock(int group, int item, boolean start, boolean repeat, int type, Date alarmTime, int hour, int minute) {
        this.group = group;
        this.item = item;
        this.start = start;
        this.repeat = repeat;
        this.type = type;
        this.alarmTime = alarmTime;
        this.hour = hour;
        this.minute = minute;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public void setItem(int item) {
        this.item = item;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setAlarmTime(Date alarmTime) {
        this.alarmTime = alarmTime;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getGroup() {
        return group;
    }

    public int getItem() {
        return item;
    }

    public boolean isStart() {
        return start;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public int getType() {
        return type;
    }

    public Date getAlarmTime() {
        return alarmTime;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }
}
