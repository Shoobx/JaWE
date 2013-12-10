package org.enhydra.jawe.components.graph.actions;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.components.graph.Graph;
import org.enhydra.jawe.components.graph.GraphController;

/**
 * Zoom in (for 15%)
 * @author Sasa Bojanic
 */
public class ZoomIn extends ActionBase {

   public ZoomIn (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {
      GraphController gc = (GraphController)jawecomponent;
      
      if (gc.getSelectedGraph() != null)
         if (gc.getSelectedGraph().getScale() < 2) {
            setEnabled(true);
            return;
         }
      
      setEnabled(false);      
   }
   
   public void actionPerformed(ActionEvent e) {
      Graph selectedGraph=((GraphController)jawecomponent).getSelectedGraph();
      if (selectedGraph==null) return;
      //setResizeAction(null);
      //editor.setScale(editor.getGraph().getScale()*1.15);
      double scale=selectedGraph.getScale()/0.85;
      scale = Math.max(Math.min(scale,2),0.1);
      selectedGraph.setScale(scale);
      try {
         Dimension prefSize=selectedGraph.getSize();
         prefSize.width=(int)(prefSize.width/0.85);
         prefSize.height=(int)(prefSize.height/0.85);
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
