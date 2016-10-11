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

import java.util.List;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.panel.PanelContainer;
import org.enhydra.jxpdl.XMLCollection;
import org.enhydra.jxpdl.XMLElement;

/**
 * Creates a table panel.
 * 
 * @author Sasa Bojanic
 * @author Zoran Milakovic
 * @author Miroslav Popov
 */
public class XMLSimpleTableSelPanel extends XMLSimpleTablePanel {

   public XMLSimpleTableSelPanel(PanelContainer pc,
                                 XMLCollection myOwner,
                                 List columnsToShow,
                                 List elementsToShow,
                                 String title,
                                 boolean hasBorder,
                                 boolean hasEmptyBorder,
                                 boolean automaticWidth,
                                 String tooltip) {
      super(pc,
            myOwner,
            columnsToShow,
            elementsToShow,
            title,
            hasBorder,
            hasEmptyBorder,
            automaticWidth,
            null);

   }

   protected void setupTable(boolean automaticWidth) {
      super.setupTable(automaticWidth);

      // selection listener for sending selection event
      ListSelectionModel lsm = allItems.getSelectionModel();
      lsm.addListSelectionListener(new ListSelectionListener() {
         public void valueChanged(ListSelectionEvent e) {
            // Ignore extra messages.
            if (e.getValueIsAdjusting())
               return;

            ListSelectionModel ls = (ListSelectionModel) e.getSource();
            if (ls.isSelectionEmpty()) {

            } else {
               try {
                  XMLElement el = getSelectedElement();
                  if (el != null) {
                     JaWEManager.getInstance()
                        .getJaWEController()
                        .getSelectionManager()
                        .setSelection(el, true);
                  }
               } catch (Exception ex) {
               }
            }
         }
      });

   }

}
