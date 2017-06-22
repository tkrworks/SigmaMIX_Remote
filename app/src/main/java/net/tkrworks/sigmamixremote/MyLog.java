package net.tkrworks.sigmamixremote;

import android.util.Log;

/**
 * Created by shun on 2017/06/21.
 */

public class MyLog {
  static void d(String tag, String format, Object... args) {
    Log.d(tag, String.format(format, args));
  }
}
