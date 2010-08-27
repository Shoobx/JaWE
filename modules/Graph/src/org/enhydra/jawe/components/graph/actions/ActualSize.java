package org.enhydra.jawe.components.graph.actions;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.components.graph.Graph;
import org.enhydra.jawe.components.graph.GraphController;

/**
 * Set the graph scale to 100%.
 * @author Sasa Bojanic
 */
public class ActualSize extends ActionBase {

   public ActualSize (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {
      GraphController gc = (GraphController)jawecomponent;
      
      if (gc.getSelectedGraph() != null && gc.getSelectedGraph().getScale() != 1)
         setEnabled(true);
      else
         setEnabled(false);
   }
   
   public void actionPerformed(ActionEvent e) {
      Graph selectedGraph=((GraphController)jawecomponent).getSelectedGraph();
      if (selectedGraph==null) return;
      //setResizeAction(null);
      double curScale=selectedGraph.getScale();
      try {
         Dimension prefSize=selectedGraph.getSize();
         prefSize.width=(int)(prefSize.width/curScale);
         prefSize.height=(int)(prefSize.height/curScale);
         selectedGraph.setPreferredSize(selectedGraph.getGraphManager().getGraphsPreferredSize());
      } catch (Exception ex) {}

      selectedGraph.setScale(1);
      if (selectedGraph.getSelectionCell() != null) {
         selectedGraph.scrollCellToVisible(selectedGraph.getSelectionCell());
      }
      
      GraphController gc = (GraphController)jawecomponent;
      gc.getSettings().getAction("ZoomIn").getAction().enableDisableAction();      
      gc.getSettings().getAction("ZoomOut").getAction().enableDisableAction();
      gc.getSettings().getAction("ActualSize").getAction().enableDisableAction();
   }
   
}
