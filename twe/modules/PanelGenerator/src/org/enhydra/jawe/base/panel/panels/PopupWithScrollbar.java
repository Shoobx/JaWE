package org.enhydra.jawe.base.panel.panels;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.plaf.basic.*;

public class PopupWithScrollbar extends BasicComboPopup {
	protected JComboBox itemList = new JComboBox();

	public PopupWithScrollbar() {
		super(new JComboBox());
	}

	public int getSelectedIndex() {
		return super.comboBox.getSelectedIndex();
	}
	
	public void addItem(String item) {
		super.comboBox.addItem(item);
	}
	
	public JComboBox getComboBox() {
		return super.comboBox;
	}
}

