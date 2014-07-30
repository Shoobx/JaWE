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
import org.enhydra.jxpdl.XMLCollectionElement;
import org.enhydra.jxpdl.elements.Transition;

/**
 * Class that realizes <B>SetTransitionType</B> action.
 * 
 * @author Sasa Bojanic
 */
public abstract class SetTransitionStyle extends ActionBase {

   protected String style;

   public SetTransitionStyle(JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public SetTransitionStyle(JaWEComponent jawecomponent, String style) {
      super(jawecomponent);
      this.style = style;
   }

   public void enableDisableAction() {
      JaWEController jc = JaWEManager.getInstance().getJaWEController();
      GraphController gc = (GraphController) jawecomponent;
      Graph g = gc.getSelectedGraph();
      if (getPackage() == jc.getMainPackage() && g != null) {
         Object[] sc = g.getSelectionCells();
         boolean en = sc != null && sc.length > 0;
         for (Object object : sc) {
            if (!(object instanceof GraphTransitionInterface)) {
               en = false;
               break;
            }
         }
         setEnabled(en);
      } else {
         setEnabled(false);
      }
   }

   public void actionPerformed(ActionEvent e) {
      GraphController gc = (GraphController) jawecomponent;
      Graph graph = gc.getSelectedGraph();
      Object[] cells = graph.getSelectionCells();
      JaWEController jc = JaWEManager.getInstance().getJaWEController();
      gc.setUpdateInProgress(true);
      jc.startUndouableChange();
      List toSelect = new ArrayList();
      for (Object cell : cells) {
         if (cell instanceof GraphTransitionInterface) {
            GraphTransitionInterface gtra = (GraphTransitionInterface) cell;
            XMLCollectionElement traOrAsoc = (XMLCollectionElement) gtra.getUserObject();
            GraphUtilities.setStyle(traOrAsoc, style);
            graph.getGraphManager().updateStyle(gtra);
            toSelect.add(traOrAsoc);
         }
      }
      JaWEManager.getInstance().getJaWEController().endUndouableChange(toSelect);
      gc.setUpdateInProgress(false);
   }
}
