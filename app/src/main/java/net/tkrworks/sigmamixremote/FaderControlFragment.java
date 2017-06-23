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
import android.widget.SeekBar;
import android.widget.Switch;
import com.triggertrap.seekarc.SeekArc;
import com.triggertrap.seekarc.SeekArc.OnSeekArcChangeListener;


public class FaderControlFragment extends Fragment {

  private SeekBar mCh1Volume;
  private SeekBar mCh2Volume;
  private Switch mIfReverse;
  private Switch mXfReverse;
  private SeekArc mIfCurve;
  private SeekArc mXfCurve;

  public FaderControlFragment() {
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
    return inflater.inflate(R.layout.fragment_fader_control, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    mIfReverse = (Switch) view.findViewById(R.id.if_rev_sw);
    mIfReverse.setOnCheckedChangeListener(new OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        ((MainActivity) getActivity()).adjustIFaderSetting(isChecked, mIfCurve.getProgress());
      }
    });

    mIfCurve = (SeekArc) view.findViewById(R.id.if_curve);
    mIfCurve.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {
      @Override
      public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
        ((MainActivity) getActivity()).adjustIFaderSetting(mIfReverse.isChecked(), i);
      }

      @Override
      public void onStartTrackingTouch(SeekArc seekArc) {

      }

      @Override
      public void onStopTrackingTouch(SeekArc seekArc) {
        ((MainActivity) getActivity()).adjustIFaderSetting(mIfReverse.isChecked(), mIfCurve.getProgress());
      }
    });

    mXfReverse = (Switch) view.findViewById(R.id.xf_rev_sw);
    mXfReverse.setOnCheckedChangeListener(new OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        ((MainActivity) getActivity()).adjustXFaderSetting(isChecked, mXfCurve.getProgress());
      }
    });

    mXfCurve = (SeekArc) view.findViewById(R.id.xf_curve);
    mXfCurve.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {
      @Override
      public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
        ((MainActivity) getActivity()).adjustXFaderSetting(mXfReverse.isChecked(), i);
      }

      @Override
      public void onStartTrackingTouch(SeekArc seekArc) {

      }

      @Override
      public void onStopTrackingTouch(SeekArc seekArc) {
        ((MainActivity) getActivity()).adjustXFaderSetting(mXfReverse.isChecked(), mXfCurve.getProgress());
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
