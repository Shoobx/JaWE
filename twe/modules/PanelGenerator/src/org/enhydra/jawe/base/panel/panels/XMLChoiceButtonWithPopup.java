package org.enhydra.jawe.base.panel.panels;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.shark.utilities.SequencedHashMap;
import org.enhydra.shark.xpdl.XMLCollectionElement;
import org.enhydra.shark.xpdl.XMLElement;

/**
 * Creates button which displays popup with available choices for inserting into
 * parent panel.
 * 
 * @author Sasa Bojanic
 * @author Miroslav Popov
 */
public class XMLChoiceButtonWithPopup extends JButton implements
		ActionListener, PopupMenuListener {

	protected XMLBasicPanel parent;
	protected SequencedHashMap choiceMap = new SequencedHashMap();
	protected JPopupMenu popup = new JPopupMenu();
	protected ImageIcon defaultIcon = null;
	protected ImageIcon pressedIcon = null;
	protected PopupWithScrollbar largePopup = new PopupWithScrollbar();
	protected boolean largePopupActivated = false;
	protected int ITEM_LIMIT = 25;

	public XMLChoiceButtonWithPopup(
			XMLMultiLineTextPanelWithChoiceButton parent, List choices,
			ImageIcon icon, ImageIcon pressedIcon) {
		this.parent = parent;
		this.defaultIcon = icon;
		this.pressedIcon = pressedIcon;
		setIcon(icon);
		addActionListener(this);
		setMargin(new Insets(1, 2, 1, 2));
		setSize(new Dimension(10, 8));
		setAlignmentY(0.5f);
		setBorderPainted(false);
		setRolloverEnabled(true);
		fillChoices(choices);
		popup.addPopupMenuListener(this);
	}

	public XMLChoiceButtonWithPopup(
			XMLMultiLineHighlightPanelWithChoiceButton parent, List choices,
			ImageIcon icon, ImageIcon pressedIcon) {
		this.parent = parent;
		this.defaultIcon = icon;
		this.pressedIcon = pressedIcon;
		setIcon(icon);
		addActionListener(this);
		setMargin(new Insets(1, 2, 1, 2));
		setSize(new Dimension(10, 8));
		setAlignmentY(0.5f);
		setBorderPainted(false);
		setRolloverEnabled(true);
		fillChoices(choices);
		popup.addPopupMenuListener(this);
	}

	public XMLChoiceButtonWithPopup(XMLHighlightPanelWithReferenceLink parent,
			List choices, ImageIcon icon, ImageIcon pressedIcon) {
		this.parent = parent;
		this.defaultIcon = icon;
		this.pressedIcon = pressedIcon;
		setIcon(icon);
		addActionListener(this);
		setMargin(new Insets(1, 2, 1, 2));
		setSize(new Dimension(10, 8));
		setAlignmentY(0.5f);
		setBorderPainted(false);
		setRolloverEnabled(true);
		fillChoices(choices);
		popup.addPopupMenuListener(this);
	}

	public XMLChoiceButtonWithPopup(
			XMLMultiLineTextPanelWithChoiceButton parent, List choices,
			ImageIcon icon) {
		this.parent = parent;
		setIcon(icon);
		addActionListener(this);
		setMargin(new Insets(1, 2, 1, 2));
		setSize(new Dimension(10, 8));
		setAlignmentY(0.5f);
		setBorderPainted(false);
		setRolloverEnabled(true);
		fillChoices(choices);
	}

	public XMLChoiceButtonWithPopup(
			XMLMultiLineHighlightPanelWithChoiceButton parent, List choices,
			ImageIcon icon) {
		this.parent = parent;
		setIcon(icon);
		addActionListener(this);
		setMargin(new Insets(1, 2, 1, 2));
		setSize(new Dimension(10, 8));
		setAlignmentY(0.5f);
		setBorderPainted(false);
		setRolloverEnabled(true);
		fillChoices(choices);
	}

	public XMLChoiceButtonWithPopup(XMLHighlightPanelWithReferenceLink parent,
			List choices, ImageIcon icon) {
		this.parent = parent;
		setIcon(icon);
		addActionListener(this);
		setMargin(new Insets(1, 2, 1, 2));
		setSize(new Dimension(10, 8));
		setAlignmentY(0.5f);
		setBorderPainted(false);
		setRolloverEnabled(true);
		fillChoices(choices);
	}

	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == this) {
			if (choiceMap.size() > 0) {
				if (largePopupActivated) {
					largePopup.show(this.getParent(), this.getX(), this.getY()
							+ this.getHeight());
				} else {
					popup.show(this.getParent(), this.getX(), this.getY()
							+ this.getHeight());
				}
				if (pressedIcon != null)
					setIcon(pressedIcon);
			}
		} else {
			String sel;
			if (largePopupActivated) {
				sel = largePopup.getComboBox().getSelectedItem().toString();
				largePopup.hide();
			} else {
				JMenuItem selected = (JMenuItem) ae.getSource();
				sel = selected.getText();
			}
			XMLElement cel = (XMLElement) choiceMap.get(sel);
			if (cel instanceof XMLCollectionElement) {
				if (parent instanceof XMLMultiLineTextPanelWithChoiceButton)
					((XMLMultiLineTextPanelWithChoiceButton) parent)
							.appendText(((XMLCollectionElement) cel).getId());
				else if (parent instanceof XMLMultiLineHighlightPanelWithChoiceButton)
					((XMLMultiLineHighlightPanelWithChoiceButton) parent)
							.appendText(((XMLCollectionElement) cel).getId());
				else if (parent instanceof XMLHighlightPanelWithReferenceLink)
					((XMLHighlightPanelWithReferenceLink) parent)
							.appendText(((XMLCollectionElement) cel).getId());
			} else {
				if (parent instanceof XMLMultiLineTextPanelWithChoiceButton)
					((XMLMultiLineTextPanelWithChoiceButton) parent)
							.appendText(sel);
				else if (parent instanceof XMLMultiLineHighlightPanelWithChoiceButton)
					((XMLMultiLineHighlightPanelWithChoiceButton) parent)
							.appendText(sel);
				else if (parent instanceof XMLHighlightPanelWithReferenceLink)
					((XMLHighlightPanelWithReferenceLink) parent)
							.appendText(sel);
			}
		}
	}

	protected void fillChoices(List choices) {
		if (choices != null) {
			Iterator it = choices.iterator();
			while (it.hasNext()) {
				Object item = it.next();
				if (item instanceof XMLElement) {
					XMLElement choice = (XMLElement) item;
					choiceMap.put(JaWEManager.getInstance()
							.getDisplayNameGenerator().getDisplayName(choice),
							choice);
				} else if (item instanceof XMLElementView) {
					XMLElementView choice = (XMLElementView) item;
					choiceMap.put(JaWEManager.getInstance()
							.getDisplayNameGenerator().getDisplayName(
									choice.getElement()), choice.getElement());
				}
			}
		}
		if (choiceMap.size() > this.ITEM_LIMIT) {
			largePopupActivated = true;
		}
		if (choiceMap.size() > 0) {
			String[] names = new String[choiceMap.size()];
			choiceMap.keySet().toArray(names);
			Arrays.sort(names);
			for (int i = 0; i < choiceMap.size(); i++) {
				if (largePopupActivated) {
					largePopup.addItem(names[i]);
				} else {
					JMenuItem mi = popup.add(names[i]);
					mi.addActionListener(this);
				}
			}
		}
		largePopup.getComboBox().addActionListener(this);
	}

	public void popupMenuCanceled(PopupMenuEvent e) {
		setIcon(defaultIcon);
	}

	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		setIcon(defaultIcon);
	}

	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		// do nothing
	}
}
