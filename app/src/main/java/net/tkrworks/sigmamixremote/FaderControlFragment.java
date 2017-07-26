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
 * FaderControlFragment.java
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
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import com.triggertrap.seekarc.SeekArc;
import com.triggertrap.seekarc.SeekArc.OnSeekArcChangeListener;


public class FaderControlFragment extends Fragment {

  private Handler mHandler;

  private SeekBar mCh1Volume;
  private SeekBar mCh2Volume;
  private Switch mIfReverse;
  private Switch mXfReverse;
  private SeekArc mIfCurve;
  private SeekArc mXfCurve;

  private UIUpdateThread mUIUpdateThread;

  private boolean isUpdatingUI = false;

  public static FaderControlFragment newInstance() {
    FaderControlFragment fragment = new FaderControlFragment();

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
    return inflater.inflate(R.layout.fragment_fader_control, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    mHandler = new Handler();

    mCh1Volume = (SeekBar) view.findViewById(R.id.ch1_if);
    mCh1Volume.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!isUpdatingUI) {
          ((MainActivity) getActivity()).adjustVolume(progress, mCh2Volume.getProgress());
        }
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
        if (!isUpdatingUI) {
          ((MainActivity) getActivity())
              .adjustVolume(mCh1Volume.getProgress(), mCh2Volume.getProgress());
        }
      }
    });

    mCh2Volume = (SeekBar) view.findViewById(R.id.ch2_if);
    mCh2Volume.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (!isUpdatingUI) {
          ((MainActivity) getActivity()).adjustVolume(mCh1Volume.getProgress(), progress);
        }
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {
        if (!isUpdatingUI) {
          ((MainActivity) getActivity())
              .adjustVolume(mCh1Volume.getProgress(), mCh2Volume.getProgress());
        }
      }
    });

    mIfReverse = (Switch) view.findViewById(R.id.if_rev_sw);
    mIfReverse.setOnCheckedChangeListener(new OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!isUpdatingUI) {
          ((MainActivity) getActivity()).adjustIFaderSetting(isChecked, mIfCurve.getProgress());
        }
      }
    });

    mIfCurve = (SeekArc) view.findViewById(R.id.if_curve);
    mIfCurve.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {
      @Override
      public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
        if (!isUpdatingUI) {
          ((MainActivity) getActivity()).adjustIFaderSetting(mIfReverse.isChecked(), i);
        }
      }

      @Override
      public void onStartTrackingTouch(SeekArc seekArc) {

      }

      @Override
      public void onStopTrackingTouch(SeekArc seekArc) {
        if (!isUpdatingUI) {
          ((MainActivity) getActivity())
              .adjustIFaderSetting(mIfReverse.isChecked(), mIfCurve.getProgress());
        }
      }
    });

    mXfReverse = (Switch) view.findViewById(R.id.xf_rev_sw);
    mXfReverse.setOnCheckedChangeListener(new OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!isUpdatingUI) {
          ((MainActivity) getActivity()).adjustXFaderSetting(isChecked, mXfCurve.getProgress());
        }
      }
    });

    mXfCurve = (SeekArc) view.findViewById(R.id.xf_curve);
    mXfCurve.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {
      @Override
      public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
        if (!isUpdatingUI) {
          ((MainActivity) getActivity()).adjustXFaderSetting(mXfReverse.isChecked(), i);
        }
      }

      @Override
      public void onStartTrackingTouch(SeekArc seekArc) {

      }

      @Override
      public void onStopTrackingTouch(SeekArc seekArc) {
        if (!isUpdatingUI) {
          ((MainActivity) getActivity())
              .adjustXFaderSetting(mXfReverse.isChecked(), mXfCurve.getProgress());
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

    mCh1Volume = null;
    mCh2Volume = null;
    mIfReverse = null;
    mXfReverse = null;
    mIfCurve = null;
    mXfCurve = null;

    mUIUpdateThread = null;
  }

  private class UIUpdateThread extends Thread {

    @Override
    public void run() {
      //super.run();

      while (true) {
        if (((MainActivity) getActivity()).isUpdateUI(2)) {
          MyLog.d("DEBUG", "ui thread2...");

          mHandler.post(new Runnable() {
            @Override
            public void run() {
              ((MainActivity) getActivity()).resetUpdateUIFlag(2);
              isUpdatingUI = true;

              mCh1Volume.setProgress(((MainActivity) getActivity()).getDspSetting(9));
              mCh2Volume.setProgress(((MainActivity) getActivity()).getDspSetting(10));

              mIfReverse.setChecked(((((MainActivity) getActivity()).getDspSetting(11) >> 4) & 0x01) == 0x01);
              mXfReverse.setChecked(((((MainActivity) getActivity()).getDspSetting(12) >> 4) & 0x01) == 0x01);

              mIfCurve.setProgress(((MainActivity) getActivity()).getDspSetting(11) & 0x0F);
              mXfCurve.setProgress(((MainActivity) getActivity()).getDspSetting(12) & 0x0F);

              mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                  MyLog.d("DEBUG", "ui thread stop2");
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
