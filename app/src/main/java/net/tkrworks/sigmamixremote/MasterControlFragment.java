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
 * MasterControlFragment.java
 */

package net.tkrworks.sigmamixremote;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.triggertrap.seekarc.SeekArc;
import com.triggertrap.seekarc.SeekArc.OnSeekArcChangeListener;

import static net.tkrworks.sigmamixremote.MyTextViewControl.*;

public class MasterControlFragment extends Fragment {

  private SeekArc mMasterGain;
  private SeekArc mBoothGain;
  private SeekArc mMonitorSelect;
  private SeekArc mMonitorLevel;
  private TextView mMasterdB;
  private TextView mBoothdB;
  private TextView mSelectRate;
  private TextView mMonitordB;

  public MasterControlFragment() {
    // Required empty public constructor
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

    mMasterGain = (SeekArc) view.findViewById(R.id.master);
    mMasterGain.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {
      @Override
      public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
        //MyLog.d("DEBUG", "progress::ch1 gain = %d", i);
        ((MainActivity) getActivity()).adjustMasterBoothGain(seekArc.getProgress(), mBoothGain.getProgress());
        setDecibel(mMasterdB, i, -120, 15);
      }

      @Override
      public void onStartTrackingTouch(SeekArc seekArc) {

      }

      @Override
      public void onStopTrackingTouch(SeekArc seekArc) {
        //MyLog.d("DEBUG", "stop::ch1 gain = %d", seekArc.getProgress());
        ((MainActivity) getActivity()).adjustMasterBoothGain(mMasterGain.getProgress(), mBoothGain.getProgress());
        setDecibel(mMasterdB, seekArc.getProgress(), -120, 15);
      }
    });

    mBoothGain = (SeekArc) view.findViewById(R.id.booth);
    mBoothGain.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {
      @Override
      public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
        //MyLog.d("DEBUG", "progress::ch1 gain = %d", i);
        ((MainActivity) getActivity()).adjustMasterBoothGain(mMasterGain.getProgress(), i);
        setDecibel(mBoothdB, i, -120, 15);
      }

      @Override
      public void onStartTrackingTouch(SeekArc seekArc) {

      }

      @Override
      public void onStopTrackingTouch(SeekArc seekArc) {
        //MyLog.d("DEBUG", "stop::ch1 gain = %d", seekArc.getProgress());
        ((MainActivity) getActivity()).adjustMasterBoothGain(mMasterGain.getProgress(), mBoothGain.getProgress());
        setDecibel(mBoothdB, seekArc.getProgress(), -120, 15);
      }
    });

    mMonitorSelect = (SeekArc) view.findViewById(R.id.select);
    mMonitorSelect.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {
      @Override
      public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
        //MyLog.d("DEBUG", "progress::ch1 gain = %d", i);
        ((MainActivity) getActivity()).adjustMonitorSelectLevel(i, mMonitorSelect.getProgress());
      }

      @Override
      public void onStartTrackingTouch(SeekArc seekArc) {

      }

      @Override
      public void onStopTrackingTouch(SeekArc seekArc) {
        //MyLog.d("DEBUG", "stop::ch1 gain = %d", seekArc.getProgress());
        ((MainActivity) getActivity()).adjustMonitorSelectLevel(mMonitorSelect.getProgress(), mMonitorLevel.getProgress());
      }
    });

    mMonitorLevel = (SeekArc) view.findViewById(R.id.monitor);
    mMonitorLevel.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {
      @Override
      public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
        //MyLog.d("DEBUG", "progress::ch1 gain = %d", i);
        ((MainActivity) getActivity()).adjustMonitorSelectLevel(mMonitorSelect.getProgress(), i);
        setDecibel(mMonitordB, i, -120, 15);
      }

      @Override
      public void onStartTrackingTouch(SeekArc seekArc) {

      }

      @Override
      public void onStopTrackingTouch(SeekArc seekArc) {
        //MyLog.d("DEBUG", "stop::ch1 gain = %d", seekArc.getProgress());
        ((MainActivity) getActivity()).adjustMonitorSelectLevel(mMonitorSelect.getProgress(), mMonitorLevel.getProgress());
        setDecibel(mMonitordB, seekArc.getProgress(), -120, 15);
      }
    });

    mMasterdB = (TextView) view.findViewById(R.id.master_db);
    mBoothdB = (TextView) view.findViewById(R.id.booth_db);
    mSelectRate = (TextView) view.findViewById(R.id.select_rate);
    mMonitordB = (TextView) view.findViewById(R.id.monitor_db);
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);

  }

  @Override
  public void onDetach() {
    super.onDetach();

  }
}
