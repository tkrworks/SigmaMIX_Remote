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

public class KnobControlFragment extends Fragment {

  private SeekArc mCh1EqHi;
  private SeekArc mCh2EqHi;
  private SeekArc mCh1EqMid;
  private SeekArc mCh2EqMid;
  private SeekArc mCh1EqLow;
  private SeekArc mCh2EqLow;

  public KnobControlFragment() {
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
    return inflater.inflate(R.layout.fragment_knob_control, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    mCh1EqHi = (SeekArc) view.findViewById(R.id.ch1_hi);
    mCh1EqHi.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {
      @Override
      public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
        ((MainActivity) getActivity()).adjust3BandEqHi(i, mCh2EqHi.getProgress());
      }

      @Override
      public void onStartTrackingTouch(SeekArc seekArc) {

      }

      @Override
      public void onStopTrackingTouch(SeekArc seekArc) {
        ((MainActivity) getActivity()).adjust3BandEqHi(mCh1EqHi.getProgress(), mCh2EqHi.getProgress());
      }
    });

    mCh2EqHi = (SeekArc) view.findViewById(R.id.ch2_hi);
    mCh2EqHi.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {
      @Override
      public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
        ((MainActivity) getActivity()).adjust3BandEqHi(mCh1EqHi.getProgress(), i);
      }

      @Override
      public void onStartTrackingTouch(SeekArc seekArc) {

      }

      @Override
      public void onStopTrackingTouch(SeekArc seekArc) {
        ((MainActivity) getActivity()).adjust3BandEqHi(mCh1EqHi.getProgress(), mCh2EqHi.getProgress());
      }
    });

    mCh1EqMid = (SeekArc) view.findViewById(R.id.ch1_mid);
    mCh1EqMid.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {
      @Override
      public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
        ((MainActivity) getActivity()).adjust3BandEqMid(i, mCh2EqMid.getProgress());
      }

      @Override
      public void onStartTrackingTouch(SeekArc seekArc) {

      }

      @Override
      public void onStopTrackingTouch(SeekArc seekArc) {
        ((MainActivity) getActivity()).adjust3BandEqMid(mCh1EqMid.getProgress(), mCh2EqMid.getProgress());
      }
    });

    mCh2EqMid = (SeekArc) view.findViewById(R.id.ch2_mid);
    mCh2EqMid.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {
      @Override
      public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
        ((MainActivity) getActivity()).adjust3BandEqMid(mCh1EqMid.getProgress(), i);
      }

      @Override
      public void onStartTrackingTouch(SeekArc seekArc) {

      }

      @Override
      public void onStopTrackingTouch(SeekArc seekArc) {
        ((MainActivity) getActivity()).adjust3BandEqMid(mCh1EqMid.getProgress(), mCh2EqMid.getProgress());
      }
    });

    mCh1EqLow = (SeekArc) view.findViewById(R.id.ch1_low);
    mCh1EqLow.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {
      @Override
      public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
        ((MainActivity) getActivity()).adjust3BandEqLow(i, mCh2EqLow.getProgress());
      }

      @Override
      public void onStartTrackingTouch(SeekArc seekArc) {

      }

      @Override
      public void onStopTrackingTouch(SeekArc seekArc) {
        ((MainActivity) getActivity()).adjust3BandEqLow(mCh1EqLow.getProgress(), mCh2EqLow.getProgress());
      }
    });

    mCh2EqLow = (SeekArc) view.findViewById(R.id.ch2_low);
    mCh2EqLow.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {
      @Override
      public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
        ((MainActivity) getActivity()).adjust3BandEqLow(mCh1EqLow.getProgress(), i);
      }

      @Override
      public void onStartTrackingTouch(SeekArc seekArc) {

      }

      @Override
      public void onStopTrackingTouch(SeekArc seekArc) {
        ((MainActivity) getActivity()).adjust3BandEqLow(mCh1EqLow.getProgress(), mCh2EqLow.getProgress());
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
