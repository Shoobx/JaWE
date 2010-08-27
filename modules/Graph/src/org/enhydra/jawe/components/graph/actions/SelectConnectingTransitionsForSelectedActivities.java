package org.enhydra.jawe.components.graph.actions;

import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.components.graph.Graph;
import org.enhydra.jawe.components.graph.GraphActivityInterface;
import org.enhydra.jawe.components.graph.GraphController;
import org.enhydra.jawe.components.graph.GraphTransitionInterface;
import org.enhydra.shark.xpdl.elements.Activity;
import org.enhydra.shark.xpdl.elements.Transition;
import org.jgraph.graph.DefaultGraphCell;

/**
 * @author Sasa Bojanic
 *
 */
public class SelectConnectingTransitionsForSelectedActivities extends ActionBase {

   public SelectConnectingTransitionsForSelectedActivities (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }
   
   public void enableDisableAction() {
      GraphController gc = (GraphController)jawecomponent;
      
      if (gc.getSelectedGraph() != null) {         
         Object[] selCels=gc.getSelectedGraph().getSelectionCells();
         boolean en=false;
         if (selCels!=null && selCels.length>0) {
            for (int i = 0; i < selCels.length; i++) {
               if (selCels[i] instanceof DefaultGraphCell) {
                  DefaultGraphCell dgc=(DefaultGraphCell)selCels[i];
                  if (dgc.getUserObject() instanceof Activity) {
                     en=true;
                     break;
                  }
               }
            }
         }
         setEnabled(en);
      } else {            
         setEnabled(false);
      }
   }
   
   public void actionPerformed(ActionEvent e) {
      GraphController gcon = (GraphController) jawecomponent;
      Graph selectedGraph = gcon.getSelectedGraph();
      if (selectedGraph == null) return;
            
      Object[] selCels=selectedGraph.getSelectionCells();
      if (selCels!=null && selCels.length>0) {
         Set transToSelect=new HashSet();
         for (int i = 0; i < selCels.length; i++) {
            if (selCels[i] instanceof DefaultGraphCell) {
               DefaultGraphCell dgc=(DefaultGraphCell)selCels[i];
               if (dgc.getUserObject() instanceof Activity) {
                  GraphActivityInterface act = (GraphActivityInterface)dgc;
                  transToSelect.addAll(act.getPort().getEdges());
               }
            }
         }      
         Iterator it=transToSelect.iterator();
         while (it.hasNext()) {
            GraphTransitionInterface gti=(GraphTransitionInterface)it.next();
            if (gti.getUserObject() instanceof Transition) {
               selectedGraph.addSelectionCell(gti);
            }
         }
      }

   }
   
}
