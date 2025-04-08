package com.fds.flex.core.portal.comparator;

import java.util.Comparator;

import com.fds.flex.core.portal.gui.model.DisplayNavModel;

public class NavbarComparator implements Comparator<DisplayNavModel> {

	@Override
	public int compare(DisplayNavModel n1, DisplayNavModel n2) {
		if (n1.getLevel() == n2.getLevel()) {
			if (n1.getSeq() == n2.getSeq()) {
				return 0;
			} else if (n1.getSeq() > n2.getSeq()) {
				return 1;
			} else {
				return -1;
			}
		} else if (n1.getLevel() > n2.getLevel()) {
			return 1;
		} else {
			return -1;
		}
	}
}
