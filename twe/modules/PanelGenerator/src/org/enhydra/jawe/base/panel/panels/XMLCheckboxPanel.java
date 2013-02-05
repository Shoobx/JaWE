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

package org.enhydra.jawe.base.panel.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.enhydra.jawe.Settings;
import org.enhydra.jawe.base.panel.PanelContainer;
import org.enhydra.jawe.base.panel.PanelSettings;
import org.enhydra.jxpdl.XMLAttribute;
import org.enhydra.jxpdl.XMLElement;

/**
 * Creates a panel with a checkbox which represents a boolean value.
 * 
 * @author Harald Meister
 * @author Sasa Bojanic
 */
public class XMLCheckboxPanel extends XMLBasicPanel {
   protected JLabel jl;

   protected JCheckBox jcb;

   protected boolean considerUncheckedAsEmpty;

   public XMLCheckboxPanel(PanelContainer pc,
                           XMLElement myOwner,
                           String title,
                           boolean isVertical,
                           boolean isEnabled,
                           boolean considerUncheckedAsEmpty,
                           String tooltip) {

      super(pc, myOwner, "", isVertical, false, true, tooltip);

      this.considerUncheckedAsEmpty = considerUncheckedAsEmpty;
      boolean rightAllignment = false;

      Color bkgCol = new Color(245, 245, 245);
      if (pc != null) {
         Settings settings = pc.getSettings();

         rightAllignment = settings.getSettingBoolean("XMLBasicPanel.RightAllignment");

         if (settings instanceof PanelSettings) {
            bkgCol = ((PanelSettings) settings).getBackgroundColor();
         }

      }

      if (title != null && !title.equals("")) {
         jl = new JLabel(title);
      } else {
         jl = new JLabel(pc.getLabelGenerator().getLabel(myOwner));
      }
      jl.setAlignmentX(Component.LEFT_ALIGNMENT);
      jl.setAlignmentY(Component.BOTTOM_ALIGNMENT);
      if (rightAllignment) {
         jl.setHorizontalAlignment(SwingConstants.RIGHT);
      } else {
         jl.setHorizontalAlignment(SwingConstants.LEFT);
      }

      jcb = new JCheckBox();
      jcb.setBorder(BorderFactory.createEmptyBorder());
      // jcb.setSelected(new Boolean(myOwner.toValue()).booleanValue());
      jcb.setSelected(setChoice());
      jcb.setAlignmentX(Component.LEFT_ALIGNMENT);
      jcb.setAlignmentY(Component.BOTTOM_ALIGNMENT);

      jcb.setEnabled(isEnabled);
      // jcb.setBackground(bkgCol);

      final XMLPanel p = this;
      jcb.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
            if (getPanelContainer() == null)
               return;
            getPanelContainer().panelChanged(p, e);
         }
      });

      if (rightAllignment && !isVertical) {
         add(Box.createHorizontalGlue());
      }
      if (isVertical) {
         add(jl);
         add(Box.createVerticalStrut(5));
         add(jcb);
      } else {
         add(jcb);
         add(new JLabel(" "));
         add(jl);
      }
      if (!rightAllignment && !isVertical) {
         add(Box.createHorizontalGlue());
      }
   }

   public void setElements() {
      if (getOwner() instanceof XMLAttribute
          && ((XMLAttribute) getOwner()).getChoices() != null) {
         List choices = ((XMLAttribute) getOwner()).getChoices();
         if (jcb.isSelected()) {
            getOwner().setValue((String) choices.get(0));
         } else {
            getOwner().setValue((String) choices.get(1));
         }
      } else {
         if (jcb.isSelected()) {
            getOwner().setValue("true");
         } else {
            getOwner().setValue("false");
         }
      }
   }

   public boolean setChoice() {
      if (getOwner() instanceof XMLAttribute
          && ((XMLAttribute) getOwner()).getChoices() != null) {
         List choices = ((XMLAttribute) getOwner()).getChoices();
         if (myOwner.toValue().equals(choices.get(0))) {
            return true;
         } else if (myOwner.toValue().equals(choices.get(1))) {
            return false;
         } else {
            if (((XMLAttribute) myOwner).getDefaultChoiceIndex() == 0) {
               return true;
            }
            return false;
         }
      } else {
         return new Boolean(myOwner.toValue()).booleanValue();
      }
   }

   public boolean getCheckboxStatus() {
      return (jcb.isSelected());
   }

   public Object getValue() {
      if (jcb.isSelected()) {
         return "true";
      }
      return "false";
   }

   public boolean isEmpty() {
      return considerUncheckedAsEmpty && !getCheckboxStatus();
   }
}
