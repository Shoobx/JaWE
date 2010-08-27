/**
 * Miroslav Popov, Aug 4, 2005
 */
package org.enhydra.jawe.components.graph.actions;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.Utils;
import org.enhydra.jawe.base.controller.JaWEController;
import org.enhydra.jawe.components.graph.Graph;
import org.enhydra.jawe.components.graph.GraphController;
import org.enhydra.jawe.components.graph.GraphEAConstants;
import org.enhydra.jawe.components.graph.GraphUtilities;
import org.enhydra.jawe.components.graph.JaWEGraphModel;
import org.enhydra.jawe.components.graph.StartEndDescription;
import org.enhydra.shark.xpdl.XMLUtil;
import org.enhydra.shark.xpdl.elements.Activity;
import org.enhydra.shark.xpdl.elements.ExtendedAttribute;
import org.enhydra.shark.xpdl.elements.Transition;
import org.jgraph.graph.DefaultGraphCell;

/**
 * @author Miroslav Popov
 *
 */
public class RotateProcess extends ActionBase {

   public RotateProcess (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }
   
   public void enableDisableAction() {
      JaWEController jc = JaWEManager.getInstance().getJaWEController();
      GraphController gc = (GraphController)jawecomponent;
      
      if (gc.getSelectedGraph() != null)         
         if (XMLUtil.getPackage(gc.getSelectedGraph().getXPDLObject()) == jc.getMainPackage()) {      
            setEnabled(true);
            return;
         }
            
      setEnabled(false);      
   }
   
   public void actionPerformed(ActionEvent e) {
      GraphController gcon = (GraphController) jawecomponent;
      Graph selectedGraph = gcon.getSelectedGraph();
      if (selectedGraph == null) return;
            
      gcon.setUpdateInProgress(true);
      JaWEManager.getInstance().getJaWEController().startUndouableChange();
      if (GraphUtilities.getGraphParticipantOrientation(selectedGraph.getWorkflowProcess(),
            selectedGraph.getXPDLObject()).equals(GraphEAConstants.EA_JAWE_GRAPH_PARTICIPANT_ORIENTATION_VALUE_HORIZONTAL)) {
         GraphUtilities.setGraphParticipantOrientation(selectedGraph.getWorkflowProcess(),
               selectedGraph.getXPDLObject(), GraphEAConstants.EA_JAWE_GRAPH_PARTICIPANT_ORIENTATION_VALUE_VERTICAL);
      } else {
         GraphUtilities.setGraphParticipantOrientation(selectedGraph.getWorkflowProcess(),
               selectedGraph.getXPDLObject(), GraphEAConstants.EA_JAWE_GRAPH_PARTICIPANT_ORIENTATION_VALUE_HORIZONTAL);
      }

      Object[] elem = JaWEGraphModel.getAll(selectedGraph.getModel());
      selectedGraph.getModel().remove(elem);
//      GraphUtilities.rotateCoordinates(elem);
      for (int i = 0; i < elem.length; i++) {
         if (elem[i] instanceof DefaultGraphCell) {
            if (((DefaultGraphCell) elem[i]).getUserObject() instanceof Activity) {
               Activity act = (Activity) ((DefaultGraphCell) elem[i]).getUserObject();

               Point p = GraphUtilities.getOffsetPoint(act);
               Utils.flipCoordinates(p);
               GraphUtilities.setOffsetPoint(act, p);
            } else if (((DefaultGraphCell) elem[i]).getUserObject() instanceof Transition) {
               Transition tr = (Transition) ((DefaultGraphCell) elem[i]).getUserObject();
               List bpoi = GraphUtilities.getBreakpoints(tr);
               for (int j = 0; j < bpoi.size(); j++) {
                  Point p = (Point) bpoi.get(j);
                  Utils.flipCoordinates(p);
               }
               GraphUtilities.setBreakpoints(tr, bpoi);
            } else if (((DefaultGraphCell) elem[i]).getUserObject() instanceof ExtendedAttribute) {
               ExtendedAttribute ea = (ExtendedAttribute) ((DefaultGraphCell) elem[i]).getUserObject();
               StartEndDescription sed = new StartEndDescription(ea);
               Point p = sed.getOffset();
               Utils.flipCoordinates(p);
               ea.setVValue(sed.toString());
            }
         }
      }      

      selectedGraph.getGraphManager().createWorkflowGraph(selectedGraph.getXPDLObject());

      List toSelect=new ArrayList();
      toSelect.add(selectedGraph.getXPDLObject());      
      JaWEManager.getInstance().getJaWEController().endUndouableChange(toSelect);
      selectedGraph.clearSelection();
      gcon.setUpdateInProgress(false);
   }
   
}
