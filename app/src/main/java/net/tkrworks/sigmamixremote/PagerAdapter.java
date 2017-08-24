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
 * PagerAdapter.java
 */

package net.tkrworks.sigmamixremote;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

class PagerAdapter extends FragmentStatePagerAdapter {

  int mNumOfTabs;

  PagerAdapter(FragmentManager fragmentManager, int numOfTabs) {
    super(fragmentManager);

    mNumOfTabs = numOfTabs;
  }

  @Override
  public Fragment getItem(int position) {
    switch (position) {
      case 0:
        return InputControlFragment.newInstance();
      case 1:
        return KnobControlFragment.newInstance();
      case 2:
        return FaderControlFragment.newInstance();
      case 3:
        return MasterControlFragment.newInstance();
      default:
        return null;
    }
  }

  @Override
  public int getCount() {
    return mNumOfTabs;
  }
}
