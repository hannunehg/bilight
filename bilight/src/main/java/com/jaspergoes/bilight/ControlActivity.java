package com.jaspergoes.bilight;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import com.jaspergoes.bilight.helpers.ColorPickerView;
import com.jaspergoes.bilight.helpers.OnColorChangeListener;
import com.jaspergoes.bilight.helpers.OnProgressChangeListener;
import com.jaspergoes.bilight.milight.Controller;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.ArrayList;
import java.util.List;

public class ControlActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_control);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        /* Set checkboxes, according to values currently stored in Controller */
        for (int i : Controller.controlDevices) {
            if (i == 0) {
                ((AppCompatCheckBox) findViewById(R.id.control_wifi_bridge)).setChecked(true);
            } else if (i == 7) {
                for (int x : Controller.controlZones) {
                    if (x == -1) {
                        break;
                    } else if (x == 0) {
                        ((AppCompatCheckBox) findViewById(R.id.control_zone_1)).setChecked(true);
                        ((AppCompatCheckBox) findViewById(R.id.control_zone_2)).setChecked(true);
                        ((AppCompatCheckBox) findViewById(R.id.control_zone_3)).setChecked(true);
                        ((AppCompatCheckBox) findViewById(R.id.control_zone_4)).setChecked(true);
                    } else {
                        switch (x) {
                            case 1:
                                ((AppCompatCheckBox) findViewById(R.id.control_zone_1)).setChecked(true);
                                break;
                            case 2:
                                ((AppCompatCheckBox) findViewById(R.id.control_zone_2)).setChecked(true);
                                break;
                            case 3:
                                ((AppCompatCheckBox) findViewById(R.id.control_zone_3)).setChecked(true);
                                break;
                            case 4:
                                ((AppCompatCheckBox) findViewById(R.id.control_zone_4)).setChecked(true);
                                break;
                        }
                    }
                }
            }
        }

        AppCompatCheckBox.OnCheckedChangeListener changeListener = new AppCompatCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                boolean wifi = ((AppCompatCheckBox) findViewById(R.id.control_wifi_bridge)).isChecked();
                boolean all = ((AppCompatCheckBox) findViewById(R.id.control_zone_1)).isChecked() && ((AppCompatCheckBox) findViewById(R.id.control_zone_2)).isChecked() && ((AppCompatCheckBox) findViewById(R.id.control_zone_3)).isChecked() && ((AppCompatCheckBox) findViewById(R.id.control_zone_4)).isChecked();
                if (wifi && all) {
                    Controller.controlDevices = new int[]{8, 7, 0};
                    Controller.controlZones = new int[]{0};
                } else if (all) {
                    Controller.controlDevices = new int[]{8, 7};
                    Controller.controlZones = new int[]{0};
                } else {
                    boolean any = ((AppCompatCheckBox) findViewById(R.id.control_zone_1)).isChecked() || ((AppCompatCheckBox) findViewById(R.id.control_zone_2)).isChecked() || ((AppCompatCheckBox) findViewById(R.id.control_zone_3)).isChecked() || ((AppCompatCheckBox) findViewById(R.id.control_zone_4)).isChecked();
                    if (any) {

                        List<Integer> zoneList = new ArrayList<Integer>();

                        if (((AppCompatCheckBox) findViewById(R.id.control_zone_1)).isChecked())
                            zoneList.add(1);
                        if (((AppCompatCheckBox) findViewById(R.id.control_zone_2)).isChecked())
                            zoneList.add(2);
                        if (((AppCompatCheckBox) findViewById(R.id.control_zone_3)).isChecked())
                            zoneList.add(3);
                        if (((AppCompatCheckBox) findViewById(R.id.control_zone_4)).isChecked())
                            zoneList.add(4);

                        if (wifi) {
                            Controller.controlDevices = new int[]{8, 7, 0};
                        } else {
                            Controller.controlDevices = new int[]{8, 7};
                        }

                        int[] ret = new int[zoneList.size()];
                        for (int i = 0; i < ret.length; i++) {
                            ret[i] = zoneList.get(i).intValue();
                        }

                        Controller.controlZones = ret;

                    } else if (wifi) {
                        Controller.controlDevices = new int[]{0};
                        Controller.controlZones = new int[]{-1};
                    } else {
                        /* None selected, at all */
                        Controller.controlDevices = new int[]{};
                        Controller.controlZones = new int[]{-1};
                    }
                }
            }
        };
        ((AppCompatCheckBox) findViewById(R.id.control_wifi_bridge)).setOnCheckedChangeListener(changeListener);
        ((AppCompatCheckBox) findViewById(R.id.control_zone_1)).setOnCheckedChangeListener(changeListener);
        ((AppCompatCheckBox) findViewById(R.id.control_zone_2)).setOnCheckedChangeListener(changeListener);
        ((AppCompatCheckBox) findViewById(R.id.control_zone_3)).setOnCheckedChangeListener(changeListener);
        ((AppCompatCheckBox) findViewById(R.id.control_zone_4)).setOnCheckedChangeListener(changeListener);

        /* Switch on */
        ((Button) findViewById(R.id.switchOn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        Controller.INSTANCE.switchOnOff(true);

                    }

                }).start();

            }
        });

        /* Switch off */
        ((Button) findViewById(R.id.switchOff)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        Controller.INSTANCE.switchOnOff(false);

                    }

                }).start();

            }
        });

        /* White mode */
        ((Button) findViewById(R.id.setWhite)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        Controller.INSTANCE.setWhite();

                    }

                }).start();

            }
        });

        /* Color */
        ((ColorPickerView) findViewById(R.id.colorpicker)).setOnColorChangeListener(new OnColorChangeListener() {

            @Override
            public void colorChanged(final int color) {

                if (Controller.newColor != (Controller.newColor = (color + 128) % 256)) {

                    synchronized (Controller.INSTANCE) {
                        Controller.INSTANCE.notify();
                    }

                }

            }

            @Override
            public void refresh() {

                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        Controller.INSTANCE.sendRefresh();

                    }

                }).start();

            }

        });

        /* Brightness */
        ((DiscreteSeekBar) findViewById(R.id.seekbar_brightness)).setProgress(Controller.newColor == -1 ? 100 : Controller.newColor);
        ((DiscreteSeekBar) findViewById(R.id.seekbar_brightness)).setOnProgressChangeListener(new OnProgressChangeListener() {

            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, final int value, boolean fromUser) {

                if (fromUser && Controller.newBrightness != (Controller.newBrightness = value)) {

                    synchronized (Controller.INSTANCE) {
                        Controller.INSTANCE.notify();
                    }

                }

            }

        });

        /* Saturation */
        ((DiscreteSeekBar) findViewById(R.id.seekbar_saturation)).setProgress(Controller.newSaturation == -1 ? 100 : Controller.newSaturation);
        ((DiscreteSeekBar) findViewById(R.id.seekbar_saturation)).setOnProgressChangeListener(new OnProgressChangeListener() {

            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, final int value, boolean fromUser) {

                if (fromUser && Controller.newSaturation != (Controller.newSaturation = 100 - value)) {

                    synchronized (Controller.INSTANCE) {
                        Controller.INSTANCE.notify();
                    }

                }

            }

        });

    }

}
