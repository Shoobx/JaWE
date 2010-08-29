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

package org.enhydra.jawe.components.extpkgrelations.actions;

import java.awt.event.ActionEvent;

import javax.swing.tree.TreePath;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.components.XPDLTreeUtil;
import org.enhydra.jawe.components.extpkgrelations.ExtPkgRelations;
import org.enhydra.jawe.components.extpkgrelations.ExtPkgRelationsPanel;

/**
 * @author Sasa Bojanic
 */
public class CollapseAll extends ActionBase {

   public CollapseAll(JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {      
      if (((ExtPkgRelations) jawecomponent).hasRelations()) {
         setEnabled(true);
      } else {
         setEnabled(false);
      }
   }
   
   public void actionPerformed(ActionEvent e) {      
      ExtPkgRelations tcon = (ExtPkgRelations)jawecomponent;
      
      ExtPkgRelationsPanel panel = (ExtPkgRelationsPanel)(tcon.getView());
      
      int clevel=-1;
      TreePath tp=panel.getTree().getSelectionPath();
      if (tp==null) {
         tp=new TreePath(panel.getTree().getModel().getRoot());
         clevel=1;
      }
      
      XPDLTreeUtil.expandOrCollapsToLevel(panel.getTree(),tp,clevel,false);
   }
}
