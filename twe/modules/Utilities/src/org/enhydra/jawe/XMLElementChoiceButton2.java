/**
* Together Workflow Editor
* Copyright (C) 2011 Together Teamsolutions Co., Ltd. 
* 
* This program is free software: you can redistribute it and/or modify 
* it under the terms of the GNU General Public License as published by 
* the Free Software Foundation, either version 3 of the License, or 
* (at your option) any later version. 
*
* This program is distributed in the hope that it will be useful, 
* but WITHOUT ANY WARRANTY; without even the implied warranty of 
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the 
* GNU General Public License for more details. 
*
* You should have received a copy of the GNU General Public License 
* along with this program. If not, see http://www.gnu.org/licenses
*/

package org.enhydra.jawe;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.plaf.basic.BasicComboPopup;

import org.enhydra.jxpdl.XMLComplexElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.utilities.SequencedHashMap;

/**
 * Creates button witch displays popup with available choices for showing xpdl objects.
 * 
 * @author Sasa Bojanic
 * @author Miroslav Popov
 */
public class XMLElementChoiceButton2 extends ChoiceButton implements
                                                         ActionListener {

   protected ChoiceButtonListener parent;

   protected SequencedHashMap choiceMap = new SequencedHashMap();

   BasicComboPopup popup;

   JComboBox aList;
   protected Class choiceType;

   public XMLElementChoiceButton2(Class choiceType, ChoiceButtonListener p, ImageIcon icon) {
      this.parent = p;
      this.choiceType = choiceType;
      aList = new JComboBox();
      aList.setRenderer(new BasicComboBoxRenderer() {
         public Component getListCellRendererComponent(JList list,
                                                       Object value,
                                                       int index,
                                                       boolean isSelected,
                                                       boolean cellHasFocus) {
            if (isSelected) {
               setBackground(list.getSelectionBackground());
               setForeground(list.getSelectionForeground());
               if (-1 < index) {
                  Object selItem = aList.getModel().getElementAt(index);
                  XMLElement el = null;
                  if (selItem != null) {
                     el = (XMLElement) choiceMap.get(selItem);
                  }
                  String ttt = "";
                  if (el != null) {
                     ttt = JaWEManager.getInstance().getTooltipGenerator().getTooltip(el);
                  } else {
                     ttt = super.getToolTipText();
                  }
                  list.setToolTipText(ttt);
               
               }
            } 
            else {
               setBackground(list.getBackground());
               setForeground(list.getForeground());
            }
            setFont(list.getFont());
            setText((value == null) ? "" : value.toString());
            return this;
         }

      });
      popup = new BasicComboPopup(aList); 
      aList.addActionListener(this);

      setIcon(icon);
      addActionListener(this);
      setMargin(new Insets(1, 2, 1, 2));
      setSize(new Dimension(10, 8));
      setAlignmentY(0.5f);
   }

   public void actionPerformed(ActionEvent ae) {
      if (ae.getSource() == this) {
         aList.removeActionListener(this);
         refresh();
         aList.addActionListener(this);
         if (choiceMap.size() > 0) {
            popup.show(this.getParent(), this.getX(), this.getY() + this.getHeight());
         }
      } else {
         Object selItem = aList.getSelectedItem();
         if (selItem != null) {
            Object obj = choiceMap.get(selItem);
            parent.selectionChanged(this, obj);
         }
         choiceMap.clear();
         popup.hide();

      }

   }

   protected void refresh() {
      choiceMap.clear();
      aList.removeAllItems();

      List choices = parent.getChoices(this);
      if (choices != null) {
         Iterator it = choices.iterator();
         while (it.hasNext()) {
            XMLElement choice = (XMLElement) it.next();
            String dispName = " "
                              + JaWEManager.getInstance()
                                 .getDisplayNameGenerator()
                                 .getDisplayName(choice) + " ";
            if (choiceMap.containsKey(dispName)) {
               if (choice instanceof XMLComplexElement) {
                  XMLElement idEl = ((XMLComplexElement) choice).get("Id");
                  dispName += "["
                              + ResourceManager.getLanguageDependentString("IdKey") + "="
                              + idEl.toValue() + "] ";
               }
            }
            choiceMap.put(dispName, choice);
         }
      }

      if (choiceMap.size() > 0) {
         List sorted = new ArrayList(choiceMap.sequence());
         Collections.sort(sorted);
         String[] names = new String[sorted.size()];
         sorted.toArray(names);
         for (int i = 0; i < choiceMap.size(); i++) {
            aList.addItem(names[i]);
         }
      }
   }

   public Class getChoiceType() {
      return choiceType;
   }

}
