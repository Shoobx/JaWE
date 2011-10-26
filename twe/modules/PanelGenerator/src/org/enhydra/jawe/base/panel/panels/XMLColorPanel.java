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

import javax.swing.JColorChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.enhydra.jawe.Utils;
import org.enhydra.jawe.base.panel.PanelContainer;
import org.enhydra.jxpdl.XMLElement;

/**
 * Creates panel with JColorChooser.
 * 
 * @author Sasa Bojanic
 */
public class XMLColorPanel extends XMLBasicPanel implements ChangeListener {

   JColorChooser cc = new JColorChooser();

   public XMLColorPanel(PanelContainer pc,
                        XMLElement myOwner,
                        String label,
                        boolean isVertical,
                        boolean isEnabled) {

      super(pc, myOwner, "", isVertical, false, true);
      Color c = Utils.getColor(myOwner.toValue());
      if (c == null) {
         c = Color.BLACK;
      }
      cc.setColor(c);
      cc.setEnabled(isEnabled);

      add(cc);

      cc.getSelectionModel().addChangeListener(this);

   }

   public void stateChanged(ChangeEvent e) {
      if (getPanelContainer() == null)
         return;
      getPanelContainer().panelChanged(this, e);
   }

   public boolean validateEntry() {
      if (isEmpty() && !getOwner().isReadOnly()) {
         XMLBasicPanel.defaultErrorMessage(this.getWindow(), "CCE");
         cc.requestFocus();
         return false;
      }
      return true;
   }

   public boolean isEmpty() {
      return cc.getColor() == null;
   }

   public void setElements() {
      if (!getOwner().isReadOnly()) {
         Color c = cc.getColor();
         String cs = String.valueOf(c.getRed())
                     + "," + String.valueOf(c.getGreen()) + ","
                     + String.valueOf(c.getBlue());
         getOwner().setValue(cs);
      }
   }

   public void requestFocus() {
      cc.requestFocus();
   }

}
