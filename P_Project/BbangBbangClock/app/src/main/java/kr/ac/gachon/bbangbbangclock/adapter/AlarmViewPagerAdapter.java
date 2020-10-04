package kr.ac.gachon.bbangbbangclock.adapter;

import android.view.ViewGroup;

import java.util.ArrayList;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import kr.ac.gachon.bbangbbangclock.fragment.AlarmFragment;

/**
 * Fragment Pager 어댑터
 *
 * @since : 19-12-05
 * @author : 류일웅
 */
public class AlarmViewPagerAdapter extends FragmentPagerAdapter {

	private ArrayList<AlarmFragment> fragments = new ArrayList<>();
	private AlarmFragment currentFragment;

	public AlarmViewPagerAdapter(FragmentManager fm) {
		super(fm);

		fragments.clear();
		fragments.add(AlarmFragment.newInstance(0));
		fragments.add(AlarmFragment.newInstance(1));
	}

	@Override
	public AlarmFragment getItem(int position) {
		return fragments.get(position);
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		if (getCurrentFragment() != object) {
			currentFragment = ((AlarmFragment) object);
		}
		super.setPrimaryItem(container, position, object);
	}

	/**
	 * Get the current fragment
	 */
	public AlarmFragment getCurrentFragment() {
		return currentFragment;
	}

}