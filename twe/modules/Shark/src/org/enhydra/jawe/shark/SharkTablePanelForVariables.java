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

package org.enhydra.jawe.shark;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.jawe.base.controller.JaWESelectionManager;
import org.enhydra.jawe.base.panel.InlinePanel;
import org.enhydra.jawe.base.panel.panels.XMLTablePanelForVariables;
import org.enhydra.jawe.shark.business.SharkConstants;
import org.enhydra.jxpdl.XMLCollection;
import org.enhydra.jxpdl.elements.DataField;
import org.enhydra.jxpdl.elements.DataFields;

public class SharkTablePanelForVariables extends XMLTablePanelForVariables {
   public static final String CREATE_OUTLOOK_VARIABLES_ACTION = "CreateOutlookVariables";

   protected Action createOutlookVarsAction;

   public SharkTablePanelForVariables(InlinePanel ipc,
                                      XMLCollection myOwner,
                                      List columnsToShow,
                                      List elementsToShow,
                                      String title,
                                      boolean hasBorder,
                                      boolean hasEmptyBorder,
                                      boolean automaticWidth,
                                      boolean miniDimension,
                                      final boolean colors,
                                      final boolean showArrows) {
      super(ipc, myOwner, columnsToShow, elementsToShow, title, hasBorder, hasEmptyBorder, automaticWidth, miniDimension, colors, showArrows);

   }

   public void createOutlookVars() {
      JaWEController jc = JaWEManager.getInstance().getJaWEController();

      if (myOwner instanceof DataFields) {
         DataFields dfs = (DataFields) myOwner;
         jc.startUndouableChange();
         List toSelect = new ArrayList();
         for (int i = 0; i < SharkConstants.POSSIBLE_OUTLOOK_VARIABLES.length; i++) {
            String ovid = SharkConstants.POSSIBLE_OUTLOOK_VARIABLES[i];
            if (dfs.getDataField(ovid) == null) {
               DataField df = new DataField(dfs);
               df.setId(ovid);
               df.setInitialValue("null");
               String name = ovid;
               if (name.equals(SharkConstants.OUTLOOK_VARIABLE_STATUS)) {
                  name = "Status";
               } else {
                  name = name.substring("OUTLOOK_VARIABLE_".length());
                  for (int j = 1; j < name.length(); j++) {
                     if (Character.isUpperCase(name.charAt(j))) {
                        name = name.substring(0, j) + " " + Character.toLowerCase(name.charAt(j)) + name.substring(j + 1);
                     }
                  }
               }
               df.setName(name);
               toSelect.add(df);
               dfs.add(df);
            }
         }
         jc.endUndouableChange(toSelect);
      }
   }

   protected JPanel createToolbar(boolean useBasicToolbar) {
      JPanel panel = super.createToolbar(useBasicToolbar);
      if (!useBasicToolbar) {
         createOutlookVarsAction = new AbstractAction(CREATE_OUTLOOK_VARIABLES_ACTION) {
            public void actionPerformed(ActionEvent ae) {
               JaWESelectionManager sm = JaWEManager.getInstance().getJaWEController().getSelectionManager();
               ipc.getJaWEComponent().setUpdateInProgress(true);

               allItems.clearSelection();
               sm.setSelection(myOwner, false);
               createOutlookVars();

               ipc.getPanelSettings().adjustActions();

               lostFocusHandle = true;
               ipc.getJaWEComponent().setUpdateInProgress(false);

               adjustActions();
            }
         };

         JButton buttonOutlookVars = createToolbarButton(ipc.getSettings(), createOutlookVarsAction, "org/enhydra/jawe/shark/images/outlook_small.gif");

         buttonOutlookVars.setRolloverEnabled(true);
         panel.add(buttonOutlookVars, panel.getComponentCount() - 1);
      }

      return panel;
   }

   protected void adjustActions() {
      super.adjustActions();
      JaWEController jc = JaWEManager.getInstance().getJaWEController();
      JaWESelectionManager sm = jc.getSelectionManager();

      convertElementAction.setEnabled((getSelectedElement() != null && sm.canDuplicate()));
      createOutlookVarsAction.setEnabled(hasMissingOutlookVars());
   }

   protected boolean hasMissingOutlookVars() {
      if (myOwner instanceof DataFields) {
         for (int i = 0; i < SharkConstants.POSSIBLE_OUTLOOK_VARIABLES.length; i++) {
            if (((DataFields) myOwner).getDataField(SharkConstants.POSSIBLE_OUTLOOK_VARIABLES[i]) == null) {
               return true;
            }
         }
      }
      return false;
   }
}
