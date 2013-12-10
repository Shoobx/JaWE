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
import java.util.List;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.jawe.components.graph.Graph;
import org.enhydra.jawe.components.graph.GraphController;
import org.enhydra.jawe.components.graph.GraphTransitionInterface;
import org.enhydra.jawe.components.graph.GraphUtilities;
import org.enhydra.shark.xpdl.elements.Transition;

/**
* Class that realizes <B>SetTransitionType</B> action.
* @author Sasa Bojanic
*/
public abstract class SetTransitionStyle extends ActionBase {

   protected String style;
   public SetTransitionStyle (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public SetTransitionStyle (JaWEComponent jawecomponent,String style) {
      super(jawecomponent);
      this.style=style;
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
      Graph graph=gc.getSelectedGraph();
      Object cell=graph.getSelectionCell();
      if (cell instanceof GraphTransitionInterface) {
         JaWEController jc=JaWEManager.getInstance().getJaWEController();      
         gc.setUpdateInProgress(true);
         jc.startUndouableChange();
         GraphTransitionInterface gtra=(GraphTransitionInterface)cell;
         Transition tra=(Transition)gtra.getUserObject();
         GraphUtilities.setStyle(tra, style);
         graph.getGraphManager().updateStyle(gtra);
         List toSelect=new ArrayList();
         toSelect.add(tra);      
         JaWEManager.getInstance().getJaWEController().endUndouableChange(toSelect);
         gc.setUpdateInProgress(false);
      }
   }
}
