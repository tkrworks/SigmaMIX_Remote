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
 * MyTextViewControl.java
 */

package net.tkrworks.sigmamixremote;

import android.widget.TextView;
import java.util.Locale;

class MyTextViewControl {
  static void setDecibel(TextView textview, int val, int dBMin, int dBMax) {
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

  static void setDecibel2(TextView textview, int val, int dBMin, int dBMax) {
    double currentRate = val / 255.0;
    int currentDecibel = (int) Math.round((currentRate) * (dBMax - dBMin) + dBMin);
    if (currentRate >= 0.0) {
      textview.setText(String.format(Locale.US, "%ddB", currentDecibel));
    } else {
      textview.setText(String.format(Locale.US, "%ddB", currentDecibel));
    }
  }

  static void setMilliSeconds(TextView textview, int val, int msMin, int msMax) {
    double currentRate = val / 225.0;
    double currentMsec = (int)((currentRate * (msMax - msMin)) * 100.0) / 100.0;
    textview.setText(String.format(Locale.US, "%.02fms", currentMsec));
  }
}
