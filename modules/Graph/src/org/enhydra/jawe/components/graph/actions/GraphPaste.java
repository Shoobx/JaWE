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

package org.enhydra.jawe.components.graph.actions;

import java.awt.Point;
import java.awt.event.ActionEvent;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.controller.JaWEActions;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.jawe.components.graph.CopyOrCutInfo;
import org.enhydra.jawe.components.graph.Graph;
import org.enhydra.jawe.components.graph.GraphController;
import org.enhydra.jawe.components.graph.GraphManager;

/**
 * Class that realizes <B>paste</B> action.
 * @author Sasa Bojanic
 */
public class GraphPaste extends ActionBase {

   public GraphPaste (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {      
      GraphController gc = (GraphController)jawecomponent;
      Graph selectedGraph = gc.getSelectedGraph();
      boolean en=false;
      if (JaWEManager.getInstance().getJaWEController().getJaWEActions().getAction(JaWEActions.PASTE_ACTION).isEnabled() && selectedGraph != null) {
         GraphManager gm=selectedGraph.getGraphManager();
         en=gc.getCopyOrCutInfo()!=null && gm.hasLane();
      }
      setEnabled(en);
   }
   
   public void actionPerformed(ActionEvent e) {
      JaWEController jc = JaWEManager.getInstance().getJaWEController();      
    
      GraphController gc = (GraphController) jawecomponent;
      Graph selectedGraph = gc.getSelectedGraph();
      if (selectedGraph == null) return;
    
      Point pasteTo =  gc.getGraphMarqueeHandler().getPopupPoint();
      CopyOrCutInfo copyOrCutInfo=gc.getCopyOrCutInfo();
      copyOrCutInfo.setPastePoint(pasteTo);

      jc.getEdit().paste();      
      
      copyOrCutInfo.setPastePoint(null);
   }
}

