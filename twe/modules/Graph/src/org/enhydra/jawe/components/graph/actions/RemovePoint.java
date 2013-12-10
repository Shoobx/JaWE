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

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.jawe.components.graph.Graph;
import org.enhydra.jawe.components.graph.GraphController;
import org.enhydra.jawe.components.graph.GraphMarqueeHandler;
import org.enhydra.jawe.components.graph.GraphTransitionInterface;
import org.enhydra.jawe.components.graph.GraphUtilities;
import org.enhydra.shark.xpdl.elements.Transition;

/**
* Class that realizes <B>RemovePoint</B> action.
* "Breaking point" is removed from transition at the popup
* position (if there is one near).
* @author Sasa Bojanic
*/
public class RemovePoint extends ActionBase {

   public RemovePoint (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {      
      JaWEController jc = JaWEManager.getInstance().getJaWEController();
      GraphController gc=(GraphController)jawecomponent;
      Graph g=gc.getSelectedGraph();
      if (getPackage() == jc.getMainPackage() && g!=null) {
         Object[] sc=g.getSelectionCells();
         if (sc!=null && sc.length==1 && sc[0] instanceof GraphTransitionInterface) {
            setEnabled(true);
         }
      } else {      
         setEnabled(false);
      }
   }
   
   public void actionPerformed(ActionEvent e) {
      GraphController gc=(GraphController)jawecomponent;
      GraphMarqueeHandler mh = gc.getGraphMarqueeHandler();
      Graph graph=gc.getSelectedGraph();
      Point addAt=mh.getPopupPoint();
//      double scale = gc.getSelectedGraph().getScale();
//      addAt.setLocation(addAt.getX()/scale, addAt.getY()/scale);     
      Object cell=graph.getSelectionCell();
      if (cell instanceof GraphTransitionInterface) {
         GraphTransitionInterface gtra=(GraphTransitionInterface)cell;
         JaWEController jc=JaWEManager.getInstance().getJaWEController();      
         gc.setUpdateInProgress(true);
         jc.startUndouableChange();
         List pnts=graph.getGraphManager().addOrRemoveBreakPoint(gtra, addAt, false);
         Transition tra=(Transition)gtra.getPropertyObject();
         GraphUtilities.setBreakpoints(tra, pnts);
         List toSelect=new ArrayList();
         toSelect.add(tra);      
         jc.endUndouableChange(toSelect);
         gc.setUpdateInProgress(false);
      }
   }
}
