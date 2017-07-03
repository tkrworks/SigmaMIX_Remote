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
 * InputControlFragment.java
 */

package net.tkrworks.sigmamixremote;

import android.content.Context;
import android.os.Bundle;
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

public class InputControlFragment extends Fragment {

  private Switch mCh1LinePhonoSw;
  private Switch mCh2LinePhonoSw;
  private SeekArc mCh1InputGain;
  private SeekArc mCh2InputGain;
  private TextView mCh1dB;
  private TextView mCh2dB;

  public InputControlFragment() {
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
    return inflater.inflate(R.layout.fragment_input_control, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    mCh1LinePhonoSw = (Switch) view.findViewById(R.id.ch1_sw);
    mCh1LinePhonoSw.setOnCheckedChangeListener(new OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        ((MainActivity) getActivity()).switchLinePhono(isChecked, mCh2LinePhonoSw.isChecked());
      }
    });

    mCh2LinePhonoSw = (Switch) view.findViewById(R.id.ch2_sw);
    mCh2LinePhonoSw.setOnCheckedChangeListener(new OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        ((MainActivity) getActivity()).switchLinePhono(mCh1LinePhonoSw.isChecked(), isChecked);
      }
    });

    mCh1InputGain = (SeekArc) view.findViewById(R.id.ch1_gain);
    mCh1InputGain.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {
      @Override
      public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
        //MyLog.d("DEBUG", "progress::ch1 gain = %d", i);
        ((MainActivity) getActivity()).adjustInputGain(i, mCh2InputGain.getProgress());
        setDecibel(mCh1dB, i, -15, 15);
      }

      @Override
      public void onStartTrackingTouch(SeekArc seekArc) {

      }

      @Override
      public void onStopTrackingTouch(SeekArc seekArc) {
        //MyLog.d("DEBUG", "stop::ch1 gain = %d", seekArc.getProgress());
        ((MainActivity) getActivity()).adjustInputGain(mCh1InputGain.getProgress(), mCh2InputGain.getProgress());
        setDecibel(mCh1dB, mCh1InputGain.getProgress(), -15, 15);
      }
    });

    mCh2InputGain = (SeekArc) view.findViewById(R.id.ch2_gain);
    mCh2InputGain.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {
      @Override
      public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
        ((MainActivity) getActivity()).adjustInputGain(mCh1InputGain.getProgress(), i);
        setDecibel(mCh2dB, i, -15, 15);
      }

      @Override
      public void onStartTrackingTouch(SeekArc seekArc) {

      }

      @Override
      public void onStopTrackingTouch(SeekArc seekArc) {
        ((MainActivity) getActivity()).adjustInputGain(mCh1InputGain.getProgress(), mCh2InputGain.getProgress());
        setDecibel(mCh2dB, mCh2InputGain.getProgress(), -15, 15);
      }
    });

    mCh1dB = (TextView) view.findViewById(R.id.ch1_db);
    mCh2dB = (TextView) view.findViewById(R.id.ch2_db);
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
