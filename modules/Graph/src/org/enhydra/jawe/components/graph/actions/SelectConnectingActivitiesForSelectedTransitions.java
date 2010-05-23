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
public class SelectConnectingActivitiesForSelectedTransitions extends ActionBase {

   public SelectConnectingActivitiesForSelectedTransitions (JaWEComponent jawecomponent) {
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
                  if (dgc.getUserObject() instanceof Transition) {
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
         Set actsToSelect=new HashSet();
         for (int i = 0; i < selCels.length; i++) {
            if (selCels[i] instanceof DefaultGraphCell) {
               DefaultGraphCell dgc=(DefaultGraphCell)selCels[i];
               if (dgc.getUserObject() instanceof Transition) {
                  GraphTransitionInterface tra = (GraphTransitionInterface)dgc;
                  actsToSelect.add(tra.getTargetActivity());
                  actsToSelect.add(tra.getSourceActivity());                  
               }
            }
         }      
         Iterator it=actsToSelect.iterator();
         while (it.hasNext()) {
            GraphActivityInterface gai=(GraphActivityInterface)it.next();
            if (gai.getUserObject() instanceof Activity) {
               selectedGraph.addSelectionCell(gai);
            }
         }
      }
      
   }
   
}
