/**
* Together Workflow Editor
* Copyright (C) 2010 Together Teamsolutions Co., Ltd. 
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

package org.enhydra.jawe.shark;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.Settings;
import org.enhydra.jawe.base.panel.PanelContainer;
import org.enhydra.jawe.base.panel.panels.PanelUtilities;
import org.enhydra.jawe.base.panel.panels.XMLBasicPanel;
import org.enhydra.jawe.base.panel.panels.XMLComboPanel;
import org.enhydra.jawe.base.panel.panels.XMLElementView;
import org.enhydra.jawe.base.panel.panels.XMLPanel;
import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.elements.ExtendedAttribute;
import org.enhydra.jxpdl.elements.WorkflowProcess;

/**
 * Creates a panel for ext. attribs with a checkbox which represents the name of extended
 * attribute.
 * 
 * @author Sasa Bojanic
 */
public class ExtAttribPanel extends XMLBasicPanel {

   protected JCheckBox jchkb;

   Dimension textDim = new Dimension(250, 20);

   protected JComboBox jcb;

   protected JLabel jl;

   public ExtAttribPanel(PanelContainer pc,
                         ExtendedAttribute myOwner,
                         List choices,
                         boolean hasEmptyBorder,
                         boolean isVertical,
                         boolean isEnabled,
                         boolean isChoiceEnabled) {

      super(pc, myOwner, pc.getLanguageDependentString("VariableKey"), isVertical, false, hasEmptyBorder);

      boolean rightAllignment = false;
      if (pc != null) {

         Settings settings = pc.getSettings();
         rightAllignment = settings.getSettingBoolean("XMLBasicPanel.RightAllignment");

         textDim = new Dimension(settings.getSettingInt("SimplePanelTextWidth"),
                                 settings.getSettingInt("SimplePanelTextHeight"));
      }

      if (isChoiceEnabled) {
         Dimension toSet;
         List chs = null;
         Object chsn = null;
         XMLElement choosen = null;
         for (int i = 0; i < choices.size(); i++) {
            XMLCollectionElement cel = (XMLCollectionElement) choices.get(i);
            if (cel.getId().equals(myOwner.getVValue())) {
               choosen = cel;
            }
         }
         chs = PanelUtilities.toXMLElementViewList(pc, choices, true);

         jcb = new JComboBox(XMLComboPanel.sortComboEntries(chs));

         if (choosen != null) {
            chsn = new XMLElementView(pc, choosen, XMLElementView.TONAME);
            jcb.setSelectedItem(chsn);
         }
         toSet = getComboDimension(chs);
         jcb.setEditable(false);

         jcb.setAlignmentX(Component.LEFT_ALIGNMENT);
         jcb.setAlignmentY(Component.BOTTOM_ALIGNMENT);
         jcb.setMinimumSize(new Dimension(toSet));
         jcb.setMaximumSize(new Dimension(toSet));
         jcb.setPreferredSize(new Dimension(toSet));

         jcb.setEnabled(isEnabled);

         final XMLPanel cp = this;
         jcb.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
               if (getPanelContainer() == null)
                  return;
               getPanelContainer().panelChanged(cp, e);
            }

         });
      } else {
         WorkflowProcess wp = XMLUtil.getWorkflowProcess(myOwner);
         String vId = myOwner.getVValue();
         XMLCollectionElement cel = wp.getDataField(vId);
         if (cel == null) {
            cel = wp.getFormalParameter(vId);
         }
         String dn = "";
         if (cel != null) {
            dn = JaWEManager.getInstance().getDisplayNameGenerator().getDisplayName(cel);
         }
         jl = new JLabel(dn + ": ");
         jl.setAlignmentX(Component.LEFT_ALIGNMENT);
         jl.setAlignmentY(Component.BOTTOM_ALIGNMENT);
         if (rightAllignment) {
            jl.setHorizontalAlignment(SwingConstants.RIGHT);
         } else {
            jl.setHorizontalAlignment(SwingConstants.LEFT);
         }

      }
      jchkb = new JCheckBox(pc.getLanguageDependentString("ReadOnlyKey"));
      jchkb.setBorder(BorderFactory.createEmptyBorder());
      jchkb.setSelected(new Boolean(myOwner.getName().equals(SharkConstants.VTP_VIEW)).booleanValue());
      jchkb.setAlignmentX(Component.LEFT_ALIGNMENT);
      jchkb.setAlignmentY(Component.BOTTOM_ALIGNMENT);
      jchkb.setMinimumSize(new Dimension(textDim));
      jchkb.setMaximumSize(new Dimension(textDim));
      jchkb.setPreferredSize(new Dimension(textDim));

      jchkb.setEnabled(isEnabled);

      final XMLPanel p = this;
      jchkb.addChangeListener(new ChangeListener() {

         public void stateChanged(ChangeEvent e) {
            if (getPanelContainer() == null)
               return;
            getPanelContainer().panelChanged(p, e);
         }

      });

      if (rightAllignment) {
         add(Box.createHorizontalGlue());
      }
      if (jcb != null) {
         add(jcb);
      } else {
         add(jl);
      }
      if (!rightAllignment) {
         add(Box.createHorizontalGlue());
      }
      add(jchkb);

   }

   public boolean validateEntry() {
      if (jcb != null) {
         Object selItem = getSelectedItem();
         if ((selItem == null || !(selItem instanceof XMLCollectionElement))
             && !getOwner().isReadOnly()) {

            XMLBasicPanel.defaultErrorMessage(this.getWindow(), "");
            jcb.requestFocus();
            return false;
         }
      }
      return true;
   }

   public void setElements() {
      if (!getOwner().isReadOnly()) {
         ExtendedAttribute ea = (ExtendedAttribute) myOwner;
         ea.setName((String) getCHKBValue());
         if (jcb != null) {
            Object sel = getSelectedItem();
            if (sel instanceof XMLCollectionElement) {
               ea.setVValue(((XMLCollectionElement) sel).getId());
            } else {
               ea.setVValue("");
            }
         }
      }
   }

   public JComboBox getComboBox() {
      return jcb;
   }

   public Object getSelectedItem() {
      try {
         Object el = null;
         if (jcb.isEditable()) {
            el = jcb.getEditor().getItem();
         } else {
            el = jcb.getSelectedItem();
         }
         if (el instanceof XMLElementView) {
            XMLElementView ev = (XMLElementView) getComboBox().getSelectedItem();
            if (ev != null) {
               if (ev.getElement() != null) {
                  return ev.getElement();
               }
               return ev.getElementString();
            }

         } else {
            if (el != null) {
               return el.toString();
            }
         }
         return "";
      } catch (Exception ex) {
         ex.printStackTrace();
         return "";
      }
   }

   public Object getValue() {
      if (jcb != null) {
         return getSelectedItem();
      }
      return jl.getText();
   }

   public void cleanup() {
   }

   public Dimension getComboDimension(List choices) {
      double w = 0;
      if (choices != null) {
         double longest = 0;
         for (int i = 0; i < choices.size(); i++) {
            try {
               w = getFontMetrics(getFont()).stringWidth(choices.get(i).toString());
               if (w > longest)
                  longest = w;
            } catch (Exception ex) {
            }
         }

         w = longest + 25;
      }
      if (w < textDim.width)
         w = textDim.width;
      return new Dimension((int) w, textDim.height);

   }

   public static Vector sortComboEntries(List ces) {
      Collections.sort(ces, new ComboEntryComparator());
      return new Vector(ces);
   }

   private static class ComboEntryComparator implements Comparator {
      public int compare(Object o1, Object o2) {
         String p1 = o1.toString();
         String p2 = o2.toString();

         return p1.compareTo(p2);
      }
   }

   public void requestFocus() {
      jcb.requestFocus();
   }

   public boolean getCheckboxStatus() {
      return (jchkb.isSelected());
   }

   public Object getCHKBValue() {
      if (jchkb.isSelected()) {
         return SharkConstants.VTP_VIEW;
      }
      return SharkConstants.VTP_UPDATE;
   }

}
