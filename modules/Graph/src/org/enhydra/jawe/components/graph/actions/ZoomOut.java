package org.enhydra.jawe.components.graph.actions;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.components.graph.Graph;
import org.enhydra.jawe.components.graph.GraphController;

/**
 * Zoom out (for 15%)
 * @author Sasa Bojanic
 */
public class ZoomOut extends ActionBase {

   public ZoomOut (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {      
      GraphController gc = (GraphController)jawecomponent;
      
      if (gc.getSelectedGraph() != null)
         if (gc.getSelectedGraph().getScale() > 0.1) {
            setEnabled(true);
            return;
         }
      
      setEnabled(false);      
   }
   
   public void actionPerformed(ActionEvent e) {
      Graph selectedGraph=((GraphController)jawecomponent).getSelectedGraph();
      if (selectedGraph==null) return;
      //setResizeAction(null);
      double scale=selectedGraph.getScale()/1.15;
      scale = Math.max(Math.min(scale,2),0.1);
      selectedGraph.setScale(scale);
      try {
         Dimension prefSize=selectedGraph.getSize();
         prefSize.width=(int)(prefSize.width/1.15);
         prefSize.height=(int)(prefSize.height/1.15);
         selectedGraph.setPreferredSize(prefSize);
      } catch (Exception ex) {}

      // With JGraph3.4.1 this causes problems
      /*if (editor.getGraph().getSelectionCell() != null) {
         editor.getGraph().scrollCellToVisible(editor.getGraph().getSelectionCell());
       }*/
      
      GraphController gc = (GraphController)jawecomponent;
      gc.getSettings().getAction("ZoomIn").getAction().enableDisableAction();      
      gc.getSettings().getAction("ZoomOut").getAction().enableDisableAction();
      gc.getSettings().getAction("ActualSize").getAction().enableDisableAction();
   }
}
