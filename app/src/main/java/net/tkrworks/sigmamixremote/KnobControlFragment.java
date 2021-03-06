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
 * KnobControlFragment.java
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
import android.widget.TextView;
import com.triggertrap.seekarc.SeekArc;
import com.triggertrap.seekarc.SeekArc.OnSeekArcChangeListener;

import static net.tkrworks.sigmamixremote.MyTextViewControl.*;

public class KnobControlFragment extends Fragment {

  private Handler mHandler;

  private SeekArc mCh1EqHi;
  private SeekArc mCh2EqHi;
  private SeekArc mCh1EqMid;
  private SeekArc mCh2EqMid;
  private SeekArc mCh1EqLow;
  private SeekArc mCh2EqLow;
  private TextView mCh1dBHi;
  private TextView mCh2dBHi;
  private TextView mCh1dBMid;
  private TextView mCh2dBMid;
  private TextView mCh1dBLow;
  private TextView mCh2dBLow;

  private UIUpdateThread mUIUpdateThread;

  private boolean isUpdatingUI = false;

  public static KnobControlFragment newInstance() {
    KnobControlFragment fragment = new KnobControlFragment();

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
    return inflater.inflate(R.layout.fragment_knob_control, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    mHandler = new Handler();

    mCh1EqHi = (SeekArc) view.findViewById(R.id.ch1_hi);
    mCh1EqHi.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {
      @Override
      public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
        if (!isUpdatingUI) {
          ((MainActivity) getActivity()).adjust3BandEqHi(i, mCh2EqHi.getProgress());
        }
        setDecibel(mCh1dBHi, i, -42, 15);
      }

      @Override
      public void onStartTrackingTouch(SeekArc seekArc) {

      }

      @Override
      public void onStopTrackingTouch(SeekArc seekArc) {
        if (!isUpdatingUI) {
          ((MainActivity) getActivity())
              .adjust3BandEqHi(mCh1EqHi.getProgress(), mCh2EqHi.getProgress());
        }
        setDecibel(mCh1dBHi, seekArc.getProgress(), -42, 15);
      }
    });

    mCh2EqHi = (SeekArc) view.findViewById(R.id.ch2_hi);
    mCh2EqHi.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {
      @Override
      public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
        if (!isUpdatingUI) {
          ((MainActivity) getActivity()).adjust3BandEqHi(mCh1EqHi.getProgress(), i);
        }
        setDecibel(mCh2dBHi, i, -42, 15);
      }

      @Override
      public void onStartTrackingTouch(SeekArc seekArc) {

      }

      @Override
      public void onStopTrackingTouch(SeekArc seekArc) {
        if (!isUpdatingUI) {
          ((MainActivity) getActivity())
              .adjust3BandEqHi(mCh1EqHi.getProgress(), mCh2EqHi.getProgress());
        }
        setDecibel(mCh2dBHi, seekArc.getProgress(), -42, 15);
      }
    });

    mCh1EqMid = (SeekArc) view.findViewById(R.id.ch1_mid);
    mCh1EqMid.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {
      @Override
      public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
        if (!isUpdatingUI) {
          ((MainActivity) getActivity()).adjust3BandEqMid(i, mCh2EqMid.getProgress());
        }
        setDecibel(mCh1dBMid, i, -42, 15);
      }

      @Override
      public void onStartTrackingTouch(SeekArc seekArc) {

      }

      @Override
      public void onStopTrackingTouch(SeekArc seekArc) {
        if (!isUpdatingUI) {
          ((MainActivity) getActivity())
              .adjust3BandEqMid(mCh1EqMid.getProgress(), mCh2EqMid.getProgress());
        }
        setDecibel(mCh1dBMid, seekArc.getProgress(), -42, 15);
      }
    });

    mCh2EqMid = (SeekArc) view.findViewById(R.id.ch2_mid);
    mCh2EqMid.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {
      @Override
      public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
        if (!isUpdatingUI) {
          ((MainActivity) getActivity()).adjust3BandEqMid(mCh1EqMid.getProgress(), i);
        }
        setDecibel(mCh2dBMid, i, -42, 15);
      }

      @Override
      public void onStartTrackingTouch(SeekArc seekArc) {

      }

      @Override
      public void onStopTrackingTouch(SeekArc seekArc) {
        if (!isUpdatingUI) {
          ((MainActivity) getActivity())
              .adjust3BandEqMid(mCh1EqMid.getProgress(), mCh2EqMid.getProgress());
        }
        setDecibel(mCh2dBMid, seekArc.getProgress(), -42, 15);
      }
    });

    mCh1EqLow = (SeekArc) view.findViewById(R.id.ch1_low);
    mCh1EqLow.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {
      @Override
      public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
        if (!isUpdatingUI) {
          ((MainActivity) getActivity()).adjust3BandEqLow(i, mCh2EqLow.getProgress());
        }
        setDecibel(mCh1dBLow, i, -42, 15);
      }

      @Override
      public void onStartTrackingTouch(SeekArc seekArc) {

      }

      @Override
      public void onStopTrackingTouch(SeekArc seekArc) {
        if (!isUpdatingUI) {
          ((MainActivity) getActivity())
              .adjust3BandEqLow(mCh1EqLow.getProgress(), mCh2EqLow.getProgress());
        }
        setDecibel(mCh1dBLow, seekArc.getProgress(), -42, 15);
      }
    });

    mCh2EqLow = (SeekArc) view.findViewById(R.id.ch2_low);
    mCh2EqLow.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {
      @Override
      public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
        if (!isUpdatingUI) {
          ((MainActivity) getActivity()).adjust3BandEqLow(mCh1EqLow.getProgress(), i);
        }
        setDecibel(mCh2dBLow, i, -42, 15);
      }

      @Override
      public void onStartTrackingTouch(SeekArc seekArc) {

      }

      @Override
      public void onStopTrackingTouch(SeekArc seekArc) {
        if (!isUpdatingUI) {
          ((MainActivity) getActivity())
              .adjust3BandEqLow(mCh1EqLow.getProgress(), mCh2EqLow.getProgress());
        }
        setDecibel(mCh2dBLow, seekArc.getProgress(), -42, 15);
      }
    });

    mCh1dBHi = (TextView) view.findViewById(R.id.ch1_db_hi);
    mCh2dBHi = (TextView) view.findViewById(R.id.ch2_db_hi);;
    mCh1dBMid = (TextView) view.findViewById(R.id.ch1_db_mid);;
    mCh2dBMid = (TextView) view.findViewById(R.id.ch2_db_mid);;
    mCh1dBLow = (TextView) view.findViewById(R.id.ch1_db_low);;
    mCh2dBLow = (TextView) view.findViewById(R.id.ch2_db_low);;

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

    mCh1EqHi = null;
    mCh2EqHi = null;
    mCh1EqMid = null;
    mCh2EqMid = null;
    mCh1EqLow = null;
    mCh2EqLow = null;
    mCh1dBHi = null;
    mCh2dBHi = null;
    mCh1dBMid = null;
    mCh2dBMid = null;
    mCh1dBLow = null;
    mCh2dBLow = null;

    mUIUpdateThread = null;
  }

  private class UIUpdateThread extends Thread {

    @Override
    public void run() {
      //super.run();

      while (true) {
        if (((MainActivity) getActivity()).isUpdateUI(1)) {
          MyLog.d("DEBUG", "ui thread1...");

          mHandler.post(new Runnable() {
            @Override
            public void run() {
              ((MainActivity) getActivity()).resetUpdateUIFlag(1);
              isUpdatingUI = true;

              mCh1EqHi.setProgress(((MainActivity) getActivity()).getDspSetting(3));
              mCh2EqHi.setProgress(((MainActivity) getActivity()).getDspSetting(4));
              mCh1EqMid.setProgress(((MainActivity) getActivity()).getDspSetting(5));
              mCh2EqMid.setProgress(((MainActivity) getActivity()).getDspSetting(6));
              mCh1EqLow.setProgress(((MainActivity) getActivity()).getDspSetting(7));
              mCh2EqLow.setProgress(((MainActivity) getActivity()).getDspSetting(8));

              mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                  MyLog.d("DEBUG", "ui thread stop1");
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
