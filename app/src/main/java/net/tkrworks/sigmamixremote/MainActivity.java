/*
 * Copylight (C) 2017, Shunichi Yamamoto, tkrworks.net
 *
 * This file is part of SigmaMIX Remote.
 *
 * SigmaMIX Remote is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option ) any later version.
 *
 * SigmaMIX Remote is distributed in the hope that it will be useful,
 * but WITHIOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.   See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SigmaMIX Remote. if not, see <http:/www.gnu.org/licenses/>.
 *
 * MainActivity.java
 */

package net.tkrworks.sigmamixremote;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.ParcelUuid;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  private SharedPreferences mPreferences;// = PreferenceManager.getDefaultSharedPreferences(this);
  private SharedPreferences.Editor mEditor;// = preferences.edit();

  private BluetoothLeScanner mBleScanner;
  private BluetoothAdapter mBleAdapter;
  private BluetoothGatt mBleGatt;
  private BLEScanCallback mBleScanCallback;
  private List<ScanFilter> mBleScanFilters = new ArrayList<>();
  private BluetoothGattService mMixerService;
  private BluetoothGattCharacteristic mXFaderSettingCharacteristic;
  private BluetoothGattCharacteristic mIFaderSettingCharacteristic;
  private BluetoothGattCharacteristic mLinePhonoSwitchCharacteristic;
  private BluetoothGattCharacteristic mInputGainCharacteristic;
  private BluetoothGattCharacteristic m3BandEqHiCharacteristic;
  private BluetoothGattCharacteristic m3BandEqMidCharacteristic;
  private BluetoothGattCharacteristic m3BandEqLowCharacteristic;
  private BluetoothGattCharacteristic mInputFaderCharacteristic;
  private BluetoothGattCharacteristic mCrossFaderCharacteristic;
  private BluetoothGattCharacteristic mMasterBoothGainCharacteristic;
  private BluetoothGattCharacteristic mMonitorSelectLevelCharacteristic;

  private List<String> mSigmaMixNameList = new ArrayList<>();
  private List<BluetoothDevice> mSigmaMixBdList = new ArrayList<>();
  private ArrayAdapter<String> mSigmaMixAdapter;

  private TabLayout tabLayout;
  private ProgressDialog sigmaScanProgressDialog;
  private SeekBar xfaderBar;

  private Handler mHandler;

  private boolean isConnectedBLE = false;

  private int currentXfaderPosition = 127;
  private int prevXfaderPosition = 127;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    if (Build.VERSION.SDK_INT >= 23) {
      requestGPSPermission();
    }

    mHandler = new Handler();

    mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    mEditor = mPreferences.edit();

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    tabLayout = (TabLayout) findViewById(R.id.tab_layout);
    tabLayout.addTab(tabLayout.newTab().setText(R.string.input));
    tabLayout.addTab(tabLayout.newTab().setText(R.string.knob));
    tabLayout.addTab(tabLayout.newTab().setText(R.string.fader));
    tabLayout.addTab(tabLayout.newTab().setText(R.string.master));
    tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

    final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
    final PagerAdapter adapter = new PagerAdapter
        (getSupportFragmentManager(), tabLayout.getTabCount());
    viewPager.setAdapter(adapter);
    viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
      @Override
      public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
      }

      @Override
      public void onTabUnselected(TabLayout.Tab tab) {

      }

      @Override
      public void onTabReselected(TabLayout.Tab tab) {

      }
    });
  }

  @Override
  protected void onStop() {
    super.onStop();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    mHandler.removeCallbacksAndMessages(null);
    mHandler = null;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    super.onPrepareOptionsMenu(menu);

    MyLog.d("DEBUG", "onPrepareOptionsMenu");

    MenuItem menuItem = (MenuItem) menu.findItem(R.id.scan_disconnect);

    if (isConnectedBLE) {
      menuItem.setTitle(getString(R.string.disconnect));
    } else {
      menuItem.setTitle(getString(R.string.scan));
    }

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    //int id = item.getItemId();
    //if (id == R.id.action_settings) {
    //  return true;
    //}

    if (item.getTitle().equals(getString(R.string.scan))) {
      MyLog.d("DEBUG", "tap scan.");

      startScan();

      sigmaScanProgressDialog = new ProgressDialog(MainActivity.this);
      sigmaScanProgressDialog.setMax(100);
      sigmaScanProgressDialog.setMessage("Scannig...");
      sigmaScanProgressDialog.setTitle("SCAN & CONNECT");
      sigmaScanProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
      sigmaScanProgressDialog.setCancelable(false);
      sigmaScanProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",
          new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
              stopScan();

              mHandler.removeCallbacksAndMessages(null);
            }
          });
      sigmaScanProgressDialog.show();

      mHandler.postDelayed(new Runnable() {
        @Override
        public void run() {
          stopScan();

          sigmaScanProgressDialog.dismiss();

          AlertDialog.Builder selectListDialog = new AlertDialog.Builder(MainActivity.this);

          if (mSigmaMixNameList.size() > 0) {
            //final String[] nameList = mSigmaMixNameList.toArray(new String[0]);
            //final BluetoothDevice[] addrList = mSigmaMixBdList.toArray(new BluetoothDevice[0]);
            final BluetoothDevice[] bdList = mSigmaMixBdList.toArray(new BluetoothDevice[0]);
            final String[] nameAndAddrList = new String[bdList.length];
            for (int i = 0; i < nameAndAddrList.length; i++) {
              nameAndAddrList[i] = String
                  .format("%s(%s)", bdList[i].getName(), bdList[i].getAddress());
            }

            selectListDialog.setTitle("Select Your SigmaMIX");
            selectListDialog.setItems(nameAndAddrList, new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {
                MyLog.d("DEBUG", "select %s", nameAndAddrList[which]);

                mBleGatt = bdList[which]
                    .connectGatt(getApplicationContext(), false, mBleGattCallback);
              }
            });
            selectListDialog.setNegativeButton("CANCEL", null);

            selectListDialog.show();
          } else {
            selectListDialog.setTitle("Not Found SigmaMIX");
            selectListDialog.setMessage("Do you scan again?");
            selectListDialog.setPositiveButton("RESCAN", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {
                MyLog.d("DEBUG", "rescan...");
              }
            });
            selectListDialog.setNegativeButton("CANCEL", null);
          }
        }
      }, 5000);
    } else if (item.getTitle().equals(getString(R.string.disconnect))) {
      mBleGatt.disconnect();
    }

    return super.onOptionsItemSelected(item);
  }

  @TargetApi(23)
  private void requestGPSPermission() {
    if (checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
    }

    //if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
    //  requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    //}
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    if (requestCode == 1) {
      if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        Log.d("PERMISSION", "Succeeded");
        Toast.makeText(MainActivity.this, "Succeed", Toast.LENGTH_SHORT).show();
      } else {
        Log.d("PERMISSION", "Failed");
        Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
      }
    } else {
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
  }

  private void startScan() {
    final BluetoothManager bm = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
    mBleAdapter = bm.getAdapter();

    ScanSettings.Builder ssb = new ScanSettings.Builder();
    if (Build.VERSION.SDK_INT >= 23) {
      ssb.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
      //ssb.setReportDelay(0);
      //ssb.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
    }
    ScanSettings ss = ssb.build();

    mBleScanCallback = new BLEScanCallback();
    mBleScanner = mBleAdapter.getBluetoothLeScanner();

    Log.d("DEBUG", "Start scannig...");

    //first_connection_flag = false;
    //init_flag = 0;
    mBleScanner.startScan(mBleScanFilters, ss, mBleScanCallback);

    /*
    handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        Log.d("DEBUG", "Stop scannig...");

        bcbpb_scanner.stopScan(bcbpb_scan_callback);
      }
    }, 5000);
    */
  }

  private void stopScan() {
    Log.d("DEBUG", "Stop scanning...");

    mHandler.removeCallbacksAndMessages(null);
    mBleScanner.stopScan(mBleScanCallback);
  }

  void switchLinePhono(boolean ch1_sw, boolean ch2_sw) {
    byte[] value = new byte[1];

    value[0] = (byte)((((ch1_sw ? 1 : 0) << 4) & 0xF0) | ((ch2_sw ? 1 : 0) & 0x0F));

    MyLog.d("DEBUG", "Line/Phono = %d(%b,%b)", value[0], ch1_sw, ch2_sw);

    if (isConnectedBLE) {
      mLinePhonoSwitchCharacteristic.setValue(value);
      mBleGatt.writeCharacteristic(mLinePhonoSwitchCharacteristic);
    }
  }

  void adjustInputGain(int ch1_value, int ch2_value) {
    byte[] value = new byte[6];
    value[5] = (byte)((ch1_value >> 16) & 0xFF);
    value[4] = (byte)((ch1_value >> 8) & 0xFF);
    value[3] = (byte)(ch1_value & 0xFF);
    value[2] = (byte)((ch2_value >> 16) & 0xFF);
    value[1] = (byte)((ch2_value >> 8) & 0xFF);
    value[0] = (byte)(ch2_value & 0xFF);

    MyLog.d("DEBUG", "InputGain = %d:[%02x, %02x, %02x] %d:[%02x, %02x, %02x]", ch1_value, value[5], value[4], value[3], ch2_value, value[2], value[1], value[0]);

    if (isConnectedBLE) {
      mInputGainCharacteristic.setValue(value);
      mBleGatt.writeCharacteristic(mInputGainCharacteristic);
    }
  }

  void adjust3BandEqHi(int ch1_value, int ch2_value) {
    byte[] value = new byte[6];
    value[5] = (byte)((ch1_value >> 16) & 0xFF);
    value[4] = (byte)((ch1_value >> 8) & 0xFF);
    value[3] = (byte)(ch1_value & 0xFF);
    value[2] = (byte)((ch2_value >> 16) & 0xFF);
    value[1] = (byte)((ch2_value >> 8) & 0xFF);
    value[0] = (byte)(ch2_value & 0xFF);

    MyLog.d("DEBUG", "3Band EQ Hi = %d:[%02x, %02x, %02x] %d:[%02x, %02x, %02x]", ch1_value, value[5], value[4], value[3], ch2_value, value[2], value[1], value[0]);

    if (isConnectedBLE) {
      m3BandEqHiCharacteristic.setValue(value);
      mBleGatt.writeCharacteristic(m3BandEqHiCharacteristic);
    }
  }

  void adjust3BandEqMid(int ch1_value, int ch2_value) {
    byte[] value = new byte[6];
    value[5] = (byte)((ch1_value >> 16) & 0xFF);
    value[4] = (byte)((ch1_value >> 8) & 0xFF);
    value[3] = (byte)(ch1_value & 0xFF);
    value[2] = (byte)((ch2_value >> 16) & 0xFF);
    value[1] = (byte)((ch2_value >> 8) & 0xFF);
    value[0] = (byte)(ch2_value & 0xFF);

    MyLog.d("DEBUG", "3Band EQ Mid = %d:[%02x, %02x, %02x] %d:[%02x, %02x, %02x]", ch1_value, value[5], value[4], value[3], ch2_value, value[2], value[1], value[0]);

    if (isConnectedBLE) {
      m3BandEqMidCharacteristic.setValue(value);
      mBleGatt.writeCharacteristic(m3BandEqMidCharacteristic);
    }
  }

  void adjust3BandEqLow(int ch1_value, int ch2_value) {
    byte[] value = new byte[6];
    value[5] = (byte)((ch1_value >> 16) & 0xFF);
    value[4] = (byte)((ch1_value >> 8) & 0xFF);
    value[3] = (byte)(ch1_value & 0xFF);
    value[2] = (byte)((ch2_value >> 16) & 0xFF);
    value[1] = (byte)((ch2_value >> 8) & 0xFF);
    value[0] = (byte)(ch2_value & 0xFF);

    MyLog.d("DEBUG", "3Band EQ Low = %d:[%02x, %02x, %02x] %d:[%02x, %02x, %02x]", ch1_value, value[5], value[4], value[3], ch2_value, value[2], value[1], value[0]);

    if (isConnectedBLE) {
      m3BandEqLowCharacteristic.setValue(value);
      mBleGatt.writeCharacteristic(m3BandEqLowCharacteristic);
    }
  }

  void adjustVolume(int ch1_value, int ch2_value) {
    byte[] value = new byte[6];
    value[5] = (byte)((ch1_value >> 16) & 0xFF);
    value[4] = (byte)((ch1_value >> 8) & 0xFF);
    value[3] = (byte)(ch1_value & 0xFF);
    value[2] = (byte)((ch2_value >> 16) & 0xFF);
    value[1] = (byte)((ch2_value >> 8) & 0xFF);
    value[0] = (byte)(ch2_value & 0xFF);

    MyLog.d("DEBUG", "Input Fader Volume = %d:[%02x, %02x, %02x] %d:[%02x, %02x, %02x]", ch1_value, value[5], value[4], value[3], ch2_value, value[2], value[1], value[0]);

    if (isConnectedBLE) {
      mInputFaderCharacteristic.setValue(value);
      mBleGatt.writeCharacteristic(mInputFaderCharacteristic);
    }
  }

  void adjustIFaderSetting(boolean rev, int curve) {
    byte[] value = new byte[1];
    value[0] = (byte)(((rev ? 1: 0) << 4) | (curve & 0x0F));

    MyLog.d("DEBUG", "IFader Setting = %02x(%b,%d)", value[0], rev, curve);

    if (isConnectedBLE) {
      mIFaderSettingCharacteristic.setValue(value);
      mBleGatt.writeCharacteristic(mIFaderSettingCharacteristic);
    }
  }

  void adjustXFaderSetting(boolean rev, int curve) {
    byte[] value = new byte[1];
    value[0] = (byte)(((rev ? 1: 0) << 4) | (curve & 0x0F));

    MyLog.d("DEBUG", "XFader Setting = %02x(%b,%d)", value[0], rev, curve);

    if (isConnectedBLE) {
      mXFaderSettingCharacteristic.setValue(value);
      mBleGatt.writeCharacteristic(mXFaderSettingCharacteristic);
    }
  }

  void adjustMasterBoothGain(int master_value, int booth_value) {
    byte[] value = new byte[6];
    value[5] = (byte)((master_value >> 16) & 0xFF);
    value[4] = (byte)((master_value >> 8) & 0xFF);
    value[3] = (byte)(master_value & 0xFF);
    value[2] = (byte)((booth_value >> 16) & 0xFF);
    value[1] = (byte)((booth_value >> 8) & 0xFF);
    value[0] = (byte)(booth_value & 0xFF);

    MyLog.d("DEBUG", "Master/Booth Gain = %d:[%02x, %02x, %02x] %d:[%02x, %02x, %02x]", master_value, value[5], value[4], value[3], booth_value, value[2], value[1], value[0]);

    if (isConnectedBLE) {
      mMasterBoothGainCharacteristic.setValue(value);
      mBleGatt.writeCharacteristic(mMasterBoothGainCharacteristic);
    }
  }

  void adjustMonitorSelectLevel(int select_value, int level_value) {
    byte[] value = new byte[6];
    value[5] = (byte)((select_value >> 16) & 0xFF);
    value[4] = (byte)((select_value >> 8) & 0xFF);
    value[3] = (byte)(select_value & 0xFF);
    value[2] = (byte)((level_value >> 16) & 0xFF);
    value[1] = (byte)((level_value >> 8) & 0xFF);
    value[0] = (byte)(level_value & 0xFF);

    MyLog.d("DEBUG", "Monitor Select/Level = %d:[%02x, %02x, %02x] %d:[%02x, %02x, %02x]", select_value, value[5], value[4], value[3], level_value, value[2], value[1], value[0]);

    if (isConnectedBLE) {
      mMonitorSelectLevelCharacteristic.setValue(value);
      mBleGatt.writeCharacteristic(mMonitorSelectLevelCharacteristic);
    }
  }

  /*
  mXFaderSettingCharacteristic;
  mIFaderSettingCharacteristic;

  mInputFaderCharacteristic;
  */

  private final BluetoothGattCallback mBleGattCallback = new BluetoothGattCallback() {
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
      super.onConnectionStateChange(gatt, status, newState);

      if (newState == BluetoothProfile.STATE_CONNECTING) {
        Log.d("DEBUG", "connecting...");
      } else if (newState == BluetoothProfile.STATE_CONNECTED) {
        Log.d("DEBUG", "connected...");

        invalidateOptionsMenu();

        gatt.discoverServices();

        isConnectedBLE = true;
      } else if (newState == BluetoothProfile.STATE_DISCONNECTING) {
        Log.d("DEBUG", "disconnecting...");
      } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
        Log.d("DEBUG", "disconnected...");

        if (gatt != null) {
          gatt.close();
          //gatt = null;
        }

        isConnectedBLE = false;
        //is_completed_char_conf = 0;

        invalidateOptionsMenu();
      }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
      super.onServicesDiscovered(gatt, status);

      if (status == BluetoothGatt.GATT_SUCCESS) {
        List<BluetoothGattService> services = gatt.getServices();

        for (BluetoothGattService service : services) {
          Log.d("DEBUG", "SERVICE = " + service.getUuid());

          if (getString(R.string.SIGMA_MIX_SERVICE_UUID).equals(service.getUuid().toString())) {
            mMixerService = gatt.getService(service.getUuid());

            if (mMixerService != null) {
              List<BluetoothGattCharacteristic> characteristics = mMixerService
                  .getCharacteristics();
              for (BluetoothGattCharacteristic characteristic : characteristics) {
                MyLog.d("DEBUG", "MIXER CHARACRTERISTIC: %s", characteristic.getUuid());

                if (getString(R.string.XF_SETTING_CHARACTERISTIC_UUID).equals(characteristic.getUuid().toString())) {
                  mXFaderSettingCharacteristic = characteristic;
                } else if (getString(R.string.IF_SETTING_CHARACTERISTIC_UUID).equals(characteristic.getUuid().toString())) {
                  mIFaderSettingCharacteristic = characteristic;
                } else if (getString(R.string.LINE_PHONO_SW_CHARACTERISTIC_UUID).equals(characteristic.getUuid().toString())) {
                  mLinePhonoSwitchCharacteristic = characteristic;
                } else if (getString(R.string.IN_GAIN_CHARACTERISTIC_UUID).equals(characteristic.getUuid().toString())) {
                  mInputGainCharacteristic = characteristic;
                } else if (getString(R.string.EQ_HI_CHARACTERISTIC_UUID).equals(characteristic.getUuid().toString())) {
                  m3BandEqHiCharacteristic = characteristic;
                } else if (getString(R.string.EQ_MID_CHARACTERISTIC_UUID).equals(characteristic.getUuid().toString())) {
                  m3BandEqMidCharacteristic = characteristic;
                } else if (getString(R.string.EQ_LOW_CHARACTERISTIC_UUID).equals(characteristic.getUuid().toString())) {
                  m3BandEqLowCharacteristic = characteristic;
                } else if (getString(R.string.IFADER_CHARACTERISTIC_UUID).equals(characteristic.getUuid().toString())) {
                  mInputFaderCharacteristic = characteristic;
                } else if (getString(R.string.XFADER_CHARACTERISTIC_UUID).equals(characteristic.getUuid().toString())) {
                  mCrossFaderCharacteristic = characteristic;

                  boolean notify_registered = gatt.setCharacteristicNotification(characteristic, true);
                  if (notify_registered) {
                    MyLog.d("DEBUG", "XFADER NOTIFICATION: SUCCESS");
                  } else {
                    MyLog.d("DEBUG", "XFADER NOTIFICATION: FAILURE");
                  }

                  List<BluetoothGattDescriptor> descriptors = characteristic.getDescriptors();
                  for (BluetoothGattDescriptor descriptor : descriptors) {
                    MyLog.d("DEBUG", "XFADER DESCRIPTOR: %s", descriptor.getUuid().toString());

                    if (getString(R.string.XFADER_DESCRIPToR_UUID).equals(descriptor.getUuid().toString())) {
                      if ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
                        MyLog.d("DEBUG", "CHARACTERISTIC ( %s ) is NOTIFY", characteristic.getUuid().toString());

                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        boolean descriptorFlag = gatt.writeDescriptor(descriptor);
                        if (descriptorFlag) {
                          MyLog.d("DEBUG", "write descriptor is OK.");
                        } else {
                          MyLog.d("DEBUG", "write descriptor is NG.");
                        }
                      }
                    }
                  }
                } else if (getString(R.string.MASTER_BOOTH_GAIN_CHARACTERISTIC_UUID).equals(characteristic.getUuid().toString())) {
                  mMasterBoothGainCharacteristic = characteristic;
                } else if (getString(R.string.MONITOR_SETTING_CHARACTERISTIC_UUID).equals(characteristic.getUuid().toString())) {
                  mMonitorSelectLevelCharacteristic = characteristic;
                }
              }
            }
          }
        }
      }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic,
        int status) {
      super.onCharacteristicRead(gatt, characteristic, status);

      MyLog.d("DEBUG", "characteristic read");
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt,
        BluetoothGattCharacteristic characteristic,
        int status) {
      super.onCharacteristicWrite(gatt, characteristic, status);

      MyLog.d("DEBUG", "characteristic write");
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt,
        BluetoothGattCharacteristic characteristic) {
      super.onCharacteristicChanged(gatt, characteristic);

      byte[] bytes = characteristic.getValue();

      if (tabLayout.getSelectedTabPosition() == 2) {
        currentXfaderPosition = (bytes[1] < 0) ? (bytes[1] + 255) : bytes[1];

        //debug MyLog.d("DEBUG", "characteristic changed %02x %02x %d", bytes[0], bytes[1], xfaderPosition);

        if (currentXfaderPosition != prevXfaderPosition) {
          mHandler.post(new Runnable() {
            @Override
            public void run() {
              if (xfaderBar == null) {
                xfaderBar = (SeekBar) findViewById(R.id.xf);
                xfaderBar.setOnTouchListener(new OnTouchListener() {
                  @Override
                  public boolean onTouch(View v, MotionEvent event) {
                    return true;
                  }
                });
              }
              xfaderBar.setProgress(currentXfaderPosition);
            }
          });
        }
        prevXfaderPosition = currentXfaderPosition;
      }
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
        int status) {
      super.onDescriptorRead(gatt, descriptor, status);

      MyLog.d("DEBUG", "descriptor read");
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
        int status) {
      super.onDescriptorWrite(gatt, descriptor, status);

      MyLog.d("DEBUG", "characteristic write");
    }
  };

  private class BLEScanCallback extends ScanCallback {

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
      super.onScanResult(callbackType, result);

      int rssi = result.getRssi();
      ScanRecord scanRecord = result.getScanRecord();
      byte[] scanRecordBytes;

      if (scanRecord != null) {
        scanRecordBytes = scanRecord.getBytes();

        BluetoothDevice device = result.getDevice();

        String bleDeviceName = device.getName();
        String bleDeviceAddress = device.getAddress();

        MyLog.d("DEBUG", "NAME: %s\n", bleDeviceName);
        MyLog.d("DEBUG", "ADDRESS: %s\n", bleDeviceAddress);

        ParcelUuid[] uuids = device.getUuids();
        if (uuids != null) {
          for (ParcelUuid uuid : uuids) {
            MyLog.d("DEBUG", "UUID: %s\n" + uuid.toString());
          }
        }

        if (bleDeviceName != null && bleDeviceName.contains("SigmaMIX")) {
          if (!mSigmaMixBdList.contains(device)) {
            mSigmaMixNameList.add(bleDeviceName);
            mSigmaMixBdList.add(device);
          }
        }
      }
    }

    @Override
    public void onBatchScanResults(List<ScanResult> results) {
      super.onBatchScanResults(results);
    }

    @Override
    public void onScanFailed(int errorCode) {
      super.onScanFailed(errorCode);
    }
  }
}
