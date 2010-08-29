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
 * Miroslav Popov, Sep 20, 2005
 * miroslav.popov@gmail.com
 */
package org.enhydra.jawe.components.graph.actions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.components.graph.GraphController;
import org.enhydra.jawe.components.graph.GraphEAConstants;
import org.enhydra.shark.xpdl.XMLUtil;

/**
 * @author Miroslav Popov
 *
 */
public class SetSelectMode extends SetToolboxMode {
   
   public SetSelectMode (GraphController jawecomponent) {
      super(jawecomponent, GraphEAConstants.SELECT_TYPE, GraphEAConstants.SELECT_TYPE_DEFAULT);
   }

   public void enableDisableAction() {
      GraphController gc = (GraphController)jawecomponent;
      
      if (gc.getSelectedGraph() != null && XMLUtil.getPackage(gc.getSelectedGraph().getXPDLObject()) == JaWEManager.getInstance().getJaWEController().getMainPackage())      
         setEnabled(true);
      else      
         setEnabled(false);      
   }
   
   public void actionPerformed(ActionEvent e) {    
      GraphController gc = (GraphController)jawecomponent;
      
      gc.getGraphMarqueeHandler().setType(type, subType, null);
   }
}
