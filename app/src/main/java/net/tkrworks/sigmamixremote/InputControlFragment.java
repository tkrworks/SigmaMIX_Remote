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
 * InputControlFragment.java
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import com.triggertrap.seekarc.SeekArc;
import com.triggertrap.seekarc.SeekArc.OnSeekArcChangeListener;

import static net.tkrworks.sigmamixremote.MyTextViewControl.*;

public class InputControlFragment extends Fragment {

  private Handler mHandler;

  private Switch mCh1LinePhonoSw;
  private Switch mCh2LinePhonoSw;
  private SeekArc mCh1InputGain;
  private SeekArc mCh2InputGain;
  private TextView mCh1dB;
  private TextView mCh2dB;
  private Spinner mFxType;
  private SeekArc mDelayTime;
  private SeekArc mFeedbackGain;
  private TextView mDTimeMs;
  private TextView mFbdB;

  private UIUpdateThread mUIUpdateThread;

  private boolean isUpdatingUI = false;

  public static InputControlFragment newInstance() {
    InputControlFragment fragment = new InputControlFragment();

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
    return inflater.inflate(R.layout.fragment_input_control, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    MyLog.d("DEBUG", "INPUT:: onViewCreated");

    mHandler = new Handler();

    mCh1LinePhonoSw = (Switch) view.findViewById(R.id.ch1_sw);
    mCh1LinePhonoSw.setOnCheckedChangeListener(new OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!isUpdatingUI) {
          ((MainActivity) getActivity()).switchLinePhono(isChecked, mCh2LinePhonoSw.isChecked());
        }
      }
    });

    mCh2LinePhonoSw = (Switch) view.findViewById(R.id.ch2_sw);
    mCh2LinePhonoSw.setOnCheckedChangeListener(new OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!isUpdatingUI) {
          ((MainActivity) getActivity()).switchLinePhono(mCh1LinePhonoSw.isChecked(), isChecked);
        }
      }
    });

    mCh1InputGain = (SeekArc) view.findViewById(R.id.ch1_gain);
    mCh1InputGain.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {
      @Override
      public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
        //MyLog.d("DEBUG", "progress::ch1 gain = %d", i);
        if (!isUpdatingUI) {
          ((MainActivity) getActivity()).adjustInputGain(i, mCh2InputGain.getProgress());
        }
        setDecibel(mCh1dB, i, -15, 15);
      }

      @Override
      public void onStartTrackingTouch(SeekArc seekArc) {

      }

      @Override
      public void onStopTrackingTouch(SeekArc seekArc) {
        //MyLog.d("DEBUG", "stop::ch1 gain = %d", seekArc.getProgress());
        if (!isUpdatingUI) {
          ((MainActivity) getActivity())
              .adjustInputGain(mCh1InputGain.getProgress(), mCh2InputGain.getProgress());
        }
        setDecibel(mCh1dB, mCh1InputGain.getProgress(), -15, 15);
      }
    });

    mCh2InputGain = (SeekArc) view.findViewById(R.id.ch2_gain);
    mCh2InputGain.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {
      @Override
      public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
        if (!isUpdatingUI) {
          ((MainActivity) getActivity()).adjustInputGain(mCh1InputGain.getProgress(), i);
        }
        setDecibel(mCh2dB, i, -15, 15);
      }

      @Override
      public void onStartTrackingTouch(SeekArc seekArc) {

      }

      @Override
      public void onStopTrackingTouch(SeekArc seekArc) {
        if (!isUpdatingUI) {
          ((MainActivity) getActivity())
              .adjustInputGain(mCh1InputGain.getProgress(), mCh2InputGain.getProgress());
        }
        setDecibel(mCh2dB, mCh2InputGain.getProgress(), -15, 15);
      }
    });

    mCh1dB = (TextView) view.findViewById(R.id.ch1_db);
    mCh2dB = (TextView) view.findViewById(R.id.ch2_db);

    mFxType = (Spinner) view.findViewById(R.id.fx_type);
    mFxType.setOnItemSelectedListener(new OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (!isUpdatingUI) {
          ((MainActivity) getActivity()).selectEffectType(position);
        }
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });

    mDelayTime = (SeekArc) view.findViewById(R.id.delay_time);
    mDelayTime.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {
      @Override
      public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
        if (!isUpdatingUI) {
          ((MainActivity) getActivity()).adjustDelayParams(i, mFeedbackGain.getProgress());
        }
        setMilliSeconds(mDTimeMs, i, 0, 300);
      }

      @Override
      public void onStartTrackingTouch(SeekArc seekArc) {

      }

      @Override
      public void onStopTrackingTouch(SeekArc seekArc) {
        if (!isUpdatingUI) {
          ((MainActivity) getActivity())
              .adjustDelayParams(mDelayTime.getProgress(), mFeedbackGain.getProgress());
        }
        setMilliSeconds(mDTimeMs, mDelayTime.getProgress(), 0, 300);
      }
    });

    mFeedbackGain = (SeekArc) view.findViewById(R.id.fb_gain);
    mFeedbackGain.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {
      @Override
      public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
        if (!isUpdatingUI) {
          ((MainActivity) getActivity()).adjustDelayParams(mDelayTime.getProgress(), i);
        }
        setDecibel2(mFbdB, i, -30, 0);
      }

      @Override
      public void onStartTrackingTouch(SeekArc seekArc) {

      }

      @Override
      public void onStopTrackingTouch(SeekArc seekArc) {
        if (!isUpdatingUI) {
          ((MainActivity) getActivity())
              .adjustDelayParams(mDelayTime.getProgress(), mFeedbackGain.getProgress());
        }
        setDecibel2(mFbdB, mFeedbackGain.getProgress(), -30, 0);
      }
    });

    mDTimeMs = (TextView) view.findViewById(R.id.delay_msec);
    mFbdB = (TextView) view.findViewById(R.id.fb_db);

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

    MyLog.d("DEBUG", "INPUT:: onDetach");

    mHandler = null;

    mCh1LinePhonoSw = null;
    mCh2LinePhonoSw = null;
    mCh1InputGain = null;
    mCh2InputGain = null;
    mCh1dB = null;
    mCh2dB = null;

    mUIUpdateThread = null;
  }

  private class UIUpdateThread extends Thread {

    @Override
    public void run() {
      //super.run();

      while (true) {
        if (((MainActivity) getActivity()).isUpdateUI(0)) {
          MyLog.d("DEBUG", "ui thread0...");

          mHandler.post(new Runnable() {
            @Override
            public void run() {
              ((MainActivity) getActivity()).resetUpdateUIFlag(0);
              isUpdatingUI = true;

              mCh1LinePhonoSw.setChecked((((MainActivity) getActivity()).getDspSetting(0) >> 4 & 0x01) == 0x01);
              mCh2LinePhonoSw.setChecked((((MainActivity) getActivity()).getDspSetting(0) & 0x01) == 0x01);
              mCh1InputGain.setProgress(((MainActivity) getActivity()).getDspSetting(1));
              mCh2InputGain.setProgress(((MainActivity) getActivity()).getDspSetting(2));
              mFxType.setSelection(((MainActivity) getActivity()).getDspSetting(17));
              mDelayTime.setProgress(((MainActivity) getActivity()).getDspSetting(19));
              mFeedbackGain.setProgress(((MainActivity) getActivity()).getDspSetting(18));

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
