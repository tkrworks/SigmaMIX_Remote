package net.tkrworks.sigmamixremote;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by shun on 2017/06/21.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
  int mNumOfTabs;

  public PagerAdapter(FragmentManager fragmentManager, int numOfTabs) {
    super(fragmentManager);

    mNumOfTabs = numOfTabs;
  }

  @Override
  public Fragment getItem(int position) {
    switch (position) {
      case 0:
        InputControlFragment inputControlTab = new InputControlFragment();
        return inputControlTab;
      case 1:
        KnobControlFragment knobControlTab = new KnobControlFragment();
        return knobControlTab;
      case 2:
        FaderControlFragment faderControlFragment = new FaderControlFragment();
        return faderControlFragment;
      case 3:
        MasterControlFragment masterControlFragment = new MasterControlFragment();
        return masterControlFragment;
      default:
        return null;
    }
  }

  @Override
  public int getCount() {
    return mNumOfTabs;
  }
}
