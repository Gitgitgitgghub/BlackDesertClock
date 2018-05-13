package hello780831.gmail.com.blackdesertclock;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceActivity;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private String TAG = "KK";
    private ExpandableListView expandableListView;
    private String[] groupList;
    private List<ArrayList<Clock>> childList;
    private ArrayList<String[]> typeList;
    private ArrayList<Integer[]> type_img;
    private DataBase dataBase;
    private Intent getGroup;
    private android.app.FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private Button goCal_btn;
    private Button setRing_btn;
    private EnergyFragment energyFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataBase = DataBase.getDataBase(MainActivity.this);
        expandableListView = findViewById(R.id.ExpandableListView);
        goCal_btn = findViewById(R.id.gocal_btn);
        goCal_btn.setOnClickListener(this);
        setRing_btn= findViewById(R.id.setring_btn);
        setRing_btn.setOnClickListener(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},999);
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initDataList();
        MyAdapter myAdapter = new MyAdapter(groupList,childList,typeList,type_img, MainActivity.this,dataBase);
        expandableListView.setAdapter(myAdapter);
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                if (expandableListView.isGroupExpanded(i)){
                    expandableListView.collapseGroup(i);
                }else {
                    expandableListView.expandGroup(i);
                }
                return true;
            }
        });
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int position) {
                for (int i = 0 ; i< expandableListView.getCount();i++){
                    if (i!=position){
                        expandableListView.collapseGroup(i);
                    }
                }
            }
        });
    }



    @Override
    protected void onPause() {
        super.onPause();
        dataBase.saveData(childList);
    }

    private void initDataList(){
        getGroup = getIntent();
        groupList = getResources().getStringArray(R.array.groupList);
        childList = dataBase.queryAll();
        typeList = new ArrayList<>();
        typeList.add(getResources().getStringArray(R.array.type_group1));
        typeList.add(getResources().getStringArray(R.array.type_group2));
        typeList.add(getResources().getStringArray(R.array.type_group3));
        typeList.add(getResources().getStringArray(R.array.type_group4));
        Integer[] imglist1 = {R.mipmap.boss_1,R.mipmap.boss_2,R.mipmap.boss_3,R.mipmap.boss_4,R.mipmap.othor};
        Integer[] imglist2 = {R.mipmap.jiagon,R.mipmap.cook,R.mipmap.worker,R.mipmap.farmer,R.mipmap.farmer,R.mipmap.othor};
        Integer[] imglist3 = {R.mipmap.weapon,R.mipmap.tree,R.mipmap.ring,R.mipmap.horse,R.mipmap.beer};
        Integer[] imglist4 = {R.mipmap.pagio,R.mipmap.pagio,R.mipmap.pagio,R.mipmap.nagon};
        type_img = new ArrayList<>();
        type_img.add(imglist1);
        type_img.add(imglist2);
        type_img.add(imglist3);
        type_img.add(imglist4);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.gocal_btn:
                fragmentManager = getFragmentManager();
                transaction = fragmentManager.beginTransaction();
                if (energyFragment == null){
                    Log.d(TAG, "new Fragment");
                    energyFragment = new EnergyFragment();
                    transaction.replace(R.id.main_content,energyFragment,"energyFragment");
                    transaction.commit();
                }else {
                    transaction.show(energyFragment).commit();
                }
                break;
            case R.id.setring_btn:
                startActivity(new Intent(this,PrefActivity.class));
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 999){
            if (grantResults[0] ==PackageManager.PERMISSION_GRANTED && grantResults[1] ==PackageManager.PERMISSION_GRANTED){
                return;
            }else {
                Toast.makeText(this,"無法取得權限",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
