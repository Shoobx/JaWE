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

package org.enhydra.jawe.components.graph.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.jawe.components.graph.Graph;
import org.enhydra.jawe.components.graph.GraphController;
import org.enhydra.jawe.components.graph.GraphManager;
import org.enhydra.jawe.components.graph.GraphParticipantComparator;
import org.enhydra.jawe.components.graph.GraphSwimlaneInterface;
import org.enhydra.jawe.components.graph.WorkflowElement;
import org.enhydra.jxpdl.XMLElement;
import org.enhydra.jxpdl.XMLUtil;
import org.enhydra.jxpdl.elements.Lane;

/*
 * @author Harald Meister harald.meister@abacus.ch
 * @author Sasa Bojanic
 */
public class MoveLanes extends ActionBase {
   
   boolean movingUp=true;
   public MoveLanes (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }
   
   public void enableDisableAction() {      
      GraphController gc = (GraphController)jawecomponent;
      JaWEController jc = JaWEManager.getInstance().getJaWEController();
      Graph selectedGraph=gc.getSelectedGraph();
      boolean en=false;
      if (selectedGraph != null) {
         XMLElement el = selectedGraph.getXPDLObject();
         if (XMLUtil.getPackage(el) == jc.getMainPackage()) {
            Object[] scells=selectedGraph.getSelectionCells();
            if (scells!=null && scells.length==1 && ((WorkflowElement)scells[0]).getPropertyObject() instanceof Lane) {
               GraphSwimlaneInterface ltm = (GraphSwimlaneInterface)scells[0];
               Lane l = (Lane)ltm.getPropertyObject();
               GraphSwimlaneInterface parentP = (GraphSwimlaneInterface)ltm.getParent();
               List children = new ArrayList(parentP.getChildSwimlanes());
               GraphParticipantComparator gpc = new GraphParticipantComparator(selectedGraph.getGraphManager());
               Collections.sort(children, gpc);
               int ind = children.indexOf(ltm);
               if (!((ind==0 && movingUp) || (ind==children.size()-1 && !movingUp))) {
                  en=true;
               }
            }
         }
      }
       
      setEnabled(en);    
   }
   
   public MoveLanes (JaWEComponent jawecomponent,boolean movingUp) {
      super(jawecomponent);
      this.movingUp=movingUp;
   }
   
   public void actionPerformed(ActionEvent e) {
      GraphController gcon = (GraphController) jawecomponent;
      Graph selectedGraph = gcon.getSelectedGraph();
      if (selectedGraph == null) return;
            
      Object cell = selectedGraph.getSelectionCell();
      GraphManager gm=selectedGraph.getGraphManager();
      gm.moveParticipant(cell, movingUp);
   }
   
}

