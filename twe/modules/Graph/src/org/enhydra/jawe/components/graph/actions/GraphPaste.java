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
         en=gc.getCopyOrCutInfo()!=null && gm.doesRootParticipantExist();
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

