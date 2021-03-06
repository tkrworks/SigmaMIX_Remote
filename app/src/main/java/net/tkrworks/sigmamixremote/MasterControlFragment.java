/*
 * Copyright (C) 2017, Shunichi Yamamoto, tkrworks.net
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
 * MasterControlFragment.java
 */

package net.tkrworks.sigmamixremote;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import com.triggertrap.seekarc.SeekArc;
import com.triggertrap.seekarc.SeekArc.OnSeekArcChangeListener;

import static net.tkrworks.sigmamixremote.MyTextViewControl.*;

public class MasterControlFragment extends Fragment {

  private Handler mHandler;

  private SeekArc mMasterGain;
  private SeekArc mBoothGain;
  private SeekArc mMonitorSelect;
  private SeekArc mMonitorLevel;
  private TextView mMasterdB;
  private TextView mBoothdB;
  private TextView mMonitordB;
  private Switch mMonitorCh;

  private UIUpdateThread mUIUpdateThread;

  private boolean isUpdatingUI = false;

  public static MasterControlFragment newInstance() {
    MasterControlFragment fragment = new MasterControlFragment();

    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_master_control, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    mHandler = new Handler();

    mMasterGain = (SeekArc) view.findViewById(R.id.master);
    mMasterGain.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {
      @Override
      public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
        //MyLog.d("DEBUG", "progress::ch1 gain = %d", i);
        if (!isUpdatingUI) {
          ((MainActivity) getActivity())
              .adjustMasterBoothGain(seekArc.getProgress(), mBoothGain.getProgress());
        }
        setDecibel(mMasterdB, i, -120, 15);
      }

      @Override
      public void onStartTrackingTouch(SeekArc seekArc) {

      }

      @Override
      public void onStopTrackingTouch(SeekArc seekArc) {
        //MyLog.d("DEBUG", "stop::ch1 gain = %d", seekArc.getProgress());
        if (!isUpdatingUI) {
          ((MainActivity) getActivity())
              .adjustMasterBoothGain(mMasterGain.getProgress(), mBoothGain.getProgress());
        }
        setDecibel(mMasterdB, seekArc.getProgress(), -120, 15);
      }
    });

    mBoothGain = (SeekArc) view.findViewById(R.id.booth);
    mBoothGain.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {
      @Override
      public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
        //MyLog.d("DEBUG", "progress::ch1 gain = %d", i);
        if (!isUpdatingUI) {
          ((MainActivity) getActivity()).adjustMasterBoothGain(mMasterGain.getProgress(), i);
        }
        setDecibel(mBoothdB, i, -120, 15);
      }

      @Override
      public void onStartTrackingTouch(SeekArc seekArc) {

      }

      @Override
      public void onStopTrackingTouch(SeekArc seekArc) {
        //MyLog.d("DEBUG", "stop::ch1 gain = %d", seekArc.getProgress());
        if (!isUpdatingUI) {
          ((MainActivity) getActivity())
              .adjustMasterBoothGain(mMasterGain.getProgress(), mBoothGain.getProgress());
        }
        setDecibel(mBoothdB, seekArc.getProgress(), -120, 15);
      }
    });

    mMonitorSelect = (SeekArc) view.findViewById(R.id.select);
    mMonitorSelect.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {
      @Override
      public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
        //MyLog.d("DEBUG", "progress::ch1 gain = %d", i);
        if (!isUpdatingUI) {
          ((MainActivity) getActivity()).adjustMonitorSelectLevel(mMonitorCh.isChecked(), i, mMonitorLevel.getProgress());
        }
      }

      @Override
      public void onStartTrackingTouch(SeekArc seekArc) {

      }

      @Override
      public void onStopTrackingTouch(SeekArc seekArc) {
        //MyLog.d("DEBUG", "stop::ch1 gain = %d", seekArc.getProgress());
        if (!isUpdatingUI) {
          ((MainActivity) getActivity())
              .adjustMonitorSelectLevel(mMonitorCh.isChecked(), mMonitorSelect.getProgress(), mMonitorLevel.getProgress());
        }
      }
    });

    mMonitorLevel = (SeekArc) view.findViewById(R.id.monitor);
    mMonitorLevel.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {
      @Override
      public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
        //MyLog.d("DEBUG", "progress::ch1 gain = %d", i);
        if (!isUpdatingUI) {
          ((MainActivity) getActivity()).adjustMonitorSelectLevel(mMonitorCh.isChecked(), mMonitorSelect.getProgress(), i);
        }
        setDecibel(mMonitordB, i, -120, 15);
      }

      @Override
      public void onStartTrackingTouch(SeekArc seekArc) {

      }

      @Override
      public void onStopTrackingTouch(SeekArc seekArc) {
        //MyLog.d("DEBUG", "stop::ch1 gain = %d", seekArc.getProgress());
        if (!isUpdatingUI) {
          ((MainActivity) getActivity())
              .adjustMonitorSelectLevel(mMonitorCh.isChecked(), mMonitorSelect.getProgress(), mMonitorLevel.getProgress());
        }
        setDecibel(mMonitordB, seekArc.getProgress(), -120, 15);
      }
    });

    mMasterdB = (TextView) view.findViewById(R.id.master_db);
    mBoothdB = (TextView) view.findViewById(R.id.booth_db);
    mMonitordB = (TextView) view.findViewById(R.id.monitor_db);

    mMonitorCh = (Switch) view.findViewById(R.id.monitor_ch_sw);
    mMonitorCh.setOnCheckedChangeListener(new OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!isUpdatingUI) {
          ((MainActivity) getActivity()).adjustMonitorSelectLevel(isChecked, mMonitorSelect.getProgress(), mMonitorLevel.getProgress());
        }
      }
    });

    mUIUpdateThread = new UIUpdateThread();
    mUIUpdateThread.start();
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

  }

  @Override
  public void onDetach() {
    super.onDetach();

    mHandler = null;

    mMasterGain = null;
    mBoothGain = null;
    mMonitorSelect = null;
    mMonitorLevel = null;
    mMasterdB = null;
    mBoothdB = null;
    mMonitordB = null;
    mMonitorCh = null;

    mUIUpdateThread = null;
  }

  private class UIUpdateThread extends Thread {

    @Override
    public void run() {
      //super.run();

      while (true) {
        if (((MainActivity) getActivity()).isUpdateUI(3)) {
          MyLog.d("DEBUG", "ui thread3... %06X", ((MainActivity) getActivity()).getDspSetting(15));

          mHandler.post(new Runnable() {
            @Override
            public void run() {
              ((MainActivity) getActivity()).resetUpdateUIFlag(3);
              isUpdatingUI = true;

              mMasterGain.setProgress(((MainActivity) getActivity()).getDspSetting(13));
              mBoothGain.setProgress(((MainActivity) getActivity()).getDspSetting(14));
              mMonitorCh.setChecked(((((MainActivity) getActivity()).getDspSetting(15) >> 7) & 0x01) == 0x01);
              mMonitorSelect.setProgress(((MainActivity) getActivity()).getDspSetting(15) & 0x7F);
              mMonitorLevel.setProgress(((MainActivity) getActivity()).getDspSetting(16));

              mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                  MyLog.d("DEBUG", "ui thread stop3");
                  isUpdatingUI = false;
                }
              }, 500);
            }
          });
        }

        try {
          Thread.sleep(100);
        } catch(InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
