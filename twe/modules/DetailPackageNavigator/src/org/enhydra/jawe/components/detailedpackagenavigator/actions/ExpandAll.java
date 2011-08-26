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

/**
 * Miroslav Popov, Aug 2, 2005
 */
package org.enhydra.jawe.components.detailedpackagenavigator.actions;

import java.awt.event.ActionEvent;

import javax.swing.JMenuItem;
import javax.swing.tree.TreePath;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.components.XPDLTreeUtil;
import org.enhydra.jawe.components.detailedpackagenavigator.DetailedPackageNavigator;
import org.enhydra.jawe.components.detailedpackagenavigator.DetailedPackageNavigatorPanel;

/**
 * @author Miroslav Popov
 */
public class ExpandAll extends ActionBase {

   public ExpandAll(JaWEComponent jawecomponent) {
      super(jawecomponent);
   }
   
   public void enableDisableAction() {      
      if (JaWEManager.getInstance().getJaWEController().getMainPackage()!=null) {
         setEnabled(true);
      } else {
         setEnabled(false);
      }
   }
   
   public void actionPerformed(ActionEvent e) {      
      DetailedPackageNavigator tcon = (DetailedPackageNavigator) jawecomponent;

      DetailedPackageNavigatorPanel panel = (DetailedPackageNavigatorPanel) (tcon.getView());

      TreePath tp=null;
      if (e.getSource() instanceof JMenuItem) {
         tp=panel.getTree().getPathForLocation(panel.getMouseClickLocation().x, panel.getMouseClickLocation().y);
      }
      if (tp==null) {
         tp=panel.getTree().getSelectionPath();
         if (tp==null) {
            tp=new TreePath(panel.getTree().getModel().getRoot());
         }
      }
      panel.getTree().removeTreeSelectionListener(tcon);
      XPDLTreeUtil.expandOrCollapsToLevel(panel.getTree(), tp, -1, true);
      if (!(e.getSource() instanceof JMenuItem)) {
         panel.setCurrentSelection();
      }
      panel.getTree().addTreeSelectionListener(tcon);
   }
}
