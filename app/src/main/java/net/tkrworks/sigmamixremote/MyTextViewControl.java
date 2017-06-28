package net.tkrworks.sigmamixremote;

import android.widget.TextView;
import java.util.Locale;

/**
 * Created by shun on 2017/06/28.
 */

public class MyTextViewControl {
  public static void setDecibel(TextView textview, int val, int dBMin, int dBMax) {
    double currentRate = (val / 127.0) - 1.0;
    int currentDecibel;
    if (currentRate > 0.0) {
      currentDecibel = (int) Math.round(currentRate * dBMax);
      if (currentDecibel == 0) {
        textview.setText(String.format(Locale.US, "%ddB", currentDecibel));
      } else {
        textview.setText(String.format(Locale.US, "+%ddB", currentDecibel));
      }
    } else {
      currentDecibel = (int) Math.round((0.0 - currentRate) * dBMin);
      textview.setText(String.format(Locale.US, "%ddB", currentDecibel));
    }
  }
}
