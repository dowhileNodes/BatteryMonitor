package com.example.batterymonitor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadBatteryInfo();
        result = (TextView) findViewById(R.id.result);
    }
    private void loadBatteryInfo() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);

        registerReceiver(batteryInfoReceiver, intentFilter);
    }
    private BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateBatteryData(intent);
        }
        private void updateBatteryData(Intent intent) {
            boolean present = intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false);

            if (present) {
                StringBuilder batteryInfo = new StringBuilder();
                int health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
                batteryInfo.append("Health:" + health).append("\n");
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                int healthLbl = -1;


                if (level != -1 && scale != -1) {
                    int batteryPct = (int) ((level / (float) scale) * 100f);
                    batteryInfo.append("Battery Pct : " + batteryPct + " %");
                }

                int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
                batteryInfo.append("PLUGGED:" + plugged).append(" \n");

                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                batteryInfo.append("STATUS:" + status).append(" \n");


                if (intent.getExtras() != null) {
                    String technology = intent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY);
                    batteryInfo.append("Technology :" + technology).append("\n");
                }

                int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
                if (temperature > 0) {
                    batteryInfo.append("Temperature :" + ((float) temperature / 10f)).append("C\n");
                }

                int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
                batteryInfo.append("Voltage :" + voltage).append("mV\n");

                long capacity = getBatteryCapacity(MainActivity.this);
                batteryInfo.append("Capacity :" + capacity).append(" mAh\n");

                result.setText(batteryInfo.toString());
            } else {
                Toast.makeText(MainActivity.this, "No Battery present", Toast.LENGTH_SHORT).show();
            }
        }
        public long getBatteryCapacity(Context ctx) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                BatteryManager mBatteryManager = (BatteryManager) ctx.getSystemService(Context.BATTERY_SERVICE);
                Long chargeCounter = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER);
                Long capacity = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

                if (chargeCounter != null && capacity != null) {
                    long value = (long) (((float) chargeCounter / (float) capacity) * 100f);
                    return value;
                }
            }

            return 0;
        }
    };
}