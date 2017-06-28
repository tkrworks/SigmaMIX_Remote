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

public class KnobControlFragment extends Fragment {

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
        setDecibel(mCh1dBHi, i, -42, 15);
      }

      @Override
      public void onStartTrackingTouch(SeekArc seekArc) {

      }

      @Override
      public void onStopTrackingTouch(SeekArc seekArc) {
        ((MainActivity) getActivity()).adjust3BandEqHi(mCh1EqHi.getProgress(), mCh2EqHi.getProgress());
        setDecibel(mCh1dBHi, seekArc.getProgress(), -42, 15);
      }
    });

    mCh2EqHi = (SeekArc) view.findViewById(R.id.ch2_hi);
    mCh2EqHi.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {
      @Override
      public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
        ((MainActivity) getActivity()).adjust3BandEqHi(mCh1EqHi.getProgress(), i);
        setDecibel(mCh2dBHi, i, -42, 15);
      }

      @Override
      public void onStartTrackingTouch(SeekArc seekArc) {

      }

      @Override
      public void onStopTrackingTouch(SeekArc seekArc) {
        ((MainActivity) getActivity()).adjust3BandEqHi(mCh1EqHi.getProgress(), mCh2EqHi.getProgress());
        setDecibel(mCh2dBHi, seekArc.getProgress(), -42, 15);
      }
    });

    mCh1EqMid = (SeekArc) view.findViewById(R.id.ch1_mid);
    mCh1EqMid.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {
      @Override
      public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
        ((MainActivity) getActivity()).adjust3BandEqMid(i, mCh2EqMid.getProgress());
        setDecibel(mCh1dBMid, i, -42, 15);
      }

      @Override
      public void onStartTrackingTouch(SeekArc seekArc) {

      }

      @Override
      public void onStopTrackingTouch(SeekArc seekArc) {
        ((MainActivity) getActivity()).adjust3BandEqMid(mCh1EqMid.getProgress(), mCh2EqMid.getProgress());
        setDecibel(mCh1dBMid, seekArc.getProgress(), -42, 15);
      }
    });

    mCh2EqMid = (SeekArc) view.findViewById(R.id.ch2_mid);
    mCh2EqMid.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {
      @Override
      public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
        ((MainActivity) getActivity()).adjust3BandEqMid(mCh1EqMid.getProgress(), i);
        setDecibel(mCh2dBMid, i, -42, 15);
      }

      @Override
      public void onStartTrackingTouch(SeekArc seekArc) {

      }

      @Override
      public void onStopTrackingTouch(SeekArc seekArc) {
        ((MainActivity) getActivity()).adjust3BandEqMid(mCh1EqMid.getProgress(), mCh2EqMid.getProgress());
        setDecibel(mCh2dBMid, seekArc.getProgress(), -42, 15);
      }
    });

    mCh1EqLow = (SeekArc) view.findViewById(R.id.ch1_low);
    mCh1EqLow.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {
      @Override
      public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
        ((MainActivity) getActivity()).adjust3BandEqLow(i, mCh2EqLow.getProgress());
        setDecibel(mCh1dBLow, i, -42, 15);
      }

      @Override
      public void onStartTrackingTouch(SeekArc seekArc) {

      }

      @Override
      public void onStopTrackingTouch(SeekArc seekArc) {
        ((MainActivity) getActivity()).adjust3BandEqLow(mCh1EqLow.getProgress(), mCh2EqLow.getProgress());
        setDecibel(mCh1dBLow, seekArc.getProgress(), -42, 15);
      }
    });

    mCh2EqLow = (SeekArc) view.findViewById(R.id.ch2_low);
    mCh2EqLow.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {
      @Override
      public void onProgressChanged(SeekArc seekArc, int i, boolean b) {
        ((MainActivity) getActivity()).adjust3BandEqLow(mCh1EqLow.getProgress(), i);
        setDecibel(mCh2dBLow, i, -42, 15);
      }

      @Override
      public void onStartTrackingTouch(SeekArc seekArc) {

      }

      @Override
      public void onStopTrackingTouch(SeekArc seekArc) {
        ((MainActivity) getActivity()).adjust3BandEqLow(mCh1EqLow.getProgress(), mCh2EqLow.getProgress());
        setDecibel(mCh2dBLow, seekArc.getProgress(), -42, 15);
      }
    });

    mCh1dBHi = (TextView) view.findViewById(R.id.ch1_db_hi);
    mCh2dBHi = (TextView) view.findViewById(R.id.ch2_db_hi);;
    mCh1dBMid = (TextView) view.findViewById(R.id.ch1_db_mid);;
    mCh2dBMid = (TextView) view.findViewById(R.id.ch2_db_mid);;
    mCh1dBLow = (TextView) view.findViewById(R.id.ch1_db_low);;
    mCh2dBLow = (TextView) view.findViewById(R.id.ch2_db_low);;
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
