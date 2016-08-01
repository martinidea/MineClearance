package com.code.martin.mineclearance;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.code.martin.mineclearance.ui.BoxTable;
import com.code.martin.mineclearance.ui.LED;
import com.code.martin.mineclearance.impl.OnGameListener;
import com.code.martin.mineclearance.impl.TimeChangeListener;
import com.code.martin.mineclearance.util.TimerUtils;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends Activity implements OnGameListener, SharedPreferences.OnSharedPreferenceChangeListener {

    @BindView(R.id.add_button)
    ImageView addButton;
    @BindView(R.id.flagLED)
    LED flagLED;
    @BindView(R.id.face_button)
    Button faceButton;
    @BindView(R.id.timeLED)
    LED timeLED;
    @BindView(R.id.box_table)
    BoxTable boxTable;
    @BindString(R.string.second)
    String second;

    TimerUtils timerUtils;
    PopupMenu popupMenu;
    int time;

    AlertDialog difficultDialog;
    AlertDialog heroRollDialog;
    AlertDialog helpDialog;
    AlertDialog breakingRecordsDialog;


    final String SP_PRIMARY_TIME = "PRIMARY_TIME", SP_MIDDLE_TIME = "MIDDLE_TIME",
            SP_EXPERT_TIME = "EXPERT_TIME", SP_PRIMARY_HERO = "PRIMARY_HERO",
            SP_MIDDLE_HERO = "MIDDLE_HERO", SP_EXPERT_HERO = "EXPERT_HERO",
            SP_NAME = "HERO_ROLL";
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.horizontalScrollView)
    HorizontalScrollView horizontalScrollView;

    private boolean firstTimeResume = true;

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    private int primaryTime;
    private int middleTime;
    private int expertTime;
    private String primaryHero;
    private String middleHero;
    private String expertHero;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        ButterKnife.bind(this);
        boxTable.setOnGameListener(this);
        flagLED.setNum(boxTable.getRemainingFlag());
        timerUtils = new TimerUtils();
        timerUtils.setOnTimeChangeListener(new TimeChangeListener() {
            @Override
            public void timeChange(int num) {
                timeLED.setNum(num);
            }
        });
        difficultDialog = new AlertDialog.Builder(this).create();
        heroRollDialog = new AlertDialog.Builder(this).create();
        helpDialog = new AlertDialog.Builder(this).create();
        breakingRecordsDialog = new AlertDialog.Builder(this).create();
        sp = getSharedPreferences(SP_NAME, MODE_PRIVATE);
        sp.registerOnSharedPreferenceChangeListener(this);
        primaryTime = sp.getInt(SP_PRIMARY_TIME, 999);
        middleTime = sp.getInt(SP_MIDDLE_TIME, 999);
        expertTime = sp.getInt(SP_EXPERT_TIME, 999);
        primaryHero = sp.getString(SP_PRIMARY_HERO, "-----");
        middleHero = sp.getString(SP_MIDDLE_HERO, "-----");
        expertHero = sp.getString(SP_EXPERT_HERO, "-----");
    }

    @Override
    protected void onResume() {
        if (firstTimeResume) {
            firstTimeResume = false;
        } else {
            timerUtils.pause();
        }
        super.onResume();
    }

    @OnClick({R.id.face_button, R.id.add_button})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.face_button:
                restart();
                break;
            case R.id.add_button:
                showPopupMenu(view);
                break;
        }


    }

    private void showPopupMenu(View view) {
        popupMenu = new PopupMenu(this, view);
        getMenuInflater().inflate(R.menu.pop_menu, popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.difficult:
                        showDifficultDialog();
                        break;
                    case R.id.help:
                        showHelpDialog();
                        break;
                    case R.id.hero_roll:
                        showHeroRollDialog();
                        break;
                }
                return false;
            }
        });
    }


    private String time2String(int time) {
        if (time < 10)
            return "00" + time + second;
        if (time < 100)
            return "0" + time + second;
        if (time < 999)
            return "" + time + second;
        else
            return "----------";
    }


    private void restart() {
        horizontalScrollView.scrollTo(0,0);
        scrollView.scrollTo(0,0);
        boxTable.restart();
        faceButton.setBackgroundResource(R.drawable.face0);
        timerUtils.stop();
        timeLED.setNum(0);
    }

    @Override
    protected void onDestroy() {
        sp.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    private void showHelpDialog() {
        helpDialog.show();
        Window window = helpDialog.getWindow();
        window.setContentView(R.layout.dialog_help);
        TextView tv_sure = (TextView) window.findViewById(R.id.tv_sure);
        tv_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpDialog.dismiss();
            }
        });
    }

    private void showHeroRollDialog() {
        heroRollDialog.show();
        Window window = heroRollDialog.getWindow();
        window.setContentView(R.layout.dialog_hero_roll);
        TextView tv_primary = (TextView) window.findViewById(R.id.tv_primary);
        TextView tv_primary_name = (TextView) window.findViewById(R.id.tv_primary_name);
        TextView tv_middle = (TextView) window.findViewById(R.id.tv_middle);
        TextView tv_middle_name = (TextView) window.findViewById(R.id.tv_middle_name);
        TextView tv_expert = (TextView) window.findViewById(R.id.tv_expert);
        TextView tv_expert_name = (TextView) window.findViewById(R.id.tv_expert_name);
        TextView tv_sure = (TextView) window.findViewById(R.id.tv_sure);
        tv_primary.setText(time2String(primaryTime));
        tv_primary_name.setText(primaryHero);
        tv_middle.setText(time2String(middleTime));
        tv_middle_name.setText(middleHero);
        tv_expert.setText(time2String(expertTime));
        tv_expert_name.setText(expertHero);
        tv_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                heroRollDialog.dismiss();
            }
        });
    }

    private void showNameDialog(final String key1, final String key2) {
        breakingRecordsDialog.show();
        Window window = breakingRecordsDialog.getWindow();
        window.setContentView(R.layout.dialog_breaking_records);
        breakingRecordsDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        final EditText et_name = (EditText) window.findViewById(R.id.et_name);
        TextView tv_sure = (TextView) window.findViewById(R.id.tv_sure);
        tv_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor = sp.edit();
                name = et_name.getText().toString();
                editor.putInt(key1, time);
                editor.putString(key2, name);
                editor.commit();
                breakingRecordsDialog.dismiss();
            }
        });
    }

    private void showDifficultDialog() {
        difficultDialog.show();
        Window window = difficultDialog.getWindow();
        window.setContentView(R.layout.dialog_difficult);
        RadioGroup group = (RadioGroup) window.findViewById(R.id.radio_parent);
        RadioButton rb_primary = (RadioButton) window.findViewById(R.id.rb_primary);
        RadioButton rb_middle = (RadioButton) window.findViewById(R.id.rb_middle);
        RadioButton rb_expert = (RadioButton) window.findViewById(R.id.rb_expert);
        TextView tv_cancel = (TextView) window.findViewById(R.id.tv_cancel);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                difficultDialog.dismiss();
            }
        });
        switch (boxTable.getBoxTableMode()) {
            case 0:
                rb_primary.setChecked(true);
                break;
            case 1:
                rb_middle.setChecked(true);
                break;
            case 2:
                rb_expert.setChecked(true);
                break;
        }
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.rb_primary:
                        boxTable.setMode(0);
                        break;
                    case R.id.rb_middle:
                        boxTable.setMode(1);
                        break;
                    case R.id.rb_expert:
                        boxTable.setMode(2);
                        break;
                }
                restart();
                difficultDialog.dismiss();
            }
        });
    }


    @Override
    protected void onPause() {
        timerUtils.pause();
        super.onPause();
    }

    @Override
    public void gameStart() {
        timerUtils.start();
    }

    @Override
    public void gameFailure() {
        timerUtils.stop();
        faceButton.setBackgroundResource(R.drawable.face3);
    }

    @Override
    public void gameSuccess() {
        time = timerUtils.getTime();
        switch (boxTable.getBoxTableMode()) {
            case 0:
                if (time < primaryTime) {
                    showNameDialog(SP_PRIMARY_TIME, SP_PRIMARY_HERO);
                }
                break;
            case 1:
                if (time < middleTime) {
                    showNameDialog(SP_MIDDLE_TIME, SP_MIDDLE_HERO);
                }
                break;
            case 2:
                if (time < expertTime) {
                    showNameDialog(SP_EXPERT_TIME, SP_EXPERT_HERO);
                }
                break;
        }
        timerUtils.stop();
        faceButton.setBackgroundResource(R.drawable.face4);
    }


    @Override
    public void gameFlagChange(int num) {
        flagLED.setNum(num);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case SP_PRIMARY_TIME:
                primaryTime = sharedPreferences.getInt(key, primaryTime);
                break;
            case SP_MIDDLE_TIME:
                middleTime = sharedPreferences.getInt(key, middleTime);
                break;
            case SP_EXPERT_TIME:
                expertTime = sharedPreferences.getInt(key, expertTime);
                break;
            case SP_PRIMARY_HERO:
                primaryHero = sharedPreferences.getString(key, primaryHero);
                break;
            case SP_MIDDLE_HERO:
                middleHero = sharedPreferences.getString(key, middleHero);
                break;
            case SP_EXPERT_HERO:
                expertHero = sharedPreferences.getString(key, expertHero);
                break;
        }
    }
}
