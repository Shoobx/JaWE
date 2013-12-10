/**
 * Miroslav Popov, Sep 21, 2005
 * miroslav.popov@gmail.com
 */
package org.enhydra.jawe.components.graph.actions;

import java.awt.event.ActionEvent;

import org.enhydra.jawe.ActionBase;
import org.enhydra.jawe.JaWEComponent;
import org.enhydra.jawe.JaWEManager;
import org.enhydra.jawe.components.graph.GraphController;

/**
 * Removes selected participant from graph (does not delete it from the model),
 * and deletes all of the elements it contains from the XPDL model.
 * @author Miroslav Popov
 * @author Sasa Bojanic
 */
public class RemoveParticipant extends ActionBase {

   public RemoveParticipant (JaWEComponent jawecomponent) {
      super(jawecomponent);
   }

   public void enableDisableAction() {
      GraphController gc = (GraphController)jawecomponent;
      
      boolean en=false;
      if (gc.getSelectedGraph() != null) {
         if (!gc.getSelectedGraph().getXPDLObject().isReadOnly()) {
            en=true;
         }
      }
      setEnabled(en);
   }
   
   public void actionPerformed(ActionEvent e) {
      GraphController gc = (GraphController)jawecomponent;
      gc.getSelectedGraph().getGraphManager().removeCells(gc.getSelectedGraph().getSelectionCells());
      JaWEManager.getInstance().getJaWEController().getSelectionManager().setSelection(gc.getSelectedGraph().getXPDLObject(), true);                              
   }   
}
