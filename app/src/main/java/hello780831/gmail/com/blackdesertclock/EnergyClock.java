package hello780831.gmail.com.blackdesertclock;

import java.util.Date;

/**
 * Created by kami on 2018/3/5.
 */

public class EnergyClock {
    private Date targetTime;
    private boolean tree;
    private boolean bell;
    private boolean start;
    private int now ;
    private int target;

    public void setStart(boolean start) {
        this.start = start;
    }

    public boolean isStart() {

        return start;
    }

    public EnergyClock(Date targetTime, boolean tree, boolean bell, boolean start, int now, int target) {

        this.targetTime = targetTime;
        this.tree = tree;
        this.bell = bell;
        this.start = start;
        this.now = now;
        this.target = target;
    }

    public Date getTargetTime() {
        return targetTime;
    }

    public boolean isTree() {
        return tree;
    }

    public boolean isBell() {
        return bell;
    }

    public int getNow() {
        return now;
    }

    public int getTarget() {
        return target;
    }

    public void setTargetTime(Date targetTime) {

        this.targetTime = targetTime;
    }

    public void setTree(boolean tree) {
        this.tree = tree;
    }

    public void setBell(boolean bell) {
        this.bell = bell;
    }

    public void setNow(int now) {
        this.now = now;
    }

    public void setTarget(int target) {
        this.target = target;
    }
}
