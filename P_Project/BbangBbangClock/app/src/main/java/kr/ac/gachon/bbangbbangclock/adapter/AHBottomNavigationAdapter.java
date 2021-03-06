package kr.ac.gachon.bbangbbangclock.adapter;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.ColorInt;
import androidx.annotation.MenuRes;
import androidx.appcompat.widget.PopupMenu;

/**
 * BottomNavigation 어댑터
 *
 * @since : 19-12-05
 * @author : 류일웅
 */
public class AHBottomNavigationAdapter {

	private Menu mMenu;
	private List<AHBottomNavigationItem> navigationItems;

	/**
	 * Constructor
	 *
	 * @param activity
	 * @param menuRes
	 */
	public AHBottomNavigationAdapter(Activity activity, @MenuRes int menuRes) {
		PopupMenu popupMenu = new PopupMenu(activity, null);
		mMenu = popupMenu.getMenu();
		activity.getMenuInflater().inflate(menuRes, mMenu);
	}

	/**
	 * Setup bottom navigation
	 *
	 * @param ahBottomNavigation AHBottomNavigation: Bottom navigation
	 */
	public void setupWithBottomNavigation(AHBottomNavigation ahBottomNavigation) {
		setupWithBottomNavigation(ahBottomNavigation, null);
	}

	/**
	 * Setup bottom navigation (with colors)
	 *
	 * @param ahBottomNavigation AHBottomNavigation: Bottom navigation
	 * @param colors             int[]: Colors of the item
	 */
	public void setupWithBottomNavigation(AHBottomNavigation ahBottomNavigation, @ColorInt int[] colors) {
		if (navigationItems == null) {
			navigationItems = new ArrayList<>();
		} else {
			navigationItems.clear();
		}

		if (mMenu != null) {
			for (int i = 0; i < mMenu.size(); i++) {
				MenuItem item = mMenu.getItem(i);
				if (colors != null && colors.length >= mMenu.size() && colors[i] != 0) {
					AHBottomNavigationItem navigationItem = new AHBottomNavigationItem(String.valueOf(item.getTitle()), item.getIcon(), colors[i]);
					navigationItems.add(navigationItem);
				} else {
					AHBottomNavigationItem navigationItem = new AHBottomNavigationItem(String.valueOf(item.getTitle()), item.getIcon());
					navigationItems.add(navigationItem);
				}
			}
			ahBottomNavigation.removeAllItems();
			ahBottomNavigation.addItems(navigationItems);
		}
	}

	/**
	 * Get Menu Item
	 *
	 * @param index
	 * @return
	 */
	public MenuItem getMenuItem(int index) {
		return mMenu.getItem(index);
	}

	/**
	 * Get Navigation Item
	 *
	 * @param index
	 * @return
	 */
	public AHBottomNavigationItem getNavigationItem(int index) {
		return navigationItems.get(index);
	}

	/**
	 * Get position by menu id
	 *
	 * @param menuId
	 * @return
	 */
	public Integer getPositionByMenuId(int menuId) {
		for (int i = 0; i < mMenu.size(); i++) {
			if (mMenu.getItem(i).getItemId() == menuId)
				return i;
		}
		return null;
	}
}