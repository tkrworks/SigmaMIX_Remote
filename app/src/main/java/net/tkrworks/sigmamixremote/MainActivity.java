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
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
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
  private BluetoothGattCharacteristic mMasterBoothGainCharacteristic;
  private BluetoothGattCharacteristic mMonitorLevelSelectCharacteristic;

  private List<String> mSigmaMixNameList = new ArrayList<>();
  private List<BluetoothDevice> mSigmaMixBdList = new ArrayList<>();
  private ArrayAdapter<String> mSigmaMixAdapter;

  private ProgressDialog sigmaScanProgressDialog;

  private Handler mHandler;

  private boolean isConnectedBLE = false;

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

    TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
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
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
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
    }

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
  public void onRequestPermissionsResult(int requestCode, String[] permissions,
      int[] grantResults) {
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

  private final BluetoothGattCallback mBleGattCallback = new BluetoothGattCallback() {
    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
      super.onConnectionStateChange(gatt, status, newState);

      if (newState == BluetoothProfile.STATE_CONNECTING) {
        Log.d("DEBUG", "connecting...");
      } else if (newState == BluetoothProfile.STATE_CONNECTED) {
        Log.d("DEBUG", "connected...");

        gatt.discoverServices();

        isConnectedBLE = true;
      } else if (newState == BluetoothProfile.STATE_DISCONNECTING) {
        Log.d("DEBUG", "disconnecting...");
      } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
        Log.d("DEBUG", "disconnected...");

        if (gatt != null) {
          gatt.close();
          gatt = null;
        }

        isConnectedBLE = false;
        //is_completed_char_conf = 0;
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
                } else if (getString(R.string.MASTER_BOOTH_GAIN_CHARACTERISTIC_UUID).equals(characteristic.getUuid().toString())) {
                  mMasterBoothGainCharacteristic = characteristic;
                } else if (getString(R.string.MONITOR_SETTING_CHARACTERISTIC_UUID).equals(characteristic.getUuid().toString())) {
                  mMonitorLevelSelectCharacteristic = characteristic;
                }
              }
            }
          }

          /*
          else if(AIRP_SERVICE_UUID.equals(service.getUuid().toString())) {
            airp_service = gatt.getService(service.getUuid());

            if(airp_service != null) {
              List<BluetoothGattCharacteristic> characteristics = airp_service.getCharacteristics();
              for(BluetoothGattCharacteristic characteristic : characteristics) {
                Log.d("DEBUG", "  AIRP CHARACTERISTIC: " + characteristic.getUuid());

                if(AIRP_CHAR_UUID.equals(characteristic.getUuid().toString())) {
                  airp_characteristic = characteristic;

                  boolean notify_registered = gatt.setCharacteristicNotification(characteristic, true);
                  if(notify_registered)
                    Log.d("DEBUG", "characteristic notification: SUCCESS");
                  else
                    Log.d("DEBUG", "characteristic notification: FAILURE");

                  List<BluetoothGattDescriptor> descriptors = characteristic.getDescriptors();
                  for(BluetoothGattDescriptor descriptor : descriptors) {
                    Log.d("DEBUG", "DESCRIPTOR: " + descriptor.getUuid());

                    if(CHARACTERISTIC_CONFIG_UUID2.equals(descriptor.getUuid().toString())) {
                      if((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
                        if(descriptor != null) {
                          Log.d("DEBUG", "Characteristic (" + characteristic.getUuid() + ") is NOTIFY");

                          descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                          boolean desc_flag = gatt.writeDescriptor(descriptor);
                          if(desc_flag) {
                            Log.d("DEBUG", "write descriptor is OK.");
                          }
                          else
                            Log.d("DEBUG", "write descriptor is NG.");
                        }
                      }
                    }
                    else if((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0) {
                      if(descriptor != null) {
                        Log.d("DEBUG", "Characteristic (" + characteristic.getUuid() + ") is INDICATE");

                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                        boolean desc_flag = gatt.writeDescriptor(descriptor);
                        if(desc_flag) {
                          Log.d("DEBUG", "write descriptor is OK.");
                        }
                        else
                          Log.d("DEBUG", "write descriptor is NG.");
                      }
                    }
                  }
                }
              }
            }
          }
          */

          /*
          if(I2C_SERVICE_UUID.equals(service.getUuid().toString())) {
            sensor_service = gatt.getService(service.getUuid());

            if(sensor_service != null) {
              List<BluetoothGattCharacteristic> characteristics = sensor_service.getCharacteristics();
              for(BluetoothGattCharacteristic characteristic : characteristics) {
                Log.d("DEBUG", "CHARACTERISTIC: " + characteristic.getUuid());

                if(SENSOR_TEST_CHAR_UUID.equals(characteristic.getUuid().toString())) {
                  //Log.d("BLE", "MEME : CUSTOM 1 " + characteristic.getUuid());

                  sensor_test_characteristic = characteristic;
                }
                else if(SENSOR_CONFIG_CHAR_UUID.equals(characteristic.getUuid().toString())) {
                  //Log.d("BLE", "MEME : CUSTOM 1 " + characteristic.getUuid());

                  sensor_config_characteristic = characteristic;
                }
                else if(SENSOR_ENABLE_CHAR_UUID.equals(characteristic.getUuid().toString())) {
                  //Log.d("BLE", "MEME : CUSTOM 2 " + characteristic.getUuid());

                  sensor_enable_characteristic = characteristic;
                }
                else if(SENSOR_RAW_CHAR_UUID.equals(characteristic.getUuid().toString())) {
                  //Log.d("BLE", "MEME : CUSTOM 2 " + characteristic.getUuid());

                  sensor_raw_characteristic = characteristic;
                }
                else if(MAGNET_CONFIG_UUID.equals(characteristic.getUuid().toString())) {
                  magnet_config_characteristic = characteristic;
                }
                else if(MAGNET_SENSITIVITY_UUID.equals(characteristic.getUuid().toString())) {
                  magnet_sensitivity_characteristic = characteristic;

                  boolean notify_registered = gatt.setCharacteristicNotification(characteristic, true);
                  if(notify_registered)
                    Log.d("DEBUG", "characteristic notification: SUCCESS");
                  else
                    Log.d("DEBUG", "characteristic notification: FAILURE");

                  List<BluetoothGattDescriptor> descriptors = characteristic.getDescriptors();
                  for(BluetoothGattDescriptor descriptor : descriptors) {
                    Log.d("DEBUG", "DESCRIPTOR: " + descriptor.getUuid());

                    if(CHARACTERISTIC_CONFIG_UUID2.equals(descriptor.getUuid().toString())) {
                      if((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
                        if(descriptor != null) {
                          Log.d("DEBUG", "Characteristic (" + characteristic.getUuid() + ") is NOTIFY");

                          descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                          boolean desc_flag = gatt.writeDescriptor(descriptor);
                          if(desc_flag) {
                            Log.d("DEBUG", "write descriptor is OK.");
                            init_flag = 1;
                          }
                          else
                            Log.d("DEBUG", "write descriptor is NG.");
                        }
                      }
                    }
                    else if((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0) {
                      if(descriptor != null) {
                        Log.d("DEBUG", "Characteristic (" + characteristic.getUuid() + ") is INDICATE");

                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
                        boolean desc_flag = gatt.writeDescriptor(descriptor);
                        if(desc_flag) {
                          Log.d("DEBUG", "write descriptor is OK.");
                          init_flag = 1;
                        }
                        else
                          Log.d("DEBUG", "write descriptor is NG.");
                      }
                    }
                  }
                }
              }
            }
          }
          */
        }
      }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic,
        int status) {
      super.onCharacteristicRead(gatt, characteristic, status);
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt,
        BluetoothGattCharacteristic characteristic,
        int status) {
      super.onCharacteristicWrite(gatt, characteristic, status);
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt,
        BluetoothGattCharacteristic characteristic) {
      super.onCharacteristicChanged(gatt, characteristic);
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
        int status) {
      super.onDescriptorRead(gatt, descriptor, status);
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
        int status) {
      super.onDescriptorWrite(gatt, descriptor, status);
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
