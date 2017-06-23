package net.tkrworks.sigmamixremote;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.triggertrap.seekarc.SeekArc;
import com.triggertrap.seekarc.SeekArc.OnSeekArcChangeListener;


public class MasterControlFragment extends Fragment {

  private SeekArc mMasterGain;
  private SeekArc mBoothGain;
  private SeekArc mMonitorSelect;
  private SeekArc mMonitorLevel;

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
      }

      @Override
      public void onStartTrackingTouch(SeekArc seekArc) {

      }

      @Override
      public void onStopTrackingTouch(SeekArc seekArc) {
        //MyLog.d("DEBUG", "stop::ch1 gain = %d", seekArc.getProgress());
        ((MainActivity) getActivity()).adjustMasterBoothGain(mMasterGain.getProgress(), mBoothGain.getProgress());
      }
    });

    mBoothGain = (SeekArc) view.findViewById(R.id.booth);
    mBoothGain.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {
      @Override
      public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
        //MyLog.d("DEBUG", "progress::ch1 gain = %d", i);
        ((MainActivity) getActivity()).adjustMasterBoothGain(mMasterGain.getProgress(), i);
      }

      @Override
      public void onStartTrackingTouch(SeekArc seekArc) {

      }

      @Override
      public void onStopTrackingTouch(SeekArc seekArc) {
        //MyLog.d("DEBUG", "stop::ch1 gain = %d", seekArc.getProgress());
        ((MainActivity) getActivity()).adjustMasterBoothGain(mMasterGain.getProgress(), mBoothGain.getProgress());
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
