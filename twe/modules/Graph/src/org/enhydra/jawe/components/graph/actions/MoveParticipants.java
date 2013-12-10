package org.enhydra.jawe.components.graph.actions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.jawe.components.graph.Graph;
import org.enhydra.jawe.components.graph.GraphController;
import org.enhydra.jawe.components.graph.GraphManager;
import org.enhydra.jawe.components.graph.WorkflowElement;
import org.enhydra.shark.xpdl.XMLElement;
import org.enhydra.shark.xpdl.XMLUtil;
import org.enhydra.shark.xpdl.elements.Participant;

/*
 * @author Harald Meister harald.meister@abacus.ch
 * @author Sasa Bojanic
 */
public class MoveParticipants extends ActionBase {
   
   boolean movingUp=true;
   public MoveParticipants (JaWEComponent jawecomponent) {
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
            if (scells!=null && scells.length==1 && ((WorkflowElement)scells[0]).getPropertyObject() instanceof Participant) {
               en=true;
            }
         }
      }
       
      setEnabled(en);    
   }
   
   public MoveParticipants (JaWEComponent jawecomponent,boolean movingUp) {
      super(jawecomponent);
      this.movingUp=movingUp;
   }
   
   public void actionPerformed(ActionEvent e) {
      GraphController gcon = (GraphController) jawecomponent;
      Graph selectedGraph = gcon.getSelectedGraph();
      if (selectedGraph == null) return;
            
      Object[] cells = selectedGraph.getSelectionCells();
      GraphManager gm=selectedGraph.getGraphManager();
      gm.moveParticipants(cells, movingUp);

   }
   
}

