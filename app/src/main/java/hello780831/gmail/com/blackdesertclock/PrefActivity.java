package hello780831.gmail.com.blackdesertclock;

import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import hello780831.gmail.com.blackdesertclock.Preference.DontAlarmTimeSetDialog;

/**
 * Created by kami on 2018/3/14.
 */

public class PrefActivity extends PreferenceActivity {
    private SharedPreferences preferences;
    private SharedPreferences edit;
    private Preference ringtone;
    private Preference dontAlarmSwitch;
    private Preference dontAlarmTime;
    private Preference dontAlarmDate;
    private DontAlarmTimeSetDialog dialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        edit = getSharedPreferences(Application.PREFERENCE_NEAE,MODE_PRIVATE);
        ringtone = findPreference(Application.PREFERENCE_KEY_RINGTONE);
        dontAlarmSwitch = findPreference(Application.PREFERENCE_KEY_DONTALARMSWITCH);
        dontAlarmTime =findPreference(Application.PREFERENCE_KEY_DONTALARMTIME);
        dontAlarmDate = findPreference(Application.PREFERENCE_KEY_DONTALARMDATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        dontAlarmTime.setSummary(edit.getString(Application.PREFERENCE_TIMESUMMERY,"點選設定區間(預設全天 0點>24點)"));
        dontAlarmDate.setSummary(edit.getString(Application.PREFERENCE_DATESUMMERY,"點選設定免打擾日期(預設每天)"));
        Uri ringToneUri = Uri.parse(preferences.getString(Application.PREFERENCE_KEY_RINGTONE,"default"));
        String ringName = RingtoneManager.getRingtone(this,ringToneUri).getTitle(this);
        ringtone.setSummary(ringName);
        dontAlarmTime.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(final Preference preference) {
                dialog = new DontAlarmTimeSetDialog(PrefActivity.this,Application.TYPE_TIME);
                dialog.setCancelable(false);
                dialog.setOnYesClickListener(new DontAlarmTimeSetDialog.OnYesClickListener() {
                    @Override
                    public void onClick(int from, int to) {
                        edit.edit().putInt("from",from).putInt("to",to).commit();
                        edit.edit().putString(Application.PREFERENCE_TIMESUMMERY,"區間從: "+edit.getInt(Application.PREFERENCE_FROM,0)+
                                "點到:"+edit.getInt(Application.PREFERENCE_TO,0)+"點").commit();
                        dialog.dismiss();
                        dontAlarmTime.setSummary("區間從: "+edit.getInt(Application.PREFERENCE_FROM,0)+"點到:"+edit.getInt(Application.PREFERENCE_TO,0)+"點");

                    }

                    @Override
                    public void onClick(boolean[] date) {}
                });
                dialog.setOnNoClickListener(new DontAlarmTimeSetDialog.OnNoClickListener() {
                    @Override
                    public void onClick() {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                return false;
            }
        });
        dontAlarmDate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(final Preference preference) {
                dialog = new DontAlarmTimeSetDialog(PrefActivity.this,Application.TYPE_DATE);
                dialog.setCancelable(false);
                dialog.setOnYesClickListener(new DontAlarmTimeSetDialog.OnYesClickListener() {
                    @Override
                    public void onClick(int from, int to) {}

                    @Override
                    public void onClick(boolean[] date) {
                        String[] dateString ={"日","一","二","三","四","五","六"};
                        String dateSummery = "免打擾日期：";
                        for (int i = 0 ; i< date.length;i++){
                            edit.edit().putBoolean(Application.PREFERENCE_DAY+i,date[i]).commit();
                            if (date[i]){
                                dateSummery += dateString[i];
                            }
                        }

                        edit.edit().putString(Application.PREFERENCE_DATESUMMERY,dateSummery).commit();
                        dialog.dismiss();
                        dontAlarmDate.setSummary(dateSummery);
                    }
                });
                dialog.setOnNoClickListener(new DontAlarmTimeSetDialog.OnNoClickListener() {
                    @Override
                    public void onClick() {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                return false;
            }
        });

    }
}
