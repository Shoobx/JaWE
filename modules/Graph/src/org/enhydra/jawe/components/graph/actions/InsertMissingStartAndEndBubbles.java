package org.enhydra.jawe.components.graph.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.components.graph.GraphController;

/**
 * Inserts missing start and end bubbles into graph.
 * @author Sasa Bojanic
 */
public class InsertMissingStartAndEndBubbles extends ActionBase {

   public InsertMissingStartAndEndBubbles (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {
      GraphController gc = (GraphController)jawecomponent;
      setEnabled(gc.getSelectedGraph()!=null && gc.getGraphSettings().shouldUseBubbles() && !gc.getSelectedGraph().getXPDLObject().isReadOnly());
   }
   
   public void actionPerformed(ActionEvent e) {
      GraphController gc = (GraphController)jawecomponent;
      if(gc.getSelectedGraph()!=null) {
         gc.setUpdateInProgress(true);
         List easToAdd=gc.getSelectedGraph().getGraphManager().insertMissingStartEndBubbles();
         if (easToAdd.size()>0) {
            JaWEManager.getInstance().getJaWEController().startUndouableChange();
            getWorkflowProcess().getExtendedAttributes().addAll(easToAdd);
            List toSelect=new ArrayList();
            toSelect.add(gc.getSelectedGraph().getGraphManager().getXPDLOwner());
            JaWEManager.getInstance().getJaWEController().endUndouableChange(toSelect);
         }
         gc.setUpdateInProgress(false);                  
      }
   }   
}
