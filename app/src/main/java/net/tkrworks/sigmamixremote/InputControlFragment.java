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
import com.triggertrap.seekarc.SeekArc;
import com.triggertrap.seekarc.SeekArc.OnSeekArcChangeListener;


public class InputControlFragment extends Fragment {

  private Switch mCh1LinePhonoSw;
  private Switch mCh2LinePhonoSw;
  private SeekArc mCh1InputGain;
  private SeekArc mCh2InputGain;

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
      }

      @Override
      public void onStartTrackingTouch(SeekArc seekArc) {

      }

      @Override
      public void onStopTrackingTouch(SeekArc seekArc) {
        //MyLog.d("DEBUG", "stop::ch1 gain = %d", seekArc.getProgress());
        ((MainActivity) getActivity()).adjustInputGain(mCh1InputGain.getProgress(), mCh2InputGain.getProgress());
      }
    });

    mCh2InputGain = (SeekArc) view.findViewById(R.id.ch2_gain);
    mCh2InputGain.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {
      @Override
      public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
        ((MainActivity) getActivity()).adjustInputGain(mCh1InputGain.getProgress(), i);
      }

      @Override
      public void onStartTrackingTouch(SeekArc seekArc) {

      }

      @Override
      public void onStopTrackingTouch(SeekArc seekArc) {
        ((MainActivity) getActivity()).adjustInputGain(mCh1InputGain.getProgress(), mCh2InputGain.getProgress());
      }
    });
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
